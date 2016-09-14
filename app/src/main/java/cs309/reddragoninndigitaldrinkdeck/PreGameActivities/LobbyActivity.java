package cs309.reddragoninndigitaldrinkdeck.PreGameActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import cs309.reddragoninndigitaldrinkdeck.Adapters.DrinkMeDeckAdapter;
import cs309.reddragoninndigitaldrinkdeck.GameActivity.GameInterfaceActivity;
import cs309.reddragoninndigitaldrinkdeck.R;
import cs309.reddragoninndigitaldrinkdeck.UserData.User;


/**
 * LobbyActivity is where all players are located between a lobby being created and a game starting.
 * Players are able to join a game while the status is "standby
 * ."
 * "determinePlayers" means that the lobby is closed and players are determining the order in which turns will be taken.
 * "startGame" is the status in which all players in the lobby will be sent to the GameInterfaceActivity.
 */
public class LobbyActivity extends Activity {

    private Firebase myFirebaseRef;

    private TextView messageTextView;
    private Button hostButton;
    private Button clientButton;
    private ListView playerListView;
    private Spinner characterSpinner;
    private User user;

    /**
     * Set to false until the player is put in the order of player turns.
     * If this is still false when the host starts the game, this player is removed from the game and sent back to the SignInActivity.
     */
    private boolean isInOrder;

    private ArrayList<String> playerList = new ArrayList<>();
    private ArrayAdapter<String> playerNameAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        //Initialize
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://reddragoninn.firebaseio.com/");
        messageTextView = (TextView) findViewById(R.id.messageTextView);
        hostButton = (Button) findViewById(R.id.hostButton);
        clientButton = (Button) findViewById(R.id.clientButton);
        isInOrder = false;

        //Character Spinner
        characterSpinner = (Spinner) findViewById(R.id.characterSpinner);


        //Set up screen
        user = SignInActivity.getUser();
        messageTextView.setText(user.getLobby());
        if(user.isHost())
            hostButton.setVisibility(View.VISIBLE);

        //Player List
        playerListView = (ListView) findViewById(R.id.playerListView);
        playerNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playerList);
        playerListView.setAdapter(playerNameAdapter);

        liveUpdatePlayerList();//Constantly updates the players in the lobby.
        determineStateListener();
    }

    /**
     * Updates the list of players as they are added. Removes players named "turn", due to funky data architecture. This could probably be fixed.
     */
    private void liveUpdatePlayerList()
    {
        myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot child : dataSnapshot.getChildren())
                {
                    playerNameAdapter.add(child.getKey());
                    playerNameAdapter.remove("turn");//TODO
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    playerNameAdapter.remove(child.getKey());
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }



    /**
     * Used by the host, this determines the state of the game.
     * "standby" means the lobby is open and accepting players.
     * "determinePlayers", as the name suggests, means the lobby is now private and players are in the process of determining the order of their turns. The deck is finalized, shuffled, and stored into Firebase here.
     * "startGame" is the signal that the game has started. All players should be in GameInterfaceLobby.
     * @param view
     */
    public void changeGameState(View view)
    {
        final Firebase firebaseLobbyRef = myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby());

        firebaseLobbyRef.child("gameState").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gameState = (String) dataSnapshot.getValue();

                if (gameState.equals("standby")) {
                    isInOrder = true;
                    firebaseLobbyRef.child("gameState").setValue("determinePlayers");
                    firebaseLobbyRef.child("playerToAppend").setValue(user.getName());
                    myFirebaseRef.child("Players/" + user.getName() + "/currentTurn").setValue(true);
                    createAndShuffleCards();
                    DrinkMeDeckAdapter.drawFromDrinkMeDeck();
                    hostButton.setText("Players ready, Start Game");

                } else if (gameState.equals("determinePlayers"))//TODO lock this until all players are in the game or kick unjoined players from the lobby.
                {
                    firebaseLobbyRef.child("gameState").setValue("startGame");
                    startGame();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * Creates a deck of cards, shuffles them, and places them into Firebase.
     * This is done by the host once the gameState is set to "determinePlayers."
     */
    private void createAndShuffleCards()
    {
        ArrayList<Integer> deck = new ArrayList<Integer>();
        for(int i = 0; i < 30; i++)
        {
            deck.add(i);
        }

        Collections.shuffle(deck); //TODO Debug

        myFirebaseRef.child("Decks/" + user.getLobby()).setValue(deck);
    }

    /**
     * Listens for changes in the gameState. This is used by clients.
     * Descriptions of the states are commented in changeGameState.
     */
    private void determineStateListener()
    {
        Firebase firebaseLobbyRef = myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby());

        firebaseLobbyRef.child("gameState").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gameState = (String) dataSnapshot.getValue();
                if(gameState != null) //If a player leaves the lobby, this conditional prevents the app from crashing if their lobby is deleted.
                {
                    if (gameState.equals("determinePlayers") && !user.isHost()) {
                        clientButton.setVisibility(View.VISIBLE);
                    } else if (gameState.equals("startGame") && !user.isHost()) {
                        startGame();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        firebaseLobbyRef.child("playerToAppend").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String playerToAppend = (String) dataSnapshot.getValue();
                clientButton.setText("Press this if you sit to the left of " + playerToAppend);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * Used when determining Player order. Pressing this adds that client to the order of the players, and hides their player order button.
     * @param view
     */
    public void addToPlayerOrder(View view)
    {
        final Firebase firebaseLobbyRef = myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby());

        firebaseLobbyRef.child("numPlayers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Long currentPlayer = dataSnapshot.getValue(Long.class);
                myFirebaseRef.child("Players/" + user.getName() + "/currentTurn").setValue(false);
                firebaseLobbyRef.child("members/" + user.getName() + "/turn").setValue(currentPlayer);
                firebaseLobbyRef.child("numPlayers").setValue(currentPlayer + 1);
                firebaseLobbyRef.child("playerToAppend").setValue(user.getName());
                DrinkMeDeckAdapter.drawFromDrinkMeDeck();                                   //This players draws the next card on the drink deck.
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        clientButton.setVisibility(View.INVISIBLE);
        isInOrder = true;   //The player has been put in order, they are ready to start the game.
    }


    /**
     * Alcohol and Fortitude are in-game stats.
     * playerState is used as a conditional to let a player know if they need to draw a card.
     */
    private void startGame()
    {
        if(isInOrder)
        {
            myFirebaseRef.child("Players/" + user.getName() + "/fortitude").setValue(20);
            myFirebaseRef.child("Players/" + user.getName() + "/alcohol").setValue(0);
            String userCharacter = (String) characterSpinner.getItemAtPosition(characterSpinner.getSelectedItemPosition());
            user.setCharacter(userCharacter);
            myFirebaseRef.child("Players/" + user.getName() + "/character").setValue(userCharacter);
            myFirebaseRef.child("Players/" + user.getName() + "/playerState").setValue("standBy");
            Intent intent = new Intent(this, GameInterfaceActivity.class);
            startActivity(intent);
            this.finish();
        }
        else                //They were not placed in the game
        {
            myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/members/" + user.getName()).removeValue();
            this.finish();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        finish();

    }
}