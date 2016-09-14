package cs309.reddragoninndigitaldrinkdeck.UserData;

import com.firebase.client.Firebase;

import java.util.ArrayList;

import cs309.reddragoninndigitaldrinkdeck.Adapters.DrinkMeDeckAdapter;
import cs309.reddragoninndigitaldrinkdeck.Adapters.PlayerAdapter;
import cs309.reddragoninndigitaldrinkdeck.GameActivity.GameInterfaceActivity;

/**
 * Created by Cory Itzen on 3/17/2015.
 * User contains data relevant to each individual player.
 * The name and lobby are used frequently throughout the lifecycle of the program to get the proper refernces from Firebase.
 * User also stores data about the status of the game.
 */
public class User
{
    private Firebase myFirebaseRef = myFirebaseRef = new Firebase("https://reddragoninn.firebaseio.com/");

    private String userName;
    private String lobby;
    private ArrayList<Integer> drinkMePile;

    private int currentFortitude; //These variables are stored here, and updated from PlayerAdapter
    private int currentAlcohol;
    private String character;   //User's playable character
    private String race;        //User's race, based on character
    private boolean isHost;
    private int increasePerTurn; //A constant that is established once the game starts.
    private int currentPlayersInLobby; //A current number of players in the lobby. When this is 0, the lobby is deleted. TODO Not sure if this is useful. Find where this is called and verify if it's used.

    private boolean isLeavingLobby; //Flag used to safely clear the user's lobby reference when player is leaving the lobby.

    public User(String userName)
    {
        this.userName = userName;
        lobby = "";
        drinkMePile = new ArrayList<Integer>();
        currentFortitude = 20;
        currentAlcohol = 0;
        character = "";
        race = "";

        isHost = false;
        increasePerTurn = 0;
        currentPlayersInLobby = 0;

        isLeavingLobby = false;
    }

    public ArrayList<Integer> getDrinkMePile()
    {
        return drinkMePile;
    }

    public void setLobby(String lobby)
    {
        this.lobby = lobby;
    }

    /**
     * Draws the top card from the top of the DrinkMePile, and updates Firebase with the current number of cards in that player's drinkMePile
     * @param cardRef
     */
    public void addNewCardRef(int cardRef)
    {
        if(!drinkMePile.contains(cardRef))
        {
            drinkMePile.add(cardRef);
        }
        myFirebaseRef.child("Players/" + userName + "/numDrinkMeCards").setValue(drinkMePile.size());
    }

    /**
     * Called from DrawDrinkMeCardActivity.activePlayerConfirmButton()
     * @param deltaFortitude
     */
    public void updateFortitude(int deltaFortitude)
    {
        currentFortitude = keepInBounds(currentFortitude + deltaFortitude); //Keeps fortitude between 0 and 20, and applies to to currentFortitude
        PlayerAdapter.setUpdatedFortitude(currentFortitude);
        GameInterfaceActivity.updateFortitudeTextView(currentFortitude);
        checkGameOver();
    }

    /**
     * Called from DrawDrinkMeCardActivity.activePlayerConfirmButton()
     * @param deltaAlcohol
     */
    public void updateAlcohol(int deltaAlcohol)
    {
        currentAlcohol = keepInBounds(currentAlcohol + deltaAlcohol);
        PlayerAdapter.setUpdatedAlcohol(currentAlcohol);
        GameInterfaceActivity.updateAlcoholTextView(currentAlcohol);
        checkGameOver();
    }

    public void setCharacter(String character)
    {
        this.character = character;
        if(character.equals("Serena"))
        {
            race = "orc";
        }
        if(character.equals("Natyli") || character.equals("Phrenk"))
        {
            race = "troll";
        }
    }

    public void setHost(boolean isHost)
    {
        this.isHost = isHost;
    }

    public void setIncreasePerTurn(int increasePerTurn)
    {
        this.increasePerTurn = increasePerTurn;
    }

    /**
     * Updated from GameInterfaceActivity.updatePlayerList.
     * @param numPlayers
     */
    public void setCurrentPlayersInLobby(int numPlayers)
    {
        this.currentPlayersInLobby = numPlayers;
    }

    public void setIsLeavingLobby(boolean isLeaving)
    {
        this.isLeavingLobby = isLeaving;
    }

    public String getName()
    {
        return userName;
    }

    public String getLobby()
    {
        return lobby;
    }

    public int getTopCardRef()
    {
        int cardRef= drinkMePile.get(drinkMePile.size() - 1);
        drinkMePile.remove(drinkMePile.size() - 1);
        DrinkMeDeckAdapter.placeCardBackInDeck(cardRef);
        myFirebaseRef.child("Players/" + userName + "/numDrinkMeCards").setValue(drinkMePile.size());
        return cardRef;
    }

    public int getNumCards()
    {
        return drinkMePile.size();
    }

    public String getRace()
    {
        return race;
    }

    public boolean isHost()
    {
        return isHost;
    }

    public int getCurrentPlayersInLobby()
    {
        return currentPlayersInLobby;
    }

    public int getIncreasePerTurn()
    {
        return increasePerTurn;
    }

    public boolean isLeavingLobby()
    {
        return isLeavingLobby;
    }

    /**
     * Clears (or resets) references to lobby, drinkMePile, currentFortitude, currentAlcohol, isHost, increasePerTurn, and currentPlayersInLobby.
     */
    public void clearRefsFromGameInterfaceActivity()
    {
        lobby = "";
        drinkMePile.clear();
        currentFortitude = 20;
        currentAlcohol = 0;
        isHost = false;
        increasePerTurn = 0; //This might be risky. Test this.
        currentPlayersInLobby = 0;
    }

    /**
     * Called everytime currentFortitude or currentAlcohol is changes, this checks gameOver status.
     * If the player is indeed in gameOver, GameInterfaceActivity.applyGameOver() is called.
     */
    private void checkGameOver()
    {
        if(currentFortitude <= currentAlcohol)
        {
            GameInterfaceActivity.applyGameOver();
        }
    }

    /**
     * Keeps a fortitude or alcohol value in the 0-20 bound.
     * @param toKeepInBound
     * @return
     */
    private int keepInBounds(int toKeepInBound)
    {
        if(toKeepInBound > 20)
            toKeepInBound = 20;
        if(toKeepInBound < 0)
            toKeepInBound = 0;

        return toKeepInBound;
    }
}
