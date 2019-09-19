package edu.bluejack182.defilm.ui.main;

public class Schedule {
    String description;
    String date;
    String id;

    public Schedule() {

    }

    public Schedule(String description, String date, String id) {
        this.description = description;
        this.date = date;
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
