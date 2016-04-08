package software33.tagmatch.Domain;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import software33.tagmatch.Utils.Constants;

/**
 * Created by Cristina on 03/04/2016.
 */
public class AdvChange extends Advertisement{

    String[] wanted;

    public AdvChange(Integer aid, User owner, String title, List<Bitmap> images, List<String> imagesIDs, String desc, String[] tags, String category, String[] wanted){
        super(aid, owner, title, images,imagesIDs, desc, tags, category);
        this.wanted = wanted;
    }

    public AdvChange(User owner, String title, List<Bitmap> images, String desc, String[] tags, String category, String[] wanted){
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

    public String[] getWantedTags() {
        return wanted;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = super.toJSON();
        try {
            jsonObject.put("wantedTags", new JSONArray(getWantedTags()));
        } catch (JSONException ignored) {}
        return jsonObject;
    }
}
