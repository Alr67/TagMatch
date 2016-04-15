package software33.tagmatch.AdCards;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import software33.tagmatch.R;
import software33.tagmatch.Utils.BitmapWorkerTask;

/**
 * Created by Rafa on 25/11/2015.
 */
public class AdapterAdvert extends RecyclerView.Adapter<AdapterAdvert.ReceptesViewHolder> implements View.OnClickListener {

    private ArrayList<AdvertContent> items;
    private View.OnClickListener listener;
    private Integer id = 0;
    private Integer height = 0;
    private Integer width = 0;

    public static class ReceptesViewHolder extends RecyclerView.ViewHolder {
        public ImageView imagen;
        public TextView nombre;

        public ReceptesViewHolder(View v) {
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imagen);
            nombre = (TextView) v.findViewById(R.id.nombre);
        }
    }
    public AdapterAdvert(ArrayList<AdvertContent> items) {
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
