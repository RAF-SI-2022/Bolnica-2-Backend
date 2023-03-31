package com.raf.si.patientservice.model.enums.healthrecord;

import com.fasterxml.jackson.annotation.JsonFormat;


public enum BloodType {

    A,B,AB,O;

    private static final BloodType[] copyOfValues = values();

    public static BloodType forName(String name){
        for(BloodType bt: copyOfValues){
            if(bt.name().equals(name))
                return bt;
        }
        return null;
    }

}
