package com.example.onpus.weddingpanda.constant;

/**
 * Created by onpus on 2018/1/8.
 */

public class InAlbumitem {
    private String image;
    private String sender;
    private String comment;
    private String id;

    public InAlbumitem(){

    }

    public InAlbumitem(String image, String sender, String comment,String id) {
        this.image = image;
        this.sender = sender;
        this.comment = comment;
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }


    public void setComment(String comment) { this.comment = comment;}
    public String getComment(){ return comment;}

    public void setId(String id) { this.id = id;}
    public String getId(){ return id;}

}
