package com.example.instagramclone.Model;

public class Story {
    private String imageurl;
    private String storyid;
    private String userid;
    private long startdate;
    private long enddate;

    public Story(String imageurl, String storyid, String userid, long startdate, long enddate) {
        this.imageurl = imageurl;
        this.storyid = storyid;
        this.userid = userid;
        this.startdate = startdate;
        this.enddate = enddate;
    }

    public Story() {
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public long getStartdate() {
        return startdate;
    }

    public void setStartdate(long startdate) {
        this.startdate = startdate;
    }

    public long getEnddate() {
        return enddate;
    }

    public void setEnddate(long enddate) {
        this.enddate = enddate;
    }
}

