package com.sifast.appsocle.models;


/**
 * Created by Asus on 14/06/2016.
 */
public class Feedback {


    private String declaredBy;
    private String declaredLong;
    private String declaredLat;
    private String messageDeclared;
    private String declartionDate;

    public Feedback() {
    }

    public String getDeclaredBy() {
        return declaredBy;
    }

    public void setDeclaredBy(String declaredBy) {
        this.declaredBy = declaredBy;
    }

    public String getDeclaredLong() {
        return declaredLong;
    }

    public void setDeclaredLong(String declaredLong) {
        this.declaredLong = declaredLong;
    }

    public String getDeclaredLat() {
        return declaredLat;
    }

    public void setDeclaredLat(String declaredLat) {
        this.declaredLat = declaredLat;
    }

    public String getMessageDeclared() {
        return messageDeclared;
    }

    public void setMessageDeclared(String messageDeclared) {
        this.messageDeclared = messageDeclared;
    }

    public String getDeclartionDate() {
        return declartionDate;
    }

    public void setDeclartionDate(String declartionDate) {
        this.declartionDate = declartionDate;
    }
}
