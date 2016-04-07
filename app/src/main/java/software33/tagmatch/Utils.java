package software33.tagmatch;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;


public class Utils {
    private static final String SH_PREF_NAME = "TagMatch_pref";

    public ArrayList<String> getPersonalData(Context context){
        ArrayList<String> data = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(SH_PREF_NAME, Context.MODE_PRIVATE);
        data.add(prefs.getString("name", null));
        data.add(prefs.getString("password", null));
        return data;
    }
}
