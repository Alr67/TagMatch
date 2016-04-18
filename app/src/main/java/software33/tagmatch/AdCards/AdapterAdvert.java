package software33.tagmatch.AdCards;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetImgurImageAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;

/**
 * Created by Rafa on 25/11/2015.
 */
public class AdapterAdvert extends RecyclerView.Adapter<AdapterAdvert.ReceptesViewHolder> implements View.OnClickListener {

    private ArrayList<AdvertContent> items;
    private View.OnClickListener listener;
    private Integer id = 0;
    private Integer height = 0;
    private Integer width = 0;
    private Context context;

    public static class ReceptesViewHolder extends RecyclerView.ViewHolder {
        public ImageView imagen, type;
        public TextView nombre, preu;

        public ReceptesViewHolder(View v) {
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imagen);
            type = (ImageView) v.findViewById(R.id.type);
            preu = (TextView) v.findViewById(R.id.preu);
            nombre = (TextView) v.findViewById(R.id.nombre);
        }
    }
    public AdapterAdvert(ArrayList<AdvertContent> items, Context context) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ReceptesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_card, viewGroup, false);
        v.setOnClickListener(this);
        v.setTag(id);
        ++id;
        height = viewGroup.getHeight();
        width = viewGroup.getWidth();
        return new ReceptesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReceptesViewHolder viewHolder, int i) {
        viewHolder.nombre.setText(items.get(i).getNom());
        String typeaux = items.get(i).getType();
        if(typeaux == Constants.typeServerGIFT) {
            viewHolder.type.setImageDrawable(context.getDrawable(R.drawable.image0));
            viewHolder.preu.setVisibility(View.INVISIBLE);
        }
        else if(typeaux == Constants.typeServerEXCHANGE) {
            viewHolder.type.setImageDrawable(context.getDrawable(R.drawable.image_placeholder));
            viewHolder.preu.setVisibility(View.INVISIBLE);
        }
        else if(typeaux == Constants.typeSell) {
            viewHolder.type.setImageDrawable(context.getDrawable(R.drawable.bar_bg));
            viewHolder.preu.setText(items.get(i).getPrice().toString() + "€");
        }
        else {
            Toast.makeText(context,"Estas fent servir una opcio no valida",Toast.LENGTH_LONG).show();
        }

        getAdvertImage(items.get(i).getAd_id(),items.get(i).getImgId());
    }

    public void getAdvertImage(Integer advertId, String photoId) {
        JSONObject jObject = new JSONObject();
        User actualUser = Helpers.getActualUser(context);
        try {
            jObject.put("username", actualUser.getAlias());
            jObject.put("password", actualUser.getPassword());
        } catch (JSONException e) {
            Log.i(Constants.DebugTAG,"HA PETAT JAVA amb Json");
            e.printStackTrace();
        }
        Log.i(Constants.DebugTAG,"Vaig a demanar la foto amb id: "+photoId);
        String url = Constants.IP_SERVER+"/ads/"+ advertId.toString()+"/photo/";
        final AdapterAdvert parent = this;
        new TagMatchGetAsyncTask(url+photoId,context) {
            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                Log.i(Constants.DebugTAG,"onPostExecute de la imatge JSON: "+jsonObject.toString());
                if(jsonObject.has("302")) {
                    try {
                        new TagMatchGetImgurImageAsyncTask(jsonObject.getString("302"),context) {
                            @Override
                            protected void onPostExecute(JSONObject jsonObject) {
                                Log.i(Constants.DebugTAG,"onPostExecute de la imatge JSON: "+jsonObject.toString());
                                try {
                                    if(jsonObject.has("image")) {
                                        Bitmap image = Helpers.stringToBitMap(jsonObject.getString("image"));

                                      //  BitmapWorkerTaskFromBitmap task = new BitmapWorkerTaskFromBitmap(parent);
                                      //  task.execute(image,height.toString(),width.toString());
                                    }
                                    else {
                                        Toast.makeText(context,jsonObject.getString("error"),Toast.LENGTH_SHORT);
                                    }
                                    //  adv = convertJSONToAdvertisement(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //  adv = convertJSONToAdvertisement(jsonObject);
            }
        }.execute(jObject);
    }

    public void setOnClickListener (View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null) {
            listener.onClick(v);
        }
    }
}
