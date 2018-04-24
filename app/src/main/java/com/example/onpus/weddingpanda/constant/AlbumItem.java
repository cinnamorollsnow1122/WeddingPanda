package com.example.onpus.weddingpanda.constant;

/**
 * Created by onpus on 2018/1/5.
 */

public class AlbumItem {
    private String albumid;
    private String coverimage;
    private String caption;
    private String creator;

    public AlbumItem(){

    }

    public AlbumItem(String albumid, String coverimage, String caption,String creator) {
        this.albumid = albumid;
        this.coverimage = coverimage;
        this.caption = caption;
        this.creator = creator;
    }
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getCoverimage() {
        return coverimage;
    }

    public void setCoverimage(String coverimage) {
        this.coverimage = coverimage;
    }


    public void setCaption(String caption) { this.caption = caption;}
    public String getCaption(){ return caption;}
}
