package com.example.newsvid;

public class commentDriver {
    String img,name,text;

    public commentDriver(){}

    public commentDriver(String img,String name,String text) {
        this.name = name;
        this.img = img;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
