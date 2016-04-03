package software33.tagmatch.Domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Cristina on 03/04/2016.
 */
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

    public static final User testUser = new User("testUser");

    /* INTENTS CODES*/
    public static final int codeImagePicker = 012;

}
