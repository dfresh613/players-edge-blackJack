package rohdez.blackjack.card;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

/*
 * class which handles the stats of the recent cards played, can possibly give the player an edge over, or other way aroudn
 */
public class CardStatTracker{
	//Use array to keep track of how many of each cards were dealt. the array index will be indicitive of the card 
	//it's counting for IE cardCount[4]=2 means a 4 was dealt twice
	private int cardCount[]=new int[16];
	private Paint rectanglePaint;
	private Paint titlePaint;
	public Paint cardText;
	private float scale;
	
	/*
	 * initialize a new cardStatTracker object which you can use to udpate the card counts, and display the cardTracker
	 * @param float scale	
	 */
	public CardStatTracker(float scale){
		this.scale=scale;;
		//init the array
		initCardCount();
		//init the shape the stat tracker will be put in
		initTrackerDesign();
	}

	/*
	 * Updates stat tracker with new card dealt
	 * @param Card card
	 */
	public void updateStatTracker(Card card){
		int scoreVal = card.getScoreValue();
		if(scoreVal==11){
			scoreVal=14;
		}
		//get the current count from the cards relative array index
		int tmpArrayVal =cardCount[scoreVal];
		//if score val is 11, then mark it in the ace category
				
		//increment value by 1, and save back in array
		tmpArrayVal+=1;
		cardCount[scoreVal]=tmpArrayVal;
		
			
		
	}
	/*
	 * inits rectangle design and title the statTracker will be drawn in
	 */
	private void initTrackerDesign(){
		//paint for rectangle object, dark grey and transparent
		rectanglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		rectanglePaint.setColor(Color.DKGRAY);
		rectanglePaint.setAlpha(200); 
		
		//paint for title of stat tracker, bigger red text
		titlePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		titlePaint.setColor(Color.RED);
		titlePaint.setTextAlign(Align.CENTER);
		titlePaint.setTextSize(scale*30);
		titlePaint.setStyle(Paint.Style.FILL);
		
		//paint for the text displaying card count. Smaller white text
		cardText = new Paint(Paint.ANTI_ALIAS_FLAG);
		cardText.setColor(Color.WHITE);
		cardText.setTextAlign(Align.CENTER);
		cardText.setTextSize(scale*20);
		
	}
	/*
	 * Draws the statTracker onto canvas
	 */
	public void drawStatTracker(Canvas canvas){
		//draw the background rectangle
		canvas.drawRect((float)scale*350, (float)(scale*40), (float)scale*550, (float)scale*350, rectanglePaint);
		canvas.drawText("Card Counter", (float)scale *450, (float) (scale*80), titlePaint);
		canvas.drawText("Value \t Count",(float)scale*450,(float)scale*100,cardText);
		//will be used to draw text for each card underneath each other
		int pos=0;
		String cardVal;
		String countText;
		//used for the total number of vard value IE 2/4 cards dealt.
		int maxCards=4;
		//iterate over each card count in array.
		for (int i=2; i<=14;i++){
			//If i is a facecard, then cardText will be 10
			if (i==10){
				//4cards per 10 - K
				cardVal="10";
				maxCards = 16;
			}else if(i==14){
				cardVal="A";
				maxCards=4;
			}else{
				cardVal=Integer.toString(i);
				maxCards=4;
			}
			//display value for current card in array. If it's 10-K don't show. THey are included in the 10 count
			if (i>10 && i<=13){
				countText=null;
						
			}else{
				countText=cardVal+"          "+cardCount[i]+"/"+maxCards;
			}
			//only show if it's a value we want to show
			if (countText!=null){
				canvas.drawText(countText,(float)scale*445,(float)(scale*125)+pos,cardText);
				pos+=25;
			}
			
		}
	}

	
	/*
	 * Inits cardCount array so all values of cards are 0
	 */
	private void initCardCount(){
		for(int i=2; i<=14;i++){
			cardCount[i]=0;
		}
		
	}
	
	

}
