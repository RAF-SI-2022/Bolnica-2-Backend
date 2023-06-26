package com.raf.si.userservice.service.impl;

import com.raf.si.userservice.dto.request.*;
import com.raf.si.userservice.dto.response.*;
import com.raf.si.userservice.exception.BadRequestException;
import com.raf.si.userservice.exception.ForbiddenException;
import com.raf.si.userservice.exception.InternalServerErrorException;
import com.raf.si.userservice.exception.NotFoundException;
import com.raf.si.userservice.mapper.UserMapper;
import com.raf.si.userservice.model.*;
import com.raf.si.userservice.model.enums.ShiftType;
import com.raf.si.userservice.repository.*;
import com.raf.si.userservice.service.EmailService;
import com.raf.si.userservice.service.UserService;
import com.raf.si.userservice.utils.HttpUtils;
import com.raf.si.userservice.utils.TokenPayload;
import com.raf.si.userservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final DepartmentRepository departmentRepository;
    private final PermissionsRepository permissionsRepository;
    private final ShiftRepository shiftRepository;
    private final ShiftTimeRepository shiftTimeRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           EmailService emailService, DepartmentRepository departmentRepository,
                           PermissionsRepository permissionsRepository,
                           ShiftRepository shiftRepository,
                           ShiftTimeRepository shiftTimeRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.emailService = emailService;
        this.departmentRepository = departmentRepository;
        this.permissionsRepository = permissionsRepository;
        this.shiftRepository = shiftRepository;
        this.shiftTimeRepository = shiftTimeRepository;
    }

    @Transactional
    @Override
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        userRepository.findUserByEmail(createUserRequest.getEmail()).ifPresent((k) -> {
            log.error("Korisnik sa mejlom '{}' vec postoji", createUserRequest.getEmail());
            throw new BadRequestException("Korisnik sa datim email-om vec postoji");
        });

        Department department = departmentRepository.findById(createUserRequest.getDepartmentId())
                .orElseThrow(() -> {
                            log.error("Odeljenje sa id-ijem '{}' ne postoji", createUserRequest.getDepartmentId());
                            throw new NotFoundException("Odeljenje sa datim id-ijem ne postoji");
                        }
                );

        List<Permission> permissions = permissionsRepository.findPermissionsByNameIsIn(
                Arrays.asList(createUserRequest.getPermissions())
        );


        User user = userRepository.save(userMapper.requestToModel(createUserRequest, department, permissions));
        log.info("Korisnik sa id-ijem '{}' uspesno sacuvan", user.getId());

        return userMapper.modelToResponse(user);
    }


    @Cacheable(value = "user", key = "#lbz")
    private User findUserByLbz(UUID lbz) {
        return userRepository.findUserByLbz(lbz).orElseThrow(() -> {
            log.error("Ne postoji korisnik sa lbz-om '{}'", lbz);
            throw new NotFoundException(String.format("Ne postoji korisnik sa lbz-om: %s ", lbz));
        });
    }

    @Override
    public UserResponse getUserByLbz(UUID lbz) {
        User user = findUserByLbz(lbz);

        return userMapper.modelToResponse(user);

    }

    @Override
    public boolean userExistsByLbzAndIsDeleted(UUID lbz) {
        return userRepository.userExists(lbz, false);
    }


    @CacheEvict(value = "user", key = "#user.lbz")
    private User userSetDeleted(User user) {
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public UserResponse deleteUser(Long id, UUID loggedLbz) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("Ne postoji korisnik sa id-ijem '{}'", id);
            throw new NotFoundException("Korisnik sa datim id-ijem ne postoji");
        });

        if (user.getLbz().equals(loggedLbz)) {
            log.error("Korisnik je pokusao obrisati sam sebe, lbz '{}'", loggedLbz);
            throw new ForbiddenException("Ova akcija nije dozvoljena");
        }

        user.setDeleted(true);
        user = userSetDeleted(user);
        log.info("Korisnicki nalog sa id-ijem '{}' je uspesno obrisan", id);

        return userMapper.modelToResponse(user);
    }


    @CachePut(value = "user", key = "#user.lbz")
    private User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public UserResponse updateUser(UUID lbz, UpdateUserRequest updateUserRequest, boolean isAdmin) {
        User user = userRepository.findUserByLbz(lbz).orElseThrow(() -> {
            log.error("Ne postoji korisnik sa lbz '{}'", lbz);
            throw new NotFoundException("Korisnik sa datim lbz-om ne postoji");
        });

        Department department = departmentRepository.findById(updateUserRequest.getDepartmentId())
                .orElseThrow(() -> {
                            log.error("Odeljenje sa id-ijem '{}' ne postoji", updateUserRequest.getDepartmentId());
                            throw new NotFoundException("Odeljenje sa datim id-ijem ne postoji");
                        }
                );

        User updatedUser = isAdmin ? userMapper.updateRequestToModel(user, updateUserRequest, department)
                : userMapper.updateRegularRequestToModel(user, updateUserRequest);

        updatedUser = updateUser(user);
        log.info("Korisnik sa lbz-om '{}' uspesno update-ovan", lbz);
        return userMapper.modelToResponse(updatedUser);
    }

    @Override
    public UserListAndCountResponse listUsers(String firstName, String lastName,
                                              String departmentName, String hospitalName,
                                              boolean includeDeleted, Boolean hasCovidAccess,
                                              Pageable pageable) {

        return userMapper.modelToUserListAndCountResponse(userRepository.listAllUsers(firstName.toLowerCase(), lastName.toLowerCase(),
                departmentName.toLowerCase(), hospitalName.toLowerCase(),
                adjustIncludeDeleteParameter(includeDeleted),
                adjustHasCovidAccessParameter(hasCovidAccess), pageable));
    }

    @Override
    public MessageResponse resetPassword(PasswordResetRequest passwordResetRequest) {
        User user = userRepository.findUserByEmail(passwordResetRequest.getEmail()).orElseThrow(() -> {
            log.error("Korisnik sa email-om '{}' ne postoji", passwordResetRequest.getEmail());
            throw new NotFoundException("Korisnik sa datim email-om ne postoji");
        });

        emailService.resetPassword(user.getEmail(), user.getPasswordToken());

        return new MessageResponse("Proverite vas email za resetovanje sifre");
    }


    private User updateUserPassword(User user, UpdatePasswordRequest updatePasswordRequest) {
        User userTmp = userMapper.setUserPassword(user, updatePasswordRequest.getPassword());
        return updateUser(userTmp);
    }

    @Override
    public MessageResponse updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        User user = userRepository.findByPasswordToken(updatePasswordRequest.getResetToken())
                .orElseThrow(() -> {
                    log.error("Token sifra '{}' ne postoji", updatePasswordRequest.getResetToken());
                    throw new NotFoundException("Token sifra ne postoji");
                });
        User updatedUser = updateUserPassword(user, updatePasswordRequest);
        log.info("Sifra promenjena za korisnika sa email-om '{}'", updatedUser.getEmail());

        return new MessageResponse("Sifra je uspesno promenjena");
    }

    @Override
    public UserResponse updateCovidAccess(UUID lbz, boolean covidAccess) {
        User user = findUserByLbz(lbz);

        if (!canUpdateCovidAccess(user)) {
            String errMessage = String.format("Nemate permisiju da promenite pristup covid-u za korisnika sa lbz-om '%s'", lbz);
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }

        user.setCovidAccess(covidAccess);
        user = userRepository.save(user);
        return userMapper.modelToResponse(user);
    }

    @Override
    public List<DoctorResponse> getAllDoctors() {
        List<String> doctorPermissions = Arrays.asList("ROLE_DR_SPEC_ODELJENJA", "ROLE_DR_SPEC", "ROLE_DR_SPEC_POV");
        log.info("Listanje svih doktora...");
        return userRepository.getAllDoctors(doctorPermissions)
                .stream()
                .map(userMapper::modelToDoctorResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorResponse> getAllDoctorsByDepartment(UUID pbo) {
        List<String> doctorPermissions = Arrays.asList("ROLE_DR_SPEC_ODELJENJA", "ROLE_DR_SPEC", "ROLE_DR_SPEC_POV");
        Department department = departmentRepository.findDepartmentByPbo(pbo).orElseThrow(() -> {
            log.error("Odeljenje sa pbo '{}' ne postoji", pbo);
            throw new NotFoundException("Odeljenje sa datim pbo ne postoji");
        });

        log.info("Listanje svih doktora po odeljenju...");
        return userRepository.getAllDoctorsByDepartment(doctorPermissions, department)
                .stream()
                .map(userMapper::modelToDoctorResponse)
                .collect(Collectors.toList());

    }

    @Override
    public List<UserResponse> getUsersByLbzList(UUIDListRequest lbzListRequest) {
        return userRepository.findByLbzInList(lbzListRequest.getUuids())
                .stream()
                .map(userMapper::modelToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorResponse getHeadOfDepartment(UUID pbo) {
        Department department = departmentRepository.findDepartmentByPbo(pbo).orElseThrow(() -> {
            log.error("Odeljenje sa pbo '{}' ne postoji", pbo);
            throw new NotFoundException("Odeljenje sa datim pbo ne postoji");
        });

        log.info("Dohvatanje nacelnika odeljenja za pbo '{}'", pbo);

        User user = userRepository.getHeadOfDepartment(department, "ROLE_DR_SPEC_ODELJENJA")
                .orElseThrow(() -> {
                    log.error("Ne postoji nacelnik odeljenja za pbo '{}'", pbo);
                    throw new NotFoundException("Ne postoji nacelnik odeljenja za dato odeljenje");
                });
        return userMapper.modelToDoctorResponse(user);
    }

    @Override
    public Integer getNumOfCovidNursesByDepartmentInTimeSlot(UUID pbo, TimeRequest request) {
        List<String> permissions = Arrays.asList(new String[] {"ROLE_MED_SESTRA", "ROLE_VISA_MED_SESTRA"});
        return (int) userRepository.countCovidNursesByPboAndShiftInTimeSlot(
                pbo,
                permissions,
                request.getStartTime(),
                request.getEndTime(),
                ShiftType.SLOBODAN_DAN
        );
    }

    @Override
    public UserListAndCountResponse getSubordinates(Pageable pageable) {
        UUID lbz = TokenPayloadUtil.getTokenPayload().getLbz();
        User user = findUserWithShiftsByLbz(lbz);

        List<Permission> permissions = user.getPermissions();
        List<String> permissionNames = permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toList());

        Page<User> subordinates = null;
        if (permissionNames.contains("ROLE_ADMIN")) {
            UUID pbb = user.getDepartment().getHospital().getPbb();
            subordinates = userRepository.findSubordinatesForAdmin(pbb, pageable);
        } else if (permissionNames.contains("ROLE_DR_SPEC_ODELJENJA")) {
            UUID pbo = user.getDepartment().getPbo();
            subordinates = userRepository.findSubordinatesForHeadOfDepartment(pbo, pageable);
        } else if (permissionNames.contains("ROLE_VISA_MED_SESTRA")) {
            UUID pbo = user.getDepartment().getPbo();
            List<String> nursePermissions = Arrays.asList(new String[] {"ROLE_MED_SESTRA"});
            subordinates = userRepository.findSubordinatesForNurse(pbo, nursePermissions, pageable);
        }

        if (subordinates == null || subordinates.isEmpty()) {
            String errMessage = "Nemate podređenih";
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        }

        return userMapper.modelToUserListAndCountResponse(subordinates);
    }

    @Transactional
    @Override
    public UserShiftResponse addShift(UUID lbz, AddShiftRequest request, String token) {
        User user = findUserWithShiftsByLbz(lbz);
        entityManager.lock(user, LockModeType.PESSIMISTIC_WRITE);

        checkShiftDateValid(request.getDate());

        ShiftType shiftType = ShiftType.valueOfNotation(request.getShiftType());
        if (shiftType == null) {
            String errMessage = String.format("Tip smene '%s' ne postoj", request.getShiftType());
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        }
        ShiftTime shiftTime = shiftTimeRepository.findByShiftType(shiftType);

        Shift shift = userMapper.addShiftRequestToModel(user, request, shiftTime);

        if (!shift.getShiftType().equals(ShiftType.SLOBODAN_DAN)) {
            long shiftLen = ChronoUnit.HOURS.between(shift.getStartTime(), shift.getEndTime());
            if (shiftLen < 6) {
                String errMessage = "Smena mora da bude bar 6 sati";
                log.error(errMessage);
                throw new BadRequestException(errMessage);
            }
            if (shiftLen > 12) {
                String errMessage = "Smena ne sme da bude duža od 12 sati";
                log.error(errMessage);
                throw new BadRequestException(errMessage);
            }
        }

        Shift existingShift = findExistingShift(user, shift);

        if (existingShift == null) {
            addNewShift(user, shift);
        } else {
            updateExistingShift(user, shift, existingShift, token);
        }

        log.info(String.format("Sacuvana smena '%s' za korisnika za lbz-om %s", shift, lbz));
        return userMapper.modelToUserShiftResponse(user);
    }

    @Override
    public UserResponse updateDaysOff(UUID lbz, int daysOff) {
        User user = findUserByLbz(lbz);

        if (daysOff > 50) {
            String errMessage = "Ne može se dati više od 50 slobodnih dana";
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
        if (daysOff < 0) {
            String errMessage = "Broj slobodnih dana ne sme biti negativan broj";
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }

        checkCanUpdateDaysOff(user, daysOff);

        user.setDaysOff(daysOff);
        user = userRepository.save(user);
        log.info(String.format("Korisniku sa lbz-om %s je promenjen broj slobodnih dana na %d", lbz,  daysOff));
        return userMapper.modelToResponse(user);
    }

    @Override
    public Boolean canScheduleForDoctor(UUID lbz, boolean covid, TimeRequest timeRequest) {
        return shiftRepository.canScheduleForLbz(lbz, covid, timeRequest.getStartTime(), timeRequest.getEndTime(), ShiftType.SLOBODAN_DAN);
    }

    @Override
    public UserShiftResponse getUserWithShiftsByLbz(UUID lbz) {
        User user = findUserWithShiftsByLbz(lbz);
        return userMapper.modelToUserShiftResponse(user);
    }

    private List<Boolean> adjustIncludeDeleteParameter(boolean includeDeleted) {
        List<Boolean> list = new ArrayList<>();
        list.add(false);
        if (includeDeleted)
            list.add(true);
        return list;
    }

    private List<Boolean> adjustHasCovidAccessParameter(Boolean hasCovidAccess) {
        if (hasCovidAccess == null) {
            return Arrays.asList(new Boolean[] {true, false});
        }
        return Arrays.asList(new Boolean[]{hasCovidAccess});
    }

    private boolean canUpdateCovidAccess(User userForUpdate) {
        TokenPayload token = TokenPayloadUtil.getTokenPayload();
        List<String> loggedInRoles = token.getPermissions();
        Department department = userForUpdate.getDepartment();
        Hospital hospital = department.getHospital();

        if (loggedInRoles.contains("ROLE_ADMIN") && hospital.getPbb().equals(token.getPbb())) {
            return true;
        }

        if (loggedInRoles.contains("ROLE_DR_SPEC_ODELJENJA") && department.getPbo().equals(token.getPbo())) {
            return true;
        }

        if (loggedInRoles.contains("ROLE_VISA_MED_SESTRA") && department.getPbo().equals(token.getPbo())) {
            List<String> roles = userForUpdate.getPermissions()
                    .stream()
                    .map(Permission::getName)
                    .collect(Collectors.toList());

            if (roles.contains("ROLE_VISA_MED_SESTRA") || roles.contains("ROLE_MED_SESTRA")) {
                return true;
            }
        }

        return false;
    }

    private User findUserWithShiftsByLbz(UUID lbz) {
        return userRepository.findByLbzAndFetchPermissions(lbz)
                .orElseThrow( () -> {
                    log.error("Ne postoji korisnik sa lbz '{}'", lbz);
                    throw new NotFoundException("Korisnik sa datim lbz-om ne postoji");
                });
    }

    private Shift findExistingShift(User user, Shift shift) {
        LocalDateTime startTime = shift.getStartTime().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endTime = startTime.plusDays(1);
        List<Shift> existingShifts = shiftRepository.findByUserAndStartTimeBetween(user, startTime, endTime);
        if (existingShifts == null || existingShifts.isEmpty()) {
            return null;
        } else if (existingShifts.size() > 1) {
            String errMessage = "Pronađene 2 smene za isti dan kako šta";
            log.error(errMessage);
            throw new InternalServerErrorException(errMessage);
        } else {
            return existingShifts.get(0);
        }
    }

    private void checkShiftDateValid(LocalDate date) {
        LocalDate now = LocalDate.now();
        LocalDate oneYearFromNow = now.plusYears(1);
        if (date.isBefore(now)) {
            String errMessage = String.format("Datum '%s' je u prošlosti", date);
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
        if (date.isAfter(oneYearFromNow)) {
            String errMessage = String.format("Datum '%s' je više od godinu dana u budućnosti", date);
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
    }

    private void addNewShift(User user, Shift shift) {
        if (shift.getShiftType().equals(ShiftType.SLOBODAN_DAN)) {
            if (canAddDayOff(user, shift)) {
                if (shiftThisYear(shift)) {
                    user.incrementUsedDaysOff();
                }
            } else {
                String errMessage = "Svi slobodni dani za tu godinu su iskorišćeni";
                log.error(errMessage);
                throw new BadRequestException(errMessage);
            }
        }

        shiftRepository.save(shift);
    }

    private void updateExistingShift(User user, Shift shift, Shift existingShift, String token) {
        checkCanUpdateShift(user, shift, existingShift, token);

        if (shift.getShiftType().equals(ShiftType.SLOBODAN_DAN) && !existingShift.getShiftType().equals(ShiftType.SLOBODAN_DAN)) {
            if (canAddDayOff(user, shift)) {
                if (shiftThisYear(shift)) {
                    user.incrementUsedDaysOff();
                }
            } else {
                String errMessage = "Svi slobodni dani za tu godinu su iskorišćeni";
                log.error(errMessage);
                throw new BadRequestException(errMessage);
            }
        } else if (!shift.getShiftType().equals(ShiftType.SLOBODAN_DAN)
                && existingShift.getShiftType().equals(ShiftType.SLOBODAN_DAN)
                && shiftThisYear(shift)) {
            user.decrementUsedDaysOff();
        }

        existingShift.setShiftType(shift.getShiftType());
        existingShift.setStartTime(shift.getStartTime());
        existingShift.setEndTime(shift.getEndTime());

        shiftRepository.save(existingShift);
    }

    private void checkCanUpdateShift(User user, Shift shift, Shift existingShift, String token) {
        TimeRequest existingShiftTimeRequest = new TimeRequest(existingShift.getStartTime(), existingShift.getEndTime());
        if (existingShift.getShiftType().equals(ShiftType.SLOBODAN_DAN)) {
            existingShiftTimeRequest.setStartTime(existingShift.getStartTime().plusYears(100));
            existingShiftTimeRequest.setEndTime(existingShiftTimeRequest.getStartTime());
        }

        TimeRequest newShiftRequest = new TimeRequest(shift.getStartTime(), shift.getEndTime());
        if (shift.getShiftType().equals(ShiftType.SLOBODAN_DAN)) {
            newShiftRequest.setStartTime(existingShift.getStartTime().plusYears(100));
            newShiftRequest.setEndTime(newShiftRequest.getStartTime());
        }

        UpdateTermsNewShiftRequest request = new UpdateTermsNewShiftRequest(existingShiftTimeRequest, newShiftRequest);

        List<String> userPermissions = user.getPermissions()
                .stream()
                .map(Permission::getName)
                .collect(Collectors.toList());

        boolean isDoctor = Permission.doctorPermissions.stream().anyMatch(perm -> userPermissions.contains(perm));
        if (isDoctor) {
            checkDoctorScheduledExamsForTimeSlot(user, request, token);
        }

        boolean isNurse = Permission.nursePermissions.stream().anyMatch(perm -> userPermissions.contains(perm));
        if (isNurse) {
            checkAndUpdateNurseAvailableTermsForTimeSlot(request, token);
        }
    }

    private void checkDoctorScheduledExamsForTimeSlot(User user, UpdateTermsNewShiftRequest request, String token) {
        UUID lbz = user.getLbz();
        List<Date> alreadyScheduled;
        try {
            alreadyScheduled = HttpUtils.checkDoctorScheduledExamsForTimeSlot(lbz, request, token);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }

        if (alreadyScheduled != null && !alreadyScheduled.isEmpty()) {
            String errMessage = String.format("Prosledjeni doktor ima zakazane preglede tog dana u terminima:");
            for (Date date : alreadyScheduled) {
                errMessage += "\n" + date;
            }
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }

    }

    private void checkAndUpdateNurseAvailableTermsForTimeSlot(UpdateTermsNewShiftRequest request, String token) {
        List<LocalDateTime> fullTerms;
        try {
            fullTerms = HttpUtils.checkAndUpdateNurseTerms(request, token);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
        if (fullTerms != null && !fullTerms.isEmpty()) {
            String errMessage = "Popunjeni su svi termini testiranja i vakcinacije za:";
            for (LocalDateTime ldt : fullTerms) {
                errMessage += "\n" + ldt;
            }
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
    }

    private boolean canAddDayOff(User user, Shift shift) {
        if (shiftThisYear(shift)) {
            return user.getUsedDaysOff() < user.getDaysOff() ? true : false;
        } else {
            LocalDateTime startDate = shift.getStartTime().with(firstDayOfYear()).truncatedTo(ChronoUnit.DAYS);
            LocalDateTime endDate = startDate.plusYears(1);
            int usedDaysOffForYear = (int) shiftRepository.countShiftsByShiftTypeForUserBetweenDates(
                    user,
                    startDate,
                    endDate,
                    ShiftType.SLOBODAN_DAN
            );
            return usedDaysOffForYear < user.getDaysOff() ? true : false;
        }
    }

    private boolean shiftThisYear(Shift shift) {
        int currentYear = LocalDateTime.now().getYear();
        int shiftYear = shift.getStartTime().getYear();
        return shiftYear == currentYear ? true : false;
    }

    private void checkCanUpdateDaysOff(User user, int daysOff) {
        if (user.getUsedDaysOff() > daysOff) {
            String errMessage = String.format("Broj iskorišćenih slobodnih dana u ovoj godini (%d) je veći od novog broja slobodnih dana (%d)", user.getUsedDaysOff(), daysOff);
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }

        LocalDateTime nextYearStart = LocalDateTime.now()
                .with(firstDayOfYear())
                .truncatedTo(ChronoUnit.DAYS)
                .plusYears(1);
        LocalDateTime nextYearEnd = nextYearStart.plusYears(1);
        int usedFreeDaysNextYear = (int) shiftRepository.countShiftsByShiftTypeForUserBetweenDates(
                user,
                nextYearStart,
                nextYearEnd,
                ShiftType.SLOBODAN_DAN
        );

        if (usedFreeDaysNextYear > daysOff) {
            String errMessage = String.format("Broj iskorišćenih slobodnih dana u sledećoj godini (%d) je veći od novog broja slobodnih dana (%d)", usedFreeDaysNextYear, daysOff);
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
    }

    @Scheduled(cron = "@yearly")
    @Transactional
    void updateUsersDaysOffAtYearEnd() {
        List<User> users = userRepository.findAll();
        LocalDateTime now = LocalDateTime.now().with(firstDayOfYear()).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endOfYear = now.plusYears(1);
        for (User user : users) {
            long freeDays = shiftRepository.countShiftsByShiftTypeForUserBetweenDates(user, now, endOfYear, ShiftType.SLOBODAN_DAN);
            user.setUsedDaysOff((int) freeDays);
        }
        userRepository.saveAll(users);
        log.info("Iskorisceni dani za sve zaposlene su azurirani");
    }
}
