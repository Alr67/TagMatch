package software33.tagmatch.Domain;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cristina on 03/04/2016.
 */
public class Advertisement {

    Integer aID;
    String title;
    User owner;
    String description;
    List<Bitmap> images;
    List<String> imagesIDs;
    String[] tags;
    Boolean done;
    String category;

    public Advertisement(){
        this.aID = 0;
        this.owner = new User();
        this.images = new ArrayList<>();
        this.description = "";
        this.title = "";
        this.tags =  new String[0];
        this.done = false;
        this.category = "";
        this.imagesIDs = new ArrayList<>();
    }

    public Advertisement(Integer aid, User owner, String title, List<Bitmap> images, List<String> imagesIDs, String desc, String[] tags, String category){
        this.aID = aid;
        this.title = title;
        this.owner = owner;
        this.images = images;
        this.description = desc;
        this.tags = tags;
        this.done = false;
        this.category = category;
        this.imagesIDs = imagesIDs;
        clearHashTag();
    }

    public Advertisement(User owner, String title, List<Bitmap> images, String desc, String[] tags, String category){
        this.aID = 0;
        this.title = title;
        this.owner = owner;
        this.images = images;
        this.description = desc;
        this.tags = tags;
        this.done = false;
        this.category = category;
        this.imagesIDs = new ArrayList<>();
        clearHashTag();
    }

    protected void clearHashTag() {
        for (int i=0; i<tags.length; ++i) {
            if(tags[i].startsWith("#"))
                tags[i] = tags[i].replaceFirst("#", "");
        }
    }

    public String getTypeImageName() {
        return "";
    }

    public String getPayTitle() {
        return "";
    }

    public String getPayDesc() {
        return "";
    }

    public String getTypeDescription()  {
        return "";
    }

    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", title);
            jsonObject.put("type", getTypeDescription());
            jsonObject.put("owner", owner.getAlias());
            Log.i("Alias user", owner.getAlias());
            jsonObject.put("category", category);
            jsonObject.put("tags", new JSONArray(tags));
            jsonObject.put("description", description);
        } catch (JSONException e) {
        }
        return jsonObject;
    }
}
