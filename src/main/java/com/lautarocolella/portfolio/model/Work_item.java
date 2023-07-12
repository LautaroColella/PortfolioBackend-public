package com.lautarocolella.portfolio.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class Work_item implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private String technologies;
    @Column(nullable = false, length = 3000)
    private String description;
    private String code_uri;
    private String live_uri;
    private String image_uri;
    private String image_alt;

    public Work_item() {}
    public Work_item(String name, LocalDate date, String technologies, String description, String code_uri, String live_uri, String image_uri, String image_alt) {
        this.name = name;
        this.date = date;
        this.technologies = technologies;
        this.description = description;
        this.code_uri = code_uri;
        this.live_uri = live_uri;
        this.image_uri = image_uri;
        this.image_alt = image_alt;
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
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public String getTechnologies() {
        return technologies;
    }
    public void setTechnologies(String technologies) {
        this.technologies = technologies;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getCode_uri() {
        return code_uri;
    }
    public void setCode_uri(String code_uri) {
        this.code_uri = code_uri;
    }
    public String getLive_uri() {
        return live_uri;
    }
    public void setLive_uri(String live_uri) {
        this.live_uri = live_uri;
    }
    public String getImage_uri() {
        return image_uri;
    }
    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }
    public String getImage_alt() {
        return image_alt;
    }
    public void setImage_alt(String image_alt) {
        this.image_alt = image_alt;
    }

    @Override
    public String toString() {
        return "work_item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", technologies='" + technologies + '\'' +
                ", description='" + description + '\'' +
                ", code_uri='" + code_uri + '\'' +
                ", live_uri='" + live_uri + '\'' +
                ", image_uri='" + image_uri + '\'' +
                ", image_alt='" + image_alt + '\'' +
                '}';
    }
}
