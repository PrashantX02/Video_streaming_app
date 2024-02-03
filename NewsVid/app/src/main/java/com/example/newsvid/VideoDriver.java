package com.example.newsvid;

public class VideoDriver {

    String videoUrl,description,name,imageUrl,add,uid;
    String likes;

    public VideoDriver(){}

    public VideoDriver(String uid,String videoUrl,String likes,String description,String name,String imageUrl,String add){
        this.videoUrl = videoUrl;
        this.description = description;
        this.name = name;
        this.imageUrl = imageUrl;
        this.add = add;
        this.likes = likes;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }
}
