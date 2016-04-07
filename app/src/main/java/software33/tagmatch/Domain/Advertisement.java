package software33.tagmatch.Domain;

import android.graphics.Bitmap;

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

    public Double getPrice() {
        return 0.0;
    }

    public String[] getWantedTags() {
        return new String[0];
    }
}
