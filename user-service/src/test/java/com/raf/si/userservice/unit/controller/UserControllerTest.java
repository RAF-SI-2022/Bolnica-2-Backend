package com.raf.si.userservice.unit.controller;

import com.raf.si.userservice.controller.UserController;
import com.raf.si.userservice.dto.request.*;
import com.raf.si.userservice.dto.response.*;
import com.raf.si.userservice.exception.ForbiddenException;
import com.raf.si.userservice.model.enums.Profession;
import com.raf.si.userservice.model.enums.Title;
import com.raf.si.userservice.service.UserService;
import com.raf.si.userservice.utils.TokenPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserService userService;
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);


        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

    }

    @Test
    public void createUser_Success() {
        CreateUserRequest createUserRequest = createUserRequest();
        UserResponse userResponse = createUserResponse();
        when(userService.createUser(createUserRequest)).thenReturn(userResponse);
        assertEquals(userController.createUser(createUserRequest),
                ResponseEntity.of(Optional.of(userResponse)));
    }

    @Test
    public void getUserByLbz_WhenUserNotAdminAndDifferentLbz_ThrowsForbiddenException() {
        when(authentication.getPrincipal()).thenReturn(getTokenPayload(false));
        assertThrows(ForbiddenException.class, () -> userController.getUserByLbz(UUID.randomUUID()));
    }

    @Test
    public void getUserByLbz_Success() {
        TokenPayload tokenPayload = getTokenPayload(true);
        UserResponse userResponse = createUserResponse();

        when(authentication.getPrincipal()).thenReturn(tokenPayload);
        when(userService.getUserByLbz(tokenPayload.getLbz())).thenReturn(userResponse);

        assertEquals(userController.getUserByLbz(tokenPayload.getLbz()),
                ResponseEntity.of(Optional.of(userResponse)));
    }

    @Test
    public void getEmployeeInfo_Success() {
        UUID lbz = UUID.randomUUID();
        UserResponse userResponse = createUserResponse();

        when(userService.getUserByLbz(lbz)).thenReturn(userResponse);

        assertEquals(userController.getEmployeeInfo(lbz),
                ResponseEntity.of(Optional.of(userResponse)));
    }

    @Test
    public void deleteUser_Success() {
        Long id = 1L;
        UserResponse userResponse = createUserResponse();
        TokenPayload tokenPayload = getTokenPayload(false);
        when(authentication.getPrincipal()).thenReturn(tokenPayload);
        when(userService.deleteUser(id, tokenPayload.getLbz())).thenReturn(userResponse);
        assertEquals(userController.deleteUser(id),
                ResponseEntity.of(Optional.of(userResponse)));
    }

    @Test
    public void getAllDoctors_Success() {
       DoctorResponse doctorResponse = createDoctorResponse(UUID.randomUUID());
       DoctorResponse doctorResponse2 = createDoctorResponse(UUID.randomUUID());

       List<DoctorResponse> list = Arrays.asList(doctorResponse, doctorResponse2);

       when(userService.getAllDoctors())
               .thenReturn(list);

       assertEquals(userController.getAllDoctors(), ResponseEntity.of(Optional.of(list)));
    }

    @Test
    public void getAllDoctorsByDepartment_Success() {
        DoctorResponse doctorResponse = createDoctorResponse(UUID.randomUUID());
        DoctorResponse doctorResponse2 = createDoctorResponse(UUID.randomUUID());

        UUID pbo = UUID.randomUUID();

        List<DoctorResponse> list = Arrays.asList(doctorResponse, doctorResponse2);

        when(userService.getAllDoctorsByDepartment(pbo))
                .thenReturn(list);

        assertEquals(userController.getAllDoctorsByDepartment(pbo), ResponseEntity.of(Optional.of(list)));
    }

    @Test
    public void updateUser_WhenUserNotAdminAndDifferentLbz_ThrowsForbiddenException() {
        when(authentication.getPrincipal()).thenReturn(getTokenPayload(false));
        assertThrows(ForbiddenException.class, () -> userController.updateUser(UUID.randomUUID(), createUpdateUserRequest()));
    }

    @Test
    public void updateUser_Success() {
        UUID lbz = UUID.randomUUID();
        UpdateUserRequest updateUserRequest = createUpdateUserRequest();

        when(authentication.getPrincipal()).thenReturn(getTokenPayload(true));
        UserResponse userResponse = createUserResponse();
        when(userService.updateUser(lbz, updateUserRequest, true)).thenReturn(userResponse);

        assertEquals(userController.updateUser(lbz, updateUserRequest),
                ResponseEntity.of(Optional.of(userResponse)));
    }

    @Test
    public void listUsers_Success() {
        String firstName = "firstName";
        String lastName = "lastName";
        String departmentName = "departmentName";
        String hospitalName = "hospitalName";
        int page = 0;
        int size = 5;

        UserListAndCountResponse userListAndCountResponse = new UserListAndCountResponse(new ArrayList<>(), 1L);

        when(userService.listUsers(firstName, lastName, departmentName, hospitalName, true, null, PageRequest.of(page, size)))
                .thenReturn(userListAndCountResponse);

        assertEquals(userController.listUsers(firstName, lastName, departmentName, hospitalName, true, null, page, size),
                ResponseEntity.of(Optional.of(userListAndCountResponse)));
    }

    @Test
    public void resetPassword_Success() {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        MessageResponse messageResponse = new MessageResponse("message");

        when(userService.resetPassword(passwordResetRequest)).thenReturn(messageResponse);
        assertEquals(userController.resetPassword(passwordResetRequest),
                ResponseEntity.of(Optional.of(messageResponse)));
    }

    @Test
    public void updatePassword_success() {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        MessageResponse messageResponse = new MessageResponse("message");

        when(userService.updatePassword(updatePasswordRequest)).thenReturn(messageResponse);
        assertEquals(userController.updatePassword(updatePasswordRequest),
                ResponseEntity.of(Optional.of(messageResponse)));
    }

    @Test
    public void getHeadOfDepartment_Success() {
        UUID pbo = UUID.randomUUID();
        DoctorResponse doctorResponse = createDoctorResponse(UUID.randomUUID());

        when(userService.getHeadOfDepartment(pbo))
                .thenReturn(doctorResponse);

        assertEquals(userController.getHeadOfDepartment(pbo).getBody(),
                doctorResponse);
    }

    @Test
    public void updateCovidAccess_Success() {
        UUID lbz = UUID.randomUUID();
        boolean covidAccess = true;
        UserResponse userResponse = new UserResponse();

        when(userService.updateCovidAccess(lbz, covidAccess))
                .thenReturn(userResponse);

        assertEquals(userController.updateCovidAccess(lbz, covidAccess).getBody(),
                userResponse);
    }

    @Test
    public void getUsersByLbzList_Success() {
        UUIDListRequest request = new UUIDListRequest();
        List<UserResponse> list = new ArrayList<>();

        when(userService.getUsersByLbzList(request))
                .thenReturn(list);

        assertEquals(userController.getUsersByLbzList(request).getBody(),
                list);
    }

    @Test
    public void getSubordinates_Success() {
        List<UserListResponse> list = new ArrayList<>();
        long count = 1;
        UserListAndCountResponse response = new UserListAndCountResponse(list, count);
        Pageable pageable = PageRequest.of(0, 5);

        when(userService.getSubordinates(pageable))
                .thenReturn(response);

        assertEquals(userController.getSubordinates(0, 5).getBody(),
                response);
    }

    @Test
    public void addShift_Success() {
        UUID lbz = UUID.randomUUID();
        AddShiftRequest request = new AddShiftRequest();
        String token = "token";
        UserShiftResponse response = new UserShiftResponse();

        when(userService.addShift(lbz, request, token))
                .thenReturn(response);

        assertEquals(userController.addShift(lbz, request, token).getBody(),
                response);
    }

    @Test
    public void updateDaysOff_Success() {
        UUID lbz = UUID.randomUUID();
        int daysOff = 1;
        UserResponse response = new UserResponse();

        when(userService.updateDaysOff(lbz, daysOff))
                .thenReturn(response);

        assertEquals(userController.updateDaysOff(lbz, daysOff).getBody(),
                response);
    }

    @Test
    public void getUserWithShifts_Success() {
        UUID lbz = UUID.randomUUID();
        UserShiftResponse response = new UserShiftResponse();

        when(userService.getUserWithShiftsByLbz(lbz))
                .thenReturn(response);

        assertEquals(userController.getUserWithShifts(lbz).getBody(),
                response);
    }

    @Test
    public void getNumOfCovidNursesByDepartmentInTimeSlot_Success() {
        UUID pbo = UUID.randomUUID();
        TimeRequest timeRequest = new TimeRequest(LocalDateTime.now(), LocalDateTime.now());
        int retVal = 1;

        when(userService.getNumOfCovidNursesByDepartmentInTimeSlot(pbo, timeRequest))
                .thenReturn(retVal);

        assertEquals(userController.getNumOfCovidNursesByDepartmentInTimeSlot(pbo, timeRequest).getBody(),
                retVal);
    }

    @Test
    public void canScheduleForDoctor_Success() {
        UUID lbz = UUID.randomUUID();
        boolean covid = true;
        TimeRequest timeRequest = new TimeRequest(LocalDateTime.now(), LocalDateTime.now());
        boolean retVal = true;

        when(userService.canScheduleForDoctor(lbz, covid, timeRequest))
                .thenReturn(retVal);

        assertEquals(userController.canScheduleForDoctor(lbz, covid, timeRequest).getBody(),
                retVal);
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

    private UserResponse createUserResponse() {
        return new UserResponse();
    }

    private DoctorResponse createDoctorResponse(UUID lbz) {
        DoctorResponse doctorResponse = new DoctorResponse();
        doctorResponse.setLbz(lbz);
        doctorResponse.setLastName("lastname");
        doctorResponse.setFirstName("firstName");

        return doctorResponse;
    }

    private TokenPayload getTokenPayload(boolean isAdmin) {
        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setPbo(UUID.randomUUID());
        tokenPayload.setPbb(UUID.randomUUID());
        tokenPayload.setHospitalName("hospitalName");
        tokenPayload.setDepartmentName("departmentName");
        tokenPayload.setLbz(UUID.fromString("1a2e21aa-e4ee-43dd-a3ad-28e043f8b435"));
        tokenPayload.setProfession(Profession.SPEC_ENDOKRINOLOG);
        tokenPayload.setTitle(Title.MR);
        if (isAdmin)
            tokenPayload.setPermissions(Collections.singletonList("ROLE_ADMIN"));
        else
            tokenPayload.setPermissions(Collections.singletonList("ROLE_DR_SPEC"));
        tokenPayload.setLastName("lastName");
        tokenPayload.setFirstName("firstName");

        return tokenPayload;
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
}
