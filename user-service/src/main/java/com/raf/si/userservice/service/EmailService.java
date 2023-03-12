package com.raf.si.userservice.service;

import java.util.UUID;

public interface EmailService {

    void resetPassword(String email, UUID password);
}
