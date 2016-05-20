package software33.tagmatch.Chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import software33.tagmatch.R;
import software33.tagmatch.Utils.Helpers;

public abstract class FirebaseUtils {

    private static Firebase myFirebaseRef = new Firebase("https://torrid-torch-42.firebaseio.com/");
    private static Firebase chatsRef = myFirebaseRef.child("chats");
    private static Firebase usersRef = myFirebaseRef.child("users");

    private static final String SH_PREF_FIREBASE = "Firebase_pref";

    private static ValueEventListener listener1;
    private static ValueEventListener listener2;
    private static ValueEventListener listener3;
    private static boolean startedListeners = false;


    public static String getMyId(Context context){
        String data;
        SharedPreferences prefs = context.getSharedPreferences(SH_PREF_FIREBASE, Context.MODE_PRIVATE);
        data = (prefs.getString("uid", null));
        return data;
    }

    public static void setMyId(String myId, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SH_PREF_FIREBASE, Context.MODE_PRIVATE).edit();
        editor.putString("uid",myId);
        editor.commit();
    }

    public static void removeMyId(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SH_PREF_FIREBASE, Context.MODE_PRIVATE).edit();
        editor.remove("uid");
        editor.commit();
    }

    public static String getChatImage(Context context){
        String data;
        SharedPreferences prefs = context.getSharedPreferences(SH_PREF_FIREBASE, Context.MODE_PRIVATE);
        data = (prefs.getString("img", null));
        return data;
    }

    public static void setChatImage(String img, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SH_PREF_FIREBASE, Context.MODE_PRIVATE).edit();
        editor.putString("img",img);
        editor.commit();
    }

    public static void removeChatImage(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SH_PREF_FIREBASE, Context.MODE_PRIVATE).edit();
        editor.remove("img");
        editor.commit();
    }

    public static Firebase getMyFirebaseRef() { return myFirebaseRef; }

    public static Firebase getChatsRef() {
        return chatsRef;
    }

    public static Firebase getUsersRef() {
        return usersRef;
    }

    public static class ChatInfo {
        String idProduct;
        String titleProduct;
        String owner;
        Map<String, Object> users = new HashMap<>();

        public ChatInfo() {
            // empty default constructor, necessary for Firebase to be able to deserialize blog posts
        }

        public ChatInfo(String idProduct, String titleProduct, String owner, Map<String, Object> users) {
            this.idProduct = idProduct;
            this.titleProduct = titleProduct;
            this.owner = owner;
            this.users = users;
        }

        public String getIdProduct() {
            return idProduct;
        }

        public String getTitleProduct() {
            return titleProduct;
        }

        public Map<String, Object> getUsers(){
            return users;
        }

        public String getOwner() {return owner;}
    }

    public static class ChatOffer {
        String senderId;
        String text;
        Boolean accepted;
        int exchangeID = -1;
        Map<String, Integer> valoration = new HashMap<>();
        public ChatOffer() {
            // empty default constructor, necessary for Firebase to be able to deserialize blog posts
        }
        public String getSenderId() {
            return senderId;
        }
        public String getText() {
            return text;
        }
        public Boolean getAccepted() { return accepted; }
        public int getExchangeID() {
            return exchangeID;
        }
        public Map<String, Integer> getValoration(){
            return valoration;
        }
    }

    public static class User {
        String alias;
        String img;
        Map<String, Object> blockeds = new HashMap<>();
        Map<String, Object> chats = new HashMap<>();
        public User() {}
        public User(String alias, String img, Map<String, Object> blockeds, Map<String, Object> chats) {
            this.alias = alias;
            this.img = img;
            this.blockeds = blockeds;
            this.chats = chats;
        }
        public String getAlias() {
            return alias;
        }
        public String getImg() {
            return img;
        }
        public Map<String, Object> getBlockeds(){
            return blockeds;
        }
        public Map<String, Object> getChats(){
            return chats;
        }
    }

    public static class ChatText {
        String senderId;
        String text;
        boolean read;
        public ChatText() {
            // empty default constructor, necessary for Firebase to be able to deserialize blog posts
        }
        public String getSenderId() {
            return senderId;
        }
        public String getText() {
            return text;
        }
        public boolean getRead() { return read; }
    }

    public static void createUser(final String email, final String password, final String name, final Map<String, Object> img, final Context context) {
        myFirebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                myFirebaseRef.authWithPassword(
                        email,
                        password,
                        new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                usersRef.child(authData.getUid()).setValue
                                        (new User(name,"",new HashMap<String, Object>(),new HashMap<String, Object>()));
                                setMyId(authData.getUid(),context);
                                FirebaseUtils.getUsersRef().child(FirebaseUtils.getMyId(context)).updateChildren(img);
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError error) {
                                Log.i("Debug-Firebase","error auth user in firebase");
                            }
                        }
                );
                //setMyId(result.get("uid").toString(), context);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Log.i("Debug-Firebase", firebaseError.getMessage());
            }
        });
    }

    public static void startService(Context context){
        Intent intent = new Intent(context, FirebaseService.class);
        context.startService(intent);
    }

    public static void startListeners(final String myId, final Context context){
        if (!startedListeners) {
            Log.i("DebugListeners","Starting listeners");
            startedListeners = true;
            final NotificationController notificationController = new NotificationController();
            listener1 = FirebaseUtils.getUsersRef().child(myId).child("chats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            listener2 = FirebaseUtils.getChatsRef().child(dataSnapshot1.getKey()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    ChatInfo c = snapshot.child("info").getValue(ChatInfo.class);
                                    ArrayList<String> arrayListMessages = new ArrayList<>((int)snapshot.child("chats").getChildrenCount());
                                    for (DataSnapshot dataSnapshot1 : snapshot.child("messages").getChildren()) {
                                        FirebaseUtils.ChatText ct = dataSnapshot1.getValue(FirebaseUtils.ChatText.class);
                                        if (!ct.getRead() && !ct.getSenderId().equals(myId)) {
                                            arrayListMessages.add(ct.getText());
                                        }
                                    }
                                    String userName = "";
                                    for (Object o : c.getUsers().values()) {
                                        if (!o.toString().equals(Helpers.getActualUser(context).getAlias())){
                                            userName = o.toString();
                                        }
                                    }
                                    if (arrayListMessages.size() > 0) notificationController.displayNotification(context,c.getTitleProduct(),userName,arrayListMessages);
                                    else notificationController.cleanEntry(c.getTitleProduct(),userName);

                                    String idUser = "";
                                    for (String s : c.getUsers().keySet()) {
                                        if (!s.equals(myId)) {
                                            idUser = s;
                                        }
                                    }

                                    listener3 = FirebaseUtils.getUsersRef().child(idUser).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }
        else Log.i("DebugListeners","Not starting listeners");
    }
}
