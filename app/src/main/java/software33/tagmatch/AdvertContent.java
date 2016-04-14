package software33.tagmatch;


import java.util.ArrayList;

/**
 * Created by Rafa on 25/11/2015.
 */
public class AdvertContent {

    private String ad_name;
    private String ad_img;
    private Integer ad_type;
    private Integer ad_price;

    public AdvertContent(String s, String img, Integer type) {
        this.ad_name = s;
        this.ad_img = img;
        this.ad_type = type;
        this.ad_price = -1;
    }

    public AdvertContent(String s, String img, Integer type, Integer price) {
        this.ad_name = s;
        this.ad_img = img;
        this.ad_type = type;
        this.ad_price = price;
    }

    public String getNom() {
        return ad_name;
    }


    public String getImg() {
        return ad_img;
    }

    public Integer getType() {
        return ad_type;
    }

    public Integer getPrice() {
        return ad_price;
    }
}
