package software33.tagmatch.ServerConnection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.Utils.Helpers;

public abstract class TagMatchPostImgAsyncTask extends AsyncTask<Byte[], Void, JSONObject> {

    private URL url;
    private Context context;
    private String imgExtension;
    private byte[] img;

    public TagMatchPostImgAsyncTask(String url, Context context, byte[] img, String imgExtension) {
        try {
            this.url = new URL(url);
            this.context = context;
            this.imgExtension = imgExtension;
            this.img = img;
        } catch (MalformedURLException e) {
            Log.e("TagMatchPostAsyncTask", "", e);
        }
    }

    protected JSONObject doInBackground(Byte[]... params) {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            connectUser(con);
            con.setRequestProperty("Content-Type", "image/" + imgExtension);

            DataOutputStream wr = new DataOutputStream(
                    con.getOutputStream());
            wr.write(img);
            //wr.write(new String(img, Charset.forName("US-ASCII")));
            wr.flush();
            wr.close();

            con.getOutputStream().close();
            con.connect();

            /*>=400 errorStream
            else inputStream*/

            JSONObject aux;

            Log.i("post img status code", Integer.toString(con.getResponseCode()));

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
        User actualUser = Helpers.getActualUser(context);
        String userPass = actualUser.getAlias() + ":" + actualUser.getPassword();
        c.setRequestProperty("Authorization", "Basic " +
                new String(Base64.encode(userPass.getBytes(), Base64.DEFAULT)));
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
