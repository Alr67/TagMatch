package software33.tagmatch.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import software33.tagmatch.Domain.Advertisement;


public class Helpers {
    private static final String SH_PREF_NAME = "TagMatch_pref";

    public ArrayList<String> getPersonalData(Context context){
        ArrayList<String> data = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(SH_PREF_NAME, Context.MODE_PRIVATE);
        data.add(prefs.getString("name", null));
        data.add(prefs.getString("password", null));
        return data;
    }

    public void logout(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SH_PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.remove("name");
        editor.remove("password");
        editor.commit();
    }

    public static Advertisement getAdvertisementFromJSON(String jsonString) {
        Advertisement adv = new Advertisement();
        try {
            JSONObject json = new JSONObject(jsonString);
            if(!json.getString("price").equals(null)) {
                Log.i("DEBUG","ES una sell");
            }
            else if(!json.get("wanted").equals(null)) {
                Log.i("DEBUG","ES un canvi");
            }
            else {
                Log.i("DEBUG","ES un regal");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return adv;
    }


}
