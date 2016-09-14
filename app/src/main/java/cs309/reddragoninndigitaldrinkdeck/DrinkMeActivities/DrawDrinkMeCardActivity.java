package cs309.reddragoninndigitaldrinkdeck.DrinkMeActivities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.firebase.client.Firebase;
import java.util.ArrayList;

import cs309.reddragoninndigitaldrinkdeck.Adapters.DrinkMeDeckAdapter;
import cs309.reddragoninndigitaldrinkdeck.DrinkMeDeck.DrinkMeCard;
import cs309.reddragoninndigitaldrinkdeck.GameActivity.GameInterfaceActivity;
import cs309.reddragoninndigitaldrinkdeck.PreGameActivities.SignInActivity;
import cs309.reddragoninndigitaldrinkdeck.R;
import cs309.reddragoninndigitaldrinkdeck.UserData.User;


public class DrawDrinkMeCardActivity extends Activity {

    private Firebase myFirebaseRef = new Firebase("https://reddragoninn.firebaseio.com/");
    private static User user = SignInActivity.getUser();

    private static ImageView cardImageView;
    private static Button activeConfirmDrinkMeButton;
    private static Button eventDrinkMeButton;
    private static ArrayList<DrinkMeCard> cards;

    private static int deltaFortitude;
    private static int deltaAlcohol;
    /**
     * endTurnAfterActivity is initally set to true.
     * If this is set to true when .finish() is called, the player's turn will be ended.
     * If this activity is started from the result of an Anytime Card, endTurnAfterActivity should be set to false to prevent the turn from cycling.
     * */
    private static boolean endTurnAfterActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_me_card);

        cardImageView = (ImageView) findViewById(R.id.cardImageView);
        activeConfirmDrinkMeButton = (Button) findViewById(R.id.activePlayerDrinkMeButton);
        eventDrinkMeButton = (Button) findViewById(R.id.eventDrinkMeButton);
        cards = new ArrayList<DrinkMeCard>();

        deltaFortitude = 0;
        deltaAlcohol = 0;

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            endTurnAfterActivity = extras.getBoolean("end_turn_after");
        }

        else
            endTurnAfterActivity = true;

        initCard();
    }

    /**
     * Checks the gameState from Firebase, and inits cards based on that.
     * If it is "startGame" (the default setting), the player just started this activity as a part of their turn.
     * If it is "roundOnTheHouse", draw the top card from Firebase. Give the effects to each player.
     * If it is "drinkingContest", host takes cards from the deck equal to number of players, and gives one card to each player...
     */
    private void initCard()
    {
        if (user.getNumCards() > 0)
        {
            DrinkMeCard card = getTopCardFromUserCards();
            cards.add(card);
            cardImageView.setImageResource(cards.get(cards.size() - 1).getImage());
            activeConfirmDrinkMeButton.setText(cards.get(cards.size() - 1).getName() + "\n" + cards.get(cards.size() - 1).getChangeFortitude(user.getRace()) + " Fortitude and " + cards.get(cards.size() - 1).getChangeAlcohol(user.getRace()) + " Alcohol");
        }
        else
        {
            activeConfirmDrinkMeButton.setText("Empty DrinkMe Pile");
        }
    }

    /**
     * Draws the top card from the user's DrinkMe Pile and returns it as a DrinkMeCard class. It also updates the total alcohol and fortitude
     */
    private DrinkMeCard getTopCardFromUserCards()
    {
        int cardRef = user.getTopCardRef();
        DrinkMeCard card = DrinkMeDeckAdapter.convertToDrinkMeCard(cardRef);
        deltaFortitude += card.getChangeFortitude(user.getRace());
        deltaAlcohol += card.getChangeAlcohol(user.getRace());
        return card;
    }

    /**
     * Adds a DrinkMeCard to the top of the cards drawing. Applies fortitude and alcohol change.
     * Should only be called when one card is being added and the state is being switched into eventDrinkMe
     * @param card
     */
    public static void addCardFromDrinkEvent(DrinkMeCard card)
    {
        deltaFortitude += card.getChangeFortitude(user.getRace());
        deltaAlcohol += card.getChangeAlcohol(user.getRace());
        cards.add(card);
        eventDrinkMeButton.setText(cards.get(cards.size() - 1).getName() + ": " + deltaFortitude + " Fortitude and " + deltaAlcohol + " Alcohol");
        cardImageView.setImageResource(cards.get(cards.size() - 1).getImage());
        activeConfirmDrinkMeButton.setVisibility(View.INVISIBLE);
        eventDrinkMeButton.setVisibility(View.VISIBLE);
    }



    /**
     * Check if there are cards in the user's DrinkMe. This
     * Iterates through the cards as the user presses the chaser button.
     * On the last card, this applies the change, ends that player's turn, and returns to the GameInterfaceActivity.
     * If endTurnAfterActivity was set to false, the player's turn won't end after
     * @param view
     */
    public void activePlayerConfirmButton(View view)
    {
        if(cards.size() == 0) //There weren't any cards to draw from, end the turn. This should only be checked at the start of the Active user's turn. After this, this conditional shouldn't go off (I think).
        {
            endTurnAfterActivity();
            finish();
        }
        else if(cards.get(cards.size() - 1).isRoundOnTheHouse())    //The last card drawn is a RoundOnTheHouse card.
        {
            DrinkMeDeckAdapter.dealRoundOnTheHouseCard();
            //TODO set image
        }
        else if(cards.get(cards.size() - 1).isDrinkingContest())    //Last card drawn is a DrinkingContest
        {
            DrinkMeDeckAdapter.dealDrinkingContestCards();
        }
        else if((cards.get(cards.size() - 1).isChaser() == false || user.getNumCards() < 1)) //If the last card the user has drawn is not a chaser or the user has no more cards to draw, stop drawing cards, apply the effects, and end the activty.
        {
            user.updateFortitude(deltaFortitude);
            user.updateAlcohol(deltaAlcohol);
            endTurnAfterActivity();
            finish();
        }
        else   //Card is a chaser, and there is at least one more card to draw.
        {
            cards.add(getTopCardFromUserCards()); //This applies fortitude and alcohol change
            //TODO Find a better way to display both the card information and the sum of the total change
            activeConfirmDrinkMeButton.setText(cards.get(cards.size() - 1).getName() + ": " + deltaFortitude + " Fortitude and " + deltaAlcohol + " Alcohol");
            cardImageView.setImageResource(cards.get(cards.size() - 1).getImage());
        }
    }

    /**
     * EventDrinkMeButton replaces ActivePlayerButton.
     * When this button is pressed, the total stats from the round are applied and the game is returned to gameInProgress.
     * @param view
     */
    public void eventDrinkMeButton(View view)
    {
        myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/gameState").setValue("gameInProgress");
        user.updateFortitude(deltaFortitude);
        user.updateAlcohol(deltaAlcohol);
        endTurnAfterActivity();
        finish();
    }

    private void endTurnAfterActivity()
    {
        if(endTurnAfterActivity)
        {
            GameInterfaceActivity.endTurn();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
        }

        return true;
    }
}
