package com.raf.si.userservice.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ShiftType {
    PRVA_SMENA("Prva smena"),
    DRUGA_SMENA("Druga smena"),
    TRECA_SMENA("Treća smena"),
    MEDJUSMENA("Međusmena"),
    SLOBODAN_DAN("Slobodan dan");

    String notation;

    ShiftType(String notation) {
        this.notation = notation;
    }

    public static ShiftType valueOfNotation(String notation) {
        for (ShiftType s : values()) {
            if (s.notation.equals(notation)) {
                return s;
            }
        }
        return null;
    }

    public String getNotation() {
        return notation;
    }
}
