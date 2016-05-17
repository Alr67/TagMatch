package software33.tagmatch.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import software33.tagmatch.Chat.FirebaseUtils;
import software33.tagmatch.Domain.AdvChange;
import software33.tagmatch.Domain.AdvGift;
import software33.tagmatch.Domain.AdvSell;
import software33.tagmatch.Domain.Advertisement;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;


public class Helpers {
    public static final String SH_PREF_NAME = "TagMatch_pref";

    public ArrayList<String> getPersonalData(Context context){
        ArrayList<String> data = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(SH_PREF_NAME, Context.MODE_PRIVATE);
        data.add(prefs.getString("name", null));
        data.add(prefs.getString("password", null));
        return data;
    }

    public static void setPersonalData(String username, String password, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(SH_PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("name", username);
        editor.putString("password",password);
        editor.commit();
    }

    public static User getActualUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SH_PREF_NAME, Context.MODE_PRIVATE);
        User user = new User(prefs.getString("name", null),
                                prefs.getString("password", null),
                                prefs.getString("email", null),
                                prefs.getString("photoId", null),
                                prefs.getString("city", null),
                                prefs.getInt("latitude",0),
                                prefs.getInt("longitude",0));
        return user;
    }

    public static void saveActualUser(String name, String password, String email,
                                      String photoId, String city, int latitude, int longitude, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(SH_PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("name", name);
        editor.putString("password", password);
        editor.putString("email", email);
        editor.putString("photoId", photoId);
        editor.putString("city", city);
        editor.putInt("latitude", latitude);
        editor.putInt("longitude", longitude);
        editor.commit();
    }

    public static void logout(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SH_PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.remove("name");
        editor.remove("password");
        FirebaseUtils.removeMyId(context);
        editor.commit();
    }

    public static int getIntFromType(String type) {
        if(type.equals(Constants.typeServerSELL)) return 0;
        else if (type.equals(Constants.typeServerEXCHANGE)) return 1;
        else return 2;
    }

    public static String checkServerError(Integer statusCode ) {
        return "";
    }


    public static Advertisement convertJSONToAdvertisement(JSONObject jsonObject) {
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

    public static Integer getDisplayHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static Integer getDisplayWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static void showError (String msg, Context context){
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        return;
    }

    public static int getIntFromCategory(String category) {
        for (int q = 0; q < Constants.categoryList.size(); ++q) {
            if(category.equals(Constants.categoryList.get(q))) return q;
        }
        return 0;
    }

    public static String cleanJSON(String input) {
        input = input.replaceAll("\\[","");
        input = input.replaceAll("\\]","");
        input = input.replaceAll("\"","");
        return input;
    }
}
