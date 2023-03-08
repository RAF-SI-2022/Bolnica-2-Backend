package com.raf.si.userservice.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Odeljenje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOdeljenja;
    @Column(nullable = false)
    private UUID poslovniBrojOdeljenja;
    @Column(nullable = false)
    private String nazivOdeljenja;
    @ManyToOne
    private Ustanova zdravstvenaUstanova;
    @Column
    private boolean obrisan;
    public void setIdOdeljenja(Long idOdeljenja) {
        this.idOdeljenja = idOdeljenja;
    }

    public Long getIdOdeljenja() {
        return idOdeljenja;
    }

    public UUID getPoslovniBrojOdeljenja() {
        return poslovniBrojOdeljenja;
    }

    public void setPoslovniBrojOdeljenja(UUID poslovniBrojOdeljenja) {
        this.poslovniBrojOdeljenja = poslovniBrojOdeljenja;
    }

    public String getNazivOdeljenja() {
        return nazivOdeljenja;
    }

    public void setNazivOdeljenja(String nazivOdeljenja) {
        this.nazivOdeljenja = nazivOdeljenja;
    }

    public Ustanova getZdravstvenaUstanova() {
        return zdravstvenaUstanova;
    }

    public void setZdravstvenaUstanova(Ustanova zdravstvenaUstanova) {
        this.zdravstvenaUstanova = zdravstvenaUstanova;
    }

    public boolean isObrisan() {
        return obrisan;
    }

    public void setObrisan(boolean obrisan) {
        this.obrisan = obrisan;
    }
}
