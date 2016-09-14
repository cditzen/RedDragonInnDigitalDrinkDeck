package cs309.reddragoninndigitaldrinkdeck.ListViews;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cs309.reddragoninndigitaldrinkdeck.R;

/**
 * Created by cditz_000 on 4/19/2015.
 */
public class PlayerListAdapter extends ArrayAdapter<Player>{


    public PlayerListAdapter(Context context, ArrayList<Player> players)
    {
        super(context, R.layout.player_listview_layout, players);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater theInflater = LayoutInflater.from(getContext());

        View theView = theInflater.inflate(R.layout.player_listview_layout, parent, false);

        TextView playerNameTextView = (TextView) theView.findViewById(R.id.playerNameTextView);
        TextView fortitudeTextView = (TextView) theView.findViewById(R.id.fortitudeTextView);
        TextView alcoholTextView = (TextView) theView.findViewById(R.id.alcoholTextView);
        TextView drinkMeTextView = (TextView) theView.findViewById(R.id.drinkMeTextView);
        ImageView portraitImageView = (ImageView) theView.findViewById(R.id.playerImageView);

        String playerName = getItem(position).getName();        //Gets the player's name and sets its typeface to bold if it is their turn.
        boolean isTurn = getItem(position).isTurn();
        playerNameTextView.setText(playerName);
        if(isTurn == true)
        {
            playerNameTextView.setTypeface(Typeface.DEFAULT_BOLD);
        }
        else
        {
            playerNameTextView.setTypeface(Typeface.DEFAULT);
        }

        String fortitude = Integer.toString(getItem(position).getFortitude());
        fortitudeTextView.setText(fortitude);

        String alcohol = Integer.toString(getItem(position).getAlcohol());
        alcoholTextView.setText(alcohol);

        String numCards = Integer.toString(getItem(position).getNumCards());
        drinkMeTextView.setText("(" + numCards + ")");

        int portrait = (getItem(position).getPortrait());
        portraitImageView.setImageResource(portrait);

        return theView;


    }
}
