package com.toostew.resume_web.entity;


import jakarta.persistence.*;

import java.time.LocalDate;

//A post is a fundamental unit for the blog, each contains all the metadata needed to properly display
@Entity
@Table(name = "post") //docker is linux-based, and is case sensitive so we must specify
public class Post {


    //metadata
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "titleURLFriendly")
    private String titleURLFriendly;

    @Column(name = "uploadDate")
    private LocalDate uploadDate;

    //Content
    @Column(name = "description")
    private String description; //Description, to be shown on the front page

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail") //MYSQL column name for this entity that points to the other entity
    private Thumbnail thumbnail; //thumbnail for the post, it's id in thumbnail

    @Column(name = "content")
    private String content; //the content, literal markdown

    @Column(name = "ptype") // post type, either post or art
    private String ptype;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleURLFriendly() {
        return titleURLFriendly;
    }

    public void setTitleURLFriendly(String titleURLFriendly) {
        this.titleURLFriendly = titleURLFriendly;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPtype() {return ptype;}

    public void setPtype(String ptype) {this.ptype = ptype;}
}
