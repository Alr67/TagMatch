package software33.tagmatch.Domain;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import software33.tagmatch.Utils.Constants;

/**
 * Created by Cristina on 03/04/2016.
 */
public class Advertisement {

    private Integer aID;
    private String title;
    private  User owner;
    private String description;
    private List<Bitmap> images;
    private String[] imagesIDs;
    private String[] tags;
    private Boolean done;
    private String category;

    public Advertisement(){
        this.aID = 0;
        this.owner = new User();
        this.images = new ArrayList<>();
        this.description = "";
        this.title = "";
        this.tags =  new String[0];
        this.done = false;
        this.category = "";
        this.imagesIDs = new String[0];
    }

    public Advertisement(Integer aid,  String title, List<Bitmap> images, String[] imagesIDs, String desc, String[] tags, String category){
        this.aID = aid;
        this.title = title;
        this.owner = new User();
        this.images = images;
        this.description = desc;
        this.tags = tags;
        this.done = false;
        this.category = category;
        this.imagesIDs = imagesIDs;
        clearHashTag();
    }

    public Advertisement(Integer aid,User owner, String title,String[] imagesIDs, String desc, String[] tags, String category){
        this.aID = aid;
        this.title = title;
        this.owner = owner;
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
        this.imagesIDs = new String[0];
        clearHashTag();
    }

    public Advertisement(Integer aid, String title, String[] imagesIDs, String desc, String[] tags, String category) {
        this.aID = aid;
        this.title = title;
        this.owner = new User();
        this.description = desc;
        this.tags = tags;
        this.done = false;
        this.category = category;
        this.imagesIDs = imagesIDs;
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
            jsonObject.put("category", category);
            jsonObject.put("tags", new JSONArray(tags));
            jsonObject.put("description", description);
        } catch (JSONException e) {
        }
        return jsonObject;
    }

    public User getUser() {
        return owner;
    }

    public Integer getID() {
        return aID;
    }

    public String getTitle() {
        return title;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User advOwner) {
         owner = advOwner;
    }

    public String getDescription() {
        return description;
    }

    public List<Bitmap> getImages() {
        return images;
    }

    public String[] getImagesIDs() {
        return imagesIDs;
    }

    public String[] getTags() {
        return tags;
    }

    public Boolean getDone() {
        return done;
    }

    public Double getPrice()  {
        return -1.0;
    }

    public String getCategory() {
        return category;
    }

}
