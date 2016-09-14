package cs309.reddragoninndigitaldrinkdeck.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.firebase.client.Firebase;

import cs309.reddragoninndigitaldrinkdeck.AnytimeCardActivities.AnytimeBuyDrinkActivity;
import cs309.reddragoninndigitaldrinkdeck.AnytimeCardActivities.AnytimeDrawCardActivity;
import cs309.reddragoninndigitaldrinkdeck.AnytimeCardActivities.ChangeStatActivity;
import cs309.reddragoninndigitaldrinkdeck.R;
import cs309.reddragoninndigitaldrinkdeck.UserData.User;

/**
 * Created by Scott on 4/11/2015.
 */
public class AnytimeCardDialog extends DialogFragment
{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Firebase.setAndroidContext(this.getActivity());

        builder.setTitle("What does your Anytime Card do?").setItems(R.array.anytime_card_options, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(which == 0)
                {
                    Intent intent = new Intent(builder.getContext(), ChangeStatActivity.class);
                    startActivity(intent);
                }
                if(which == 1)
                {
                    Intent intent = new Intent(builder.getContext(), AnytimeBuyDrinkActivity.class);
                    startActivity(intent);
                }
                if(which == 2)
                {
                    Intent intent = new Intent(builder.getContext(), AnytimeDrawCardActivity.class);
                    startActivity(intent);
                }
            }
        });

        return builder.create();
    }

}
