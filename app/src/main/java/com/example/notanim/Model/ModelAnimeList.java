package com.example.notanim.Model;

public class ModelAnimeList {
    private String id,animeName,seasonsAnime,year,typeAnime,rating,sampul,airedAnime,uid;
    long timestamp;

    public ModelAnimeList(String id, String animeName, String seasonsAnime, String year, String typeAnime, String rating, String sampul, String airedAnime, String uid, long timestamp) {
        this.id = id;
        this.animeName = animeName;
        this.seasonsAnime = seasonsAnime;
        this.year = year;
        this.typeAnime = typeAnime;
        this.rating = rating;
        this.sampul = sampul;
        this.airedAnime = airedAnime;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public ModelAnimeList(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnimeName() {
        return animeName;
    }

    public void setAnimeName(String animeName) {
        this.animeName = animeName;
    }

    public String getSeasonsAnime() {
        return seasonsAnime;
    }

    public void setSeasonsAnime(String seasonsAnime) {
        this.seasonsAnime = seasonsAnime;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTypeAnime() {
        return typeAnime;
    }

    public void setTypeAnime(String typeAnime) {
        this.typeAnime = typeAnime;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getSampul() {
        return sampul;
    }

    public void setSampul(String sampul) {
        this.sampul = sampul;
    }

    public String getAiredAnime() {
        return airedAnime;
    }

    public void setAiredAnime(String airedAnime) {
        this.airedAnime = airedAnime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
