package com.raf.si.patientservice.model.enums.examination;

import com.fasterxml.jackson.annotation.JsonFormat;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ExaminationStatus {

    ZAKAZANO("Zakazano"),
    OTKAZANO("Otkazano"),
    U_TOKU("U toku"),
    ZAVRSENO("Završeno");



    private String notation;

    ExaminationStatus(String notation){
        this.notation = notation;
    }

    public static ExaminationStatus valueOfNotation(String notation) {
        for (ExaminationStatus e : values()) {
            if (e.notation.equals(notation)) {
                return e;
            }
        }
        return null;
    }

    public String getExaminationStatus() {
        return notation;
    }
}
