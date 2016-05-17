package software33.tagmatch.Utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.Toast;

import software33.tagmatch.AdCards.Home;
import software33.tagmatch.Chat.MainChatActivity;
import software33.tagmatch.Login_Register.Login;
import software33.tagmatch.R;
import software33.tagmatch.Users.MyAdverts;
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
            Intent intent = new Intent(parent, Home.class);
            parent.startActivity(intent);
            parent.finish();
        }  else if (id == R.id.nav_my_adverts) {
            Intent intent = new Intent(parent, MyAdverts.class);
            parent.startActivity(intent);
            parent.finish();
        }  else if (id == R.id.nav_logout) {
            Helpers.logout(parent.getApplicationContext());
            Intent intent3 = new Intent(parent.getApplicationContext(), Login.class);
            parent.startActivity(intent3);
            parent.finish();
        }  else if (id == R.id.nav_discovery_tagmatch) {
            Intent intent = new Intent(parent.getApplicationContext(), Home.class);
            intent.putExtra("previousActivity", "disc");
            String url = Constants.IP_SERVER + "/users/discovery";
            intent.putExtra("url", url);
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
