package software33.tagmatch.AdCards;


import android.util.Log;

import software33.tagmatch.Utils.Constants;

/**
 * Created by Rafa on 25/11/2015.
 */
public class AdvertContent {

    private String ad_name;
    private Integer ad_id;
    private String ad_img_id;

    private String ad_type;
    private Double ad_price;

    private String ad_owner;
    private boolean ad_sold = false;

    public AdvertContent(String s, String img, String type) {
        this.ad_name = s;
        this.ad_img_id = img;
        this.ad_type = type;
        this.ad_price = -1.0;
    }

    public AdvertContent( String s, String img, String type, Double price, String ad_owner, Integer id) {
        this.ad_name = s;
        this.ad_img_id = img;
        this.ad_type = type;
        this.ad_price = price;
        this.ad_owner = ad_owner;
        this.ad_id = id;
    }

    public AdvertContent( String s, String img, String type, Double price, String ad_owner, Integer id, boolean sold) {
        this.ad_name = s;
        this.ad_img_id = img;
        this.ad_type = type;
        this.ad_price = price;
        this.ad_owner = ad_owner;
        this.ad_id = id;
        this.ad_sold = sold;
    }

    public String getNom() {
        return ad_name;
    }

    public Integer getAd_id() {return ad_id;}

    public String getImgId() {
        return ad_img_id;
    }

    public String getType() {
        return ad_type;
    }

    public Double getPrice() {
        return ad_price;
    }

    public String getOwner() {
        return ad_owner;
    }

    public boolean getSold() { return ad_sold; }
}
