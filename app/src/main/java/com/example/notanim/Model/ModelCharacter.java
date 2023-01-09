package com.example.notanim.Model;

public class ModelCharacter {
    String id, characterName, animeName, gender, image_uri, age, day, month,uid;
    long timestamp;

    public ModelCharacter(String id, String characterName, String animeName, String gender, String image_uri, String age, String day, String month, String uid, long timestamp) {
        this.id = id;
        this.characterName = characterName;
        this.animeName = animeName;
        this.gender = gender;
        this.image_uri = image_uri;
        this.age = age;
        this.day = day;
        this.month = month;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public ModelCharacter() {

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

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getAnimeName() {
        return animeName;
    }

    public void setAnimeName(String animeName) {
        this.animeName = animeName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
