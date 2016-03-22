package software33.tagmatch;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void validateUser(View view){
        String validation = "";
        if(!validateMail()){
            validation = validation + getString(R.string.registration_wrong_mail);
        }
        if(!validateUsername()){
            if(!validation.equals(""))
                validation = validation + "\n";
            validation = validation + getString(R.string.registration_wrong_username);
        }
        if(!validatePassword()){
            if(!validation.equals(""))
                validation = validation + "\n";
            validation = validation + getString(R.string.registration_wrong_password);
        }
        if(!validation.equals("")){
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, validation, duration);
            toast.show();
        } else {

        }
    }

    private boolean validateMail(){
        return true;
    }

    private boolean validateUsername(){
        return true;
    }

    private boolean validatePassword(){
        EditText pass1 = (EditText) findViewById(R.id.registrationPassword);
        EditText pass2 = (EditText) findViewById(R.id.registrationPasswordConfirm);
        return pass1.getText().toString().equals(pass2.getText().toString());
    }
}
