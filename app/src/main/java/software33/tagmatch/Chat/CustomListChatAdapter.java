package software33.tagmatch.Chat;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.SyncStateContract;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import software33.tagmatch.R;
import software33.tagmatch.Utils.Constants;

public class CustomListChatAdapter extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private String myId;
    private static LayoutInflater inflater=null;
    public Resources res;
    ListChatModel tempValues=null;
    int i=0;

    /*************  CustomAdapter Constructor *****************/
    public CustomListChatAdapter(Activity a, String myId, ArrayList d, Resources resLocal) {

        /********** Take passed values **********/
        activity = a;
        this.myId = myId;
        data=d;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{


        public TableRow table;
        public TextView text;
        public TextView text1;
        public ImageView image;
        public TextView tvNumMessages;
        public TextView tvOffer;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate chat_itemem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.chat_item, null);

            /****** View Holder Object to contain chat_itemem.xml file elements ******/

            holder = new ViewHolder();
            holder.table = (TableRow) vi.findViewById(R.id.tableItemChat);
            holder.text = (TextView) vi.findViewById(R.id.text);
            holder.text1=(TextView)vi.findViewById(R.id.text1);
            holder.image=(ImageView)vi.findViewById(R.id.image);
            holder.tvNumMessages=(TextView)vi.findViewById(R.id.tvNumMessages);
            holder.tvOffer=(TextView)vi.findViewById(R.id.tvOffer);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            vi = inflater.inflate(R.layout.empty_list_chat, null);
        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( ListChatModel ) data.get( position );

            /************  Set Model values in Holder elements ***********/

            if (tempValues.getOwner().equals(myId))
                holder.table.setBackgroundColor(activity.getResources().getColor(R.color.my_chat_color));
            else holder.table.setBackgroundResource(R.drawable.bar_bg);

            holder.text.setText( tempValues.getUserName() );
            holder.text1.setText( tempValues.getTitleProduct() );

            if (tempValues.getImage().equals("")){
                holder.image.setImageResource(
                        res.getIdentifier(
                                "software33.tagmatch:drawable/"+ Constants.defaultImage
                                ,null,null));
            }
            else {
                //Set image Base64
                byte[] imageAsBytes = Base64.decode(tempValues.getImage(), Base64.DEFAULT);
                holder.image.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            }

            if (tempValues.getMessages()==0) holder.tvNumMessages.setText("");
            else holder.tvNumMessages.setText(String.valueOf(tempValues.getMessages()));

            if (tempValues.getNewOffer()!= 0){
                if (tempValues.getNewOffer() == 1) holder.tvOffer.setText(R.string.mainchat_new_offer);
                else if (tempValues.getNewOffer() == 2) holder.tvOffer.setText(R.string.mainchat_pending_offer);
                else if (tempValues.getNewOffer() == 3) holder.tvOffer.setText(R.string.mainchat_closed_offer);
                else holder.tvOffer.setText(R.string.mainchat_rated_offer);
            }
            else {
                holder.tvOffer.setText("");
            }
            /******** Set Item Click Listner for LayoutInflater for each row *******/

            vi.setOnClickListener(new OnItemClickListener( position ));
            vi.setOnLongClickListener(new OnItemLongClickListener( position ));
        }

        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {


            MainChatActivity sct = (MainChatActivity)activity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ****/

            sct.onItemClick(mPosition);
        }
    }

    /********* Called when Item Long click in ListView ************/
    private class OnItemLongClickListener  implements View.OnLongClickListener {
        private int mPosition;

        OnItemLongClickListener(int position){
            mPosition = position;
        }

        @Override
        public boolean onLongClick(View arg0) {


            MainChatActivity sct = (MainChatActivity)activity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ****/

            sct.onItemLongClick(mPosition);

            return true;
        }
    }
}
