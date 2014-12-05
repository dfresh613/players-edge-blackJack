package rohdez.blackjack.view;

import rohdez.blackjack.R;
import rohdez.blackjack.activity.GameActivity;
import rohdez.blackjack.activity.OptionActivity;
import rohdez.blackjack.storage.UserPreferences;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.View;
/**
 * Title View for Players Edge Blackjack
 * @author Doug Rohde
 *
 */
public class TitleView extends View {
	private Bitmap titleGraphic;
	private Bitmap playButtonUp;
	private Bitmap playButtonDown;
	private Bitmap optionButtonUp;
	private Bitmap optionButtonDown;
	private boolean playButtonPressed;
	private boolean optionButtonPressed;
	private Context myContext;
	private float volume;
	private SoundPool sounds;
	private int blipSound;
	private boolean soundEnabled;

	private int screenW;
	private int screenH;
	private float scale;
	private Paint titlePaint;
	private int titleTextHeight = 0;

	public TitleView(Context context) {
		super(context);
		myContext = context;
		titleGraphic = BitmapFactory.decodeResource(getResources(),
				R.drawable.title_graphic);
		playButtonUp = BitmapFactory.decodeResource(getResources(),
				R.drawable.play_button_up);
		playButtonDown = BitmapFactory.decodeResource(getResources(),
				R.drawable.play_button_down);
		optionButtonUp = BitmapFactory.decodeResource(getResources(),
				R.drawable.option_button_up);
		optionButtonDown = BitmapFactory.decodeResource(getResources(),
				R.drawable.option_button_down);

		sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		blipSound = sounds.load(myContext, R.raw.blip, 1);
		AudioManager audioManager = (AudioManager) myContext
				.getSystemService(Context.AUDIO_SERVICE);
		volume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		refreshPreferences(context);
		initTitle();

	}

	/**
	 * method to reload user preferences, should be called from activity when
	 * starting up again
	 */
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		screenW = w;
		screenH = h;
		scale = myContext.getResources().getDisplayMetrics().density;

	}

	/**
	 * refreshes preferences to be used in View. Called when initially created
	 * and when onResume
	 * 
	 * @param context
	 */
	public void refreshPreferences(Context context) {
		// Get user preferences from sharedPreferences and set whether sound is
		// enabled
		UserPreferences userPrefs = new UserPreferences(context);
		this.soundEnabled = userPrefs.getSoundEnabled();
	}

	/*
	 * Initializes the paint and style of title
	 */
	private void initTitle() {
		titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		titlePaint.setColor(Color.BLUE);

		float titleTextHeight = 150;
		titlePaint.setTextSize(titleTextHeight);
		titlePaint.setStyle(Paint.Style.FILL);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.rgb(0, 153, 0));
		canvas.drawBitmap(titleGraphic,
				(screenW - titleGraphic.getWidth()) / 2, scale * 300, null);
		// draw title
		canvas.drawText("Blackjack", (float) (scale * 50),
				(float) (scale * 175), titlePaint);
		// if play button pressed dispay the pressed graphic
		if (playButtonPressed) {
			canvas.drawBitmap(playButtonDown,
					(screenW - playButtonUp.getWidth()) / 2,
					(int) (screenH * 0.7), null);
		} else {
			canvas.drawBitmap(playButtonUp,
					(screenW - playButtonUp.getWidth()) / 2,
					(int) (screenH * 0.7), null);
		}
		// option button pressed, display pressed graphic
		if (optionButtonPressed) {
			canvas.drawBitmap(optionButtonDown,
					(screenW - optionButtonUp.getWidth()) / 2,
					(int) (screenH * 0.85), null);
		} else {
			canvas.drawBitmap(optionButtonUp,
					(screenW - optionButtonDown.getWidth()) / 2,
					(int) (screenH * 0.85), null);

		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		int eventaction = event.getAction();
		int X = (int) event.getX();
		int Y = (int) event.getY();

		switch (eventaction) {
		case MotionEvent.ACTION_DOWN:
			// play button
			if (X > (screenW - playButtonUp.getWidth()) / 2
					&& X < (screenW - playButtonUp.getWidth()) / 2
							+ playButtonUp.getWidth()
					&& Y > (int) (screenH * 0.7)
					&& Y < (int) (screenH * 0.7) + playButtonUp.getHeight()) {
				playButtonPressed = true;
			}
			// option button;
			if (X > (screenW - optionButtonUp.getWidth()) / 2
					&& X < (screenW - optionButtonUp.getWidth()) / 2
							+ optionButtonUp.getWidth()
					&& Y > (int) (screenH * 0.85)
					&& Y < (int) (screenH * 0.85) + optionButtonUp.getHeight()) {
				optionButtonPressed = true;
			}
			break;

		case MotionEvent.ACTION_MOVE:
			break;

		case MotionEvent.ACTION_UP:
			if (playButtonPressed) {
				Intent gameIntent = new Intent(myContext, GameActivity.class);
				myContext.startActivity(gameIntent);

				if (soundEnabled) {
					sounds.play(blipSound, volume, volume, 1, 0, 1);
				}
			}
			if (optionButtonPressed) {
				Intent optionIntent = new Intent(myContext,
						OptionActivity.class);
				myContext.startActivity(optionIntent);
				if (soundEnabled) {
					sounds.play(blipSound, volume, volume, 1, 0, 1);
				}

			}
			playButtonPressed = false;
			optionButtonPressed = false;
			break;
		}
		invalidate();
		return true;
	}

}
