package com.raf.si.patientservice.model.enums.patient;

import com.raf.si.patientservice.model.enums.user.Profession;

public enum FamilyStatus {
    OBA_RODITELJA("Oba roditelja"),
    JEDAN_RODITELJ("Jedan roditelj"),
    BEZ_RODITELJA("Bez roditelja"),
    RODITELJI_RAZVEDENI("Roditelji razvedeni"),
    USVOJEN("Usvojen");

    private String notation;

    FamilyStatus(String notation) {
        this.notation = notation;
    }

    public static FamilyStatus valueOfNotation(String notation) {
        for (FamilyStatus f : values()) {
            if (f.notation.equals(notation)) {
                return f;
            }
        }
        return null;
    }

    public String getNotation() {
        return notation;
    }
}
