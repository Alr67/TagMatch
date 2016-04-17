package software33.tagmatch.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.Toast;

import software33.tagmatch.Advertisement.NewAdvertisement;
import software33.tagmatch.Advertisement.ViewAdvert;
import software33.tagmatch.Chat.MainChatActivity;
import software33.tagmatch.MainActivity;
import software33.tagmatch.R;
import software33.tagmatch.Users.ViewProfile;

/**
 * Created by Cristina on 12/04/2016.
 */
public final class NavigationController {

    
    public static Boolean onItemSelected(Integer id, Activity parent) {

        Log.i("DEBUG","item selected");
        if (id == R.id.nav_viewUser) {
            Intent intent = new Intent(parent, ViewProfile.class);
            parent.startActivity(intent);
            parent.finish();
        } else if (id == R.id.nav_xats) {
            Intent intent = new Intent(parent, MainChatActivity.class);
            parent.startActivity(intent);
            parent.finish();
        } else if (id == R.id.nav_main) {
            Intent intent = new Intent(parent, MainActivity.class);
            parent.startActivity(intent);
            parent.finish();
        } else if (id == R.id.nav_newAdvert) {
            Intent intent = new Intent(parent, NewAdvertisement.class);
            parent.startActivity(intent);
            parent.finish();
        } else if (id == R.id.nav_viewAdvert) {
            Intent intent = new Intent(parent, ViewAdvert.class);
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.TAG_BUNDLE_IDVIEWADVERTISEMENT,Constants.idTEST);
            intent.putExtras(bundle);
            parent.startActivity(intent);
            parent.finish();
        } else {
            Toast.makeText(parent.getApplicationContext(), "TODO", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) parent.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
