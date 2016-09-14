package cs309.reddragoninndigitaldrinkdeck.GameActivity;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import cs309.reddragoninndigitaldrinkdeck.Adapters.LobbyAdapter;
import cs309.reddragoninndigitaldrinkdeck.Adapters.PlayerAdapter;
import cs309.reddragoninndigitaldrinkdeck.Adapters.TurnAdapter;
import cs309.reddragoninndigitaldrinkdeck.Dialogs.AnytimeCardDialog;
import cs309.reddragoninndigitaldrinkdeck.Dialogs.LogoutExitDialog;
import cs309.reddragoninndigitaldrinkdeck.DrinkMeActivities.DealDrinkMeCardActivity;
import cs309.reddragoninndigitaldrinkdeck.DrinkMeActivities.DrinkEventActivity;
import cs309.reddragoninndigitaldrinkdeck.ListViews.Player;
import cs309.reddragoninndigitaldrinkdeck.ListViews.PlayerListAdapter;
import cs309.reddragoninndigitaldrinkdeck.PreGameActivities.SignInActivity;
import cs309.reddragoninndigitaldrinkdeck.R;
import cs309.reddragoninndigitaldrinkdeck.UserData.User;


public class GameInterfaceActivity extends ActionBarActivity {

    private static Firebase myFirebaseRef;

    private static User user;

    private static boolean isTurn;                      //Set to true at the beginning of a turn, and false at the end.
    private static TextView nameTextView;               //Name banner, containing the name and number of cards TODO Maybe separate these two
    private static TextView currentFortitudeTextView;   //Current Fortitude of the player
    private static TextView currentAlcoholTextView;     //Current Alcohol of the player
    private static TextView gameStatusTextView;           //Hidden textView, displayed at game-over
    private ListView historyListView;                   //listview for history TODO discard
    private static Button dealCardButton;               //Active player button
    private static boolean isInGame;                    //Set to true at the beginning of the game, and false at game-over
    private static boolean wonGame;                     //Used to properly delete the lobby if this player won the game.

