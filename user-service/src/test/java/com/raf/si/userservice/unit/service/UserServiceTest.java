package com.raf.si.userservice.unit.service;

import com.raf.si.userservice.dto.request.*;
import com.raf.si.userservice.dto.response.*;
import com.raf.si.userservice.exception.BadRequestException;
import com.raf.si.userservice.exception.ForbiddenException;
import com.raf.si.userservice.exception.InternalServerErrorException;
import com.raf.si.userservice.exception.NotFoundException;
import com.raf.si.userservice.mapper.UserMapper;
import com.raf.si.userservice.model.*;
import com.raf.si.userservice.model.enums.Profession;
import com.raf.si.userservice.model.enums.ShiftType;
import com.raf.si.userservice.model.enums.Title;
import com.raf.si.userservice.repository.*;
import com.raf.si.userservice.service.EmailService;
import com.raf.si.userservice.service.UserService;
import com.raf.si.userservice.service.impl.UserServiceImpl;
import com.raf.si.userservice.utils.HttpUtils;
import com.raf.si.userservice.utils.TokenPayload;
import com.raf.si.userservice.utils.TokenPayloadUtil;
import net.bytebuddy.asm.Advice;
import org.assertj.core.api.OptionalAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;

public class UserServiceTest {

    private UserService userService;
    private UserMapper userMapper;
    private UserRepository userRepository;
    private DepartmentRepository departmentRepository;
    private PermissionsRepository permissionsRepository;
    private ShiftRepository shiftRepository;
    private ShiftTimeRepository shiftTimeRepository;
    private PasswordEncoder passwordEncoder;
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        departmentRepository = mock(DepartmentRepository.class);
        permissionsRepository = mock(PermissionsRepository.class);
        shiftRepository = mock(ShiftRepository.class);
        shiftTimeRepository = mock(ShiftTimeRepository.class);
        EmailService emailService = mock(EmailService.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        entityManager = mock(EntityManager.class);
        userMapper = new UserMapper(passwordEncoder);
        userService = new UserServiceImpl(userRepository, userMapper, emailService,
                departmentRepository, permissionsRepository, shiftRepository, shiftTimeRepository);

        ReflectionTestUtils.setField(
                userService,
                "entityManager",
                entityManager
        );
    }

    @AfterEach
    void cleanup() {
        Mockito.framework().clearInlineMocks();
    }

    @Test
    public void createUser_WhenUserWithGivenEmailExists_ThrowBadRequestException() {
        CreateUserRequest createUserRequest = createUserRequest();

        when(userRepository.findUserByEmail(createUserRequest.getEmail()))
                .thenReturn(Optional.of(new User()));

        assertThrows(BadRequestException.class, () -> userService.createUser(createUserRequest));
    }

    @Test
    public void createUser_WhenDepartmentWithIdNotExist_ThrowNotFoundException() {
        CreateUserRequest createUserRequest = createUserRequest();

        when(userRepository.findUserByEmail(createUserRequest.getEmail()))
                .thenReturn(Optional.empty());

        when(departmentRepository.findById(createUserRequest.getDepartmentId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.createUser(createUserRequest));
    }

    @Test
    public void createUser_success() {
        CreateUserRequest createUserRequest = createUserRequest();

        Department department = createDepartment();
        List<Permission> permissionList = new ArrayList<>();
        permissionList.add(createPermission(1L, "ROLE_ADMIN"));
        permissionList.add(createPermission(2L, "ROLE_DR_SPEC"));

        when(userRepository.findUserByEmail(createUserRequest.getEmail()))
                .thenReturn(Optional.empty());
        when(departmentRepository.findById(createUserRequest.getDepartmentId()))
                .thenReturn(Optional.of(department));
        when(permissionsRepository.findPermissionsByNameIsIn(anyList()))
                .thenReturn(permissionList);

        User user = userMapper.requestToModel(createUserRequest, createDepartment(), permissionList);

        when(userRepository.save(any())).thenReturn(user);

        assertEquals(userService.createUser(createUserRequest), userMapper.modelToResponse(user));
    }

    @Test
    public void getUserByLbz_WhenUserWithGivenLbzNotExist_ThrowNotFoundException() {
        UUID lbz = UUID.randomUUID();

        when(userRepository.findUserByLbz(lbz)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserByLbz(lbz));
    }

    @Test
    public void getUserByLbz_Success() {
        UUID lbz = UUID.randomUUID();

        User user = createUser();

        when(userRepository.findUserByLbz(lbz)).thenReturn(Optional.of(user));

        assertEquals(userService.getUserByLbz(lbz), userMapper.modelToResponse(user));
    }

    @Test
    public void userExistsByLbzAndIsDeleted_Success() {
        UUID lbz = UUID.randomUUID();

        when(userRepository.userExists(lbz, false)).thenReturn(true);
        assertTrue(userService.userExistsByLbzAndIsDeleted(lbz));
    }

    @Test
    public void deleteUser_WhenGivenIdNotExist_ThrowsNotFoundException() {
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(id, UUID.randomUUID()));
    }

