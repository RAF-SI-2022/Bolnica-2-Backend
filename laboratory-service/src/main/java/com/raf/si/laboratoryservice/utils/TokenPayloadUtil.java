package com.raf.si.laboratoryservice.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class TokenPayloadUtil {

    public static TokenPayload getTokenPayload(){
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        return (TokenPayload) authentication.getPrincipal();
    }
}
