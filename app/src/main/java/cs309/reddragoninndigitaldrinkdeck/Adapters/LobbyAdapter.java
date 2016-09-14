package cs309.reddragoninndigitaldrinkdeck.Adapters;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import cs309.reddragoninndigitaldrinkdeck.GameActivity.GameInterfaceActivity;
import cs309.reddragoninndigitaldrinkdeck.ListViews.Player;

/**
 * Created by cditz_000 on 4/19/2015.
 */
public class LobbyAdapter {
    private static Firebase myFirebaseRef = new Firebase("https://reddragoninn.firebaseio.com/");

    public static void playerWatcher(final String playerName)
    {
        myFirebaseRef.child("Players/" + playerName + "/alcohol").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try
                {
                    Player updatedPlayer = GameInterfaceActivity.getPlayer(playerName);
                    updatedPlayer.setAlcohol(dataSnapshot.getValue(long.class).intValue());
                    GameInterfaceActivity.updatePlayerList();
                }
                catch(NullPointerException e)
                {

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        myFirebaseRef.child("Players/" + playerName + "/fortitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try
                {
                    Player updatedPlayer = GameInterfaceActivity.getPlayer(playerName);
                    updatedPlayer.setFortitude(dataSnapshot.getValue(long.class).intValue());
                    GameInterfaceActivity.updatePlayerList();
                }
                catch(NullPointerException e)
                {

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        myFirebaseRef.child("Players/" + playerName + "/currentTurn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try
                {
                    Player updatedPlayer = GameInterfaceActivity.getPlayer(playerName);
                    updatedPlayer.setIsTurn(dataSnapshot.getValue(boolean.class));
                    GameInterfaceActivity.updatePlayerList();
                }
                catch(NullPointerException e)
                {

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        myFirebaseRef.child("Players/" + playerName + "/character").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try
                {
                    Player updatedPlayer = GameInterfaceActivity.getPlayer(playerName);
                    updatedPlayer.setCharacter(dataSnapshot.getValue(String.class));
                }
                catch(NullPointerException e)
                {

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        myFirebaseRef.child("Players/" + playerName + "/numDrinkMeCards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try
                {
                    Player updatedPlayer = GameInterfaceActivity.getPlayer(playerName);
                    updatedPlayer.setNumCards(dataSnapshot.getValue(long.class).intValue());
                    GameInterfaceActivity.updatePlayerList();
                }
                catch(NullPointerException e)
                {

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
