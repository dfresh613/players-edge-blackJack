package rohdez.blackjack.card;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

/**
 * This card moves the back facing card along a straight line just like its parent.
 * The difference here is the back face shows rather than the front face.
 * @author Ron.Coleman
 *
 */
public class BCard extends ACard {
	protected Bitmap cardBack;
	
	/**
	 * Copy constructor
	 * @param card Card to move
	 * @param start Start point
	 * @param dest Destination point
	 * @param pile Destination pile
	 * @param cardBack card back face
	 */
	public BCard(Card card, Point start, Point dest, List<Card> pile, Bitmap cardBack) {
		super(card, start, dest, pile, statTracker);
		this.cardBack = cardBack;
	}
	
	/**
	 * Draws the card's face.
	 * @param canvas Canvas to draw on
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	@Override
	protected void drawFace(Canvas canvas,int x, int y) {	
    	if(cardBack != null)
    		canvas.drawBitmap(cardBack, x,y,null);
	}
}
