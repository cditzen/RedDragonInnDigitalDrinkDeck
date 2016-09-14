package cs309.reddragoninndigitaldrinkdeck.AnytimeCardActivities;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.firebase.client.Firebase;

import java.util.ArrayList;

import cs309.reddragoninndigitaldrinkdeck.GameActivity.GameInterfaceActivity;
import cs309.reddragoninndigitaldrinkdeck.PreGameActivities.SignInActivity;
import cs309.reddragoninndigitaldrinkdeck.R;
import cs309.reddragoninndigitaldrinkdeck.UserData.User;


public class ChangeStatActivity extends Activity {

    private Firebase myFirebaseRef;

    private User user;
    private ListView anytimeCardPlayerListView;

    private ArrayList<String> playerList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private Spinner fortitudeSpinner;
    private Spinner alcoholSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anytime_card);

        //Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://reddragoninn.firebaseio.com/");

        user = SignInActivity.getUser();
        anytimeCardPlayerListView = (ListView) findViewById(R.id.anytimeCardPlayerListView);

        playerList = (ArrayList) GameInterfaceActivity.getPlayerList().clone();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, playerList);
        anytimeCardPlayerListView.setAdapter(adapter);

        fortitudeSpinner = (Spinner) findViewById(R.id.fortitudeSpinner);
        fortitudeSpinner.setSelection(4);

        alcoholSpinner = (Spinner) findViewById(R.id.alcoholSpinner);
        alcoholSpinner.setSelection(4);
    }

    public void confirmAnytimeCardChange(View view)
    {
        SparseBooleanArray checkedPositions = anytimeCardPlayerListView.getCheckedItemPositions();
        for(int i = 0; i < playerList.size(); i++)
        {
            if(checkedPositions.get(i) == true)
            {
                int deltaFortitude = (fortitudeSpinner.getSelectedItemPosition() - 4 )* -1;
                int deltaAlcohol = (alcoholSpinner.getSelectedItemPosition() - 4) * -1;

                if(deltaFortitude != 0)
                {
                    myFirebaseRef.child("Players/" + anytimeCardPlayerListView.getItemAtPosition(i) + "/deltaFortitude").setValue(deltaFortitude);
                }
                if(deltaAlcohol != 0)
                {
                    myFirebaseRef.child("Players/" + anytimeCardPlayerListView.getItemAtPosition(i) + "/deltaAlcohol").setValue(deltaAlcohol);
                }
            }
        }
        finish();
    }
}