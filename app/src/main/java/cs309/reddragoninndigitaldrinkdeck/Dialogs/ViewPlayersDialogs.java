package cs309.reddragoninndigitaldrinkdeck.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.firebase.client.Firebase;

import java.util.ArrayList;

import cs309.reddragoninndigitaldrinkdeck.GameActivity.GameInterfaceActivity;
import cs309.reddragoninndigitaldrinkdeck.UserData.User;

/**
 * Created by Scott on 4/11/2015.
 */
public class ViewPlayersDialogs extends DialogFragment
{
    private static Firebase myFirebaseRef;
    private static AlertDialog mainDialog;
    private static User user;

    private static ArrayList<String> playerList = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Firebase.setAndroidContext(this.getActivity());
        myFirebaseRef = new Firebase("https://reddragoninn.firebaseio.com/");
        builder.setTitle("Players");
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Can start new intent here
            }
        });
        builder = populateListViewing(builder);
        // Create the AlertDialog object and return it
        mainDialog = builder.create();
        return mainDialog;
    }

    private AlertDialog.Builder populateListViewing(AlertDialog.Builder builder)
    {
        playerList = GameInterfaceActivity.getPlayerList();
        String[] playerData = new String[playerList.size()];
        for(int i=0; i<playerList.size(); i++)
        {
            playerData[i] = playerList.get(i);
        }
        builder.setItems(playerData, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Could put something here, what happens when a player is selected
            }
        });
        return builder;
    }
}
