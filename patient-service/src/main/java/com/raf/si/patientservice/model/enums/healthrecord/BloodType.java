package com.raf.si.patientservice.model.enums.healthrecord;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BloodType {

    A("A"),
    B("B"),
    AB("AB"),
    O("0");

    private String notation;

    BloodType(String notation) {
        this.notation = notation;
    }

    public static BloodType valueOfNotation(String notation) {
        for (BloodType g : values()) {
            if (g.notation.equals(notation)) {
                return g;
            }
        }
        return null;
    }

}
