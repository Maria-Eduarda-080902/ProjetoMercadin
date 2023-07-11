package com.example.projetomercadinho.models;

import com.google.type.Date;

public class PropostaData {
    private String product_image;
    private String product_description;
    private String authorKey;
    private String local;
    private String date;
    private String time;
    private String info;
    private String key;

    private String status;

    public PropostaData() {
        super();
    }

    public PropostaData(String product_image, String product_description, String authorKey, String local, String date, String time, String info){
        this.product_image = product_image;
        this.product_description = product_description;
        this.authorKey = authorKey;
        this.local = local;
        this.date = date;
        this.time = time;
        this.info = info;
        this.status = "pendente";
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getAuthorKey() {
        return authorKey;
    }

    public void setAuthorKey(String authorKey) {
        this.authorKey = authorKey;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