    private static ArrayList<String> listofPlayerNames = new ArrayList<>();
    private ArrayList<String> historyLog = new ArrayList<>();
    //private ArrayAdapter<String> adapter;
    private static PlayerListAdapter playerListAdapter;
    private static ArrayList<Player> playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_interface);

        //initialize Firebase
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://reddragoninn.firebaseio.com/");

        //Setup User
        user = SignInActivity.getUser();
        isTurn = user.isHost();                         //If they are the host, the game starts out as their turn. Else, it is not their turn.
        TurnAdapter.getIncreasePerTurn();               //Communicates with the user how much they increase per turn.
        isInGame = true;                                //This conditional is checked for every button. This is set to false from applyGameOver(). This prevents them from accessing the AnytimeCardButton, as well as DrinkMeCard button.
        wonGame = false;

        //initialize GUI
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        currentFortitudeTextView = (TextView) findViewById(R.id.currentFortitudeTextView);
        currentAlcoholTextView = (TextView) findViewById(R.id.currentAlcoholTextView);
        gameStatusTextView = (TextView) findViewById(R.id.gameStatusTextView);
        historyListView = (ListView) findViewById(R.id.historyListView);
        dealCardButton = (Button) findViewById(R.id.dealCardButton);

        //Prepare TextViews
        nameTextView.setText("Hello " + user.getName());
        dealCardButton.setVisibility(View.INVISIBLE);

        playerList = new ArrayList<>();
        playerListAdapter = new PlayerListAdapter(this, playerList);
        historyListView.setAdapter(playerListAdapter);

        //Begin listeners
        turnListener();                             //Listens for the Active Player turn flag on Firebase
        PlayerAdapter.drinkMeCardListener(this);    //Listens for DrinkMe flags on Firebase
        gameStateListener();                        //Listens for changes in the gameState
        initPlayerList();                           //Initializes the playerList
        PlayerAdapter.playerDataListener();         //Listens for changes to player data
    }

    /**
     * Updates list of current player in the lobby.
     * Updates the number of playerList in each lobby for each user.
     * Used for DrinkingContest, and possibly listViews
     */
    private void initPlayerList()
    {
        listofPlayerNames.clear();
        myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Player player = new Player(child.getKey(), 20, 0);
                    if(!player.getName().equals("turn"))
                    {
                        listofPlayerNames.add(child.getKey());
                        playerListAdapter.add(player);
                        LobbyAdapter.playerWatcher(player.getName());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                user.setCurrentPlayersInLobby(listofPlayerNames.size());
                updatePlayerList(); //updates from Firebase

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren())
                {
                    listofPlayerNames.remove(child.getKey());
                    for(int i = 0; i < playerList.size(); i++)
                    {
                        Player player = playerList.get(i);
                        if(player.getName().equals(child.getKey()))
                        {
                            playerListAdapter.remove(player);
                            System.out.println("GameInterfaceActivity.onChildRemoved() playerListAdapter.getCount(): " + playerListAdapter.getCount());
                            if(playerListAdapter.getCount() == 1 && isInGame)
                            {
                                applyWinCondition();
                            }
                        }
                    }
                }
                user.setCurrentPlayersInLobby(playerList.size());
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
     * Iterates through all the Players in PlayerList and updates their data from Firebase
     */
    public static void updatePlayerList()
    {
        for(int i = 0; i < playerList.size(); i++)
        {
            Player player = playerList.get(i);
            playerListAdapter.remove(player);
            playerListAdapter.insert(player, i);
        }
    }

    /**
     * This is used by DrinkMeDeckAdapter to get a current list of all playerList.
     * TODO Update DrinkMeDeckAdapter to read in an ArrayList of type Player
     * @return
     */
    public static ArrayList<String> getPlayerList()
    {
        return listofPlayerNames;
    }

    /**
     * This is used in TurnAdapter to get a list of all players in the lobby.
     * @return
     */
    public static ArrayList<Player> getPlayers()
    {
        return playerList;
    }

    /**
     * Called from playerWatcher, this gets a Player Object from a String playerName
     * @param playerName
     * @return
     */
    public static Player getPlayer(String playerName)
    {
        for(int i = 0; i < playerList.size(); i++)
        {
            if(playerList.get(i).getName().equals(playerName))
            {
                return playerList.get(i);
            }
        }
        return null;
    }

    /**
     * Converts the screen to Active Player when it is this user's turn.
     */
    private void turnListener()
    {
        myFirebaseRef.child("Players/" + user.getName() + "/currentTurn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try
                {
                    if((boolean) dataSnapshot.getValue())
                    {
                        Toast.makeText(getApplicationContext(), "It is your turn. Play an Action Card and deal a DrinkMe Card to another adventurer", Toast.LENGTH_LONG).show();
                        isTurn = true;
                        dealCardButton.setVisibility(View.VISIBLE);
                    }
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
     * Used when it is the Active Player's turn and they press the DealDrinkmeCardButton
     * @param view
     */
    public void startDealDrinkMeCardActivity(View view)
    {
        if(isInGame == true)
        {
            Intent intent = new Intent(this, DealDrinkMeCardActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Updates currentFortitudeTextView.
     * Called from user.updateFortitude()
     */
    public static void updateFortitudeTextView(int updatedFortitude)
    {
        currentFortitudeTextView.setText(String.valueOf(updatedFortitude));
    }

    /**
     * Updates alcoholTextView.
     * Called from user.updateAlcohol()
     */
    public static void updateAlcoholTextView(int updatedAlcohol)
    {
        currentAlcoholTextView.setText(String.valueOf(updatedAlcohol));
    }


    /**
     * Turns this player into an inactive player. endTurn, increaseTurnNumber, and findNextPlayer are all called together in the process of changing turns between playerList.
     */
    public static void endTurn()
    {
        if(isTurn)
        {
            TurnAdapter.increaseTurnNumber();
            isTurn = false;
            myFirebaseRef.child("Players/" + user.getName() + "/currentTurn").setValue(false);
            dealCardButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Listener for the lobby's gameState.
     * Starts the DrinkMeCard Activity with custom parameters (determined within DrinkMeCard Activity)
     */
    private void gameStateListener()
    {
        myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/gameState").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gameState = (String) dataSnapshot.getValue();
                if(gameState != null) //If a player leaves the lobby, this conditional prevents the app from crashing if their lobby is deleted.
                {
                    if((gameState.equals("roundOnTheHouse") || gameState.equals("drinkingContest")) && isTurn == false && isInGame) //If one of these conditional occurs,it is not currently the user's turn, and the player is still in the game, start the DrawDrinkMeCardActivity.
                    {
                        startDrinkEventActivity();
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    /**
     * This method will apply all changes that come as a result of a player being in the gameOver status.
     * This is called from User.checkGameOver(), which is updated every time a player's reference on Firebase receives a change in fortitude or alcohol.
     */
    public static void applyGameOver()
    {
        if(isInGame == true)    //It is possible that this method gets called simultaneously from different sources.
        {
            TurnAdapter.deletePlayerAndEndTurn();
        }
        isInGame = false;
        gameStatusTextView.setText("GameOver!");
        gameStatusTextView.setVisibility(View.VISIBLE);
        dealCardButton.setVisibility(View.INVISIBLE);
    }

    private void applyWinCondition()
    {
        isInGame = false;
        wonGame = true;
        gameStatusTextView.setText("You Win!");
        gameStatusTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Called whenever the gameState changes to a DrinkEvent activity. Sends this player to a DrinkEventActivity.
     */
    private void startDrinkEventActivity()
    {
        Intent intent = new Intent(this, DrinkEventActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_interface, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_anytime_card:
                AnytimeCardDialog newSelect = new AnytimeCardDialog();
                if(isInGame == true)
                {
                    newSelect.show(getSupportFragmentManager(), "playAnytime");
                }
                return true;
            case R.id.action_exit:
                confirmLogoutExit();
                return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void confirmLogoutExit()
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = new LogoutExitDialog();
        newFragment.show(ft, "logout_exit");
    }

    @Override
    protected void onDestroy()
    {
        if(isInGame || wonGame)
        {
            TurnAdapter.deletePlayerAndEndTurn();
        }
        user.clearRefsFromGameInterfaceActivity();
        listofPlayerNames.clear();
        playerList.clear();
        super.onDestroy();
        this.finish();
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
