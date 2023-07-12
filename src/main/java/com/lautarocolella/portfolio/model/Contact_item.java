package com.lautarocolella.portfolio.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class Contact_item implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private long id;
    @Column(nullable = false)
    private String name;
    private String link;
    @Column(nullable = false)
    private String account;
    private String image_uri;
    private String image_alt;

    public Contact_item() {}

    public Contact_item(String name, String link, String account, String image_uri, String image_alt) {
        this.name = name;
        this.link = link;
        this.account = account;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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
        return "contact_item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", account='" + account + '\'' +
                ", image_uri='" + image_uri + '\'' +
                ", image_alt='" + image_alt + '\'' +
                '}';
    }
}
