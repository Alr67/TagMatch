package software33.tagmatch.ServerConnection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import software33.tagmatch.Utils.Helpers;

/**
 * Created by rafax on 05/05/2016.
 */
public class TagMatchGetTrendingAsyncTask extends AsyncTask<JSONObject, Void, JSONObject>{
    private URL url;
    private Context context;

    public TagMatchGetTrendingAsyncTask(String url2, Context coming_context) {
        try {
            url = new URL(url2);
            context = coming_context;
        } catch (MalformedURLException e) {
            Log.v("TagMatchGetAsyncTask", "", e);
        }
    }

    protected JSONObject doInBackground(final JSONObject... params) {

        try {
            final String user = params[0].getString("username").toString();
            final String password = params[0].getString("password").toString();

            String userPass = user + ":" + password;

            String basicAuth;
            if (url.getHost().contains("heroku")) {
                basicAuth = "Basic " + new String(Base64.encode(userPass.getBytes(), Base64.NO_WRAP));
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

                con.setRequestProperty("Authorization", basicAuth);

                con.connect();

                JSONObject aux;

                if (con.getResponseCode() >= 400) {
                    aux = new JSONObject(Helpers.iStreamToString(con.getErrorStream()));
                }
                else if (con.getResponseCode() == 302) {
                    aux = new JSONObject();
                    aux.put("302", con.getURL().toString());
                }
                else if (con.getResponseCode() == 200) {
                    aux = new JSONObject();
                    aux.put("200", Helpers.iStreamToString(con.getInputStream()));
                }
                else {
                    aux = new JSONObject();
                    aux.put("ELSE", con.getInputStream());
                }

                con.disconnect();

                return aux;
            } else {
                basicAuth = "Basic " + new String(Base64.encode(userPass.getBytes(), Base64.NO_WRAP));
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

                con.setRequestProperty("Authorization", basicAuth);

                con.connect();

                JSONObject aux;

                if (con.getResponseCode() >= 400) {
                    aux = new JSONObject(Helpers.iStreamToString(con.getErrorStream()));
                }
                else if (con.getResponseCode() == 302) {
                    aux = new JSONObject();
                    aux.put("302", con.getURL().toString());
                }
                else if (con.getResponseCode() == 200) {
                    aux = new JSONObject();
                    aux.put("200", con.getInputStream());
                }
                else {
                    aux = new JSONObject();
                    aux.put("ELSE", con.getInputStream());
                }

                con.disconnect();
                return aux;
            }
        } catch (IOException | JSONException e) {
            Log.e("error", e.getMessage());
            return null;
        }
    }

}
