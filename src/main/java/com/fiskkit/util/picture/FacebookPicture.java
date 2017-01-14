package com.fiskkit.util.picture;

import java.util.Map;

import com.fiskkit.model.User;

/**
 * Created by joshuaellinger on 4/6/15.
 */
public class FacebookPicture implements Picture {
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
        if (user.getFacebookId() == null || user.getFacebookId().isEmpty()) {
            return "";
        };
        return "https://graph.facebook.com/" + user.getFacebookId() + "/picture";
    }

    public String sizeImageUrl(String url, Map<String, String> settings) {
        if (!settings.get("type").isEmpty()) {
            if (settings.get("type") == "large") {
                url += "?type=large";
            } else if (settings.get("type") == "large-square") {
                url += "?width=180&height=180";
            } else if (settings.get("type") == "medium" || settings.get("type") == "normal") {
                url += "?type=normal";
            } else if (settings.get("type") == "small") {
                url += "?type=small";
            } else if (settings.get("type") == "small-square") {
                url += "?type=square";
            }
        } else if (!settings.get("height").isEmpty() || !settings.get("width").isEmpty()) {
            url += "?";
            if (!settings.get("height").isEmpty()) {
                url += "height=" + settings.get("height");
            }
            if (!settings.get("height").isEmpty() && !settings.get("width").isEmpty()) {
                url += '&';
            }
            if (!settings.get("width").isEmpty()) {
                url += "width=" + settings.get("width");
            }
        }
        return url;
    }
}
