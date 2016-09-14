package cs309.reddragoninndigitaldrinkdeck.PreGameActivities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import cs309.reddragoninndigitaldrinkdeck.R;
import cs309.reddragoninndigitaldrinkdeck.UserData.User;


public class FindLobbyActivity extends ActionBarActivity {

    private Firebase myFirebaseRef;

    private User user;
    private TextView greetingTextView;
    private ListView lobbyListView;

    private ArrayList<String> listOfLobbies = new ArrayList<>();
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_lobby);

        //Prepare Firebase Reference
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://reddragoninn.firebaseio.com/");

        //Initialize
        user = SignInActivity.getUser();
        greetingTextView = (TextView) findViewById(R.id.greetingTextView);
        lobbyListView = (ListView) findViewById(R.id.lobbyListView);
        greetingTextView.setText(user.getName() + ",\nWelcome to the Red Dragon Inn.\nJoin a group of adventurers, or start your own party.");

        //Prepare adapter and read in lobbies
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listOfLobbies);
        lobbyListView.setAdapter(adapter);
        lobbyListView.setOnItemClickListener(clickListener);
        lobbyEventListener();
    }

    /**
     * Gets all current lobbies and adds them to the ListView Adapter
     * If Lobbies are added or removed, this updates those changes.
     * //TODO Need to mark private lobbies as private or hidden.
     */
    private void lobbyEventListener()
    {
        myFirebaseRef.child("Lobbies/").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                        adapter.add(child.getKey());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    adapter.remove(child.getKey());
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
     * Joins the selected lobby.
     */
    private AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            joinLobby(listOfLobbies.get(position));
        }
    };

    /**
     * Turns this player into the host, and creates a lobby with their name appended with "'s lobby".
     * The Host if given player number 0.
     * The Lobby gameState is set to standBy. This means other players can join.
     * Starts the LobbyActivity.
     * @param view
     */
    public void hostLobby(View view)
    {
        user.setLobby(user.getName() + "'s Lobby");

        System.out.println("FindLobbyActivity.hostLobby() user.getLobby(): " + user.getLobby());

        user.setHost(true);

        //Updates all the lobby information into Firebase. TODO We could probably do a map here...
        myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/members/" + user.getName() + "/" + user.getName()).setValue(true);
        myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/members/" + user.getName() + "/turn").setValue(0);//TODO I have to double stack the name of the lobby in order for onchildchanged to read this.
        myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/gameState").setValue("standby");
        myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/numPlayers").setValue(1);
        myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/playerToAppend").setValue(user.getName());

        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }

    /**
     * Attempts to join a selected lobby.
     * This lobby's gameState must be in standBy to successfully join.
     * @param lobby
     */
    private void joinLobby(String lobby)
    {
        final String lobbyToJoin = lobby;

        myFirebaseRef.child("Lobbies/" + lobby + "/" + lobby + "/gameState").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gameState = (String) dataSnapshot.getValue();

                if(gameState.equals("standby"))
                {
                    user.setLobby(lobbyToJoin);

                    myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/members/" + user.getName() + "/" + user.getName()).setValue(true);

                    user.setHost(false);
                    startLobbyActivity();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * Starts LobbyActivity.
     * This is called from joinLobby.
     */
    private void startLobbyActivity()
    {
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_lobby, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id)
        {
            case R.id.logout:
                logout();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logout()
    {
        setResult(Activity.RESULT_FIRST_USER);
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        finish();
        System.exit(0); //Rolling the dice
    }
}
