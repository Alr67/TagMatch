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
import android.support.v4.util.Pair;
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
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import software33.tagmatch.AdCards.AdvertContent;
import software33.tagmatch.Domain.AdvGift;
import software33.tagmatch.Domain.Advertisement;
import software33.tagmatch.Domain.Offer;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchPostAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchPutAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.DialogError;
import software33.tagmatch.Utils.Helpers;

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
    private String idProduct;
    Advertisement adv;
    private String idChat;
    private String myId;
    private String idUser;
    private String imageChat;
    private String offerId;
    private boolean fromAdvert = false;
    private ChildEventListener mListener;
    private ValueEventListener mListenerChats;
    private ValueEventListener mListenerOffers;

    private HashMap<String, ChatMessage> unreadChatMessageMap = new HashMap<>();
    private ArrayList<String> advertisementsTitles = new ArrayList<>();
    private ArrayList<Integer> advertisementsIds = new ArrayList<>();
    private int positionMyAdv;


    // Yes OR No
    private String canSendOffers;

    private boolean isMyAdv = false;
    private boolean offerHasExchangeID = false;
    private int offerExchangeID;
    private boolean rateAvailable = false;
    private int rateAdvId;
    private int starts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);

        setTitle("");

        Bundle b = getIntent().getExtras();
        userName = b.getString("UserName");
        idProduct = b.getString("IdProduct");
        titleProduct = b.getString("TitleProduct");
        idChat = b.getString("IdChat");
        idUser = b.getString("IdUser");
        if (b.getBoolean("isMyAdv")) isMyAdv = true;
        if (b.getBoolean("FromAdvert")) fromAdvert = true;
        imageChat = FirebaseUtils.getChatImage(this);
        FirebaseUtils.removeChatImage(this);
        myId = FirebaseUtils.getMyId(this);

        //Get Firebase Reference
        myFirebaseRef = FirebaseUtils.getMyFirebaseRef();

        messagesRef = myFirebaseRef.child("chats").child(idChat).child("messages");
        offersRef = myFirebaseRef.child("chats").child(idChat).child("offer");
        usersRef = myFirebaseRef.child("users");

        //Donwload my adverts
        downloadMyAdvertsFromServer();

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

        if (fromAdvert){
            Map<String, Object> chats = new HashMap<>();
            chats.put(idChat,true);
            usersRef.child(myId).child("chats").updateChildren(chats);
        }

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
                setListData(dataSnapshot.getKey(), c.getSenderId(), c.getText(), c.getRead());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ChatMessage message = unreadChatMessageMap.get(dataSnapshot.getKey());
                chatArrayAdapter.updateMessageToRead(message);
                unreadChatMessageMap.remove(dataSnapshot.getKey());
            }

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
                    getOfferFromServer(o.getOfferId(), o.getValoration());
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
    public void setListData(String id, String senderId, String text, boolean read) {
        ChatMessage message = new ChatMessage((senderId.equals(myId)), senderId, text, read);
        if (!read) {
            unreadChatMessageMap.put(id, message);
        }
        chatArrayAdapter.add(message);
    }

    public void setOfferData(String senderId, String text, Boolean accepted, int exchangeID, final Map<String, Integer> valoration){
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
            if (valoration.containsKey(Helpers.getActualUser(this).getAlias())){
                layoutOffer.setVisibility(View.VISIBLE);
                bCancelOffer.setVisibility(View.VISIBLE);
                bAcceptOffer.setVisibility(View.VISIBLE);
                tvPendingOffer.setVisibility(View.GONE);
                tvContentOffer.setText(R.string.rate_offer);
                rateAvailable = true;
                rateAdvId = valoration.get(Helpers.getActualUser(this).getAlias());

                bCancelOffer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        createNotValorationDialog(valoration, getString(R.string.dialog_title_not_rate_deal),
                                getString(R.string.dialog_message_not_rate_deal),
                                getString(R.string.dialog_confirm_not_rate_deal),
                                getString(R.string.dialog_cancel_not_rate_deal)).show();
                    }
                });
                bAcceptOffer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        createValorationDialog(valoration).show();
                    }
                });
            }
            else hideOffer();
        }

        if (exchangeID != 0) {
            offerHasExchangeID = true;
            offerExchangeID = exchangeID;
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
                    chats.put(idChat,true);
                    usersRef.child(idUser).child("chats").updateChildren(chats);
                }
                else if (!(boolean)snapshot.child(idChat).getValue()) {
                    Map<String, Object> chats = new HashMap<>();
                    chats.put(idChat,true);
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
            AlertDialog alertDialog = createDeleteDialog();
            alertDialog.show();

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
        final TextView tvErrorOffer = (TextView) view.findViewById( R.id.tvErrorOffer);
        final LinearLayout layoutMyAdvs = (LinearLayout) view.findViewById(R.id.layoutMyAdvs);
        final ListView listViewMyAdvs = (ListView) view.findViewById(R.id.listViewMyAdvs);
        final ArrayAdapter<String> listArrayAdapter;
        positionMyAdv = -1;

        listArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.layout_offer_my_adv, advertisementsTitles);
        listViewMyAdvs.setAdapter(listArrayAdapter);
        listViewMyAdvs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                positionMyAdv = position;
            }
        });

        tvSetOffer.setVisibility(View.INVISIBLE);
        offerInformation.setVisibility(View.INVISIBLE);
        tvErrorOffer.setVisibility(View.INVISIBLE);
        layoutMyAdvs.setVisibility(View.GONE);

        offerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, final int position, long id) {
                if (offerList.getSelectedItem().toString().equals("No offer selected")) {
                    tvSetOffer.setVisibility(View.INVISIBLE);
                    offerInformation.setVisibility(View.INVISIBLE);
                    offerInformation.setText("");
                    layoutMyAdvs.setVisibility(View.GONE);
                    positionMyAdv = -1;
                }
                if (offerList.getSelectedItem().toString().equals("Offer money")){
                    tvSetOffer.setVisibility(View.VISIBLE);
                    offerInformation.setVisibility(View.VISIBLE);
                    offerInformation.setText("");
                    layoutMyAdvs.setVisibility(View.GONE);
                    offerInformation.setHint(R.string.dialog_money_information_offer);
                    offerInformation.setInputType(InputType.TYPE_CLASS_NUMBER);
                    positionMyAdv = -1;
                }
                if (offerList.getSelectedItem().toString().equals("Offer my advert")){
                    tvSetOffer.setVisibility(View.VISIBLE);
                    layoutMyAdvs.setVisibility(View.VISIBLE);
                    offerInformation.setVisibility(View.GONE);
                    offerInformation.setText("");
                    positionMyAdv = -1;
                }
                if (offerList.getSelectedItem().toString().equals("Other offers")){
                    tvSetOffer.setVisibility(View.VISIBLE);
                    layoutMyAdvs.setVisibility(View.GONE);
                    offerInformation.setVisibility(View.VISIBLE);
                    offerInformation.setText("");
                    offerInformation.setHint(R.string.dialog_other_information_offer);
                    offerInformation.setInputType(InputType.TYPE_CLASS_TEXT);
                    positionMyAdv = -1;
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
                        else if (offerList.getSelectedItem().toString().equals("Offer my advert")) {
                            if (positionMyAdv == -1){
                                tvErrorOffer.setText(R.string.dialog_error_send_offer_no_information);
                                tvErrorOffer.setVisibility(View.VISIBLE);
                            }
                            else {
                                tvErrorOffer.setVisibility(View.INVISIBLE);
                                createOffer(offerList.getSelectedItem().toString(), "", d);
                            }
                        }
                        else if (s.isEmpty() || s.equals("")){
                            tvErrorOffer.setText(R.string.dialog_error_send_offer_no_information);
                            tvErrorOffer.setVisibility(View.VISIBLE);
                        }
                        else {
                            tvErrorOffer.setVisibility(View.INVISIBLE);
                            createOffer(offerList.getSelectedItem().toString(), s, d);
                        }
                    }
                });
            }
        });

        return d;
    }

    private void createOffer(String typeOffer, String content, AlertDialog d){
        String senderId = myId;
        Map<String, Object> values = new HashMap<>();

        switch(typeOffer){
            case "Offer money":
                content = content+"€";
                break;
            case "Offer my advert":
                values.put("exchangeID", advertisementsIds.get(positionMyAdv));
                content = advertisementsTitles.get(positionMyAdv);
                break;
            case "Other offers":
                break;
        }


        values.put("senderId", senderId);
        values.put("text", content);
        values.put("accepted", false);

        sendOfferToServer(values, d);
    }

    private void denyOffer(){
        Map<String, Object> values = new HashMap<>();

        offersRef.setValue(values);

        addMessageOffer(2,"");
        hideOffer();
    }

    private void acceptOffer(){
        Map<String, Object> values = new HashMap<>();
        values.put("accepted", true);

        //Build the valoration
        Map<String, Integer> valoration = new HashMap<>();
        valoration.put(""+userName,Integer.parseInt(idProduct));
        if (offerHasExchangeID) valoration.put(""+Helpers.getActualUser(this).getAlias(), offerExchangeID);
        values.put("valoration",valoration);

        offersRef.updateChildren(values);

        addMessageOffer(1,"");

        hideOffer();
    }

    private void hideOffer(){
        layoutOffer.setVisibility(View.GONE);
    }

    private void addMessageOffer(int status, String content){
        String alias = Helpers.getActualUser(this).getAlias();
        String senderId = myId;

        Map<String, Object> values = new HashMap<>();
        values.put("senderId", "FirebaseAutoMessage");
        if (status == 0) values.put("text", getString(R.string.message_information_offer)+alias+" "+getString(R.string.message_made_offer)+" "+content);
        else if (status == 1) values.put("text", getString(R.string.message_information_offer)+alias+" "+getString(R.string.message_accepted_offer));
        else values.put("text", getString(R.string.message_information_offer)+alias+" "+getString(R.string.message_denied_offer));
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
                                else acceptOfferOnServer();
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

    public AlertDialog createNotValorationDialog(Map<String, Integer> valoration, String title, String message, String positive, String negative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        valoration.remove(Helpers.getActualUser(this).getAlias());
        final Map<String, Object> value = new HashMap<>();
        value.put("valoration",valoration);

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positive,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseUtils.getChatsRef().child(idChat).child("offer").updateChildren(value);
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

    public AlertDialog createValorationDialog(Map<String, Integer> valoration) {
        valoration.remove(Helpers.getActualUser(this).getAlias());
        final Map<String, Object> value = new HashMap<>();
        value.put("valoration",valoration);

        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_rate_deal, null);

        final AlertDialog d = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);


        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                starts = (int)rating;
            }
        });

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        sendRateToServer(value, d);
                    }
                });
            }
        });

        return d;
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

    private AlertDialog createDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.dialog_title_delete_chat)
                .setMessage(R.string.dialog_message_delete_chat)
                .setPositiveButton(R.string.dialog_confirm_delete_chat,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onPossitiveButtonClick();
                            }
                        })
                .setNegativeButton(R.string.dialog_cancel_delete_chat,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //onNegativeButtonClick();
                            }
                        });

        return builder.create();
    }

    private void onPossitiveButtonClick(){
        //Remove from Firebase putting the value to false
        Map<String, Object> value = new HashMap<>();
        value.put(idChat,false);
        usersRef.child(myId).child("chats").updateChildren(value);
        startActivity(new Intent(this, MainChatActivity.class));
        messagesRef.removeEventListener(mListener);
        offersRef.removeEventListener(mListenerOffers);
        finish();
    }

    @Override
    public void onBackPressed(){
        messagesRef.removeEventListener(mListener);
        offersRef.removeEventListener(mListenerOffers);
        if (fromAdvert){
            Intent intent = new Intent(this, MainChatActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private void acceptOfferOnServer() {
        JSONObject jsonObject = new JSONObject();

        final Context context = this.getApplicationContext();
        new TagMatchPutAsyncTask(Constants.IP_SERVER + "/offer/"+offerId+"/accept", this){
            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                try {
                    Log.i(Constants.DebugTAG,"JSON2: \n"+jsonObject);
                    if(jsonObject.has("error")) {
                        String error = jsonObject.get("error").toString();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        acceptOffer();
                    }
                } catch (JSONException ignored){

                }
            }
        }.execute(jsonObject);
    }

    private void downloadMyAdvertsFromServer() {
        JSONObject jObject = new JSONObject();
        User actualUser = Helpers.getActualUser(this);
        String url = Constants.IP_SERVER+"/users/"+actualUser.getAlias()+"/ads?limit="+Constants.SERVER_limitAdverts;
        try {
            jObject.put("username", actualUser.getAlias());
            jObject.put("password", actualUser.getPassword());
            new TagMatchGetAsyncTask(url,this) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    if(jsonObject.has("arrayResponse")) {
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("arrayResponse");
                            if(jsonArray.length()>0) {
                                for (int n = 0; n < jsonArray.length(); n++) {
                                    JSONObject object = jsonArray.getJSONObject(n);
                                    Advertisement newAdvert = Helpers.convertJSONToAdvertisement(object);
                                    advertisementsTitles.add(newAdvert.getTitle());
                                    advertisementsIds.add(newAdvert.getID());
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.execute(jObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendRateToServer(final Map<String, Object> value, final AlertDialog d){
        Log.i("DebugRate",String.valueOf(starts));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rating", starts);
        } catch (JSONException e) {
        }
        final Context context = this;
        new TagMatchPostAsyncTask(Constants.IP_SERVER + "/ads/"+idProduct+"/rate", this, true){
            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                try {
                    Log.i(Constants.DebugTAG,"JSON2: \n"+jsonObject);
                    if(jsonObject.has("error")) {
                        String error = jsonObject.get("error").toString();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        FirebaseUtils.getChatsRef().child(idChat).child("offer").updateChildren(value);
                        d.dismiss();
                    }
                } catch (JSONException ignored){

                }
            }
        }.execute(jsonObject);
    }

    private void sendOfferToServer(final Map<String, Object> value, final AlertDialog d){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("destinedUser", userName);
            jsonObject.put("offerAdvertisement", idProduct);
            if (value.containsKey("exchangeID")) jsonObject.put("offeredExchangeAdvertisement", value.get("exchangeID"));
            jsonObject.put("offeredText", value.get("text"));
        } catch (JSONException e) {
        }
        final Context context = this;
        new TagMatchPostAsyncTask(Constants.IP_SERVER + "/offer", this, true){
            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                try {
                    Log.i("DebugOffer","JSON2: \n"+jsonObject);
                    if(jsonObject.has("error")) {
                        String error = jsonObject.get("error").toString();
                        Toast.makeText(context, "This advert is already closed", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        value.put("offerID",jsonObject.get("offerId").toString());
                        offersRef.setValue(value);
                        addMessageOffer(0,value.get("text").toString());
                        d.dismiss();
                    }
                } catch (JSONException ignored){

                }
            }
        }.execute(jsonObject);
    }

    private void getOfferFromServer(String offerId, final Map<String, Integer> valoration){
        JSONObject jObject = new JSONObject();
        User actualUser = Helpers.getActualUser(this);
        String url = Constants.IP_SERVER+"/offer/"+offerId;
        this.offerId = offerId;
        try {
            jObject.put("username", actualUser.getAlias());
            jObject.put("password", actualUser.getPassword());
            new TagMatchGetAsyncTask(url,this) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        if(jsonObject.has("error")) {
                            String error = jsonObject.get("error").toString();
                            Helpers.showError(error,getApplicationContext());
                        }
                        else if (jsonObject.has("offerId")){
                            Offer offer = Helpers.convertJSONToOffer(jsonObject);
                            setOfferData(offer.getUserThatOffers(),offer.getOfferedText(),
                                    offer.isAccepted(), offer.getOfferedExchangeAdvertisement(), valoration);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.execute(jObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
