package software33.tagmatch.AdCards;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import software33.tagmatch.R;
import software33.tagmatch.Utils.BitmapWorkerTask;
import software33.tagmatch.Utils.Constants;

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
        viewHolder.imagen.setImageDrawable(context.getDrawable(R.drawable.advert_gift));
        Integer typeaux = items.get(i).getType();
        if(typeaux == Constants.card_giveaway) {
            viewHolder.type.setImageDrawable(context.getDrawable(R.drawable.image0));
            viewHolder.preu.setVisibility(View.INVISIBLE);
        }
        else if(typeaux == Constants.card_exchange) {
            viewHolder.type.setImageDrawable(context.getDrawable(R.drawable.image_placeholder));
            viewHolder.preu.setVisibility(View.INVISIBLE);
        }
        else if(typeaux == Constants.card_sell) {
            viewHolder.type.setImageDrawable(context.getDrawable(R.drawable.bar_bg));
            viewHolder.preu.setText(items.get(i).getPrice().toString() + "€");
        }
        else {
            Toast.makeText(context,"Estas fent servir una opcio no valida",Toast.LENGTH_LONG).show();
        }
        BitmapWorkerTask task = new BitmapWorkerTask(viewHolder.imagen);
        task.execute(items.get(i).getImg(),height.toString(),width.toString());
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
