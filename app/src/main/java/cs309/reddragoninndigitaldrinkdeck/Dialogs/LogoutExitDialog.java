package cs309.reddragoninndigitaldrinkdeck.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import cs309.reddragoninndigitaldrinkdeck.R;

import static android.support.v4.app.ActivityCompat.finishAffinity;

/**
 * Created by tdwelter on 4/26/2015.
 */
public class LogoutExitDialog extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_logout_exit_text)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity(getActivity());
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // User cancelled dialog
                }
            });

        // Create AlertDialog and return
        return builder.create();
    }

}
