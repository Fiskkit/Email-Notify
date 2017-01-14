package com.fiskkit.model;

/**
 * Created by joshuaellinger on 4/13/15.
 */
public class Fisk {
    private int id;
    private User user;
    private String fiskUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUrl(String article_id){
        this.fiskUrl = "http://fiskkit.com/articles/" + article_id + "/fisk/" + id;
    }

    public String getUrl(){
        return fiskUrl;
    }
}
