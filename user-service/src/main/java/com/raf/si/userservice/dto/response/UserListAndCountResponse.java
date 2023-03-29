package com.raf.si.userservice.dto.response;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class UserListAndCountResponse {

    private final List<UserListResponse> userList;
    private final Long count;
}
