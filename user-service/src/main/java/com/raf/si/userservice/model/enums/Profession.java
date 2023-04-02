package com.raf.si.userservice.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Profession {
    MED_SESTRA("Med. sestra"),
    SPEC_BIOHEMICAR("Spec. biohemičar"),
    SPEC_GASTROENTEROLOG("Spec. gastroenterolog"),
    SPEC_GINEKOLOG("Spec. ginekolog"),
    SPEC_ENDOKRINOLOG("Spec. endokrinolog"),
    SPEC_KARDIOLOG("Spec. kardiolog"),
    SPEC_NEUROLOG("Spec. neurolog"),
    SPEC_NEFROLOG("Spec. nefrolog"),
    SPEC_PSIHIJATAR("Spec. psihijatar"),
    SPEC_PULMOLOG("Spec. pulmolog"),
    SPEC_UROLOG("Spec. urolog"),
    SPEC_HEMATOLOG("Spec. hematolog"),
    SPEC_HIRURG("Spec. hirurg"),
    LAB_TEHNICAR("Lab. tehničar"),
    MED_BIOHEMICAR("Med. biohemičar");


    String notation;

    Profession(String notation) {
        this.notation = notation;
    }

    public static Profession valueOfNotation(String notation) {
        for (Profession p : values()) {
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
