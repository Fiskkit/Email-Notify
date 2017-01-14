package com.fiskkit.util.picture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.fiskkit.model.User;

/**
 * Created by joshuaellinger on 4/6/15.
 */
public class PictureUtil {
	
	private static final Logger LOGGER = Logger.getLogger("");
	
    private List<Picture> pictureArrayList;
    

    public PictureUtil() {
        List<Picture> pictureArrayList = new ArrayList<Picture>();
        // Add picture types here.
        pictureArrayList.add(new FacebookPicture());
        pictureArrayList.add(new LinkedInPicture());

        // Sort picture types.
        Collections.sort(pictureArrayList, new Comparator<Picture>() {
            public int compare(Picture o1, Picture o2) {
                return o1.getWeight() - o2.getWeight();
            }
        });
        this.pictureArrayList = pictureArrayList;
    }

    /**
     * Get a sized picture url from a user, and Map.
     *
     * @param user
     * @param settings
     * @return
     */
    public String getUserPictureUrl(User user, Map<String, String> settings) {
        String pictureUrl = "";

        for (int i = 0; i < this.pictureArrayList.size(); i++) {
            Picture pictureType = this.pictureArrayList.get(i);
            pictureUrl = pictureType.getImageUrl(user);
            
            if ((pictureUrl == null) || (pictureUrl.isEmpty())){
            	pictureUrl = ""; // reset to empty string
            	continue;
            } else {
            	try {
            		pictureUrl = pictureType.sizeImageUrl(pictureUrl, settings);
            	} catch (Exception e) {
            		LOGGER.severe("getUserPictureUrl():user=" + user.getEmail() + ":" +
            				e.getMessage() +":" + e.getClass().getName());
            		LOGGER.severe("pictureUrl=" + pictureUrl);
            		pictureUrl = "";
            	}
                break;
            }
            
            
        }
        return pictureUrl;
    }
}
