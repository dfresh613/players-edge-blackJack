package rohdez.blackjack.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import rohdez.blackjack.R;
import rohdez.blackjack.card.ACard;
import rohdez.blackjack.card.BCard;
import rohdez.blackjack.card.Card;
import rohdez.blackjack.card.CardStatTracker;
import rohdez.blackjack.player.ComputerTurnOptions;
import rohdez.blackjack.player.TurnOptions;
import rohdez.blackjack.storage.UserPreferences;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Game view for Players Edge BlackJack
 * @author Doug Rohde
 *
 */
public class GameView extends View {

	private int screenW;
	private int screenH;
	private Context myContext;
	private List<Card> deck = new ArrayList<Card>();
	private int scaledCardW;
	private int scaledCardH;
	private Paint whitePaint;
	private List<Card> myHand = new ArrayList<Card>();
	private List<Card> oppHand = new ArrayList<Card>();
	private float scale;
	private Bitmap cardBack;
	private List<Card> discardPile = new ArrayList<Card>();
	private List<List<Card>> splitHands = new ArrayList<List<Card>>();
	private boolean myTurn = true;
	private int movingCardIdx = -1;
	private boolean split = false;
	private int movingX;
	private int movingY;
	private boolean showDealerCards = false;
	private Bitmap nextCardButton;
	private Bitmap hitButtonEnabled;
	private Bitmap stayButtonEnabled;
	private Bitmap splitButtonEnabled;
	private Bitmap splitButtonPressed;
	private Bitmap hitButtonPressed;
	private TurnOptions myOpt;
	private ComputerTurnOptions compOpt;
	private Bitmap stayButtonPressed;
	private List<String> myOptions = new ArrayList<String>();
	private String compOption = "";
	private boolean splitButtonClicked = false;
	private boolean hitButtonClicked = false;
	private boolean stayButtonClicked = false;
	private boolean soundEnabled;
	private boolean trackerEnabled;
	private float volume;
	private SoundPool sounds;
	private int blipSound;
	private int booSound;
	private int tapSound;
	private int twentyOneSound;
	private int winnaSound;
	private int dealerWins = 0;
	private int userWins = 0;
	private ACard inFlight;
	private boolean compGo = true;
	private CardStatTracker statTracker;
	private int cardsDealt = 1;
	private boolean doneDealing = false;
	private Paint rulePaint;
	private final String WINNER_PLAYER = "player";
	private final String WINNER_DEALER = "dealer";

	public GameView(Context context) {
		super(context);
		myContext = context;
		// DIP: device independent pixel
		// See
		// http://developer.android.com/reference/android/util/DisplayMetrics.html#density
		scale = myContext.getResources().getDisplayMetrics().density;

		// add sounds to soundpool for use later
		sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		blipSound = sounds.load(myContext, R.raw.blip, 1);
		booSound = sounds.load(myContext, R.raw.boo, 1);
		twentyOneSound = sounds.load(myContext, R.raw.twentyone, 1);
		tapSound = sounds.load(myContext, R.raw.tap, 1);
		winnaSound = sounds.load(myContext, R.raw.winner, 1);

		// init audiomanager to play sounds later
		AudioManager audioManager = (AudioManager) myContext
				.getSystemService(Context.AUDIO_SERVICE);
		volume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);

