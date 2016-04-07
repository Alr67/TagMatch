package software33.tagmatch.Advertisement;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import software33.tagmatch.Utils.Constants;

/**
 * Created by Cristina on 03/04/2016.
 */
public class CustomSpinnerOnItemSelectedListener  implements AdapterView.OnItemSelectedListener{

    private NewAdvertisement parentActivity;

    public CustomSpinnerOnItemSelectedListener(NewAdvertisement activity) {
        parentActivity = activity;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {

        Toast.makeText(parent.getContext(),
                "On Item Select : \n" + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_LONG).show();
        String newItem = parent.getItemAtPosition(pos).toString();
        if(newItem.equals(Constants.typeSell) || newItem.equals(Constants.typeExchange) || newItem.equals(Constants.typeGift)) {
            parentActivity.onTypeSpinnerChanged(newItem);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

}
