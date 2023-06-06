package com.raf.si.patientservice.model.enums.testing;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Availability {
    MOGUCE_ZAKAZATI_U_OVOM_TERMINU("Moguće zakazati u ovom terminu"),
    POTPUNO_POPUNJEN_TERMIN("Potpuno popunjen termin");

    private String notation;

    Availability(String notation){
        this.notation = notation;
    }

    public static Availability valueOfNotation(String notation) {
        for (Availability a : values()) {
            if (a.notation.equals(notation)) {
                return a;
            }
        }
        return null;
    }

    public String getNotation() {
        return notation;
    }
}
