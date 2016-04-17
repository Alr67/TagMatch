package software33.tagmatch.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

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

    public void logout(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SH_PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.remove("name");
        editor.remove("password");
        editor.commit();
    }

    public static String checkServerError(Integer statusCode ) {
        return "";
    }


}
