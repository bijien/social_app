package com.example.laborator6map.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Eveniment extends Entity<Long> {

    Utilizator creator;
    private String nume;
    private String locatie;
    private String descriere;
    private LocalDateTime data;
    private List<Utilizator> participantiList;
    private List<Utilizator> participantiAbonatiLaNotificariList;




    public Eveniment(Utilizator creator, String nume, String locatie, String descriere, LocalDateTime data, List<Utilizator> participantiList, List<Utilizator> participantiAbonatiLaNotificariList) {
        this.creator = creator;
        this.nume = nume;
        this.locatie = locatie;
        this.descriere = descriere;
        this.data = data;
        this.participantiList = participantiList;
        this.participantiAbonatiLaNotificariList = participantiAbonatiLaNotificariList;
    }

    public void setParticipantiAbonatiLaNotificariList(List<Utilizator> participantiAbonatiLaNotificariList) {
        this.participantiAbonatiLaNotificariList = participantiAbonatiLaNotificariList;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public void setLocatie(String locatie) {
        this.locatie = locatie;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public void setParticipantiList(List<Utilizator> participantiList) {
        this.participantiList = participantiList;
    }

    public String getNume() {
        return nume;
    }

    public String getLocatie() {
        return locatie;
    }

    public LocalDateTime getData() {
        return data;
    }

    public List<Utilizator> getParticipantiList() {
        return participantiList;

    }

    public void addParticipantiList(Utilizator utilizator){
        participantiList.add(utilizator);

    }

    public void addParticipantiAbonatiLaNotificariList(Utilizator utilizator){
        participantiAbonatiLaNotificariList.add(utilizator);

    }

    public void deleteParticipantiList(Utilizator utilizator){
        participantiList.remove(utilizator);

    }

    public void deleteParticipantiAbonatiLaNotificariList(Utilizator utilizator){
        participantiAbonatiLaNotificariList.remove(utilizator);

    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public Utilizator getCreator() {
        return creator;
    }

    public void setCreator(Utilizator creator) {
        this.creator = creator;
    }

    public List<Utilizator> getParticipantiAbonatiLaNotificariList() {
        return participantiAbonatiLaNotificariList;
    }

    @Override
    public String toString() {
        return "Eveniment{" +
                "creator=" + creator +
                ", nume='" + nume + '\'' +
                ", locatie='" + locatie + '\'' +
                ", descriere='" + descriere + '\'' +
                ", data=" + data +
                ", participantiList=" + participantiList +
                ", participantiAbonatiLaNotificariList=" + participantiAbonatiLaNotificariList +
                '}';
    }
}


