package edu.bluejack182.defilm;

public class Review {
    private double rating;
    private String text;
    private String user;

    public Review() {
    }


    public Review(double rating, String text, String user) {
        this.rating = rating;
        this.text = text;
        this.user = user;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
