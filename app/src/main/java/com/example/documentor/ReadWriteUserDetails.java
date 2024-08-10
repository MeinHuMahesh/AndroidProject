package com.example.documentor;

public class ReadWriteUserDetails {
    public String roll;
    public String email;
    public String pass;
    public String phone;
    public String gender;
    public String year;
    public ReadWriteUserDetails() {
    }
    public String stream;
    public ReadWriteUserDetails( String roll, String email, String pass, String phone, String gender, String year, String stream) {

        this.roll = roll;
        this.email = email;
        this.pass = pass;
        this.phone = phone;
        this.gender = gender;
        this.year = year;
        this.stream = stream;
    }



    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }
}
