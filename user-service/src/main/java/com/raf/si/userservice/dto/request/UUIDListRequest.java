package com.raf.si.userservice.dto.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UUIDListRequest {
    private List<UUID> uuids;
}
