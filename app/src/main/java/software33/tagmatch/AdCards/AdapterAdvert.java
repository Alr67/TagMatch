package software33.tagmatch.AdCards;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetImageAsyncTask;
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
        viewHolder.imagen.setImageDrawable(context.getDrawable(R.drawable.loadinggif));
        viewHolder.nombre.setText(items.get(i).getNom());
        //Picasso.with(context).load(R.drawable.loading).into(viewHolder.imagen);

        String typeaux = items.get(i).getType();
        if(items.get(i).getImgId()!="") {
            try {
                getAdvertImage(items.get(i).getAd_id(), items.get(i).getImgId(), context, viewHolder.imagen);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else viewHolder.imagen.setImageDrawable(context.getDrawable(R.drawable.image_placeholder));

        if(typeaux == Constants.typeServerGIFT) {
            viewHolder.type.setImageDrawable(context.getDrawable(R.drawable.advert_gift));
            viewHolder.preu.setVisibility(View.INVISIBLE);
        }
        else if(typeaux == Constants.typeServerEXCHANGE) {
            viewHolder.type.setImageDrawable(context.getDrawable(R.drawable.advert_exchange));
            viewHolder.preu.setVisibility(View.INVISIBLE);
        }
        else if(typeaux == Constants.typeServerSELL) {
            viewHolder.type.setImageDrawable(context.getDrawable(R.drawable.advert_sell));
            viewHolder.preu.setText(items.get(i).getPrice().toString() + "€");
        }
        else {
            Toast.makeText(context,"Estas fent servir una opcio no valida",Toast.LENGTH_LONG).show();
        }


    }

    public void getAdvertImage(Integer advertId, String photoId, final Context context, final ImageView image) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", Helpers.getActualUser(context).getAlias());
        jsonObject.put("password", Helpers.getActualUser(context).getPassword());
        new TagMatchGetImageAsyncTask(Constants.IP_SERVER + "/ads/" + advertId + "/photo/" + photoId, context) {
            @Override
            protected void onPostExecute(String url) {
                Picasso.with(context).load(url).error(R.drawable.image0).into(image);
            }
        }.execute(jsonObject);

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
