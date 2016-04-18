package software33.tagmatch.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import software33.tagmatch.Domain.AdvChange;
import software33.tagmatch.Domain.AdvGift;
import software33.tagmatch.Domain.AdvSell;
import software33.tagmatch.Domain.Advertisement;
import software33.tagmatch.Domain.User;


public class Helpers {
    public static final String SH_PREF_NAME = "TagMatch_pref";

    public ArrayList<String> getPersonalData(Context context){
        ArrayList<String> data = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(SH_PREF_NAME, Context.MODE_PRIVATE);
        data.add(prefs.getString("name", null));
        data.add(prefs.getString("password", null));
        return data;
    }

    public static User getActualUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SH_PREF_NAME, Context.MODE_PRIVATE);
        return new User(prefs.getString("name", null),prefs.getString("password", null));

    }

    public static void logout(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SH_PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.remove("name");
        editor.remove("password");
        editor.commit();
    }

    public static String checkServerError(Integer statusCode ) {
        return "";
    }


    public static Advertisement convertJSONToAdvertisement(JSONObject jsonObject) {
        Log.i(Constants.DebugTAG,"Lets convert to JSON");
        Advertisement advert = new Advertisement();
        String title,description,type,ownerName,category, userPhotoId, city;
        title = description = type = ownerName = category = userPhotoId = city = "";
        Integer id = 0;
        String[] tags = new String[0];
        String[] photoIds = new String[0];
        try {
            if(jsonObject.has("id")) id = jsonObject.getInt("id");
            if(jsonObject.has("type")) type = jsonObject.getString("type");
            if(jsonObject.has("name")) title = jsonObject.getString("name");
            if(jsonObject.has("owner")) ownerName = jsonObject.getString("owner");
            if(jsonObject.has("category")) category = jsonObject.getString("category");
            if(jsonObject.has("description")) description = jsonObject.getString("description");
            if(jsonObject.has("userPhotoId")) userPhotoId = jsonObject.getString("userPhotoId");
            if(jsonObject.has("city")) city = jsonObject.getString("city");
            User owner = new User(ownerName,userPhotoId,city);
            Log.i(Constants.DebugTAG,"User name from owner: " +owner.getAlias());
            if(jsonObject.has("tags")) {
                JSONArray array = jsonObject.getJSONArray("tags");
                tags = new String[array.length()];
                for(int i = 0; i < array.length();++i) {
                    tags[i] = array.getString(i);
                }
            }
            if(jsonObject.has("photoIds")) {
                JSONArray array = jsonObject.getJSONArray("photoIds");
                photoIds = new String[array.length()];
                for(int i = 0; i < array.length();++i) {
                    photoIds[i] = array.getString(i);
                }
            }
            if(Constants.typeServerSELL.contains(type)) {
                Double price = 0.0;
                if(jsonObject.has("price")) price = jsonObject.getDouble("price");
                advert = new AdvSell(id,title,photoIds,description,tags,category,price);
            }
            else if(Constants.typeServerEXCHANGE.contains(type)) {
                String[] tagsWanted = new String[0];
                if(jsonObject.has("wantedTags"))  {
                    JSONArray array = jsonObject.getJSONArray("wantedTags");
                    tagsWanted = new String[array.length()];
                    for(int i = 0; i < array.length();++i) {
                        tagsWanted[i] = array.getString(i);
                    }
                }
                advert = new AdvChange(id,title,photoIds,description,tags,category,tagsWanted);
            }
            else {
                advert = new AdvGift(id,title,photoIds,description,tags,category);
            }
            advert.setOwner(owner);
            return advert;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(Constants.DebugTAG,"User name from covnert func: "+advert.getUser().getAlias());
        return advert;
    }


    public static Bitmap stringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

}
