package software33.tagmatch.Chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public abstract class FirebaseUtils {

    private static Firebase myFirebaseRef = new Firebase("https://torrid-torch-42.firebaseio.com/");
    private static Firebase chatsRef = myFirebaseRef.child("chats");
    private static Firebase usersRef = myFirebaseRef.child("users");

    private static final String SH_PREF_FIREBASE = "Firebase_pref";


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

    public static Firebase getChatsRef() {
        return chatsRef;
    }

    public static Firebase getUsersRef() {
        return usersRef;
    }

    public static class ChatInfo {
        String idProduct;
        Map<String, Object> users = new HashMap<>();

        public ChatInfo() {
            // empty default constructor, necessary for Firebase to be able to deserialize blog posts
        }

        public ChatInfo(String idProduct, Map<String, Object> users) {
            this.idProduct = idProduct;
            this.users = users;
        }

        public String getIdProduct() {
            return idProduct;
        }

        public Map<String, Object> getUsers(){
            return users;
        }
    }

}
