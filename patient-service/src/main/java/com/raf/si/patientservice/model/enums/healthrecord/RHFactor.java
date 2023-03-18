package com.raf.si.patientservice.model.enums.healthrecord;

import com.raf.si.patientservice.model.enums.user.Title;

public enum RHFactor {
    PLUS("+"),
    MINUS("-");

    String notation;

    RHFactor(String notation) {
        this.notation = notation;
    }

    public static RHFactor valueOfNotation(String notation) {
        for (RHFactor rh : values()) {
            if (rh.notation.equals(notation)) {
                return rh;
            }
        }
        return null;
    }

    public String getNotation() {
        return notation;
    }
}
