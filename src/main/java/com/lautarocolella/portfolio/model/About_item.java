package com.lautarocolella.portfolio.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class About_item implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private long id;
    @Column(nullable = false)
    private int item_type;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false, length = 3000)
    private String description;
    private String link;
    private String image_uri;
    private String image_alt;

    public About_item() {}

    public About_item(int item_type, String name, LocalDate date, String description, String link, String image_uri, String image_alt) {
        this.item_type = item_type;
        this.name = name;
        this.date = date;
        this.description = description;
        this.link = link;
        this.image_uri = image_uri;
        this.image_alt = image_alt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getItem_type() {
        return item_type;
    }

    public void setItem_type(int item_type) {
        this.item_type = item_type;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
        return "About_item{" +
                "id=" + id +
                ", item_type=" + item_type +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", description=" + description + '\'' +
                ", link='" + link + '\'' +
                ", image_uri='" + image_uri + '\'' +
                ", image_alt='" + image_alt + '\'' +
                '}';
    }
}
