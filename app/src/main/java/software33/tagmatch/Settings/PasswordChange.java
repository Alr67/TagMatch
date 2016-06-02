package software33.tagmatch.Settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchPutAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;
import software33.tagmatch.Utils.NavigationController;

public class PasswordChange extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_change);
        setSupportActionBar(toolbar);
        setTitle(R.string.changing_title);

        //para que no se abra el teclado al entrar en la activity
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    protected void validatePassword(View view) {
        String oldPassword = ((TextView) findViewById(R.id._change_password_old_password)).getText().toString();
        final String newPassword = ((TextView) findViewById(R.id._change_password_new_password)).getText().toString();
        String repitedPassword = ((TextView) findViewById(R.id._change_password_repite_new_password)).getText().toString();

        if(oldPassword.equals("") || newPassword.equals("") || repitedPassword.equals(""))
            Toast.makeText(getApplicationContext(), getString(R.string.change_password_error_password_empty), Toast.LENGTH_LONG).show();
        else if (!Helpers.getActualUser(this).getPassword().equals(oldPassword))
            Toast.makeText(getApplicationContext(), getString(R.string.change_password_error_incorrect_password), Toast.LENGTH_LONG).show();
        else if(!newPassword.equals(repitedPassword))
            Toast.makeText(getApplicationContext(), getString(R.string.change_password_error_password_missmatch), Toast.LENGTH_LONG).show();
        else{
            try{
                JSONObject jObject = new JSONObject();
                jObject.put("password", newPassword);
                new TagMatchPutAsyncTask(Constants.IP_SERVER + "/users", this) {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        mDialog = new ProgressDialog(PasswordChange.this);
                        mDialog.setMessage(getString(R.string.changing_pass));
                        mDialog.show();
                    }
                    @Override
                    protected void onPostExecute(JSONObject jsonObject) {
                        try {
                            if(jsonObject.has("error")) {
                                String error = jsonObject.get("error").toString();
                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                            } else
                                Helpers.setPersonalData(Helpers.getActualUser(getApplicationContext()).getAlias(), newPassword, getApplicationContext());
                                Toast.makeText(getApplicationContext(), getString(R.string.change_password_ok), Toast.LENGTH_LONG).show();
                        } catch (JSONException ignored) {}
                    }
                }.execute(jObject);
            } catch (Exception ignored) {}
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return  NavigationController.onItemSelected(item.getItemId(),this);
    }
}
