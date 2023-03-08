package com.raf.si.userservice.model;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
public class Ustanova {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBolnice;
    @Column(nullable = false)
    private UUID poslovniBrojBolnice;
    @Column(nullable = false)
    private String punNaziv;
    @Column(nullable = false)
    private String skracenNaziv;
    @Column(nullable = false)
    private String mesto;
    @Column(nullable = false)
    private String adresa;
    @Column(nullable = false)
    private Date datumOsnivanja;
    @Column(nullable = false)
    private String delatnost;
    @Column
    private boolean obrisan;

    public Long getIdBolnice() {
        return idBolnice;
    }

    public void setIdBolnice(Long idBolnice) {
        this.idBolnice = idBolnice;
    }

    public UUID getPoslovniBrojBolnice() {
        return poslovniBrojBolnice;
    }

    public void setPoslovniBrojBolnice(UUID poslovniBrojBolnice) {
        this.poslovniBrojBolnice = poslovniBrojBolnice;
    }

    public String getPunNaziv() {
        return punNaziv;
    }

    public void setPunNaziv(String punNaziv) {
        this.punNaziv = punNaziv;
    }

    public String getSkracenNaziv() {
        return skracenNaziv;
    }

    public void setSkracenNaziv(String skracenNaziv) {
        this.skracenNaziv = skracenNaziv;
    }

    public String getMesto() {
        return mesto;
    }

    public void setMesto(String mesto) {
        this.mesto = mesto;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public Date getDatumOsnivanja() {
        return datumOsnivanja;
    }

    public void setDatumOsnivanja(Date datumOsnivanja) {
        this.datumOsnivanja = datumOsnivanja;
    }

    public String getDelatnost() {
        return delatnost;
    }

    public void setDelatnost(String delatnost) {
        this.delatnost = delatnost;
    }

    public boolean isObrisan() {
        return obrisan;
    }

    public void setObrisan(boolean obrisan) {
        this.obrisan = obrisan;
    }
}
