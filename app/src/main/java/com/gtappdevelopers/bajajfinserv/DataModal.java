package com.gtappdevelopers.bajajfinserv;

public class DataModal {

    private String date;
    private String message;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataModal(String date, String message) {
        this.date = date;
        this.message = message;
    }
}
