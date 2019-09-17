package edu.bluejack182.defilm.ui.main;

import java.util.Date;

public class Schedule {
    String description;
    String date;

    public Schedule() {

    }

    public Schedule(String description, String date) {
        this.description = description;
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
