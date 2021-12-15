package com.example.laborator6map.domain;


import java.time.LocalDate;

public class Prietenie extends Entity<Tuple<Long,Long>> {
    private String status;

    private LocalDate localDate;

    public Prietenie(){
        localDate=LocalDate.now();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return ""+this.getId().toString()+" "+localDate.toString()+" "+status;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }
}