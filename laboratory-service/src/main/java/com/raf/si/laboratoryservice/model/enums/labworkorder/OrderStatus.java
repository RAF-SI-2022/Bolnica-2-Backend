package com.raf.si.laboratoryservice.model.enums.labworkorder;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralStatus;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OrderStatus {
    NEOBRADJEN("Neobrađen"),
    U_OBRADI("U obradi"),
    OBRADJEN("Obrađen");

    String notation;

    OrderStatus(String notation){
        this.notation = notation;
    }

    public static OrderStatus valueOfNotation(String notation) {
        for (OrderStatus o : values()) {
            if (o.notation.equals(notation)) {
                return o;
            }
        }
        return null;
    }

    public String getNotation() {
        return notation;
    }
}
