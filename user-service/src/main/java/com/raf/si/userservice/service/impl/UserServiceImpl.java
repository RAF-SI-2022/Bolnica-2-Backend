package com.raf.si.userservice.service.impl;

import com.raf.si.userservice.dto.request.*;
import com.raf.si.userservice.dto.response.*;
import com.raf.si.userservice.exception.BadRequestException;
import com.raf.si.userservice.exception.ForbiddenException;
import com.raf.si.userservice.exception.NotFoundException;
import com.raf.si.userservice.mapper.UserMapper;
import com.raf.si.userservice.model.Department;
import com.raf.si.userservice.model.Hospital;
import com.raf.si.userservice.model.Permission;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.model.enums.ShiftType;
import com.raf.si.userservice.repository.*;
import com.raf.si.userservice.service.EmailService;
import com.raf.si.userservice.service.UserService;
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
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;

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
    public Integer getNumOfCovidNursesByDepartment(UUID pbo) {
        List<String> permissions = Arrays.asList(new String[] {"ROLE_MED_SESTRA", "ROLE_VISA_MED_SESTRA"});
        return (int) userRepository.countCovidNursesByPbo(pbo, permissions);
    }

    @Override
    public UserListAndCountResponse getSubordinates(Pageable pageable) {
        UUID lbz = TokenPayloadUtil.getTokenPayload().getLbz();
        User user = userRepository.findByLbzAndFetchPermissions(lbz)
                .orElseThrow( () -> {
                    log.error("Ne postoji korisnik sa lbz '{}'", lbz);
                    throw new NotFoundException("Korisnik sa datim lbz-om ne postoji");
                });

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
            throw new BadRequestException(errMessage);
        }

        return userMapper.modelToUserListAndCountResponse(subordinates);
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

    @Scheduled(cron = "@yearly")
    @Transactional
    void updateUsersDaysOffAtYearEnd() {
        List<User> users = userRepository.findAll();
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        for (User user : users) {
            long freeDays = shiftRepository.countShiftsByShiftTypeForUserAfterDate(user, now, ShiftType.SLOBODAN_DAN);
            user.setUsedDaysOff((int) freeDays);
        }
        userRepository.saveAll(users);
        log.info("Iskorisceni dani za sve zaposlene su azurirani");
    }
}
