package software33.tagmatch.ServerConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import software33.tagmatch.R;
import software33.tagmatch.Utils.Constants;


public class TagMatchGetImgurImageAsyncTaskAdvert extends AsyncTask<JSONObject, Void, JSONObject> {
    private URL url;
    private Context context;

    public TagMatchGetImgurImageAsyncTaskAdvert(String url2, Context coming_context) {
        try {
            if(url2.contains("http://")) {
                String aux = url2.replace("http://", "https://");
                url2 = aux;
            }
            url = new URL(url2);
            context = coming_context;
        } catch (MalformedURLException e) {
            Log.v("TagMatchGetAsyncTask", "", e);
        }
    }

    protected JSONObject doInBackground(final JSONObject... params) {
        try {
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        //    con.setConnectTimeout(5000);
        //    con.setReadTimeout(5000);
            con.setRequestMethod("GET");

            JSONObject aux;

            Log.i(Constants.DebugTAG,"responseCode: "+con.getResponseCode());
            if (con.getResponseCode() >= 400){
                aux = new JSONObject(iStreamToString(con.getErrorStream()));
                Log.i(Constants.DebugTAG,"error: "+aux);
             }
            else {
                //  Bitmap image = BitmapFactory.decodeStream(con.getInputStream());
                aux = new JSONObject();
                aux.put("inputStream",con.getInputStream());
                Log.i(Constants.DebugTAG,"input: "+aux);
            }

            con.disconnect();
            Log.i(Constants.DebugTAG,"Ja he desconnectat");

            return aux;

        } catch (IOException | JSONException e) {
            Log.e(Constants.DebugTAG, e.getMessage());
            Map<String, String> map = new HashMap<>();
            if(e.getMessage().contains("failed to connect to")){
                if(e.getMessage().contains("Network is unreachable")){
                    map.put("error", context.getString(R.string.no_network_connection));
                } else {
                    map.put("error", context.getString(R.string.connection_timeout));
                }
            } else {
                map.put("error", "Unexpected error");
            }
            return new JSONObject(map);
        }

    }

    public String bitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
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
