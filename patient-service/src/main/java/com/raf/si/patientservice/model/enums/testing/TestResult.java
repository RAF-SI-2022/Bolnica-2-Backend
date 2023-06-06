package com.raf.si.patientservice.model.enums.testing;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TestResult {
    NEOBRADJEN("Neobrađen"),
    POZITIVAN("Pozitivan"),
    NEGATIVAN("Negativan");

    private String notation;

    TestResult(String notation){
        this.notation = notation;
    }

    public static TestResult valueOfNotation(String notation) {
        for (TestResult t : values()) {
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
