package com.raf.si.patientservice.model.enums.certificate;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CovidCertificateType {

    PRIMLJENA_VAKCINA("Primljena vakcina"),
    NEGATIVAN_PCR_TEST("Negativan PCR test"),
    OPORAVAK_OD_COVIDA("Oporavak od covida");

    private final String notation;

    CovidCertificateType (String notation){this.notation= notation;}

    public static CovidCertificateType valueOfNotation(String notation) {
        for (CovidCertificateType e : values()) {
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
