package edu.bluejack182.defilm;

public class Review {
    private String id;
    private double rating;
    private String text;
    private String user;
    private String movieId;
    private String movieName;

    public Review() {
    }


    public Review(double rating, String text, String user, String id, String movieId, String movieName) {
        this.rating = rating;
        this.text = text;
        this.user = user;
        this.id = id;
        this.movieId = movieId;
        this.movieName = movieName;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieID() {
        return movieId;
    }

    public void setMovieID(String movieId) {
        this.movieId = movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }
}
