package com.toostew.resume_web.entity;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Set;


//literally the same as file but can only accept images
@Entity
public class Thumbnail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "original_name")
    private String original_name;

    @Column(name = "stored_name")
    private String stored_name;

    @Column(name = "content_type")
    private String content_type;

    @Column(name = "size")
    private long size;

    @Column(name = "date_created")
    private LocalDate date_created;

    @OneToMany(mappedBy = "thumbnail") //the name of the field within the java entity that has the FK(many to one) to this entity
    private Set<Post> post;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginal_name() {
        return original_name;
    }

    public void setOriginal_name(String original_name) {
        this.original_name = original_name;
    }

    public String getStored_name() {
        return stored_name;
    }

    public void setStored_name(String stored_name) {
        this.stored_name = stored_name;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDate getDate_created() {
        return date_created;
    }

    public void setDate_created(LocalDate date_created) {
        this.date_created = date_created;
    }
}
