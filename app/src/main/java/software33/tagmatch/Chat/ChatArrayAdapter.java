package software33.tagmatch.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import software33.tagmatch.R;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private ImageView readImage;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;

    @Override
    public void remove(ChatMessage object) {
        chatMessageList.remove(object);
        super.remove(object);
    }

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.left) {
            row = inflater.inflate(R.layout.right_chat, parent, false);
            readImage = (ImageView) row.findViewById(R.id.readImg);
            if (chatMessageObj.getRead())readImage.setVisibility(View.VISIBLE);
            else readImage.setVisibility(View.GONE);
        }else{
            row = inflater.inflate(R.layout.left_chat, parent, false);
        }
        chatText = (TextView) row.findViewById(R.id.msgr);
        chatText.setText(chatMessageObj.message);

        return row;
    }

    public void updateMessageToRead(ChatMessage message){
        int pos = this.getPosition(message);
        message.setRead(true);
        this.insert(message,pos);
    }
}