    @Test
    public void deleteUser_WhenGivenLbzIsLoggedUser_ThrowsForbiddenException() {
        Long id = 1L;

        User user = createUser();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertThrows(ForbiddenException.class, () -> userService.deleteUser(id, user.getLbz()));
    }

    @Test
    public void deleteUser_Success() {
        Long id = 1L;
        User user = createUser();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        user.setDeleted(true);
        when(userRepository.save(user)).thenReturn(user);

        assertEquals(userService.deleteUser(id, UUID.randomUUID()), userMapper.modelToResponse(user));
    }

    @Test
    public void updateUser_WhenUserWithGivenLbzNotFound_ThrowsNotFoundException() {
        UpdateUserRequest updateUserRequest = createUpdateUserRequest();
        UUID lbz = UUID.randomUUID();

        when(userRepository.findUserByLbz(lbz))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(lbz, updateUserRequest, true));
    }

    @Test
    public void updateUser_WhenDepartmentIdNotFound_ThrowsNotFoundException() {
        UpdateUserRequest updateUserRequest = createUpdateUserRequest();
        UUID lbz = UUID.randomUUID();
        User user = createUser();

        when(userRepository.findUserByLbz(lbz)).thenReturn(Optional.of(user));
        when(departmentRepository.findById(updateUserRequest.getDepartmentId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(lbz, updateUserRequest, true));
    }

    @Test
    public void updateUser_WhenUserIsAdmin_Success() {
        UpdateUserRequest updateUserRequest = createUpdateUserRequest();
        UUID lbz = UUID.randomUUID();
        User user = createUser();
        Department department = createDepartment();

        when(userRepository.findUserByLbz(lbz)).thenReturn(Optional.of(user));
        when(departmentRepository.findById(updateUserRequest.getDepartmentId()))
                .thenReturn(Optional.of(department));

        user = userMapper.updateRequestToModel(user, updateUserRequest, department);

        when(userRepository.save(user)).thenReturn(user);


        assertEquals(userService.updateUser(lbz, updateUserRequest, true), userMapper.modelToResponse(user));
    }

    @Test
    public void updateUser_WhenUserIsNotAdmin_Success() {
        UpdateUserRequest updateUserRequest = createUpdateUserRequest();
        UUID lbz = UUID.randomUUID();
        User user = createUser();
        user.setPassword(passwordEncoder.encode(updateUserRequest.getOldPassword()));
        Department department = createDepartment();

        when(userRepository.findUserByLbz(lbz)).thenReturn(Optional.of(user));
        when(departmentRepository.findById(updateUserRequest.getDepartmentId()))
                .thenReturn(Optional.of(department));

        System.out.println(passwordEncoder.matches(updateUserRequest.getOldPassword(), user.getPassword()));
        user = userMapper.updateRequestToModel(user, updateUserRequest, department);

        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);

        assertEquals(userService.updateUser(lbz, updateUserRequest, false), userMapper.modelToResponse(user));
    }

    @Test
    public void listUsers_Success() {
        String firstName = "firstName";
        String lastName = "lastName";
        String departmentName = "departmentName";
        String hospitalName = "hospitalName";
        Pageable pageable = PageRequest.of(0, 5);

        List<User> users = new ArrayList<>();
        users.add(createUser());
        Page<User> pages = new PageImpl<>(users);

        when(userRepository.listAllUsers(any(), any(), any(), any(), any(), anyList(), any()))
                .thenReturn(pages);

        UserListAndCountResponse userListAndCountResponse = userMapper.modelToUserListAndCountResponse(pages);

        assertEquals(userService.listUsers(firstName, lastName, departmentName, hospitalName, true, null, pageable),
                userListAndCountResponse);
    }

    @Test
    public void resetPassword_WhenEmailNotFound_ThrowsNotFoundException() {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setEmail("email@something.com");

        when(userRepository.findUserByEmail(passwordResetRequest.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.resetPassword(passwordResetRequest));
    }

    @Test
    public void resetPassword_Success() {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setEmail("email@something.com");

        User user = createUser();

        when(userRepository.findUserByEmail(passwordResetRequest.getEmail()))
                .thenReturn(Optional.of(user));

        assertEquals(userService.resetPassword(passwordResetRequest),
                new MessageResponse("Proverite vas email za resetovanje sifre"));
    }

    @Test
    public void updatePassword_WhenPasswordTokenNotValid_ThrowsNotFoundException() {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setResetToken(UUID.randomUUID());
        updatePasswordRequest.setPassword("new password");

        when(userRepository.findByPasswordToken(updatePasswordRequest.getResetToken()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updatePassword(updatePasswordRequest));
    }

    @Test
    public void updatePassword_Success() {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setResetToken(UUID.randomUUID());
        updatePasswordRequest.setPassword("new password");

        User user = createUser();

        when(userRepository.findByPasswordToken(updatePasswordRequest.getResetToken()))
                .thenReturn(Optional.of(user));

        user = userMapper.setUserPassword(user, updatePasswordRequest.getPassword());

        when(userRepository.save(user)).thenReturn(user);

        assertEquals(userService.updatePassword(updatePasswordRequest),
                new MessageResponse("Sifra je uspesno promenjena"));
    }

    @Test
    public void getAllDoctors_Success() {
        User user = createUser();

        when(userRepository.getAllDoctors(any()))
                .thenReturn(Collections.singletonList(user));

        DoctorResponse doctorResponse = userMapper.modelToDoctorResponse(user);

        assertEquals(userService.getAllDoctors(), Collections.singletonList(doctorResponse));
    }

    @Test
    public void getAllDoctorsByDepartment_WhenDepartmentPboNotExist_ThrowNotFoundException() {
        UUID pbo = UUID.randomUUID();

        when(departmentRepository.findDepartmentByPbo(pbo))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getAllDoctorsByDepartment(pbo));
    }

    @Test
    public void getAllDoctorsByDepartment_Success() {
        User user = createUser();

        when(departmentRepository.findDepartmentByPbo(user.getDepartment().getPbo()))
                .thenReturn(Optional.of(user.getDepartment()));

        when(userRepository.getAllDoctorsByDepartment(any(), any()))
                .thenReturn(Collections.singletonList(user));

        DoctorResponse doctorResponse = userMapper.modelToDoctorResponse(user);

        assertEquals(userService.getAllDoctorsByDepartment(user.getDepartment().getPbo()),
                Collections.singletonList(doctorResponse));
    }

    @Test
    public void getHeadOfDepartment_WhenDepartmentDoesNotExist_ThrowNotFoundException() {
        UUID pbo = UUID.randomUUID();

        when(departmentRepository.findDepartmentByPbo(pbo))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getHeadOfDepartment(pbo));
    }

    @Test
    public void getHeadOfDepartment_WhenHeadOfDepartmentDoesNotExist_ThrowNotFoundException() {
        Department department = createDepartment();

        when(departmentRepository.findDepartmentByPbo(department.getPbo()))
                .thenReturn(Optional.of(department));

        when(userRepository.getHeadOfDepartment(any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getHeadOfDepartment(department.getPbo()));
    }

    @Test
    public void getHeadOfDepartment_Success() {
        Department department = createDepartment();
        User user = createUser();

        when(departmentRepository.findDepartmentByPbo(department.getPbo()))
                .thenReturn(Optional.of(department));

        when(userRepository.getHeadOfDepartment(any(), any()))
                .thenReturn(Optional.of(user));

        assertEquals(userService.getHeadOfDepartment(department.getPbo()),
                userMapper.modelToDoctorResponse(user));
    }

    @Test
    public void updateCovidAccess_WhenUserDoesntHavePermissionToChange_ThrowBadRequestException() {
        User user = makeUser(new ArrayList<>());
        mockTokenPayloadUtil();

        Department department = new Department();
        department.setPbo(UUID.randomUUID());

        Hospital hospital = new Hospital();
        hospital.setPbb(UUID.randomUUID());

        department.setHospital(hospital);
        user.setDepartment(department);

        when(userRepository.findUserByLbz(user.getLbz()))
                .thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class,
                () -> userService.updateCovidAccess(user.getLbz(), true));
    }

    @Test
    public void updateCovidAccess_UserAdminAndInSameHospital_Success() {
        User user = makeUser(List.of("ROLE_ADMIN"));
        mockTokenPayloadUtil(List.of("ROLE_ADMIN"));

        when(userRepository.findUserByLbz(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(user))
                .thenReturn(user);

        assertEquals(userService.updateCovidAccess(user.getLbz(), true),
                userMapper.modelToResponse(user));
    }

    @Test
    public void updateCovidAccess_UserDrSpecOdeljenjaAndSameDepartment_Success() {
        User user = makeUser(List.of("ROLE_DR_SPEC_ODELJENJA"));
        mockTokenPayloadUtil(List.of("ROLE_DR_SPEC_ODELJENJA"));

        when(userRepository.findUserByLbz(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(user))
                .thenReturn(user);

        assertEquals(userService.updateCovidAccess(user.getLbz(), true),
                userMapper.modelToResponse(user));
    }

    @Test
    public void updateCovidAccess_UserVisaMedSestra_Success() {
        User user = makeUser(List.of("ROLE_VISA_MED_SESTRA"));
        mockTokenPayloadUtil(List.of("ROLE_VISA_MED_SESTRA"));

        when(userRepository.findUserByLbz(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(user))
                .thenReturn(user);

        assertEquals(userService.updateCovidAccess(user.getLbz(), true),
                userMapper.modelToResponse(user));
    }

    @Test
    public void getUsersByLbzList_Success() {
        User user = makeUser(new ArrayList<>());
        List<UserResponse> responseList = List.of(userMapper.modelToResponse(user));
        UUIDListRequest request = new UUIDListRequest();
        request.setUuids(List.of(user.getLbz()));

        when(userRepository.findByLbzInList(List.of(user.getLbz())))
                .thenReturn(List.of(user));

        assertEquals(userService.getUsersByLbzList(request),
                responseList);
    }

    @Test
    public void getNumOfCovidNursesByDepartmentInTimeSlot_Success() {
        long retVal = 1;
        UUID pbo = createDepartment().getPbo();
        TimeRequest timeRequest = new TimeRequest(LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.countCovidNursesByPboAndShiftInTimeSlot(any(), anyList(), any(), any(), any()))
                .thenReturn(retVal);

        assertEquals(userService.getNumOfCovidNursesByDepartmentInTimeSlot(pbo, timeRequest),
                (int) retVal);
    }

    @Test
    public void getSubordinates_NoSubordinatesFound_ThrowsNotFoundException() {
        User user = makeUser(new ArrayList<>());

        mockTokenPayloadUtil();

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class,
                () -> userService.getSubordinates(PageRequest.of(0, 5)));
    }

    @Test
    public void getSubordinates_UserIsAdmin_Success() {
        User user = makeUser(List.of("ROLE_ADMIN"));
        Pageable pageable = PageRequest.of(0, 5);

        Page<User> page = new PageImpl<>(List.of(user), pageable, 1L);

        UserListAndCountResponse response = userMapper.modelToUserListAndCountResponse(page);

        mockTokenPayloadUtil();

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(userRepository.findSubordinatesForAdmin(user.getDepartment().getHospital().getPbb(), pageable))
                .thenReturn(page);

        assertEquals(userService.getSubordinates(pageable),
                response);
    }

    @Test
    public void getSubordinates_UserIsDrSpecOdeljenja_Success() {
        User user = makeUser(List.of("ROLE_DR_SPEC_ODELJENJA"));
        Pageable pageable = PageRequest.of(0, 5);

        Page<User> page = new PageImpl<>(List.of(user), pageable, 1L);

        UserListAndCountResponse response = userMapper.modelToUserListAndCountResponse(page);

        mockTokenPayloadUtil();

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(userRepository.findSubordinatesForHeadOfDepartment(user.getDepartment().getPbo(), pageable))
                .thenReturn(page);

        assertEquals(userService.getSubordinates(pageable),
                response);
    }

    @Test
    public void getSubordinates_UserIsVisaMedSestra_Success() {
        User user = makeUser(List.of("ROLE_VISA_MED_SESTRA"));
        Pageable pageable = PageRequest.of(0, 5);

        Page<User> page = new PageImpl<>(List.of(user), pageable, 1L);

        UserListAndCountResponse response = userMapper.modelToUserListAndCountResponse(page);

        mockTokenPayloadUtil();

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(userRepository.findSubordinatesForNurse(any(), any(), any()))
                .thenReturn(page);

        assertEquals(userService.getSubordinates(pageable),
                response);
    }

    @Test
    public void addShift_DateInPast_ThrowsBadRequestException() {
        User user = makeUser(new ArrayList<>());

        AddShiftRequest request = makeAddShiftRequest();
        request.setDate(LocalDate.now().minusDays(1));

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class,
                () -> userService.addShift(user.getLbz(), request, ""));
    }

    @Test
    public void addShift_DateMoreThanYearInFuture_ThrowsBadRequestException() {
        User user = makeUser(new ArrayList<>());

        AddShiftRequest request = makeAddShiftRequest();
        request.setDate(LocalDate.now().plusYears(2));

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class,
                () -> userService.addShift(user.getLbz(), request, ""));
    }

    @Test
    public void addShift_ShiftTypeDoesntExist_ThrowsNotFoundException() {
        User user = makeUser(new ArrayList<>());

        AddShiftRequest request = makeAddShiftRequest();
        request.setShiftType("A");

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class,
                () -> userService.addShift(user.getLbz(), request, ""));
    }

    @Test
    public void addShift_ShiftShorterThan6Hours_ThrowsBadRequestException() {
        User user = makeUser(new ArrayList<>());

        AddShiftRequest request = makeAddShiftRequest();
        request.setEndTime(request.getStartTime().plusHours(1));

        ShiftTime shiftTime = makeShiftTime();
        shiftTime.setShiftType(ShiftType.MEDJUSMENA);

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(shiftTimeRepository.findByShiftType(any()))
                .thenReturn(shiftTime);

        assertThrows(BadRequestException.class,
                () -> userService.addShift(user.getLbz(), request, ""));
    }

    @Test
    public void addShift_ShiftLongerThan12Hours_ThrowsBadRequestException() {
        User user = makeUser(new ArrayList<>());

        AddShiftRequest request = makeAddShiftRequest();
        request.setStartTime(LocalTime.of(1, 1, 1));
        request.setEndTime(LocalTime.of(20, 1, 1));

        ShiftTime shiftTime = makeShiftTime();
        shiftTime.setShiftType(ShiftType.MEDJUSMENA);

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(shiftTimeRepository.findByShiftType(any()))
                .thenReturn(shiftTime);

        assertThrows(BadRequestException.class,
                () -> userService.addShift(user.getLbz(), request, ""));
    }

    @Test
    public void addShift_2ShiftsInSameDay_ThrowsInternalServerException() {
        User user = makeUser(new ArrayList<>());

        AddShiftRequest request = makeAddShiftRequest();

        ShiftTime shiftTime = makeShiftTime();

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(shiftTimeRepository.findByShiftType(any()))
                .thenReturn(shiftTime);
        when(shiftRepository.findByUserAndStartTimeBetween(any(), any(), any()))
                .thenReturn(List.of(new Shift(), new Shift()));

        assertThrows(InternalServerErrorException.class,
                () -> userService.addShift(user.getLbz(), request, ""));
    }

    @Test
    public void addShift_UsedAllDaysOffThisYear_ThrowsInternalServerException() {
        User user = makeUser(new ArrayList<>());
        user.setDaysOff(0);

        AddShiftRequest request = makeAddShiftRequest();

        ShiftTime shiftTime = makeShiftTime();
        shiftTime.setShiftType(ShiftType.SLOBODAN_DAN);

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(shiftTimeRepository.findByShiftType(any()))
                .thenReturn(shiftTime);
        when(shiftRepository.findByUserAndStartTimeBetween(any(), any(), any()))
                .thenReturn(null);

        assertThrows(BadRequestException.class,
                () -> userService.addShift(user.getLbz(), request, ""));
    }

    @Test
    public void addShift_UsedAllDaysOffYearInTheFuture_ThrowsInternalServerException() {
        User user = makeUser(new ArrayList<>());
        user.setDaysOff(0);

        AddShiftRequest request = makeAddShiftRequest();
        request.setDate(LocalDate.now().plusYears(1).minusDays(1));

        ShiftTime shiftTime = makeShiftTime();
        shiftTime.setShiftType(ShiftType.SLOBODAN_DAN);

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(shiftTimeRepository.findByShiftType(any()))
                .thenReturn(shiftTime);
        when(shiftRepository.findByUserAndStartTimeBetween(any(), any(), any()))
                .thenReturn(null);

        assertThrows(BadRequestException.class,
                () -> userService.addShift(user.getLbz(), request, ""));
    }

    @Test
    public void addShift_NoExistingShift_Success() {
        User user = makeUser(new ArrayList<>());

        AddShiftRequest request = makeAddShiftRequest();

        ShiftTime shiftTime = makeShiftTime();

        Shift shift = userMapper.addShiftRequestToModel(user, request, shiftTime);
        List<Shift> shifts = Arrays.asList(new Shift[] {shift});
        user.setShifts(shifts);

        UserShiftResponse response = userMapper.modelToUserShiftResponse(user);

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(shiftTimeRepository.findByShiftType(any()))
                .thenReturn(shiftTime);
        when(shiftRepository.findByUserAndStartTimeBetween(any(), any(), any()))
                .thenReturn(null);

        assertEquals(userService.addShift(user.getLbz(), request, ""),
                response);
    }

    @Test
    public void addShift_ExistingShiftBothDaysOff_Success() {
        User user = makeUser(new ArrayList<>());

        AddShiftRequest request = makeAddShiftRequest();

        ShiftTime shiftTime = makeShiftTime();
        shiftTime.setShiftType(ShiftType.SLOBODAN_DAN);

        Shift shift = userMapper.addShiftRequestToModel(user, request, shiftTime);
        List<Shift> shifts = Arrays.asList(new Shift[] {shift});
        user.setShifts(shifts);

        UserShiftResponse response = userMapper.modelToUserShiftResponse(user);

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(shiftTimeRepository.findByShiftType(any()))
                .thenReturn(shiftTime);
        when(shiftRepository.findByUserAndStartTimeBetween(any(), any(), any()))
                .thenReturn(shifts);

        assertEquals(userService.addShift(user.getLbz(), request, ""),
                response);
    }

    @Test
    public void addShift_CantChangeShiftToDayOff_ThrowsBadRequestException() {
        User user = makeUser(new ArrayList<>());
        user.setDaysOff(0);

        AddShiftRequest request = makeAddShiftRequest();

        ShiftTime shiftTime = makeShiftTime();
        shiftTime.setShiftType(ShiftType.SLOBODAN_DAN);

        Shift shift = userMapper.addShiftRequestToModel(user, request, shiftTime);
        shift.setShiftType(ShiftType.PRVA_SMENA);
        List<Shift> shifts = Arrays.asList(new Shift[] {shift});
        user.setShifts(shifts);

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(shiftTimeRepository.findByShiftType(any()))
                .thenReturn(shiftTime);
        when(shiftRepository.findByUserAndStartTimeBetween(any(), any(), any()))
                .thenReturn(shifts);

        assertThrows(BadRequestException.class,
                () -> userService.addShift(user.getLbz(), request, ""));
    }

    @Test
    public void addShift_DoctorAlreadyScheduled_ThrowsBadRequestException() {
        User user = makeUser(List.of("ROLE_DR_SPEC"));

        AddShiftRequest request = makeAddShiftRequest();

        ShiftTime shiftTime = makeShiftTime();

        Shift shift = userMapper.addShiftRequestToModel(user, request, shiftTime);
        List<Shift> shifts = Arrays.asList(new Shift[] {shift});
        user.setShifts(shifts);

        mockHttpUtils();

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(shiftTimeRepository.findByShiftType(any()))
                .thenReturn(shiftTime);
        when(shiftRepository.findByUserAndStartTimeBetween(any(), any(), any()))
                .thenReturn(shifts);

        assertThrows(BadRequestException.class,
                () -> userService.addShift(user.getLbz(), request, ""));
    }

    @Test
    public void addShift_NurseHasFilledTerms_ThrowsBadRequestException() {
        User user = makeUser(List.of("ROLE_MED_SESTRA"));

        AddShiftRequest request = makeAddShiftRequest();

        ShiftTime shiftTime = makeShiftTime();

        Shift shift = userMapper.addShiftRequestToModel(user, request, shiftTime);
        List<Shift> shifts = Arrays.asList(new Shift[] {shift});
        user.setShifts(shifts);

        mockHttpUtils();

        when(userRepository.findByLbzAndFetchPermissions(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(shiftTimeRepository.findByShiftType(any()))
                .thenReturn(shiftTime);
        when(shiftRepository.findByUserAndStartTimeBetween(any(), any(), any()))
                .thenReturn(shifts);

        assertThrows(BadRequestException.class,
                () -> userService.addShift(user.getLbz(), request, ""));
    }

    @Test
    public void updateDaysOff_MoreThan50DaysOff_ThrowsBadRequestException() {
        User user = makeUser(new ArrayList<>());

        when(userRepository.findUserByLbz(user.getLbz()))
                .thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class,
                () -> userService.updateDaysOff(user.getLbz(), 51));
    }

    @Test
    public void updateDaysOff_NegativeNumberOfDays_ThrowsBadRequestException() {
        User user = makeUser(new ArrayList<>());

        when(userRepository.findUserByLbz(user.getLbz()))
                .thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class,
                () -> userService.updateDaysOff(user.getLbz(), -1));
    }

    @Test
    public void updateDaysOff_NoFreeDaysThisYear_ThrowsBadRequestException() {
        User user = makeUser(new ArrayList<>());
        user.setUsedDaysOff(5);

        when(userRepository.findUserByLbz(user.getLbz()))
                .thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class,
                () -> userService.updateDaysOff(user.getLbz(), 4));
    }

    @Test
    public void updateDaysOff_NoFreeDaysNextYearYear_ThrowsBadRequestException() {
        User user = makeUser(new ArrayList<>());

        when(userRepository.findUserByLbz(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(shiftRepository.countShiftsByShiftTypeForUserBetweenDates(any(), any(), any(), any()))
                .thenReturn(2L);

        assertThrows(BadRequestException.class,
                () -> userService.updateDaysOff(user.getLbz(), 1));
    }

    @Test
    public void updateDaysOff_Success() {
        User user = makeUser(new ArrayList<>());

        UserResponse response = userMapper.modelToResponse(user);

        when(userRepository.findUserByLbz(user.getLbz()))
                .thenReturn(Optional.of(user));
        when(shiftRepository.countShiftsByShiftTypeForUserBetweenDates(any(), any(), any(), any()))
                .thenReturn(0L);
        when(userRepository.save(user))
                .thenReturn(user);

        assertEquals(userService.updateDaysOff(user.getLbz(), user.getDaysOff()),
                response);
    }

    private AddShiftRequest makeAddShiftRequest() {
        AddShiftRequest request = new AddShiftRequest();

        request.setDate(LocalDate.now().plusDays(1));
        request.setShiftType(ShiftType.PRVA_SMENA.getNotation());
        request.setStartTime(LocalTime.now().plusMinutes(30));
        request.setEndTime(request.getStartTime().plusHours(2));

        return request;
    }

    private ShiftTime makeShiftTime() {
        ShiftTime shiftTime = new ShiftTime();

        shiftTime.setShiftType(ShiftType.PRVA_SMENA);
        shiftTime.setStartTime(LocalTime.now());
        shiftTime.setEndTime(shiftTime.getStartTime().plusHours(8));

        return shiftTime;
    }

    private CreateUserRequest createUserRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setFirstName("firstName");
        createUserRequest.setLastName("lastName");
        createUserRequest.setDateOfBirth(new Date());
        createUserRequest.setGender("gender");
        createUserRequest.setJmbg("jmbg");
        createUserRequest.setResidentialAddress("address");
        createUserRequest.setPlaceOfLiving("place");
        createUserRequest.setPhone("phone");
        createUserRequest.setEmail("email@something.com");
        createUserRequest.setTitle(Title.MR.getNotation());
        createUserRequest.setProfession(Profession.SPEC_HIRURG.getNotation());
        createUserRequest.setDepartmentId(1L);
        createUserRequest.setPermissions(new String[]{"ROLE_ADMIN"});

        return createUserRequest;
    }

    private UpdateUserRequest createUpdateUserRequest() {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setDepartmentId(1L);
        updateUserRequest.setJmbg("jmbg");
        updateUserRequest.setEmail("email@something.com");
        updateUserRequest.setGender("gender");
        updateUserRequest.setFirstName("firstName");
        updateUserRequest.setLastName("lastName");
        updateUserRequest.setPhone("phone");
        updateUserRequest.setUsername("username");
        updateUserRequest.setDateOfBirth(new Date());
        updateUserRequest.setProfession(Profession.SPEC_GASTROENTEROLOG.getNotation());
        updateUserRequest.setTitle(Title.MR.getNotation());
        updateUserRequest.setPlaceOfLiving("place");
        updateUserRequest.setResidentialAddress("address");
        updateUserRequest.setOldPassword("password");
        updateUserRequest.setNewPassword("newPassword");

        return updateUserRequest;
    }

    private Permission createPermission(Long id, String name) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setName(name);
        permission.setDaysOff(30);

        return permission;
    }

    private Department createDepartment() {
        Department department = new Department();
        department.setId(1L);
        department.setPbo(UUID.fromString("63d7a34e-1456-11ee-be56-0242ac120002"));
        department.setName("name");
        department.setHospital(createHospital());

        return department;
    }

    private User createUser() {
        Department department = createDepartment();
        department.setHospital(createHospital());
        List<Permission> permissions = Collections.singletonList(createPermission(1L, "ROLE_ADMIN"));
        return userMapper.requestToModel(createUserRequest(), department, permissions);
    }

    private User makeUser(List<String> permissions) {
        Department department = createDepartment();
        User user = new User();

        user.setDepartment(department);
        user.setLbz(UUID.fromString("abbf1c28-1456-11ee-be56-0242ac120002"));
        user.setDaysOff(10);
        user.setCovidAccess(true);
        user.setGender("Muski");
        user.setId(1L);
        user.setEmail("mail@mail.com");
        user.setLbz(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));
        user.setTitle(Title.MR);
        user.setUsedDaysOff(0);
        user.setPassword("pass");
        user.setDateOfBirth(new Date());
        user.setDeleted(false);
        user.setFirstName("Ime");
        user.setLastName("Prezime");
        user.setJMBG("000000000000");
        user.setUsername("username");
        List<Permission> permissionList = new ArrayList<>();
        for (int i = 0; i < permissions.size(); i++) {
            permissionList.add(createPermission((long) i, permissions.get(i)));
        }
        user.setPermissions(permissionList);

        return user;
    }

    private Hospital createHospital() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setPlace("place");
        hospital.setActivity("activity");
        hospital.setAddress("address");
        hospital.setPbb(UUID.fromString("5ede62ec-1456-11ee-be56-0242ac120002"));
        hospital.setShortName("shortName");
        hospital.setFullName("fullName");
        hospital.setDateOfEstablishment(new Date());

        return hospital;
    }

    private void mockTokenPayloadUtil() {
        Mockito.mockStatic(TokenPayloadUtil.class);

        TokenPayload tokenPayload = makeTokenPayload();

        when(TokenPayloadUtil.getTokenPayload())
                .thenReturn(tokenPayload);
    }

    private  void mockHttpUtils() {
        Mockito.mockStatic(HttpUtils.class);

        when(HttpUtils.checkDoctorScheduledExamsForTimeSlot(any(), any(), any()))
                .thenReturn(List.of(new Date()));
        when(HttpUtils.checkAndUpdateNurseTerms(any(), any()))
                .thenReturn(List.of(LocalDateTime.now()));
    }

    private void mockTokenPayloadUtil(List<String> permissions) {
        Mockito.mockStatic(TokenPayloadUtil.class);

        TokenPayload tokenPayload = makeTokenPayload(permissions);

        when(TokenPayloadUtil.getTokenPayload())
                .thenReturn(tokenPayload);
    }

    private TokenPayload makeTokenPayload() {
        TokenPayload tokenPayload = new TokenPayload();

        tokenPayload.setPbo(UUID.fromString("63d7a34e-1456-11ee-be56-0242ac120002"));
        tokenPayload.setPbb(UUID.fromString("5ede62ec-1456-11ee-be56-0242ac120002"));
        tokenPayload.setLbz(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));
        tokenPayload.setPermissions(List.of("ROLE_ADMIN", "ROLE_VISA_MED_SESTRA", "ROLE_DR_SPEC_ODELJENJA"));

        return tokenPayload;
    }

    private TokenPayload makeTokenPayload(List<String> permissions) {
        TokenPayload tokenPayload = new TokenPayload();

        tokenPayload.setPbo(UUID.fromString("63d7a34e-1456-11ee-be56-0242ac120002"));
        tokenPayload.setPbb(UUID.fromString("5ede62ec-1456-11ee-be56-0242ac120002"));
        tokenPayload.setLbz(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));
        tokenPayload.setPermissions(permissions);

        return tokenPayload;
    }
}
