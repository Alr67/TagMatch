package software33.tagmatch.Advertisement;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by Cristina on 03/04/2016.
 */
public class CustomSpinnerOnItemSelectedListener  implements AdapterView.OnItemSelectedListener{

    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {

        Toast.makeText(parent.getContext(),
                "On Item Select : \n" + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

}
