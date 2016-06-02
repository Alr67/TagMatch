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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.Utils.Helpers;

public abstract class TagMatchPostAsyncTask extends AsyncTask<JSONObject, Void, JSONObject> {

    private URL url;
    private Context context;
    private boolean needLogin;

    public TagMatchPostAsyncTask(String url, Context context, boolean needLogin) {
        try {
            this.url = new URL(url);
            this.context = context;
            this.needLogin = needLogin;
        } catch (MalformedURLException e) {
            Log.e("TagMatchPostAsyncTask", "", e);
        }
    }

    protected JSONObject doInBackground(JSONObject... params) {
        try {
            if (url.getHost().contains("heroku")) {
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                con.setConnectTimeout(35000);
                con.setReadTimeout(35000);
                con.setDoInput(true);
                con.setRequestMethod("POST");

                if (needLogin) { connectUser(con); }
                con.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(params[0].toString());
                wr.flush();
                wr.close();

                con.getOutputStream().close();
                con.connect();

                /*>=400 errorStream
                else inputStream*/

                JSONObject aux;

                Log.i("post status code", Integer.toString(con.getResponseCode()));

                if (con.getResponseCode() >= 400)  aux = new JSONObject(Helpers.iStreamToString(con.getErrorStream()));
                else  aux = new JSONObject(Helpers.iStreamToString(con.getInputStream()));

                con.disconnect();
                return aux;
            } else {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                con.setDoInput(true);
                con.setRequestMethod("POST");
            Log.i("DEBUG post status code", Integer.toString(con.getResponseCode()));
                if (needLogin) {
                    connectUser(con);
                }
                con.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(params[0].toString());
                wr.flush();
                wr.close();

                con.getOutputStream().close();
                con.connect();

                /*>=400 errorStream
                else inputStream*/

                JSONObject aux;

                Log.i("post status code", Integer.toString(con.getResponseCode()));

                if (con.getResponseCode() >= 400)
                    aux = new JSONObject(Helpers.iStreamToString(con.getErrorStream()));
                else
                    aux = new JSONObject(Helpers.iStreamToString(con.getInputStream()));

                con.disconnect();

                return aux;
            }
        } catch (IOException | JSONException e) {
            Log.e("DEBUGERROR", e.getMessage());
            Map<String, String> map = new HashMap<>();
            if (e.getMessage().contains("failed to connect to")) {
                if (e.getMessage().contains("Network is unreachable"))
                    map.put("error", context.getString(R.string.no_network_connection));
            } else if (e.getMessage().equals("timeout"))
                map.put("error", context.getString(R.string.connection_timeout));
            else {
                map.put("error", "Unexpected Error");
            }
            return new JSONObject(map);
        }
    }

    private void connectUser(HttpURLConnection c) {
        User actualUser = Helpers.getActualUser(context);
        String userPass = actualUser.getAlias() + ":" + actualUser.getPassword();
        c.setRequestProperty("Authorization", "Basic " +
                new String(Base64.encode(userPass.getBytes(), Base64.DEFAULT)));
    }



}
