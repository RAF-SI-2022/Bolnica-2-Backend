package com.raf.si.laboratoryservice.model.enums.scheduledlabexam;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ExamStatus {
    ZAKAZANO("Zakazano"),
    OTKAZANO("Otkazano"),
    ZAVRSENO("Završeno");

    String notation;

    ExamStatus(String notation){
        this.notation = notation;
    }

    public static ExamStatus valueOfNotation(String notation) {
        for (ExamStatus e : values()) {
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
