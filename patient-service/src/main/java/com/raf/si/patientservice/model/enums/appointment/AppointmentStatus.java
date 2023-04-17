package com.raf.si.patientservice.model.enums.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AppointmentStatus {

    ZAKAZAN("Zakazan"),
    OTKAZAN("Otkazan"),
    REALIZOVAN("Realizovan");


    private final String notation;

    AppointmentStatus(String notation){
        this.notation = notation;
    }

    public static AppointmentStatus valueOfNotation(String notation) {
        for (AppointmentStatus e : values()) {
            if (e.notation.equals(notation)) {
                return e;
            }
        }
        return null;
    }
}