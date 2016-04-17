package software33.tagmatch.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


    public static final List<String> categoryList = new ArrayList<>(Arrays.asList("Cotxes","Motos","Videojocs"));
    public static final String typeSell = "Sell";
    public static final String typeExchange = "Exchange";
    public static final String typeGift = "Gift";
    public static final List<String> typeList = new ArrayList<>(Arrays.asList(typeSell,typeExchange,typeGift));

    public static final String IP_ALEJANDRO = "192.168.1.129:8080";
    public static final String IP_ALEIX = "192.168.1.41:8080";
    public static final String IP_HEROKU = "tagmatch.herokuapp.com";
    public static final String IP_HEROKU_DEVELOP = "tagmatchdevelop.herokuapp.com";
    public static final String IP_LOCAL = "localhost:8080";
    public static final String IP_SERVER = "https://" + IP_HEROKU_DEVELOP;

    public static final String defaultImage = "image0";

    /* INTENTS CODES*/
    public static final int codeImagePicker = 012;
    public static final int codeCameraPicker = 013;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 14;

}
