package com.raf.si.patientservice.model.enums.patient;

import com.raf.si.patientservice.model.enums.user.Profession;

public enum Gender {
    MUSKI("Muški"),
    ZENSKI("Ženski");

    private String notation;

    Gender(String notation) {
        this.notation = notation;
    }

    public static Gender valueOfNotation(String notation) {
        for (Gender g : values()) {
            if (g.notation.equals(notation)) {
                return g;
            }
        }
        return null;
    }

    public String getNotation() {
        return notation;
    }
}
