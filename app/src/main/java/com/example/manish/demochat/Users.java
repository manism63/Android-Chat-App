package com.example.manish.demochat;

/**
 * Created by Manish on 10/11/2017.
 */

public class Users {
    public String name;
    public String status;
    public String image;
    public String thumb_image;

    public Users(){

    }

    public Users(String name, String status, String image, String thumb_image) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.thumb_image = thumb_image;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }



}
