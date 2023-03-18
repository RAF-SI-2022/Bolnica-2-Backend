package com.raf.si.patientservice.model.enums.medicalhistory;

import com.raf.si.patientservice.model.enums.patient.Education;

public enum TreatmentResult {
    U_TOKU("U toku"),
    OPORAVLJEN("Oporavljen"),
    PREMINUO("Preminuo"),
    STALNE_POSLEDICE("Stalne posledice");

    private String notation;

    TreatmentResult(String notation) {
        this.notation = notation;
    }

    public static TreatmentResult valueOfNotation(String notation) {
        for (TreatmentResult t : values()) {
            if (t.notation.equals(notation)) {
                return t;
            }
        }
        return null;
    }

    public String getNotation() {
        return notation;
    }
}
