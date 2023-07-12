package com.lautarocolella.portfolio.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class Contact_message implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private long id;
    @Column(nullable = false)
    private String subject;
    @Column(nullable = false, length = 3000)
    private String message;
    @Column(nullable = false)
    private String reply;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private Boolean readed;

    public Contact_message() { }

    public Contact_message(String subject, String message, String reply, LocalDate date, Boolean readed) {
        this.subject = subject;
        this.message = message;
        this.reply = reply;
        this.date = date;
        this.readed = readed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getReaded() {
        return readed;
    }

    public void setReaded(Boolean readed) {
        this.readed = readed;
    }

    @Override
    public String toString() {
        return "Contact_message{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                ", reply='" + reply + '\'' +
                ", date=" + date +
                ", readed=" + readed +
                '}';
    }
}
