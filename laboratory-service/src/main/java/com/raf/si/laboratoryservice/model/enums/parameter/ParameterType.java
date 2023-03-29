package com.raf.si.laboratoryservice.model.enums.parameter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ParameterType {
    NUMERICKA("Numerička"),
    TEKSTUALNA("Tekstualna");

    String notation;

    ParameterType(String notation){
        this.notation = notation;
    }

    public static ParameterType valueOfNotation(String notation) {
        for (ParameterType p : values()) {
            if (p.notation.equals(notation)) {
                return p;
            }
        }
        return null;
    }

    public String getNotation() {
        return notation;
    }
}
