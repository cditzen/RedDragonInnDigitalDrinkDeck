package cs309.reddragoninndigitaldrinkdeck.DrinkMeActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.firebase.client.Firebase;

import java.util.ArrayList;

import cs309.reddragoninndigitaldrinkdeck.Adapters.PlayerAdapter;
import cs309.reddragoninndigitaldrinkdeck.GameActivity.GameInterfaceActivity;
import cs309.reddragoninndigitaldrinkdeck.PreGameActivities.SignInActivity;
import cs309.reddragoninndigitaldrinkdeck.R;
import cs309.reddragoninndigitaldrinkdeck.UserData.User;


public class DealDrinkMeCardActivity extends Activity {

    private Firebase myFirebaseRef;

    private ListView playerListCheckedListView;
    private Button drinkMeCardButton;
    private User user;
    private String playerToDealCard;

    private ArrayList<String> playerList = new ArrayList<>();

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_drinkme_card);

        myFirebaseRef = new Firebase("https://reddragoninn.firebaseio.com/");
        user = SignInActivity.getUser();
        playerListCheckedListView = (ListView) findViewById(R.id.dealCardCheckedListView);
        drinkMeCardButton = (Button) findViewById(R.id.drinkMeCardButton);
        drinkMeCardButton.setText("Deal a DrinkMe Card");
        playerToDealCard = "";

        drinkMeCardButton.setClickable(false);

        playerList = (ArrayList) GameInterfaceActivity.getPlayerList().clone();
        playerList.remove(user.getName());

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, playerList);
        playerListCheckedListView.setAdapter(adapter);
        playerListCheckedListView.setOnItemClickListener(clickListener);
    }


    private AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            playerToDealCard = playerList.get(position);
            drinkMeCardButton.setText("Deal a DrinkMe Card to " + playerToDealCard);
            drinkMeCardButton.setClickable(true);
        }
    };

    /**
     * When user has selected a player to deal the drinkMe card to, this changes that player's playerState to "DrinkMe".
     * @param view
     */
    public void dealDrinkMeCardButton(View view)
    {
        PlayerAdapter.selectPlayerToBuyDrinkMeCard(playerToDealCard);                 //Changes player's state to "DrinkMe"
        Intent intent = new Intent(this, DrawDrinkMeCardActivity.class);
        startActivity(intent);
        finish();
    }
}
