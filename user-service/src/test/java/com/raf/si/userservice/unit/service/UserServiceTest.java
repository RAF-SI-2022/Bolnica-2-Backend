package com.raf.si.userservice.unit.service;

import com.raf.si.userservice.dto.request.CreateUserRequest;
import com.raf.si.userservice.dto.request.PasswordResetRequest;
import com.raf.si.userservice.dto.request.UpdatePasswordRequest;
import com.raf.si.userservice.dto.request.UpdateUserRequest;
import com.raf.si.userservice.dto.response.DoctorResponse;
import com.raf.si.userservice.dto.response.MessageResponse;
import com.raf.si.userservice.dto.response.UserListAndCountResponse;
import com.raf.si.userservice.exception.BadRequestException;
import com.raf.si.userservice.exception.ForbiddenException;
import com.raf.si.userservice.exception.NotFoundException;
import com.raf.si.userservice.mapper.UserMapper;
import com.raf.si.userservice.model.Department;
import com.raf.si.userservice.model.Hospital;
import com.raf.si.userservice.model.Permission;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.model.enums.Profession;
import com.raf.si.userservice.model.enums.Title;
import com.raf.si.userservice.repository.DepartmentRepository;
import com.raf.si.userservice.repository.PermissionsRepository;
import com.raf.si.userservice.repository.UserRepository;
import com.raf.si.userservice.service.EmailService;
import com.raf.si.userservice.service.UserService;
import com.raf.si.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserService userService;
    private UserMapper userMapper;
    private UserRepository userRepository;
    private DepartmentRepository departmentRepository;
    private PermissionsRepository permissionsRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        departmentRepository = mock(DepartmentRepository.class);
        permissionsRepository = mock(PermissionsRepository.class);
        EmailService emailService = mock(EmailService.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        userMapper = new UserMapper(passwordEncoder);
        userService = new UserServiceImpl(userRepository, userMapper, emailService,
                departmentRepository, permissionsRepository);
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

        return permission;
    }

    private Department createDepartment() {
        Department department = new Department();
        department.setId(1L);
        department.setPbo(UUID.randomUUID());
        department.setName("name");

        return department;
    }

    private User createUser() {
        Department department = createDepartment();
        department.setHospital(createHospital());
        List<Permission> permissions = Collections.singletonList(createPermission(1L, "ROLE_ADMIN"));
        return userMapper.requestToModel(createUserRequest(), department, permissions);
    }

    private Hospital createHospital() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setPlace("place");
        hospital.setActivity("activity");
        hospital.setAddress("address");
        hospital.setPbb(UUID.randomUUID());
        hospital.setShortName("shortName");
        hospital.setFullName("fullName");
        hospital.setDateOfEstablishment(new Date());

        return hospital;
    }
}
