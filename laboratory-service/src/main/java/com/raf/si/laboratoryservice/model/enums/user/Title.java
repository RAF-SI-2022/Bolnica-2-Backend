package com.raf.si.laboratoryservice.model.enums.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Title {
    PROF_DR_MED("Prof. dr. med."),
    DR_MED_SPEC("Dr med. spec."),
    DR_SCI_MED("Dr sci. med."),
    DIPL_FARM("Dipl. farm."),
    MAG_FARM("Mag. farm."),
    MR("Mr");

    String notation;

    Title(String notation) {
        this.notation = notation;
    }

    @JsonCreator
    public static Title valueOfNotation(String notation) {
        for (Title t : values()) {
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
