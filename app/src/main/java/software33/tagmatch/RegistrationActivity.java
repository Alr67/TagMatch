package software33.tagmatch;

import android.content.Context;
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
        if (!validatePassword()) {
            Context context = getApplicationContext();
            CharSequence text = getString(R.string.registration_wrong_password);
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            EditText pass = (EditText) findViewById(R.id.registrationPassword);
            EditText user = (EditText) findViewById(R.id.registrationUsername);
            EditText mail = (EditText) findViewById(R.id.registrationMail);

            try {
                JSONObject jObject = new JSONObject();
                jObject.put("username", user.getText().toString());
                jObject.put("password", pass.getText().toString());
                jObject.put("email", mail.getText().toString());

                new TagMatchPostAsyncTask(getString(R.string.ip_server) + "/users"){
                    @Override
                    protected void onPostExecute(JSONObject jsonObject) {
                        try {
                            String error = jsonObject.get("error").toString();
                            Toast.makeText(RegistrationActivity.this, error, Toast.LENGTH_LONG).show();
                        } catch (JSONException ignored) {
                        }
                    }
                }.execute(jObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private boolean validatePassword() {
        EditText pass1 = (EditText) findViewById(R.id.registrationPassword);
        EditText pass2 = (EditText) findViewById(R.id.registrationPasswordConfirm);
        return pass1.getText().toString().equals(pass2.getText().toString());
    }
}
