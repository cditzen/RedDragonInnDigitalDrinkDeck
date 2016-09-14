package cs309.reddragoninndigitaldrinkdeck.Adapters;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import cs309.reddragoninndigitaldrinkdeck.DrinkMeActivities.DrawDrinkMeCardActivity;
import cs309.reddragoninndigitaldrinkdeck.GameActivity.GameInterfaceActivity;
import cs309.reddragoninndigitaldrinkdeck.PreGameActivities.SignInActivity;
import cs309.reddragoninndigitaldrinkdeck.UserData.User;

/**
 * Keeps track of player stats from Firebase
 * Created by Cory Itzen on 3/29/2015.
 */
public class PlayerAdapter {

    private static Firebase myFirebaseRef = new Firebase("https://reddragoninn.firebaseio.com/");
    private static User user = SignInActivity.getUser();

    /**
     * Updates user's changes to deltaFortitude and deltaAlcohol
     * This is used when another player applies a change to this player
     */
    public static void playerDataListener()
    {
        myFirebaseRef.child("Players/" + user.getName() + "/deltaFortitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int deltaFortitude = dataSnapshot.getValue(long.class).intValue();
                user.updateFortitude(deltaFortitude);
                myFirebaseRef.child("Players/" + user.getName() + "/deltaFortitude").removeValue();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        myFirebaseRef.child("Players/" + user.getName() + "/deltaAlcohol").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int deltaAlcohol = dataSnapshot.getValue(long.class).intValue();
                user.updateAlcohol(deltaAlcohol);
                myFirebaseRef.child("Players/" + user.getName() + "/deltaAlcohol").removeValue();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * Called from User.setFortitude(), this takes in a fortitude (that is already bounded between 0 and 20), and updates Firebase for other players
     * @param updatedFortitude
     */
    public static void setUpdatedFortitude(final int updatedFortitude)
    {
        myFirebaseRef.child("Players/" + user.getName() + "/fortitude").setValue(updatedFortitude);
    }

    /**
     * Called from user.setAlcohol(), this takes in an bounded alcohol value, and updates firebase.
     * @param updatedAlcohol
     */
    public static void setUpdatedAlcohol(final int updatedAlcohol)
    {
        myFirebaseRef.child("Players/" + user.getName() + "/alcohol").setValue(updatedAlcohol);
    }

    public static void drinkMeCardListener(Context context)
    {
        final Context c = context;
        myFirebaseRef.child("Players/" + user.getName() + "/playerState").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String playerState = (String) dataSnapshot.getValue();
                if(playerState != null) //If a player leaves the lobby, this conditional prevents the app from crashing if their lobby is deleted.
                {
                    if(playerState.equals("DrinkMe"))
                    {
                        Toast.makeText(c, "You were dealt a DrinkMe Card", Toast.LENGTH_SHORT).show();
                        myFirebaseRef.child("Players/" + user.getName() + "/playerState").setValue("standBy");
                        DrinkMeDeckAdapter.drawFromDrinkMeDeck();
                    }
                    if(playerState.equals("drawDrinkMeCard"))     //This player was the target of another player's DrinkMeCard
                    {
                        Intent intent = new Intent(c, DrawDrinkMeCardActivity.class);
                        myFirebaseRef.child("Players/" + user.getName() + "/playerState").setValue("standBy");
                        intent.putExtra("end_turn_after", false);
                        c.startActivity(intent);
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        myFirebaseRef.child("Prompts/" + user.getLobby()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String message = dataSnapshot.getValue(String.class);
                if(message != null)
                {
                    Toast.makeText(c, dataSnapshot.getValue(String.class), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * Sets a player's playerState to "drawDrinkMeCard", which causes them to start a modified DrawDrinkMeCardActivity, that prevents them from ending their turn.
     * @param playerToChangeState
     */
    public static void setPlayerToDrawDrinkMeCard(String playerToChangeState)
    {
        myFirebaseRef.child("Players/" + playerToChangeState + "/playerState").setValue("drawDrinkMeCard");
    }

    /**
     * Sets a player's playerState to "DrinkMe", adding a DrinkMe card to their personal Drink Pile
     * @param playerToChangeState
     */
    public static void selectPlayerToBuyDrinkMeCard(String playerToChangeState)
    {
        myFirebaseRef.child("Players/" + playerToChangeState + "/playerState").setValue("DrinkMe");
    }
}
