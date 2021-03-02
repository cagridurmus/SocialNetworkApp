package com.example.instagramclone.Model;

public class Post {
    private String postId;
    private String postImage;
    private String postInfo;
    private String publisher;

    public Post() {
    }

    public Post(String postId, String postImage, String postInfo, String publisher) {
        this.postId = postId;
        this.postImage = postImage;
        this.postInfo = postInfo;
        this.publisher = publisher;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostInfo() {
        return postInfo;
    }

    public void setPostInfo(String postInfo) {
        this.postInfo = postInfo;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
