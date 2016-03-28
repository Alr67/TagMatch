package software33.tagmatch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void validateUser(View view) {

        String user = ((EditText) findViewById(R.id.registrationUsername)).getText().toString();
        String pass = ((EditText) findViewById(R.id.registrationPassword)).getText().toString();
        String email = ((EditText) findViewById(R.id.registrationMail)).getText().toString();

        if(user.isEmpty()) {
            showError(getString(R.string.registration_wrong_username_void));
            return;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showError(getString(R.string.registration_wrong_mail_match));
            return;
        }

        if (!validatePassword()) {
            return;
        }

        try {
            JSONObject jObject = new JSONObject();
            jObject.put("username", user);
            jObject.put("password", pass);
            jObject.put("email", email);

            boolean err = false;

            new TagMatchPostAsyncTask(getString(R.string.ip_server) + "/users"){
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        String error = jsonObject.get("error").toString();
                        showError(error);
                    } catch (JSONException ignored) {
                        continueRegistration();
                    }
                }
            }.execute(jObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void backToLogin(View view){
        //Intent act = new Intent(this, )
    }

    private void continueRegistration(){
        EditText user = (EditText) findViewById(R.id.registrationUsername);
        Intent act = new Intent(this, RegistrationActivity2.class);
        act.putExtra("username", user.getText().toString());
        startActivity(act);
    }

    private boolean validatePassword() {
        String pass1 = ((EditText) findViewById(R.id.registrationPassword)).getText().toString();
        String pass2 = ((EditText) findViewById(R.id.registrationPasswordConfirm)).getText().toString();
        if(pass1.isEmpty() || pass2.isEmpty()){
            showError(getString(R.string.registration_wrong_mail_match));
            return false;
        }
        if(!pass1.equals(pass2)){
            showError(getString(R.string.registration_wrong_password_missmatch));
            return false;
        }
        return true;
    }

    private void showError (String msg){
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        return;
    }


}
