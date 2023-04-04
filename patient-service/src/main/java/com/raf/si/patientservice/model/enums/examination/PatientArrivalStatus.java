package com.raf.si.patientservice.model.enums.examination;


import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PatientArrivalStatus {
    NIJE_DOSAO("Nije došao"),
    OTKAZAO("Otkazao"),
    CEKA("Čeka"),
    PRIMLJEN("Primljen"),
    ZAVRSIO("Završio");

    private String notation;

    PatientArrivalStatus (String notation){this.notation= notation;}

    public static PatientArrivalStatus valueOfNotation(String notation) {
        for (PatientArrivalStatus e : values()) {
            if (e.notation.equals(notation)) {
                return e;
            }
        }
        return null;
    }

    public String getPatientArrivalStatus() {
        return notation;
    }

}
