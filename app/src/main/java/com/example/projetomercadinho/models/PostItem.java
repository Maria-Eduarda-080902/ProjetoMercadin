package com.example.projetomercadinho.models;

import java.util.Date;

public class PostItem {

    private String userId;
    private Date postCreation;
    private String  postPicture;
    private String oferta;
    private String procura;
    private int likes;
    private String key;

    public PostItem(String userId, Date postCreation, String postPicture, String oferta, String procura, int likes) {
        this.userId = userId;
        this.postCreation = postCreation;
        this.postPicture = postPicture;
        this.oferta = oferta;
        this.procura = procura;
        this.likes = likes;
    }

    public PostItem(){}

    public String getUserId() {
        return userId;
    }

    public Date getPostCreation() {
        return postCreation;
    }

    public String getPostPicture() {
        return postPicture;
    }

    public String getOferta() {
        return oferta;
    }

    public String getProcura() {
        return procura;
    }

    public int getLikes() {
        return likes;
    }

    public String getKey(){return  key;}

    public void setPostPicture(String postPicture) {
        this.postPicture = postPicture;
    }

    public void setKey(String key){
        this.key = key;
    }
}
