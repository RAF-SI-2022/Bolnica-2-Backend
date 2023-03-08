package com.raf.si.userservice.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class Korisnik {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(nullable = false)
    private UUID licniBrojZaposlenog;
    @Column(nullable = false)
    private String ime;
    @Column(nullable = false)
    private String prezime;
    @Column(nullable = false)
    private Date datumRodjenja;
    @Column(nullable = false)
    private String pol;
    @Column(nullable = false)
    private String JMBG;
    @Column(nullable = false)
    private String adresaStanovanja;
    @Column(nullable = false)
    private String mestoStanovanja;
    @Column
    private String kontaktTelefon;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private String titula;
    @Column(nullable = false)
    private String zanimanje;
    @Column(nullable = false,unique = true)
    private String korisnickoIme;
    @Column(nullable = false)
    private String lozinka;
    @Column(nullable = false)
    private boolean obrisan;
    @ManyToOne
    private Odeljenje odeljenje;
    @ManyToMany
    private List<Privilegije> privilegije;

    public void setId(Long id) {
        this.userId = id;
    }

    public Long getId() {
        return userId;
    }

    public UUID getLicniBrojZaposlenog() {
        return licniBrojZaposlenog;
    }

    public void setLicniBrojZaposlenog(UUID licniBrojZaposlenog) {
        this.licniBrojZaposlenog = licniBrojZaposlenog;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public Date getDatumRodjenja() {
        return datumRodjenja;
    }

    public void setDatumRodjenja(Date datumRodjenja) {
        this.datumRodjenja = datumRodjenja;
    }

    public String getPol() {
        return pol;
    }

    public void setPol(String pol) {
        this.pol = pol;
    }

    public String getJMBG() {
        return JMBG;
    }

    public void setJMBG(String JMBG) {
        this.JMBG = JMBG;
    }

    public String getAdresaStanovanja() {
        return adresaStanovanja;
    }

    public void setAdresaStanovanja(String adresaStanovanja) {
        this.adresaStanovanja = adresaStanovanja;
    }

    public String getMestoStanovanja() {
        return mestoStanovanja;
    }

    public void setMestoStanovanja(String mestoStanovanja) {
        this.mestoStanovanja = mestoStanovanja;
    }

    public String getKontaktTelefon() {
        return kontaktTelefon;
    }

    public void setKontaktTelefon(String kontaktTelefon) {
        this.kontaktTelefon = kontaktTelefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitula() {
        return titula;
    }

    public void setTitula(String titula) {
        this.titula = titula;
    }

    public String getZanimanje() {
        return zanimanje;
    }

    public void setZanimanje(String zanimanje) {
        this.zanimanje = zanimanje;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public boolean isObrisan() {
        return obrisan;
    }

    public void setObrisan(boolean obrisan) {
        this.obrisan = obrisan;
    }

    public Odeljenje getOdeljenje() {
        return odeljenje;
    }

    public void setOdeljenje(Odeljenje odeljenje) {
        this.odeljenje = odeljenje;
    }

    public List<Privilegije> getPrivilegije() {
        return privilegije;
    }

    public void setPrivilegije(List<Privilegije> privilegije) {
        this.privilegije = privilegije;
    }
}
