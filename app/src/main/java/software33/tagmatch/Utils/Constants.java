package software33.tagmatch.Utils;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import software33.tagmatch.Domain.AdvGift;
import software33.tagmatch.Domain.AdvSell;
import software33.tagmatch.Domain.User;

public abstract class Constants {
    public static final String typeServerGIFT = "com.walatime.model.GiveawayAdvertisement";
    public static final String typeServerEXCHANGE = "com.walatime.model.ExchangeAdvertisement";
    public static final String typeServerSELL = "com.walatime.model.SellAdvertisement";

    public static final String giftImage = "gift";
    public static final String sellImage = "euro";
    public static final String changeImage = "change";
    public static final String favouriteNo = "heart";
    public static final String favouriteYes = "heart";

    public static final String SH_PREF_NAME = "TagMatch_pref";

    public static List<String> categoryList = new ArrayList<>(Arrays.asList("Motor"));
    public static final String typeSell = "Sell";
    public static final String typeExchange = "Exchange";
    public static final String typeGift = "Gift";
    public static final List<String> typeList = new ArrayList<>(Arrays.asList(typeSell,typeExchange,typeGift));

    public static final String IP_ALEJANDRO = "192.168.1.129:8080";
    public static final String IP_ALEIX = "192.168.1.41:8080";
    public static final String IP_HEROKU = "tagmatch.herokuapp.com";
    public static final String IP_SERVER_DEVELOP = "tagmatchdevelop.herokuapp.com";
    public static final String IP_LOCAL = "localhost:8080";
    public static final String IP_SERVER = "https://" + IP_SERVER_DEVELOP;

    public static final String defaultImage = "image0";

    /* INTENTS CODES*/
    public static final int codeImagePicker = 012;
    public static final int codeCameraPicker = 013;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 14;

    /* CODI DE CARDS*/
 //   public static final Integer card_giveaway = 0;
 //   public static final Integer card_exchange = 1;
 //   public static final Integer card_sell = 2;

    /* Bundle TAGS */
    public static final String TAG_BUNDLE_IDVIEWADVERTISEMENT = "AdvertisementId";
    public static final String TAG_BUNDLE_USERVIEWADVERTISEMENT = "UserAdvertisement";

    /* Home advert server preferences */
    public static final Integer SERVER_IdGreaterThan = 0;
    public static final Integer SERVER_IdSmallerThan = 5;
    public static final Integer SERVER_limitAdverts = 40;

    /** A ELIMINAR */
    public static final User testUser = new User("test","test");
    public static final List<Bitmap> testImages = new ArrayList(0);
    public static final String testTags[]= new String[] {"Ankit","Bohra","Xyz"};
    public static final AdvGift testAdvertGift = new AdvGift(testUser,"REGAL 1",testImages,"TExtmolt llarg",testTags,"Categoria");
    public static final AdvSell testAdvertSell = new AdvSell(testUser,"REGAL 1",testImages,"TExtmolt llarg",testTags,"Categoria",32.2);
    public static final Integer idTEST = 4;
    public static final String DebugTAG = "DEBUG";
    public static final String DebugTAGDelete = "DEBUG-DeleteTask";
}
