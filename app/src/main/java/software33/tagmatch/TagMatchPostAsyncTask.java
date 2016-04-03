package software33.tagmatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class TagMatchPostAsyncTask extends AsyncTask<JSONObject, Void, JSONObject> {

    private URL url;
    private Context context;
    private String imgExtension;
    private boolean needLogin;

    public TagMatchPostAsyncTask(String url, Context context) {
        try {
            this.url = new URL(url);
            this.context = context;
            needLogin = false;
        } catch (MalformedURLException e) {
            Log.e("TagMatchPostAsyncTask", "", e);
        }
    }

    public TagMatchPostAsyncTask(String url, Context context, String imgExtension) {
        try {
            this.url = new URL(url);
            this.context = context;
            this.imgExtension = imgExtension;
            needLogin = true;
        } catch (MalformedURLException e) {
            Log.e("TagMatchPostAsyncTask", "", e);
        }
    }

    protected JSONObject doInBackground(JSONObject... params) {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoInput(true);
            con.setRequestMethod("POST");

            if (needLogin) {
                connectUser(con);
            }

            if (imgExtension == null)
                con.setRequestProperty("Content-Type", "application/json");
            else
                con.setRequestProperty("Content-Type", "image/" + imgExtension);

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
                aux = new JSONObject(iStreamToString(con.getErrorStream()));
            else
                aux = new JSONObject(iStreamToString(con.getInputStream()));

            con.disconnect();

            return aux;

        } catch (IOException | JSONException e) {
            Log.e("error", e.getMessage());
            Map<String, String> map = new HashMap<>();
            if (e.getMessage().contains("failed to connect to")) {
                if (e.getMessage().contains("Network is unreachable")) {
                    map.put("error", context.getString(R.string.no_network_connection));
                } else {
                    map.put("error", context.getString(R.string.connection_timeout));
                }
            } else {
                map.put("error", "Unexpected Error");
            }
            return new JSONObject(map);
        }
    }

    private void connectUser(HttpURLConnection c) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String user = prefs.getString("name", "");
        final String password = prefs.getString("password", "");

        Log.i("userPost", user);
        Log.i("passPost", password);
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password.toCharArray());
            }
        });
        String userPass = user + ":" + password;
        c.setRequestProperty("Authorization", "basic " +
                Base64.encode(userPass.getBytes(), Base64.DEFAULT));
    }

    public String iStreamToString(InputStream is1) {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is1), 4096);
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String contentOfMyInputStream = sb.toString();
        return contentOfMyInputStream;
    }


}
