package com.raf.si.userservice.service;

import com.raf.si.userservice.repository.KorisnikRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KorisnikService {

    private KorisnikRepository korisnikRepository;

    public KorisnikService(KorisnikRepository korisnikRepository){
        this.korisnikRepository = korisnikRepository;
    }
}
