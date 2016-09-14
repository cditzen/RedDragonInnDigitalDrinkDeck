package cs309.reddragoninndigitaldrinkdeck.Adapters;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cs309.reddragoninndigitaldrinkdeck.DrinkMeActivities.DrawDrinkMeCardActivity;
import cs309.reddragoninndigitaldrinkdeck.DrinkMeDeck.DrinkMeCard;
import cs309.reddragoninndigitaldrinkdeck.GameActivity.GameInterfaceActivity;
import cs309.reddragoninndigitaldrinkdeck.PreGameActivities.SignInActivity;
import cs309.reddragoninndigitaldrinkdeck.R;
import cs309.reddragoninndigitaldrinkdeck.UserData.User;

/**
 * Created by Cory Itzen on 3/29/2015.
 */
public class DrinkMeDeckAdapter {

    private static Firebase myFirebaseRef = new Firebase("https://reddragoninn.firebaseio.com/");
    private static User user = SignInActivity.getUser();


    /**
     * Adds a copy of the reference of the top card from the DrinkMe Deck, and then calls deleteTopCard().
     */
    public static void drawFromDrinkMeDeck()
    {
        Query queryRef = myFirebaseRef.child("Decks/" + user.getLobby()).limitToFirst(1);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot querySnapshot) {
                System.out.println("DrinkMeDeckAdapter.drawFromDrinkMeDeck() -> lobby: " + user.getLobby());
                if(querySnapshot.getChildren().iterator().hasNext())
                {
                    try
                    {
                        int cardRef = querySnapshot.getChildren().iterator().next().getValue(long.class).intValue();
                        int cardRefArrayPosition = Integer.parseInt(querySnapshot.getChildren().iterator().next().getKey());
                        user.addNewCardRef(cardRef);
                        myFirebaseRef.child("Decks/" + user.getLobby() + "/" + cardRefArrayPosition).removeValue(); //Deletes that card from the Firebase DrinkDeck
                    }
                    catch(NullPointerException e)
                    {

                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    public static DrinkMeCard convertToDrinkMeCard(int cardRef)
    {
        DrinkMeCard card;

        if(cardRef <= 2)        //0, 1, 2
        {
            card = new DrinkMeCard("Dark Ale", R.drawable.darkale);
            card.setChangeAlcohol(1);
        }
        else if(cardRef == 3)   //3
        {
            card = new DrinkMeCard("Dark Ale with a Chaser", R.drawable.darkalewithchaser);
            card.setChangeAlcohol(1);
            card.setChaser(true);
        }
        else if(cardRef <= 6)   //4, 5, 6
        {
            card = new DrinkMeCard("Light Ale", R.drawable.lightale);
            card.setChangeAlcohol(1);
        }
        else if(cardRef <= 9)  //7, 8, 9
        {
            card = new DrinkMeCard("Light Ale with a Chaser", R.drawable.lightalewithchaser);
            card.setChangeAlcohol(1);
            card.setChaser(true);
        }
        else if(cardRef <= 12)   //10, 11, 12
        {
            card = new DrinkMeCard("Wine", R.drawable.wine);
            card.setChangeAlcohol(2);
        }
        else if(cardRef == 13)    //13
        {
            card = new DrinkMeCard("Wine with a Chaser", R.drawable.winewithchaser);
            card.setChangeAlcohol(2);
            card.setChaser(true);
        }
        else if(cardRef <= 15 )   //14, 15
        {
            card = new DrinkMeCard("Elven Wine", R.drawable.elvenwine);
            card.setChangeAlcohol(3);
        }
        else if(cardRef == 16)    // 16
        {
            card = new DrinkMeCard("Elven Wine with a Chaser", R.drawable.elvenwinewithchaser);
            card.setChangeAlcohol(3);
            card.setChaser(true);
        }
        else if(cardRef <= 19)    // 17, 18, 19
        {
            card = new DrinkMeCard("Dragon Breath Ale", R.drawable.dragonbreathale);
            card.setChangeAlcohol(4);
        }
        else if(cardRef == 20)  //20
        {
            card = new DrinkMeCard("Dirty Dishwater", R.drawable.dirtydishwater);
            card.setChangeFortitude(-1);
        }
        else if(cardRef == 21)  //21
        {
            card = new DrinkMeCard("Holy Water", R.drawable.holywater);
            card.setChangeFortitude(2);
        }
        else if(cardRef == 22)  //22
        {
            card = new DrinkMeCard("We're Cutting You Off!", R.drawable.coffee);
            card.setChangeAlcohol(-1);
        }
        else if(cardRef == 23)  //23
        {
            card = new DrinkMeCard("Wizard's Brew", R.drawable.wizardsbrew);
            card.setChangeAlcohol(2);
            card.setChangeFortitude(2);
        }
        else if(cardRef <= 25)    //24, 25
        {
            card = new DrinkMeCard("Drinking Contest", R.drawable.drinkingcontest);
            card.setDrinkingContest(true);
        }
        else if(cardRef <= 27)    //26, 27
        {
            card = new DrinkMeCard("Round On The House", R.drawable.roundonthehouse);
            card.setRoundOnTheHouse(true);
        }
        else if(cardRef == 28)  //28
        {
            card = new DrinkMeCard("Orcish Rotgut", R.drawable.orcishrotgut);
            card.setChangeFortitude(-2);
            card.setUniqueRace("orc");
            card.setRacialAlcohol(2);
            card.setRacialFortitude(0);
        }
        else if(cardRef == 29)  //29
        {
            card = new DrinkMeCard("Troll Swill", R.drawable.trollswill);
            card.setChangeFortitude(-1);
            card.setChangeAlcohol(1);
            card.setUniqueRace("troll");
            card.setRacialFortitude(0);
            card.setRacialAlcohol(2);
        }
        else                    //30
        {
            card = new DrinkMeCard("Water", R.drawable.water);
        }
        return card;
    }

    /**
     * Used for Round on the House
     * Finds the first card from Firebase that isn't an Event Card, and makes a DrinkEvent Firebase Ref where all players are given their cards to draw from.
     * Sets this into the Lobby, which all players use to reference and display on their device.
     */
    public static void dealRoundOnTheHouseCard()
    {
        Query queryRef = myFirebaseRef.child("Decks/" + user.getLobby());
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren())
                {
                    int cardRef = child.getValue(long.class).intValue();
                    DrinkMeCard card = convertToDrinkMeCard(cardRef);
                    myFirebaseRef.child("Decks/" + user.getLobby() + "/" + child.getKey()).removeValue();
                    placeCardBackInDeck(cardRef);   //Place that card back in the deck
                    if(!card.isRoundOnTheHouse() && !card.isDrinkingContest())   //Is not roundOnTheHouse or Drinking Contest.
                    {
                        DrawDrinkMeCardActivity.addCardFromDrinkEvent(card);
                        myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/roundOnTheHouse").setValue(cardRef);
                        myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/gameState").setValue("roundOnTheHouse");
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    };

    /**
     * Deals a drinkMeCard to each individual player using GameInterface.getPlayerList().
     * Keeps track of the highest alcohol.
     * If multiple players have the highest alcohol, hmmm...
     */
    public static void dealDrinkingContestCards()
    {
        final ArrayList<String> playersInLobby = GameInterfaceActivity.getPlayerList();

        Query queryRef = myFirebaseRef.child("Decks/" + user.getLobby()).limitToFirst(playersInLobby.size());
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int playerIndex = 0;
                ArrayList<String> playersWithHighestAlcohol = new ArrayList<String>();
                int highestAlcohol = -5;
                for(DataSnapshot child : dataSnapshot.getChildren())
                {
                    String playerToDeal = playersInLobby.get(playerIndex);
                    myFirebaseRef.child("Players/" + playerToDeal + "/drinkingContest").setValue(child.getValue());
                    DrinkMeCard card = convertToDrinkMeCard(child.getValue(long.class).intValue());

                    if(card.getChangeAlcohol(user.getRace()) > highestAlcohol)        //If this player has the highest alcohol, set him to have the highest alcohol.
                    {
                        playersWithHighestAlcohol.clear();
                        playersWithHighestAlcohol.add(playerToDeal);
                        highestAlcohol = card.getChangeAlcohol(""); //Get the normal alcohol.
                    }
                    else if(card.getChangeAlcohol(user.getRace()) == highestAlcohol)  //If this player is tied for the highest alcohol, add this player to the list.
                    {
                        playersWithHighestAlcohol.add(playerToDeal);
                        highestAlcohol = card.getChangeAlcohol(""); //Get the normal alcohol
                    }

                    if(playerToDeal.equals(user.getName()))             //Add the drinkMeCard directly to the user's DrawDrinkMeCardActivity.
                    {
                        DrawDrinkMeCardActivity.addCardFromDrinkEvent(card);
                    }

                    myFirebaseRef.child("Decks/" + user.getLobby() + "/" + child.getKey()).removeValue();
                    placeCardBackInDeck(child.getValue(long.class).intValue());
                    playerIndex++;
                }
                myFirebaseRef.child("Lobbies/" + user.getLobby() + "/" + user.getLobby() + "/gameState").setValue("drinkingContest");
                announceDrinkingContestWinners(playersWithHighestAlcohol);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * Gets the winners of the drinking contest and puts a message on firebase prompting players to give them gold.
     * @param players Winners of the DrinkingContest
     */
    private static void announceDrinkingContestWinners(ArrayList<String> players)
    {
        myFirebaseRef.child("Prompts/" + user.getLobby()).removeValue();
        try
        {
            String message = "";
            if(players.size() <= 1)
            {
                message = players.get(0) + " won the DrinkingContest.\nGive " + players.get(0) + " one gold coin.";
            }
            else if(players.size() == 2)
            {
                message = players.get(0) + " and " + players.get(1) + " won the Drinking Contest.\nSplit the gold between them.";
            }
            else    //There are 3 or more winners. Make a unique message containing these names. TODO Test that this works.
            {
                for(int i = 0; i < players.size() - 2; i++)
                {
                    message += players.get(i) + ", ";
                }
                message += "and " + players.get(players.size() - 1) + " won the Drinking Contest. Split the gold between them.";
            }

            myFirebaseRef.child("Prompts/" + user.getLobby()).setValue(message);
        }
        catch(NullPointerException e)
        {

        }


    }

    public static void placeCardBackInDeck(final int cardRef)
    {
        Query queryRef = myFirebaseRef.child("Decks/" + user.getLobby()).limitToLast(1);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot querySnapshot) {
                if(querySnapshot.getChildren().iterator().hasNext()) //Prevents finding a null object from Firebase if this is called after last player left lobby
                {
                    int lastRefInDeck = Integer.parseInt(querySnapshot.getChildren().iterator().next().getKey()) + 1;
                    myFirebaseRef.child("Decks/" + user.getLobby() + "/" + lastRefInDeck).setValue(cardRef);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }
}

