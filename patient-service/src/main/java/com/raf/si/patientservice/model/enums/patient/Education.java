package com.raf.si.patientservice.model.enums.patient;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Education {
    BEZ_OSNOVNOG_OBRAZOVANJA("Bez osnovnog obrazovanja"),
    OSNOVNO_OBRAZOVANJE("Osnovno obrazovanje"),
    SREDNJE_OBRAZOVANJE("Srednje obrazovanje"),
    VISE_OBRAZOVANJE("Više obrazovanje"),
    VISOKO_OBRAZOVANJE("Visoko obrazovanje");

    private String notation;

    Education(String notation){
        this.notation = notation;
    }

    public static Education valueOfNotation(String notation) {
        for (Education e : values()) {
            if (e.notation.equals(notation)) {
                return e;
            }
        }
        return null;
    }

    public String getNotation() {
        return notation;
    }
}
