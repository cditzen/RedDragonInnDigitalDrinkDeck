package cs309.reddragoninndigitaldrinkdeck.DrinkMeDeck;

/**
 * Created by Cory Itzen on 3/26/2015.
 */
public class DrinkMeCard {

    /*Name of the card and the resource for that card's image*/
    private String name;
    private int imageResource;

    /*Basic card settings*/
    private int changeFortitude;
    private int changeAlcohol;

    /*Unique card booleans*/
    private boolean isChaser;
    private boolean isRoundOnTheHouse;
    private boolean isDrinkingContest;

    /*Alternate fortitude and alcohol values used for unique races */
    private String uniqueRace;      //String containing names of race that gets dealt alternate effects
    private boolean isRacialCard;   //If this card uses alternate effects for the unique race
    private int racialFortitude;    //alternate fortitude and alcohol
    private int racialAlcohol;


    public DrinkMeCard(String name, int imageResource)
    {
        this.name = name;
        this.imageResource = imageResource;
        changeFortitude = 0;
        changeAlcohol = 0;
        isChaser = false;
        isRoundOnTheHouse = false;
        isDrinkingContest = false;

        uniqueRace = "not applicable";
        isRacialCard = false;
        racialFortitude = 0;
        racialAlcohol = 0;
    }

    public void setChangeFortitude(int changeFortitude) {
        this.changeFortitude = changeFortitude;
    }

    public void setChangeAlcohol(int changeAlcohol) {
        this.changeAlcohol = changeAlcohol;
    }

    public void setChaser(boolean isChaser)
    {
        this.isChaser = isChaser;
    }

    public void setRoundOnTheHouse(boolean isRoundOnTheHouse)
    {
        this.isRoundOnTheHouse = isRoundOnTheHouse;
    }

    public void setDrinkingContest(boolean isDrinkingContest)
    {
        this.isDrinkingContest = isDrinkingContest;
    }

    public void setUniqueRace(String race)
    {
        this.uniqueRace = race;
    }

    public void setRacialCard(boolean isRacialCard)
    {
        this.isRacialCard = isRacialCard;
    }

    public void setRacialFortitude(int racialFortitude)
    {
        this.racialFortitude = racialFortitude;
    }

    public void setRacialAlcohol(int racialAlcohol)
    {
        this.racialAlcohol = racialAlcohol;
    }

    public String getName()
    {
        return name;
    }

    public int getImage()
    {
        return imageResource;
    }

    /**
     * Return the proper fortitude based on the user's race.
     * @param characterRace
     * @return
     */
    public int getChangeFortitude(String characterRace)
    {
        if(characterRace.equals(uniqueRace))
        {
            return racialFortitude;
        }
        else
        {
            return changeFortitude;
        }

    }

    /**
     * Returns the proper alcohol based on the user's race.
     * @param characterRace
     * @return
     */
    public int getChangeAlcohol(String characterRace)
    {
        if(characterRace.equals(uniqueRace))
        {
            return racialAlcohol;
        }
        else
        {
            return changeAlcohol;
        }
    }

    public boolean isChaser()
    {
        return isChaser;
    }

    public boolean isRoundOnTheHouse()
    {
        return isRoundOnTheHouse;
    }

    public boolean isDrinkingContest()
    {
        return isDrinkingContest;
    }
}
