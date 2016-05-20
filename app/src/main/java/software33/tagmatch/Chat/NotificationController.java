package software33.tagmatch.Chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.util.Pair;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import software33.tagmatch.R;

public class NotificationController {

    private int idNotification = 100;
    private HashMap<Pair<String, String>, Object[]> messagesForEveryChat = new HashMap<>();

    public void cleanEntry(String titleProduct, String author){
        messagesForEveryChat.remove(new Pair<>(titleProduct, author));
    }

    public void displayNotification(Context context, String titleProduct, String author, ArrayList<String> messages) {
        Log.i("Start", "notification");

   /* Invoking the default notification service */
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(context);

        mBuilder.setContentTitle("New Message");
        mBuilder.setContentText("You've received new message.");
        mBuilder.setTicker("New Message Alert!");
        mBuilder.setSmallIcon(R.drawable.image0);

        /* Add Big View Specific Configuration */

        Object[] texts = messages.toArray();

        messagesForEveryChat.put(new Pair<>(titleProduct, author),texts);

        // Moves events into the big view
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        int numMessages = 0;

        for (Pair p : messagesForEveryChat.keySet()){
            if (messagesForEveryChat.get(p).length != 0) {
                String titleProduct2 = p.first.toString();
                String author2 = p.second.toString();
                inboxStyle.setBigContentTitle("Tagmatch:");
                Spannable sb = new SpannableString(titleProduct2 + " / " + author2 + ": ");
                sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, titleProduct2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                sb.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), titleProduct2.length() + 3, titleProduct2.length() + 3 + author2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                inboxStyle.addLine(sb);

                for (Object o : messagesForEveryChat.get(p)){
                    inboxStyle.addLine("    "+o.toString());
                    ++numMessages;
                }
            }
        }



        /* Increase notification number every time a new notification arrives */
        mBuilder.setNumber(numMessages);

        mBuilder.setStyle(inboxStyle);

   /* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(context, MainChatActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainChatActivity.class);

   /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

   /* notificationID allows you to update the notification later on. */
        int notificationID = idNotification;
        mNotificationManager.notify(notificationID, mBuilder.build());
    }
}
