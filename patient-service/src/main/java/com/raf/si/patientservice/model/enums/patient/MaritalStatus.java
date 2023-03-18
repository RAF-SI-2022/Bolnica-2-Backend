package com.raf.si.patientservice.model.enums.patient;

import com.raf.si.patientservice.model.enums.user.Profession;

public enum MaritalStatus {
    U_BRAKU("U braku"),
    RAZVEDEN("Razveden"),
    UDOVAC("Udovac"),
    SAMAC("Samac");

    private String notation;

    MaritalStatus(String notation) {
        this.notation = notation;
    }

    public static MaritalStatus valueOfNotation(String notation) {
        for (MaritalStatus m : values()) {
            if (m.notation.equals(notation)) {
                return m;
            }
        }
        return null;
    }

    public String getNotation() {
        return notation;
    }
}
