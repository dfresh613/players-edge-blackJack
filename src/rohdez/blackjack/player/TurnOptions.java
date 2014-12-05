package rohdez.blackjack.player;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.widget.Toast;

import rohdez.blackjack.R;
import rohdez.blackjack.card.Card;
//Class to perform options based on actions selected by use
public class TurnOptions {
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
	private int prevCard=0;	
	protected Context myContext;
	
	public TurnOptions(List<Card> tmpDeck,Context myContext){
		this.tmpDeck = tmpDeck;
		this.myContext = myContext;
		bust =false;		
		setOptions(tmpDeck);		
	}
	private void setOptions(List<Card> tmpDeck){
		tmpOptions.clear();
		tmpOptions.add(optionStay);
		noOfAces=0;
		for (Card tmpCard : tmpDeck) {
			tmpScore=tmpCard.getScoreValue();
			//handle possibility of split, and if found to be splitable hand, add that to the option list for player
			if(tmpDeck.size()== 2 && prevCard == tmpCard.getRank()){
				tmpOptions.add(TurnOptions.optionSplit);
			}else{
				prevCard = tmpCard.getRank();
			}
			
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
			//if deckvalue is over 21, only option you have is bust. Also sets bust to true
			if(deckValue >21){
				tmpOptions.clear();
				tmpOptions.add(TurnOptions.optionBust);
				bust=true;				
			}else{
				tmpOptions.add(TurnOptions.optionHit);
			}
			
		}
		//make toast informing you have blackjack
		if (deckValue==21 && tmpDeck.size()==2){
			Toast.makeText(myContext, "You hit blackJack!.", Toast.LENGTH_SHORT).show();			
			
		}else if (deckValue==21){
			Toast.makeText(myContext, "You got 21!",Toast.LENGTH_SHORT).show();			
		}
	}
	public int getDeckValue(){
		return deckValue;
	}
	public void clearOptions(){
		tmpOptions.clear();
	}
	
	public List<String> getTurnOptions(){
		return tmpOptions;
	}
	
	public List<Card> executeHit(){
		return tmpDeck;
	}
	public boolean isBust(){
		return bust;
	}
}
	