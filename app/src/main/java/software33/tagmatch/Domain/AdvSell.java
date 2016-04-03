package software33.tagmatch.Domain;

import android.media.Image;

import java.util.List;

/**
 * Created by Cristina on 03/04/2016.
 */
public class AdvSell extends Advertisement {

    private Double price;

    public AdvSell(Integer aid, User owner, String title, List<Image> images, List<String> imagesIDs, String desc,String[] tags, String category,Double price){
        super(aid, owner, title, images,imagesIDs, desc, tags, category);
        this.price = price;
    }

    public AdvSell( User owner, String title, List<Image> images, String desc, String[] tags, String category,Double price){
        super(owner, title, images, desc, tags, category);
        this.price = price;
    }

    @Override
    public String getTypeImageName() {
        return Constants.sellImage;
    }

    @Override
    public String getPayTitle() {
        return "Price: ";
    }

    @Override
    public String getPayDesc() {
        return price.toString() + " €";
    }

    @Override
    public String getTypeDescription()  {
        return Constants.typeServerSELL;
    }

    @Override
    public Double getPrice()  {
        return price;
    }

}
