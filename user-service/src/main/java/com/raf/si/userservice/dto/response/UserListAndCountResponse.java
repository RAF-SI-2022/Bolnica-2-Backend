package com.raf.si.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserListAndCountResponse {

    private List<UserListResponse> userList;
    private Long count;
}
