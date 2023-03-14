package com.raf.si.userservice.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class UserListAndCountResponse {

    private List<UserListResponse> userList;
    private Long count;
}
