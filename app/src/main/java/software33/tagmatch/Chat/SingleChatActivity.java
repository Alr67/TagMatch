package software33.tagmatch.Chat;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import software33.tagmatch.R;

public class SingleChatActivity extends AppCompatActivity {

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;

    private Firebase myFirebaseRef;
    private Firebase messagesRef;
    private Firebase usersRef;
    private String userName;
    private String titleProduct;
    private String idChat;
    private String myId;
    private String idUser;
    private ChildEventListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);

        setTitle("");

        Bundle b = getIntent().getExtras();
        userName = b.getString("UserName");
        titleProduct = b.getString("TitleProduct");
        idChat = b.getString("IdChat");
        idUser = b.getString("IdUser");
        myId = "56d3f1d3-6b50-473a-9aa3-ff0007b3df29";

        //Get Firebase Reference
        myFirebaseRef =
                new Firebase("https://torrid-torch-42.firebaseio.com/");

        messagesRef = myFirebaseRef.child("chats").child(idChat).child("messages");
        usersRef = myFirebaseRef.child("users");



        /************** Set the action Bar *****************/

       ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                | ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_single_chat);

        TextView mytext = (TextView) findViewById(R.id.singleChatUserName);
        mytext.setText(userName);

        mytext = (TextView) findViewById(R.id.singleChatTitleProduct);
        mytext.setText(titleProduct);

        ImageView image = (ImageView) findViewById(R.id.singleChatImage);
        image.setImageResource(R.drawable.image0);

        /****************************************************/


        /************** Set the chat ************************/

        buttonSend = (Button) findViewById(R.id.send);

        listView = (ListView) findViewById(R.id.msgview);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right_chat);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        //Update Data
        mListener = this.messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                ChatText c = dataSnapshot.getValue(ChatText.class);
                setListData(c.getSenderId(), c.getText());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {}

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");
            }

        });
    }

    private static class ChatText {
        String senderId;
        String text;
        public ChatText() {
            // empty default constructor, necessary for Firebase to be able to deserialize blog posts
        }
        public String getSenderId() {
            return senderId;
        }
        public String getText() {
            return text;
        }
    }

    //Set data in the array
    public void setListData(String senderId, String text)
    {
        chatArrayAdapter.add(new ChatMessage((senderId.equals(myId)), senderId, text));
    }

    private boolean sendChatMessage() {
        String text = chatText.getText().toString();
        if (!text.isEmpty()) {
            //TODO: Get my id
            String senderId = myId;

            Map<String, Object> values = new HashMap<>();
            values.put("senderId", senderId);
            values.put("text", text);

            messagesRef.push().setValue(values);
            chatText.setText("");

            //Check if the other user have a chat with you ONCE
            checkUserChat();
        }

        return true;
    }

    private void checkUserChat() {
        usersRef.child(idUser).child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild(idChat)) {
                    Map<String, Object> chats = new HashMap<>();
                    chats.put(idChat,"");
                    usersRef.child(idUser).child("chats").setValue(chats);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_option_single_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_image) {
            return true;
        }
        if (id == R.id.action_send_offer) {
            return true;
        }
        if (id == R.id.action_block_user) {
            return true;
        }
        if (id == R.id.action_delete_chat) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //To close the connection

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //chatArrayAdapter.cleanup();
    }
}
