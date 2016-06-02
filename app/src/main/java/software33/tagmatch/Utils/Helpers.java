package software33.tagmatch.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import android.widget.ImageView;
import android.widget.TextView;

import android.widget.EditText;

import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import software33.tagmatch.ServerConnection.TagMatchGetImageAsyncTask;
import software33.tagmatch.Users.ViewProfile;


public class Helpers {

    public static ArrayList<String> getPersonalData(Context context){
        ArrayList<String> data = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(Constants.SH_PREF_NAME, Context.MODE_PRIVATE);
        data.add(prefs.getString("name", null));
        data.add(prefs.getString("password", null));
        return data;
    }

    public static void saveDeviceToken(Context context, String token) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SH_PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("device_token", token);
        editor.commit();
    }

    public static void eraseDeviceToken(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SH_PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.remove("device_token");
    }

    public static String getDeviceToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SH_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString("device_token", "ERROR");
    }

    public static void setPersonalData(String username, String password, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SH_PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("name", username);
        editor.putString("password",password);
        editor.commit();
    }

    public static User getActualUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SH_PREF_NAME, Context.MODE_PRIVATE);
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
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SH_PREF_NAME, Context.MODE_PRIVATE).edit();
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
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SH_PREF_NAME, Context.MODE_PRIVATE).edit();
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

    public static void setNavHeader(final View nav_header, final Context context) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", Helpers.getActualUser(context).getAlias());
            jsonObject.put("password", Helpers.getActualUser(context).getPassword());
            new TagMatchGetImageAsyncTask(Constants.IP_SERVER + "/users/" + Helpers.getActualUser(context).getAlias() + "/photo", context) {
                @Override
                protected void onPostExecute(String url) {
                    ImageView contenedor = (ImageView) nav_header.findViewById(R.id.image_nav);
                    if (url == null) {
                        Picasso.with(context).load(R.drawable.image0).centerCrop().resize(contenedor.getMeasuredWidth(), contenedor.getMeasuredHeight()).into(contenedor);
                    } else
                        Picasso.with(context).load(url).error(R.drawable.image0).centerCrop().resize(contenedor.getMeasuredWidth(), contenedor.getMeasuredHeight()).into(contenedor);
                }
            }.execute(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((ImageView) nav_header.findViewById(R.id.image_nav)).setImageDrawable(context.getDrawable(R.drawable.loading_gif)); //NO TOCAR NUNCA JAMAS BAJO NINGUNA CIRCUNSTANCIA

        ((TextView) nav_header.findViewById(R.id.user_name_header)).setText(Helpers.getActualUser(context).getAlias());
    }
    public static boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }
}
