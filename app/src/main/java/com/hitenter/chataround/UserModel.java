package com.hitenter.chataround;

public class UserModel {


    String name, gender, email;
    String age;

    public UserModel(){}


    public UserModel(String name, String gender, String email, String age) {
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.age = age;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
