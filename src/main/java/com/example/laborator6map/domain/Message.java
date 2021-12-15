package com.example.laborator6map.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Message extends Entity<Long> {
    private Utilizator from;
    private List<Utilizator> to;
    private LocalDateTime data;
    private String message;
    private Long reply;

    public Message(Utilizator from, List<Utilizator> to, LocalDateTime data, String message, Long reply) {
        this.from = from;
        this.to = to;
        this.data = data;
        this.message = message;
        this.reply = reply;
    }


    public Utilizator getFrom() {
        return from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public LocalDateTime getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public Long getReply() {
        return reply;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    public void addTo(Utilizator to) {
        this.to.add(to);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + this.getId() +
                ", from=" + from +
                ", to=" + to +
                ", data=" + data.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")) +
                ", message='" + message + '\'' +
                ", reply=" + reply +
                '}';
    }

}
