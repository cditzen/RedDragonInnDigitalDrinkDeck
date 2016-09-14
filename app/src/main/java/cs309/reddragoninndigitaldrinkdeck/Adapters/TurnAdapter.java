package cs309.reddragoninndigitaldrinkdeck.Adapters;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import cs309.reddragoninndigitaldrinkdeck.GameActivity.GameInterfaceActivity;
import cs309.reddragoninndigitaldrinkdeck.PreGameActivities.SignInActivity;
import cs309.reddragoninndigitaldrinkdeck.UserData.User;

/**
 * Created by cditz_000 on 4/19/2015.
 */
public class TurnAdapter
{

    private static Firebase myFirebaseRef = new Firebase("https://reddragoninn.firebaseio.com/");
    private static User user = SignInActivity.getUser();

    /**
     * Increases this player's turn count by the numOfPlayers. Calls findNextPlayer.
     */
    public static void increaseTurnNumber()
    {
        final Firebase firebaseLobbyRef = myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/members/" + user.getName() + "/turn");

        firebaseLobbyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try
                {
                    int originalTurnNum = dataSnapshot.getValue(Long.class).intValue();

                    int newTurnNum = originalTurnNum + user.getIncreasePerTurn();
                    firebaseLobbyRef.setValue(newTurnNum);
                    findNextPlayer();

                }
                catch(NullPointerException e)
                {

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    /**
     * Sets the next player in order as active.
     * If the user is leaving the lobby, their lobby reference is cleared.
     * This is called either to end an Active Players' turn during gameplay, or when an Active Player is being kicked from the lobby.
     */
    public static void findNextPlayer() {
        Query queryRef = myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/members/").orderByChild("turn").limitToFirst(1);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot querySnapshot) {
                if(querySnapshot.getChildren().iterator().hasNext()) //Prevents finding a null object from Firebase if this is called after last player left lobby
                {
                    String nextPlayer = querySnapshot.getChildren().iterator().next().getKey();
                    myFirebaseRef.child("Players/" + nextPlayer + "/currentTurn").setValue(true);
                }
                if(user.isLeavingLobby())                           //When the user is leaving the lobby.
                {
                    user.setLobby("");
                    user.setIsLeavingLobby(false);                  //This is no longer needed.
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    /**
     * Deletes this player from the order of players.
     * If it is currently that player's turn, the next player is set as active
     */
    public static void deletePlayerAndEndTurn()
    {
        //if(user.getCurrentPlayersInLobby() <= 1)            //Get rid of the lobby and the deck
        System.out.println("TurnAdapter.deletePlayerAndEndTurn() getPlayers.size(): " + GameInterfaceActivity.getPlayers().size());
        if(GameInterfaceActivity.getPlayers().size() <= 1)
        {
            myFirebaseRef.child("Lobbies/" + user.getLobby()).removeValue();
            myFirebaseRef.child("Decks/" + user.getLobby()).removeValue();
            user.setLobby("");
        }
        else                                                //carefully extract the player
        {
            myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/members/" + user.getName()).removeValue();

            myFirebaseRef.child("Players/" + user.getName() + "/currentTurn").addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    user.setIsLeavingLobby(true);
                    if ((boolean) dataSnapshot.getValue())  //This conditional is fired when it is the current player's turn.
                    {
                        TurnAdapter.findNextPlayer();       //user.setLobby("") and user.setIsLeavingLobby("") are performed in a conditional in findNextPlayer()
                    }
                    else
                    {
                        user.setLobby("");
                        user.setIsLeavingLobby(false);      //This flag is no longer needed.
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }
    }


    /**
     * Called at the beginning of the game to determine how high to increment the turn counter every time a turn is ended. This number is never changed.
     */
    public static void getIncreasePerTurn()
    {
        myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/numPlayers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user.setIncreasePerTurn(dataSnapshot.getValue(long.class).intValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
}
