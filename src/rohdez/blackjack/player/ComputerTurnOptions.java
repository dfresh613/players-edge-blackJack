package rohdez.blackjack.player;

import java.util.ArrayList;
import java.util.List;


import rohdez.blackjack.card.Card;

public class ComputerTurnOptions {
protected List<Card> tmpDeck;
protected List<String> tmpOptions =  new ArrayList<String>();
public static final String optionHit = "Hit";
public static final String optionStay= "Stay";
public static final String optionSplit="Split";
public static final String optionBust = "Bust";
protected int deckValue;
protected boolean acesHandled=true;
protected int noOfAces=0;
protected boolean bust = false;
protected int tmpScore;
private String option ="";


	public ComputerTurnOptions(List<Card> tmpDeck) {
		this.tmpDeck = tmpDeck;
		bust=false;
		setOptions(tmpDeck);
	}
	
	//computer options will only return one option computer must do
	private void setOptions(List<Card> tmpDeck){
		option ="";
		for (Card tmpCard : tmpDeck) {
			tmpScore=tmpCard.getScoreValue();
			//handle possibility of split, and if found to be splitable hand, add that to the option list for player
			if (tmpScore == 11){
				//first time ace comes out, will always be a soft
				//hardNumber=false;
				noOfAces+=1;
				acesHandled=false;				
			}
			
			deckValue+=tmpScore;
			//if deckvalue is greater than 21 and the ace hasn't been handled, change the value of the deck
			//so that the ace counts as 1
			if (deckValue > 21 && ! acesHandled){
				while(noOfAces>0 && deckValue >21){
					noOfAces-=1;
					deckValue-=10;
				}
				//if noOf aces is 0 then we don't need to come into this option to begin with
				if (noOfAces<=0){
					acesHandled=true;
				}
			}			
		}
		if(deckValue >21){
			option=optionBust;
			bust=true;				
		}else if (deckValue<=16){
			option=optionHit;
		//indicates a soft 17, dealer must hit
		}else if (deckValue == 17 && !acesHandled){
			option = optionHit;
		}else if (deckValue >= 17 && deckValue <= 21){
			option = optionStay;
		}else if (deckValue>21){
			option = optionBust;
			bust=true;
		}
	}
	public String getCompOption(){
		return option;
	}
	public boolean isBust(){
		return bust;
	}
	public int getDeckValue(){
		return deckValue;
	}
	
}
