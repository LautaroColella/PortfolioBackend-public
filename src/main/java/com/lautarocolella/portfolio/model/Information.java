package com.lautarocolella.portfolio.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class Information implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, length = 2000)
    private String information;

    public Information() {}

    public Information(String name, String information) {
        this.name = name;
        this.information = information;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    @Override
    public String toString() {
        return "information{" +
                "name='" + name + '\'' +
                ", information='" + information + '\'' +
                '}';
    }
}
