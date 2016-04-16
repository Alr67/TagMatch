package software33.tagmatch.ServerConnection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import software33.tagmatch.R;


public class TagMatchGetAsyncTask extends AsyncTask<JSONObject, Void, JSONObject> {
    private URL url;
    private Context context;

    public TagMatchGetAsyncTask(String url2, Context coming_context) {
        try {
            url = new URL(url2);
            context = coming_context;
        } catch (MalformedURLException e) {
            Log.v("TagMatchGetAsyncTask", "", e);
        }
    }

    protected JSONObject doInBackground(final JSONObject... params) {
        /*ConnectivityManager connMgr = (ConnectivityManager) PER ERRORS DE XARXA
        getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
                // fetch data
        } else {
            // display error
        }*/


        try {
            final String user = params[0].getString("username").toString();
            final String password = params[0].getString("password").toString();

            String userPass = user + ":" + password;

            Log.i("DEBUG-UserGet",userPass);

            String basicAuth;
            if (url.getHost().contains("heroku")) {
                basicAuth = "Basic " + new String(Base64.encode(userPass.getBytes(), Base64.NO_WRAP));
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                con.setRequestMethod("GET");

                con.setRequestProperty("Authorization", basicAuth);

                JSONObject aux;

                if (con.getResponseCode() >= 400){
                    aux = new JSONObject(iStreamToString(con.getErrorStream()));
                }
                else
                    aux = new JSONObject(iStreamToString(con.getInputStream()));

                con.disconnect();

                return aux;
            }
            else {
                basicAuth = "Basic " + new String(Base64.encode(userPass.getBytes(), Base64.DEFAULT));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                con.setRequestMethod("GET");

                con.setRequestProperty("Authorization", basicAuth);

                JSONObject aux;

                if (con.getResponseCode() >= 400){
                    aux = new JSONObject(iStreamToString(con.getErrorStream()));
                }
                else
                    aux = new JSONObject(iStreamToString(con.getInputStream()));

                con.disconnect();

                return aux;
            }


        } catch (IOException | JSONException e) {
            Log.e("error", e.getMessage());
            Map<String, String> map = new HashMap<>();
            if(e.getMessage().contains("failed to connect to")){
                if(e.getMessage().contains("Network is unreachable")){
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

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
