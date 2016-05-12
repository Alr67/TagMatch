package software33.tagmatch.Chat;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import software33.tagmatch.R;
import software33.tagmatch.Utils.DialogError;

public class SingleChatActivity extends AppCompatActivity {

    private ChatArrayAdapter chatArrayAdapter;
    private ListView chatView;
    private EditText chatText;
    private ImageButton buttonSend;
    private LinearLayout layoutOffer;
    private TextView tvContentOffer;
    private Button bCancelOffer;
    private Button bAcceptOffer;
    private TextView tvPendingOffer;

    private Firebase myFirebaseRef;
    private Firebase messagesRef;
    private Firebase offersRef;
    private Firebase usersRef;
    private String userName;
    private String titleProduct;
    private String idChat;
    private String myId;
    private String idUser;
    private String imageChat;
    private ChildEventListener mListener;
    private ValueEventListener mListenerOffers;

    // Yes OR No
    private String canSendOffers;
    private boolean isMyAdv = false;

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
        if (b.getBoolean("isMyAdv")) isMyAdv = true;
        imageChat = FirebaseUtils.getChatImage(this);
        FirebaseUtils.removeChatImage(this);
        myId = FirebaseUtils.getMyId(this);

        //Get Firebase Reference
        myFirebaseRef = FirebaseUtils.getMyFirebaseRef();

        messagesRef = myFirebaseRef.child("chats").child(idChat).child("messages");
        offersRef = myFirebaseRef.child("chats").child(idChat).child("offer");
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

        if (imageChat == null || imageChat.equals("")){
            image.setImageResource(R.drawable.image0);
        }
        else {
            byte[] imageAsBytes = Base64.decode(imageChat, Base64.DEFAULT);
            image.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }
        /****************************************************/


        /************** Set the chat ************************/

        layoutOffer = (LinearLayout) findViewById(R.id.layoutOffer);
        tvContentOffer = (TextView) findViewById(R.id.tvContentOffer);
        bCancelOffer = (Button) findViewById(R.id.bCancelOffert);
        bAcceptOffer = (Button) findViewById(R.id.bAcceptOffer);
        tvPendingOffer = (TextView) findViewById(R.id.tvPendingOffer);
        layoutOffer.setVisibility(View.GONE);

        bCancelOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                createOfferDialog(true, getString(R.string.dialog_title_deny_offer),
                        getString(R.string.dialog_message_deny_offer),
                        getString(R.string.dialog_confirm_deny_offer),
                        getString(R.string.dialog_cancel_deny_offer)).show();
            }
        });
        bAcceptOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                createOfferDialog(false, getString(R.string.dialog_title_accept_offer),
                        getString(R.string.dialog_message_accept_offer),
                        getString(R.string.dialog_confirm_accept_offer),
                        getString(R.string.dialog_cancel_accept_offer)).show();
            }
        });

        buttonSend = (ImageButton) findViewById(R.id.send);

        chatView = (ListView) findViewById(R.id.msgview);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right_chat);
        chatView.setAdapter(chatArrayAdapter);

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

        chatView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                chatView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        //Update Data
        mListener = this.messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                FirebaseUtils.ChatText c = dataSnapshot.getValue(FirebaseUtils.ChatText.class);
                if (!c.getSenderId().equals(myId) && !c.getRead()){
                    Map<String, Object> values = new HashMap<>();
                    values.put("read", true);
                    messagesRef.child(dataSnapshot.getKey()).updateChildren(values);
                }
                setListData(c.getSenderId(), c.getText(), c.getRead());
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

        mListenerOffers = this.offersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    FirebaseUtils.ChatOffer o = dataSnapshot.getValue(FirebaseUtils.ChatOffer.class);
                    setOfferData(o.getSenderId(), o.getText(), o.getAccepted(), o.getExchangeID());
                }
                else {
                    canSendOffers = "Yes";
                    hideOffer();
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    //Set data in the array
    public void setListData(String senderId, String text, boolean read) {
        chatArrayAdapter.add(new ChatMessage((senderId.equals(myId)), senderId, text, read));
    }

    public void setOfferData(String senderId, String text, Boolean accepted, String exchangeID){
        if (!accepted) {
            canSendOffers = "Yes";
            layoutOffer.setVisibility(View.VISIBLE);
            tvContentOffer.setText(text);
            if (senderId.equals(myId)) {
                bCancelOffer.setVisibility(View.GONE);
                bAcceptOffer.setVisibility(View.GONE);
                tvPendingOffer.setVisibility(View.VISIBLE);
            } else {
                bCancelOffer.setVisibility(View.VISIBLE);
                bAcceptOffer.setVisibility(View.VISIBLE);
                tvPendingOffer.setVisibility(View.GONE);
            }
        }
        else {
            canSendOffers = "No";
            hideOffer();
        }
    }

    private boolean sendChatMessage() {
        String text = chatText.getText().toString();
        text = text.trim();
        if (!text.isEmpty()) {
            String senderId = myId;

            Map<String, Object> values = new HashMap<>();
            values.put("senderId", senderId);
            values.put("text", text);
            values.put("read", false);

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
                    usersRef.child(idUser).child("chats").updateChildren(chats);
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
        if (isMyAdv) getMenuInflater().inflate(R.menu.menu_option_my_single_chat, menu);
        else getMenuInflater().inflate(R.menu.menu_option_single_chat, menu);
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
            if (canSendOffers != null){
                if (canSendOffers.equals("Yes")) {
                    createDialogSendOffer().show();
                } else {
                    createErrorDialog().show();
                }
            }
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

    public AlertDialog createDialogSendOffer() {
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_send_offer, null);

        final Spinner offerList = (Spinner) view.findViewById(R.id.offerList);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.offers_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        offerList.setAdapter(adapter);

        final TextView tvSetOffer = (TextView) view.findViewById(R.id.tvSetOffer);
        final EditText offerInformation = (EditText) view.findViewById(R.id.offerInformation);
        final TextView tvErrorOffer = (TextView) view.findViewById(R.id.tvErrorOffer);

        tvSetOffer.setVisibility(View.INVISIBLE);
        offerInformation.setVisibility(View.INVISIBLE);
        tvErrorOffer.setVisibility(View.INVISIBLE);

        offerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, final int position, long id) {
                if (offerList.getSelectedItem().toString().equals("No offer selected")) {
                    tvSetOffer.setVisibility(View.INVISIBLE);
                    offerInformation.setVisibility(View.INVISIBLE);
                }
                if (offerList.getSelectedItem().toString().equals("Offer money")){
                    tvSetOffer.setVisibility(View.VISIBLE);
                    offerInformation.setVisibility(View.VISIBLE);
                    offerInformation.setHint(R.string.dialog_money_information_offer);
                    offerInformation.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                if (offerList.getSelectedItem().toString().equals("Offer my advert")){
                    tvSetOffer.setVisibility(View.VISIBLE);
                    offerInformation.setVisibility(View.INVISIBLE);
                }
                if (offerList.getSelectedItem().toString().equals("Other offers")){
                    tvSetOffer.setVisibility(View.VISIBLE);
                    offerInformation.setVisibility(View.VISIBLE);
                    offerInformation.setHint(R.string.dialog_other_information_offer);
                    offerInformation.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        final AlertDialog d = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String s = offerInformation.getText().toString();
                        if (offerList.getSelectedItem().toString().equals("No offer selected")){
                            tvErrorOffer.setText(R.string.dialog_error_send_offer_no_offer);
                            tvErrorOffer.setVisibility(View.VISIBLE);
                        }
                        else if (s.isEmpty() || s.equals("")){
                            tvErrorOffer.setText(R.string.dialog_error_send_offer_no_information);
                            tvErrorOffer.setVisibility(View.VISIBLE);
                        }
                        else {
                            tvErrorOffer.setVisibility(View.INVISIBLE);
                            createOffer(offerList.getSelectedItem().toString(), s);
                            d.dismiss();
                        }
                    }
                });
            }
        });

        return d;
    }

    private void createOffer(String typeOffer, String content){
        String senderId = myId;
        Map<String, Object> values = new HashMap<>();
        values.put("senderId", senderId);
        values.put("text", content);
        values.put("accepted", false);

        switch(typeOffer){
            case "Offer money":
                break;
            case "Offer my advert":
                values.put("exchangeID", "ID");
                break;
            case "Other offers":
                break;
        }

        offersRef.setValue(values);

        addMessageOffer(0);
    }

    private void denyOffer(){
        Map<String, Object> values = new HashMap<>();

        offersRef.setValue(values);

        addMessageOffer(2);
        hideOffer();
    }

    private void acceptOffer(){
        Map<String, Object> values = new HashMap<>();
        values.put("accepted", true);

        offersRef.updateChildren(values);

        addMessageOffer(1);
        hideOffer();
    }

    private void hideOffer(){
        layoutOffer.setVisibility(View.GONE);
    }

    private void addMessageOffer(int status){
        String senderId = myId;

        Map<String, Object> values = new HashMap<>();
        values.put("senderId", senderId);
        if (status == 0) values.put("text", getString(R.string.message_made_offer));
        else if (status == 1) values.put("text", getString(R.string.message_accepted_offer));
        else values.put("text", getString(R.string.message_denied_offer));
        values.put("read", false);

        messagesRef.push().setValue(values);
        chatText.setText("");

        //Check if the other user have a chat with you ONCE
        checkUserChat();
    }

    public AlertDialog createOfferDialog(boolean deny, String title, String message, String positive, String negative) {
        final boolean deny1 = deny;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positive,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (deny1) denyOffer();
                                else acceptOffer();
                            }
                        })
                .setNegativeButton(negative,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //onNegativeButtonClick();
                            }
                        });

        return builder.create();
    }

    public AlertDialog createErrorDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.title_dialog_error_create_offer)
                .setMessage(R.string.message_dialog_error_create_offer)
                .setPositiveButton(R.string.button_dialog_error_create_offer,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        return builder.create();
    }

    @Override
    public void onBackPressed(){
        messagesRef.removeEventListener(mListener);
        offersRef.removeEventListener(mListenerOffers);
        finish();
    }
}
