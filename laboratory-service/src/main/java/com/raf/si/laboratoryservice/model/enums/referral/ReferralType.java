package com.raf.si.laboratoryservice.model.enums.referral;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ReferralType {
    LABORATORIJA("Laboratorija"),
    DIJAGNOSTIKA("Dijagnostika"),
    STACIONAR("Stacionar"),
    COVID_ODSEK("Covid odsek");

    String notation;

    ReferralType(String notation){
        this.notation = notation;
    }

    public static ReferralType valueOfNotation(String notation) {
        for (ReferralType r : values()) {
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
