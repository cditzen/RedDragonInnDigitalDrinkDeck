package cs309.reddragoninndigitaldrinkdeck.AnytimeCardActivities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import cs309.reddragoninndigitaldrinkdeck.Adapters.PlayerAdapter;
import cs309.reddragoninndigitaldrinkdeck.GameActivity.GameInterfaceActivity;
import cs309.reddragoninndigitaldrinkdeck.R;

public class AnytimeDrawCardActivity extends Activity {

    private ListView playerListView;

    private ArrayList<String> playerList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anytime_draw_card);

        playerListView = (ListView) findViewById(R.id.anytimeDrawCardListView);

        playerList = (ArrayList) GameInterfaceActivity.getPlayerList().clone();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, playerList);
        playerListView.setAdapter(adapter);
    }

    public void confirmDealCardButton(View view)
    {
        if(playerListView.getCheckedItemPosition() >= 0)
        {
            String playerToDraw = playerList.get(playerListView.getCheckedItemPosition());
            PlayerAdapter.setPlayerToDrawDrinkMeCard(playerToDraw);
        }
        finish();
    }
}
