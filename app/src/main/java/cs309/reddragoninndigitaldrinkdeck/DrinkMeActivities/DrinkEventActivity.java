package cs309.reddragoninndigitaldrinkdeck.DrinkMeActivities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import cs309.reddragoninndigitaldrinkdeck.Adapters.DrinkMeDeckAdapter;
import cs309.reddragoninndigitaldrinkdeck.Dialogs.ViewPlayersDialogs;
import cs309.reddragoninndigitaldrinkdeck.DrinkMeDeck.DrinkMeCard;
import cs309.reddragoninndigitaldrinkdeck.PreGameActivities.SignInActivity;
import cs309.reddragoninndigitaldrinkdeck.R;
import cs309.reddragoninndigitaldrinkdeck.UserData.User;

/**
 * This activity is used when another player plays a DrinkEvent card.
 * The player who deals the DrinkEvent card is responsible for dealing the correct cards to each player.
 * Depending on the gameState, this class will either get the RoundOnTheHouse card, or a unique DrinkEvent card.
 * Pressing confirm applies that card's effect, and finishes the activity.
 */
public class DrinkEventActivity extends ActionBarActivity {

    private Firebase myFirebaseRef = new Firebase("https://reddragoninn.firebaseio.com/");
    private User user = SignInActivity.getUser();

    private ImageView drinkEventCardImageView;
    private static Button drinkEventButton;
    private static ArrayList<DrinkMeCard> cards;

    private static int deltaFortitude;
    private static int deltaAlcohol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_event);

        drinkEventCardImageView = (ImageView) findViewById(R.id.drinkEventImageView);     //TODO set up DrinkEventActivity.xml
        drinkEventButton = (Button) findViewById(R.id.drinkEventButton);
        cards = new ArrayList<DrinkMeCard>();

        deltaFortitude = 0;
        deltaAlcohol = 0;

        initCard();
    }

    private void initCard()
    {
        myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/gameState").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gameState = dataSnapshot.getValue(String.class);
                if(gameState.equals("roundOnTheHouse"))
                {
                    myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/roundOnTheHouse").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int cardRef = dataSnapshot.getValue(long.class).intValue();
                            DrinkMeCard card = DrinkMeDeckAdapter.convertToDrinkMeCard(cardRef);
                            addCardFromDrinkEvent(card);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
                else if(gameState.equals("drinkingContest"))
                {
                    myFirebaseRef.child("Players/" + user.getName() + "/drinkingContest").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int cardRef = dataSnapshot.getValue(long.class).intValue();
                            DrinkMeCard card = DrinkMeDeckAdapter.convertToDrinkMeCard(cardRef);
                            addCardFromDrinkEvent(card);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     *
     * @param card
     */
    private void addCardFromDrinkEvent(DrinkMeCard card)
    {
        deltaFortitude += card.getChangeFortitude(user.getRace());
        deltaAlcohol += card.getChangeAlcohol(user.getRace());
        cards.add(card);
        drinkEventButton.setText(cards.get(cards.size() - 1).getName() + ": " + deltaFortitude + " Fortitude and " + deltaAlcohol + " Alcohol");
        drinkEventCardImageView.setImageResource(cards.get(cards.size() - 1).getImage());
    }

    public void confirmDrinkEventButton(View view)
    {
        user.updateFortitude(deltaFortitude);
        user.updateAlcohol(deltaAlcohol);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drink_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_view_players:
                ViewPlayersDialogs newView = new ViewPlayersDialogs();
                newView.show(getSupportFragmentManager(), "viewPlayers");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
