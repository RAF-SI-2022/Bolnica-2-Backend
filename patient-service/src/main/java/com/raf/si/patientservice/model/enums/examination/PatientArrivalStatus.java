package com.raf.si.patientservice.model.enums.examination;

public enum PatientArrivalStatus {
    NIJE_DOSAO("Nije dosao"),
    OTKAZAO("Otkazao"),
    CEKA("Ceka"),
    PRIMLJEN("Primljen"),
    ZAVRSIO("Zavrsio");



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
