package com.raf.si.laboratoryservice.model.enums.referral;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ReferralStatus {
    NEREALIZOVAN("Nerealizovan"),
    REALIZOVAN("Realizovan");

    String notation;

    ReferralStatus(String notation){
        this.notation = notation;
    }

    public static ReferralStatus valueOfNotation(String notation) {
        for (ReferralStatus r : values()) {
            if (r.notation.equals(notation)) {
                return r;
            }
        }
        return null;
    }

    public String getNotation() {
        return notation;
    }
}
