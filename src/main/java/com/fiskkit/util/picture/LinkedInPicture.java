package com.fiskkit.util.picture;

import java.util.Map;

import com.fiskkit.model.User;

/**
 * Created by joshuaellinger on 4/6/15.
 */
public class LinkedInPicture implements Picture {
    String name;
    int weight;
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getImageUrl(User user) {
        return user.getLinkedinProfileImage();
    }

    public String sizeImageUrl(String url, Map<String, String> settings) {
        return url;
    }
}
