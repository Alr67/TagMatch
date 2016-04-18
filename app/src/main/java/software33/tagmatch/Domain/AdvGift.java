package software33.tagmatch.Domain;

import android.graphics.Bitmap;

import java.util.List;

import software33.tagmatch.Utils.Constants;

/**
 * Created by Cristina on 03/04/2016.
 */
public class AdvGift extends Advertisement{


    public AdvGift(Integer id, String title, String[] photoIds, String description, String[] tags, String category) {
        super(id, title,photoIds, description, tags, category);
    }

    public AdvGift(User owner, String title, List<Bitmap> images, String description, String[] tags, String category) {
        super(owner,title,images,description,tags,category);
    }

    @Override
    public String getTypeImageName() {
        return Constants.giftImage;
    }

    @Override
    public String getPayDesc() {
        return "Hurry up, it's a gift!";
    }

    @Override
    public String getTypeDescription() {
        return Constants.typeServerGIFT;
    }
}
