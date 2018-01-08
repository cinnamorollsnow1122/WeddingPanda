package com.example.onpus.weddingpanda.constant;

/**
 * Created by onpus on 2018/1/5.
 */

public class AlbumItem {
    private String albumid;
    private String coverimage;
    private String caption;

    public AlbumItem(){

    }

    public AlbumItem(String albumid, String coverimage, String caption) {
        this.albumid = albumid;
        this.coverimage = coverimage;
        this.caption = caption;
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
