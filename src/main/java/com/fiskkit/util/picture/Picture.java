package com.fiskkit.util.picture;

import java.util.Map;

import com.fiskkit.model.User;

/**
 * Created by joshuaellinger on 4/6/15.
 */
public interface Picture {
    public String getName();

    public void setName(String name);

    public int getWeight();

    public void setWeight(int weight);

    public String getImageUrl(User user);

    public String sizeImageUrl(String url, Map<String, String> settings);
}