		whitePaint = new Paint();
		whitePaint.setAntiAlias(true);
		whitePaint.setColor(Color.WHITE);
		whitePaint.setStyle(Paint.Style.STROKE);
		whitePaint.setTextAlign(Paint.Align.LEFT);
		whitePaint.setTextSize(scale * 15);
		refreshPreferences(myContext);
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		screenW = w;
		screenH = h;
		Bitmap tempBitmap = BitmapFactory.decodeResource(
				myContext.getResources(), R.drawable.card_back);
		scaledCardW = (int) (screenW / 8);
		scaledCardH = (int) (scaledCardW * 1.28);
		cardBack = Bitmap.createScaledBitmap(tempBitmap, scaledCardW,
				scaledCardH, false);
		nextCardButton = BitmapFactory.decodeResource(getResources(),
				R.drawable.arrow_next);
		hitButtonEnabled = BitmapFactory.decodeResource(getResources(),
				R.drawable.hitbutton);
		stayButtonEnabled = BitmapFactory.decodeResource(getResources(),
				R.drawable.staybutton);
		splitButtonEnabled = BitmapFactory.decodeResource(getResources(),
				R.drawable.splitbutton);
		hitButtonPressed = BitmapFactory.decodeResource(getResources(),
				R.drawable.hitbuttonpressed);
		stayButtonPressed = BitmapFactory.decodeResource(getResources(),
				R.drawable.staybuttonpressed);
		splitButtonPressed = BitmapFactory.decodeResource(getResources(),
				R.drawable.splitbuttonpressed);
		statTracker = new CardStatTracker(scale);
		initRulePaint();
		initCards();
		dealCards();
		if (!myTurn) {
			makeComputerPlay();
		}
	}

	/**
	 * refereshes preferences to be used in View. Called when initially created
	 * and when onResume
	 * 
	 * @param context
	 */
	public void refreshPreferences(Context context) {
		// Get user preferences from sharedPreferences and set whether sound is
		// enabled
		UserPreferences userPrefs = new UserPreferences(context);
		this.soundEnabled = userPrefs.getSoundEnabled();
		this.trackerEnabled = userPrefs.getTrackerEnabled();
	}

	/*
	 * inits the paint objects for the rules to be displayed on board
	 */
	private void initRulePaint() {
		rulePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		rulePaint.setStyle(Paint.Style.FILL);
		rulePaint.setColor(Color.rgb(15, 75, 5));
		rulePaint.setTextSize(30);
		rulePaint.setTextSkewX((float) -.25);

		// Use a different type face for the rules on table
		Typeface tf = Typeface.create("Dolly", Typeface.BOLD);
		rulePaint.setTypeface(tf);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.rgb(0, 153, 0));
		if (trackerEnabled) {
			statTracker.drawStatTracker(canvas);
			canvas.drawText("Cards in Deck: " + deck.size(),
					(float) scale * 445, (float) (scale * 125) + 275,
					statTracker.cardText);
		}
		// draw rules to board
		canvas.drawText("Dealer Stays On Hard 17, Hits On Soft 17", scale * 55,
				scale * 600, rulePaint);
		canvas.drawText("All Pushes Tie", scale * 175, scale * 625, rulePaint);
		canvas.drawText("Deck Is Shuffled When Less Than 25 Cards", scale * 55,
				scale * 650, rulePaint);
		if (doneDealing) {
			canvas.drawText("Player: " + Integer.toString(myOpt.getDeckValue())
					+ "\t Wins " + userWins, 10,
					screenH - whitePaint.getTextSize() - 250, whitePaint);
		}
		// Show dealer cards becomes true when it becomes dealers turn,
		// otherwise only display one card
		if (showDealerCards) {
			canvas.drawText(
					"Dealer: " + Integer.toString(compOpt.getDeckValue())
							+ "\t Wins " + dealerWins, 10,
					whitePaint.getTextSize() + 10, whitePaint);
			if (oppHand.size() > 7) {
				canvas.drawBitmap(nextCardButton,
						screenW - nextCardButton.getWidth() + (30 * scale),
						screenH - nextCardButton.getHeight() + scaledCardH
								- (90 * scale), null);
			}
			for (int i = 0; i < oppHand.size(); i++) {
				if (i < 7) {
					// draw x relative to number of card being dealt (muliply
					// width my number of cards already dealt)
					//
					canvas.drawBitmap(oppHand.get(i).getBitmap(), i
							* (scaledCardW + 5), whitePaint.getTextSize()
							+ (50 * scale), null);
				}

			}
		} else {
			canvas.drawText("Dealer: \t Wins: " + dealerWins, 10,
					whitePaint.getTextSize() + 10, whitePaint);
			// Only show one of the dealers card
			for (int i = 0; i < oppHand.size(); i++) {
				if (i == 1 || oppHand.size() == 1) {
					canvas.drawBitmap(cardBack, i * (scaledCardW + 5),
							whitePaint.getTextSize() + (50 * scale), null);
				} else {
					canvas.drawBitmap(oppHand.get(i).getBitmap(), i
							* (scaledCardW + 5), whitePaint.getTextSize()
							+ (50 * scale), null);
				}
			}
		}
		// Simulate the card deck
		canvas.drawBitmap(cardBack, (screenW / 2) - cardBack.getWidth() - 10,
				(screenH / 2) - (cardBack.getHeight() / 2), null);

		// show right arrow if hand is too large for screen
		if (myHand.size() > 7) {
			canvas.drawBitmap(nextCardButton,
					screenW - nextCardButton.getWidth() - (30 * scale), screenH
							- nextCardButton.getHeight() - scaledCardH
							- (90 * scale), null);
		}
		// show card until no cards left in hand
		if (!split) {
			for (int i = 0; i < myHand.size(); i++) {
				if (i == movingCardIdx) {
					canvas.drawBitmap(myHand.get(i).getBitmap(), movingX,
							movingY, null);
				} else {
					// only draw cards that can be seen
					if (i < 7) {
						canvas.drawBitmap(myHand.get(i).getBitmap(), i
								* (scaledCardW + 5), screenH - scaledCardH
								- whitePaint.getTextSize() - (50 * scale), null);
					}
				}
			}
		} else {
			// If there's a split, then draw differently
			List<Card> tmpHand;
			int spacing = 5;
			for (int i = 0; i < splitHands.size(); i++) {
				tmpHand = splitHands.get(i);
				for (int x = 0; x < tmpHand.size(); x++) {
					canvas.drawBitmap(tmpHand.get(i).getBitmap(), i
							* (scaledCardW + spacing), screenH - scaledCardH
							- whitePaint.getTextSize() - (50 * scale), null);
				}
				spacing += 50;
			}
		}
		// During start of new hand, will call dealCards, until all cards are
		// dealt
		// if there's a card currently inFLight, will wait to deal next card
		if (!doneDealing && inFlight == null) {
			if (cardsDealt == 5) {
				doneDealing = true;
				refreshMyOptions();
				refreshComputerOptions();
			} else {
				dealCards();
			}
		}
		if (myTurn && doneDealing) {
			// display option hit if available and myturn
			if (myOptions.contains(TurnOptions.optionHit) && !hitButtonClicked) {
				canvas.drawBitmap(hitButtonEnabled, 0, screenH - scaledCardH
						- whitePaint.getTextSize() + (60 * scale), null);
			} else if (myOptions.contains(TurnOptions.optionHit)
					&& hitButtonClicked) {
				canvas.drawBitmap(hitButtonPressed, 0, screenH - scaledCardH
						- whitePaint.getTextSize() + (60 * scale), null);
			}
			// display opition stay if available and myturn
			if (myOptions.contains(TurnOptions.optionStay)
					&& !stayButtonClicked) {
				canvas.drawBitmap(stayButtonEnabled,
						(stayButtonEnabled.getWidth() * 2) + 10, screenH
								- scaledCardH - whitePaint.getTextSize()
								+ (60 * scale), null);
			} else if (myOptions.contains(TurnOptions.optionStay)
					&& stayButtonClicked) {
				canvas.drawBitmap(stayButtonPressed,
						(stayButtonEnabled.getWidth() * 2) + 10, screenH
								- scaledCardH - whitePaint.getTextSize()
								+ (60 * scale), null);
			}
			// display opiton split if available and myturn
			if (myOptions.contains(TurnOptions.optionSplit)
					&& !splitButtonClicked) {
				canvas.drawBitmap(splitButtonEnabled,
						splitButtonEnabled.getWidth() + 5, screenH
								- scaledCardH - whitePaint.getTextSize()
								+ (60 * scale), null);
			} else if (myOptions.contains(TurnOptions.optionSplit)
					&& !splitButtonClicked) {
				canvas.drawBitmap(splitButtonPressed,
						splitButtonPressed.getWidth() + 5, screenH
								- scaledCardH - whitePaint.getTextSize()
								+ (60 * scale), null);

			}
		}
		// If there's a card currently in flight, draw it.
		if (inFlight != null) {
			inFlight.draw(canvas);
			if (inFlight.hasArrived()) {
				deck.remove(0);
				if (soundEnabled) {
					sounds.play(tapSound, volume, volume, 1, 0, 1);
				}
				// When the card lands, it's only then in the players/opps hand.
				// So now we refresh their options
				inFlight = null;
				if (myTurn) {
					refreshMyOptions();
				} else {
					// when the card gets back, computer analyzes his options
					// and acts
					refreshComputerOptions();
					makeComputerPlay();
				}

			}
		}

		invalidate();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int eventaction = event.getAction();
		int X = (int) event.getX();
		int Y = (int) event.getY();
		switch (eventaction) {
		case MotionEvent.ACTION_DOWN:
			if (myTurn && inFlight == null) {
				// handle clicking hit button
				if ((X > 0 && X < hitButtonEnabled.getWidth() && (Y > screenH
						- scaledCardH - whitePaint.getTextSize() + (60 * scale) && Y < screenH
						- scaledCardH
						- whitePaint.getTextSize()
						+ (60 * scale)
						+ hitButtonEnabled.getHeight()))
						&& myOptions.contains(TurnOptions.optionHit)) {
					hitButtonClicked = true;
				}
				// clicking split button
				if (((X > hitButtonEnabled.getWidth() + 5 && X < screenW) && (Y > screenH
						- scaledCardH - whitePaint.getTextSize() + (60 * scale) && Y < screenH
						- scaledCardH
						- whitePaint.getTextSize()
						+ (60 * scale)
						+ hitButtonEnabled.getHeight()))
						&& myOptions.contains(TurnOptions.optionSplit)) {
					splitButtonClicked = true;
					// split;
				}
				// clicking stay button
				if (((X > (hitButtonEnabled.getWidth() * 2) + 10 && X < screenW) && (Y > screenH
						- scaledCardH - whitePaint.getTextSize() + (60 * scale) && Y < screenH
						- scaledCardH
						- whitePaint.getTextSize()
						+ (60 * scale)
						+ hitButtonEnabled.getHeight()))
						&& myOptions.contains(TurnOptions.optionStay)) {
					stayButtonClicked = true;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (myTurn && inFlight == null) {
				// hit button
				if ((X > 0 && X < hitButtonEnabled.getWidth() && (Y > screenH
						- scaledCardH - whitePaint.getTextSize() + (60 * scale) && Y < screenH
						- scaledCardH
						- whitePaint.getTextSize()
						+ (60 * scale)
						+ hitButtonEnabled.getHeight()))
						&& myOptions.contains(TurnOptions.optionHit)) {
					if (soundEnabled) {
						sounds.play(blipSound, volume, volume, 1, 0, 1);
					}
					hitButtonClicked = true;
					Card card = deck.get(0);
					// starting point deck
					Point start = new Point((screenW / 2) - cardBack.getWidth()
							- 10, (screenH / 2) - (cardBack.getHeight() / 2));
					// Get ending point my hand
					Point dest = new Point(0, (int) (screenH - scaledCardH
							- whitePaint.getTextSize() - (50 * scale)));
					inFlight = new ACard(card, start, dest, myHand, statTracker);
				}
				// split button
				if (((X > hitButtonEnabled.getWidth() + 5 && X < screenW) && (Y > screenH
						- scaledCardH - whitePaint.getTextSize() + (60 * scale) && Y < screenH
						- scaledCardH
						- whitePaint.getTextSize()
						+ (60 * scale)
						+ hitButtonEnabled.getHeight()))
						&& myOptions.contains(TurnOptions.optionSplit)) {
					if (soundEnabled) {
						sounds.play(blipSound, volume, volume, 1, 0, 1);
					}
					splitButtonClicked = true;
					// divides your hand into two, draws a new card into each,
					// and
					// adds a new split deck for each.
					List<Card> tmpHand = new ArrayList<Card>();
					tmpHand.add(myHand.get(0));
					myHand.remove(0);
					// TODO: draw animated card to appropriate spots.
					// Take card from myHand, slide to side, deal additional
					// card
					// next to hand
					// take additional card moved tos ide and put deal another
					// card
					// next to it
					drawCard(myHand);
					drawCard(tmpHand);
					splitHands.add(myHand);
					splitHands.add(tmpHand);
					// split = true;

				}
				// stay button
				if (((X > (hitButtonEnabled.getWidth() * 2) + 10 && X < screenW) && (Y > screenH
						- scaledCardH - whitePaint.getTextSize() + (60 * scale) && Y < screenH
						- scaledCardH
						- whitePaint.getTextSize()
						+ (60 * scale)
						+ hitButtonEnabled.getHeight()))
						&& myOptions.contains(TurnOptions.optionStay)) {
					if (soundEnabled) {
						sounds.play(blipSound, volume, volume, 1, 0, 1);
					}
					stayButtonClicked = true;
					myTurn = false;

					makeComputerPlay();
				}
				// unclick buttons
				stayButtonClicked = false;
				hitButtonClicked = false;
				splitButtonClicked = false;
			}
		}

		invalidate();
		return true;
	}

	/**
	 * Get new turnOption object for my hand, and update options I should have
	 */
	private void refreshMyOptions() {
		myOpt = new TurnOptions(myHand, myContext);
		myOptions = myOpt.getTurnOptions();
		if (myOpt.isBust()) {
			myTurn = false;
			makeComputerPlay();
		}
		if (myOpt.getDeckValue() == 21) {
			if (soundEnabled) {
				sounds.play(twentyOneSound, volume, volume, 1, 0, 1);
			}
		}
	}

	/**
	 * Get new turnOption object for computers hand, and update action computer
	 * should take
	 */
	private void refreshComputerOptions() {
		compOpt = new ComputerTurnOptions(oppHand);
		compOption = compOpt.getCompOption();
	}

	/**
	 * Init the deck of cards to be played with
	 */
	private void initCards() {
		for (int i = 0; i < 4; i++) {
			for (int j = 102; j < 115; j++) {
				int tempId = j + (i * 100);
				Card tempCard = new Card(tempId);

				// pkgName is the java class package name
				String pkgName = myContext.getPackageName();

				// 1) getResources() or myContext.getResources() doesn't matter
				// 2) nowhere is ".png" mentioned
				int resourceId = myContext.getResources().getIdentifier(
						"card" + tempId, "drawable", pkgName);

				// decodeResource apparently interprets resourceId
				Bitmap tempBitmap = BitmapFactory.decodeResource(
						myContext.getResources(), resourceId);
				scaledCardW = (int) (screenW / 8);
				scaledCardH = (int) (scaledCardW * 1.28);
				Bitmap scaledBitmap = Bitmap.createScaledBitmap(tempBitmap,
						scaledCardW, scaledCardH, false);
				tempCard.setBitmap(scaledBitmap);
				deck.add(tempCard);
			}
		}
	}

	/**
	 * deprecated, use new Acard
	 * 
	 * @param handToDraw
	 */
	private void drawCard(List<Card> handToDraw) {
		handToDraw.add(0, deck.get(0));
		statTracker.updateStatTracker(deck.get(0));
		deck.remove(0);
		if (deck.isEmpty()) {
			initCards();
		}
	}

	/**
	 * Deal cards one at a time to player and dealer
	 */
	private void dealCards() {
		if (cardsDealt == 1) {
			Collections.shuffle(deck, new Random());
		}

		// starting point deck
		Point start = new Point((screenW / 2) - cardBack.getWidth() - 10,
				(screenH / 2) - (cardBack.getHeight() / 2));
		// Ending points: my hand and dealer hand
		Point destOppHand = new Point(0,
				(int) (whitePaint.getTextSize() + (50 * scale)));
		Point destMyHand = new Point(0, (int) (screenH - scaledCardH
				- whitePaint.getTextSize() - (50 * scale)));
		// after each card is dealt, we have to wait for the inflight card to
		// finish drawing before we send another one.
		// We want to deal the cards 1 to the player, 1 to the dealer, to do
		// this. The player will be dealt on odd numbers of i
		// player will be the odd numbers of i

		// Dealing 1 card to my hand, then one to opp hand
		Card card = deck.get(0);
		// to my hand if odd, to dealer hand if even
		if (!doneDealing) {
			if (inFlight == null && ((cardsDealt & 1) == 1)) {
				inFlight = new ACard(card, start, destMyHand, myHand,
						statTracker);
				cardsDealt++;
			} else if (inFlight == null && ((cardsDealt & 1) == 0)) {
				// first card is face down for the dealer
				if (cardsDealt == 2) {
					inFlight = new BCard(card, start, destOppHand, oppHand,
							cardBack);
				} else {
					inFlight = new ACard(card, start, destOppHand, oppHand,
							statTracker);
				}
				cardsDealt++;
			}
		}
	}

	/**
	 * Make computer take his turn
	 */
	private void makeComputerPlay() {
		// If there's a card in flight the computer won't go
		if (inFlight != null) {
			return;
		}
		// now we can show dealer cards
		showDealerCards = true;
		while (compGo && inFlight == null) {
			switch (compOption) {
			case TurnOptions.optionHit:
				Card card = deck.get(0);
				// starting point deck
				Point start = new Point((screenW / 2) - cardBack.getWidth()
						- 10, (screenH / 2) - (cardBack.getHeight() / 2));
				// Ending point Computers hand
				Point dest = new Point(0,
						(int) (whitePaint.getTextSize() + (50 * scale)));
				inFlight = new ACard(card, start, dest, oppHand, statTracker);

				break;
			case TurnOptions.optionBust:
				compGo = false;
				break;
			case TurnOptions.optionStay:
				compGo = false;
				break;
			}

			invalidate();
		}

		if (compGo == false && !myTurn) {
			if (compOpt.getDeckValue() == 21 && oppHand.size() == 2) {
				Toast.makeText(myContext, "Dealer Hit Blackjack",
						Toast.LENGTH_SHORT).show();
			} else if (compOpt.getDeckValue() == 21) {
				Toast.makeText(myContext, "Dealer hit 21", Toast.LENGTH_SHORT)
						.show();
			}
			endHand();
		}
		invalidate();
	}

	/*
	 * End hand, see which player is closer to 21 or bust, and decide winer
	 */
	private void endHand() {
		myOpt.clearOptions();
		int myScore = myOpt.getDeckValue();
		int compScore = compOpt.getDeckValue();
		int myRange = myScore - 21;
		int compRange = compScore - 21;
		String winner = "";
		if ((!myOpt.isBust() && myRange > compRange)
				|| (!myOpt.isBust() && compOpt.isBust())) {
			winner = WINNER_PLAYER;
			userWins += 1;
		} else if ((!compOpt.isBust() && compRange > myRange)
				|| (myOpt.isBust() && !compOpt.isBust())) {
			winner = WINNER_DEALER;
			dealerWins += 1;
		} else {
			winner = "Nobody";
		}

		showWinnerDialog(winner);
	}

	/*
	 * end of hand show dialog displaying winner with an ok button to start new
	 * hand
	 * 
	 * @param String winner - the player to display won
	 */
	private void showWinnerDialog(String winner) {
		final Dialog endGameDialog = new Dialog(myContext);
		// create dialog box
		endGameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		endGameDialog.setContentView(R.layout.end_game_dialog);
		TextView endGameText = (TextView) endGameDialog
				.findViewById(R.id.endHandText);
		endGameText.setText(winner + " has won!");
		Button okButton = (Button) endGameDialog
				.findViewById(R.id.nextHandButton);
		okButton.setText("New Hand!");
		if (winner.equals(WINNER_PLAYER) && soundEnabled) {
			sounds.play(winnaSound, volume, volume, 1, 0, 1);
		} else if (winner.equals(WINNER_DEALER) && soundEnabled) {
			sounds.play(booSound, volume, volume, 1, 0, 1);
		}
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				endGameDialog.dismiss();
				initNewHand();
			}
		});
		endGameDialog.show();

	}

	/*
	 * init new hand to be dealt, redeals cards to all hands and refreshes
	 * options
	 */
	private void initNewHand() {

		myTurn = true;
		showDealerCards = false;
		// if there's more than 25 cards in the deck continue playing, otherwise
		// reshuffle deck
		if (deck.size() <= 25) {
			deck.addAll(myHand);
			deck.addAll(oppHand);
			deck.addAll(discardPile);
			myHand.clear();
			oppHand.clear();
			discardPile.clear();
			statTracker = new CardStatTracker(scale);
		} else {
			discardPile.addAll(myHand);
			discardPile.addAll(oppHand);
			myHand.clear();
			oppHand.clear();
		}
		doneDealing = false;
		cardsDealt = 1;
		// refreshMyOptions();
		compGo = true;

		refreshComputerOptions();
		if (!myTurn && inFlight == null) {
			makeComputerPlay();
		}
	}
}
