package software33.tagmatch.Domain;

import android.media.Image;

import java.util.List;

/**
 * Created by Cristina on 03/04/2016.
 */
public class AdvGift extends Advertisement{

    public AdvGift(Integer aid, User owner, String title, List<Image> images, List<String> imagesIDs, String desc, String[] tags, String category){
        super(aid, owner, title, images,imagesIDs, desc, tags, category);
    }

    public AdvGift( User owner, String title, List<Image> images, String desc,String[] tags, String category){
        super(owner, title, images, desc, tags, category);
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
