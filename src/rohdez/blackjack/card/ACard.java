package rohdez.blackjack.card;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

/**
 * This card implements an animated card that knows how to move on a straight line
 * in the game world from its start point to its destination point.
 * @author Ron.Coleman
 */
public class ACard extends Card {
	public final static Integer SPEED = 25;
	protected Point position = new Point(0,0);
	protected Point dest;
	protected Point start;
	protected static CardStatTracker statTracker;
	protected List<Card> pile;
	
	/** Copy constructor
	 * @param card Card to copy
	 * @param start Starting point of the card
	 * @param dest Ending point of the card
	 * @param pile Destination pile of the card when it arrives
	 * @param stattracker to Update to display cards
	 */
	public ACard(Card card, Point start, Point dest, List<Card> pile, CardStatTracker statTracker) {
		super(card.getId());
		
		this.position = start;
		
		this.statTracker=statTracker;
		
		this.dest = dest;
		
		this.pile = pile;
		
		this.bmp = card.bmp;
	}
	
	/**
	 * Draws the card to this canvas.
	 * @param canvas Canvas
	 */
	public void draw(Canvas canvas) {
        // If card has landed, there's nothing more to do.
        if(this.hasArrived())
            return;

        // Get the total distance to travel
        double sx = dest.x - position.x;
        double sy = dest.y - position.y;

        double s = Math.sqrt(sx * sx + sy * sy);

        // Calculate incremental distance to move in x direction
        double theta = Math.asin(sx / s);
        double dx = SPEED * Math.sin(theta) + 0.5;
        
        // Calculate incremental distance to move in y direction
        // Note: we can't use theta since theta<0 on sine curve
        // doesn't necessarily allow for sx<0 and sy<0. Thus, we must
        // compute the gamma angle of attack using cosine.
        double gamma = Math.acos(sy / s);
        double dy = SPEED * Math.cos(gamma) + 0.5;

        // Move the card, relative
        position.x += dx;
        position.y += dy;

        // Correct overshoot, if necessary
        if (Math.abs(dest.x - position.x) <= SPEED &&
        	Math.abs(dest.y - position.y) <= SPEED) {
        	// Add the card to the discard pile since it landed there
        	statTracker.updateStatTracker(this);
            pile.add(0,this);
            
            // Make landed
            position.x = dest.x;
            position.y = dest.y;
        }
        else
        	drawFace(canvas,position.x,position.y);		
	}
	
	/**
	 * Draws the card face.
	 * @param canvas Canvas to draw on
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	protected void drawFace(Canvas canvas, int x, int y) {
    	Bitmap bmp = this.getBitmap();
    	
    	if(bmp != null)
    		canvas.drawBitmap(bmp, x,y,null);
	}
	 
	/**
	 * Returns true if the card has reached its landing point.
	 * @return True if card is at landing point, false otherwise
	 */
    public boolean hasArrived() {
        return dest.x == position.x && dest.y == position.y;
     }

}
