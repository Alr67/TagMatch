package software33.tagmatch.Domain;

import android.media.Image;

import java.util.List;

/**
 * Created by Cristina on 03/04/2016.
 */
public class AdvChange extends Advertisement{

    List<String> wanted;

    public AdvChange(Integer aid, User owner, String title, List<Image> images, List<String> imagesIDs, String desc, String[] tags, String category, List<String> wanted){
        super(aid, owner, title, images,imagesIDs, desc, tags, category);
        this.wanted = wanted;
    }

    public AdvChange(User owner, String title, List<Image> images, String desc, String[] tags, String category, List<String> wanted){
        super(owner, title, images, desc, tags, category);
        this.wanted = wanted;

    }

    @Override
    public String getTypeImageName() {
        return Constants.changeImage;
    }

    @Override
    public String getPayTitle() {
        return "In exchange: ";
    }

    @Override
    public String getPayDesc() {
        String exch = "";
        for (String tag: wanted) {
            exch = exch + " #" + tag;
        }
        return exch;
    }

    @Override
    public String getTypeDescription() {
        return Constants.typeServerEXCHANGE;
    }

    @Override
    public List<String> getWantedTags() {
        return wanted;
    }
}
