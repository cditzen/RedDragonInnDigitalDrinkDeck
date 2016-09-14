package cs309.reddragoninndigitaldrinkdeck.ListViews;

import android.widget.ImageView;

import com.firebase.client.Firebase;

import cs309.reddragoninndigitaldrinkdeck.PreGameActivities.SignInActivity;
import cs309.reddragoninndigitaldrinkdeck.R;
import cs309.reddragoninndigitaldrinkdeck.UserData.User;

/**
 * Created by cditz_000 on 4/19/2015.
 */
public class Player {

    private String name;
    private String character;
    private int portrait;
    private int fortitude;
    private int alcohol;
    private int numCards;
    private boolean isTurn;

    public Player(String name, int fortitude, int alcohol)
    {
        this.name = name;
        this.fortitude = fortitude;
        this.alcohol = alcohol;
        this.isTurn = false;
        numCards = 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPortrait() {
        return portrait;
    }

    public void setCharacter(String character) {
        switch(character) {
            case "Deirdre":
                this.portrait = R.drawable.deirdre;
                break;
            case "Zot":
                this.portrait = R.drawable.zot;
                break;
            case "Fiona":
                this.portrait = R.drawable.fiona;
                break;
            case "Gerki":
                this.portrait = R.drawable.gerki;
                break;
            case "Gog":
                this.portrait = R.drawable.gog;
                break;
            case "Eve":
                this.portrait = R.drawable.eve;
                break;
            case "Dimli":
                this.portrait = R.drawable.dimli;
                break;
            case "Fleck":
                this.portrait = R.drawable.fleck;
                break;
        }
    }

    public int getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(int alcohol) {
        this.alcohol = alcohol;
    }

    public int getFortitude() {
        return fortitude;
    }

    public void setFortitude(int fortitude) {
        this.fortitude = fortitude;
    }

    public int getNumCards() {
        return numCards;
    }

    public void setNumCards(int numCards) {
        this.numCards = numCards;
    }

    public boolean isTurn()
    {
        return isTurn;
    }

    public void setIsTurn(boolean isTurn)
    {
        this.isTurn = isTurn;
    }
}

