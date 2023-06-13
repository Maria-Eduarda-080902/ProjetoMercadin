package com.example.projetomercadinho.models;

public class UserData {
    public String name , email , pass, cpf, birthdate, cep, bio;
    public String profile_pic; //uri download
    public UserData(){
    }
    public UserData(String name , String email , String pass, String cpf, String birthdate, String cep) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.cpf = cpf;
        this.birthdate = birthdate;
        this.cep = cep;
        bio = "";
        profile_pic = "";
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setProfilePic(String profilePic) {
        this.profile_pic = profilePic;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setName(String name) {this.name = name;}

    public void setValues(UserData user) {
        this.name = user.name;
        this.email = user.email;
        this.pass = user.pass;
        this.cpf = user.cpf;
        this.birthdate = user.birthdate;
        this.cep = user.cep;
        bio = user.bio;
        profile_pic = user.profile_pic;
    }
}
