package rohdez.blackjack.view;

import rohdez.blackjack.R;
import rohdez.blackjack.storage.UserPreferences;
import android.content.Context;
import android.content.SharedPreferences;
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
 * Option View for Players Edge Blackjack
 * @author Doug Rohde
 *
 */
public class OptionView extends View {
	private boolean soundButtonPressed;
	private boolean statTrackerButtonPressed;
	private Context myContext;
	private float volume;
	private SoundPool sounds;
	private int blipSound;
	private Paint boxPaint;
	private Paint boxClickedPaint;
	private Paint buttonLabelPaint;
	private Paint buttonLabelClickedPaint;
	private int screenW;
	private int screenH;
	private float scale;
	private Bitmap soundEnabledButton;
	private Bitmap soundDisabledButton;
	private Bitmap trackerDisabledButton;
	private Bitmap trackerEnabledButton;
	private boolean soundEnabled = true;
	private boolean trackerEnabled = true;
	private Paint titlePaint;
	private int titleTextHeight = 0;

	public OptionView(Context context) {
		super(context);
		myContext = context;
		initTitle();
		// init buttons to be used
		soundEnabledButton = BitmapFactory.decodeResource(getResources(),
				R.drawable.soundenabled);
		soundDisabledButton = BitmapFactory.decodeResource(getResources(),
				R.drawable.sounddisabled);
		trackerEnabledButton = BitmapFactory.decodeResource(getResources(),
				R.drawable.trackerenabled);
		trackerDisabledButton = BitmapFactory.decodeResource(getResources(),
				R.drawable.trackerdisabled);
		// init sounds and audiomanager
		sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		blipSound = sounds.load(myContext, R.raw.blip, 1);
		AudioManager audioManager = (AudioManager) myContext
				.getSystemService(Context.AUDIO_SERVICE);
		volume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);

		// Get options from persistant storage
		UserPreferences userPrefs = new UserPreferences(myContext);

		this.soundEnabled = userPrefs.getSoundEnabled();
		this.trackerEnabled = userPrefs.getTrackerEnabled();
	}

	/**
	 * send the sound boolean here to flip the bit and change true/false
	 * 
	 * @param soundBooleanToChange
	 */
	public void changeSoundPref(boolean soundBooleanToChange) {
		// flips boolean
		this.soundEnabled = soundBooleanToChange;
		this.soundEnabled ^= true;
		SharedPreferences sharedPref = myContext.getSharedPreferences(
				UserPreferences.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean(UserPreferences.SOUND_PREF, soundEnabled);
		editor.commit();
	}

	/**
	 * Send tracker boolean here to flip the bit and change true/false
	 * 
	 * @param trackerBooleanToChange
	 */
	public void changeTrackerPref(boolean trackerBooleanToChange) {
		this.trackerEnabled = trackerBooleanToChange;
		this.trackerEnabled ^= true;
		SharedPreferences sharedPref = myContext.getSharedPreferences(
				UserPreferences.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean(UserPreferences.TRACKER_PREF, trackerEnabled);
		editor.commit();
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		screenW = w;
		screenH = h;
		scale = myContext.getResources().getDisplayMetrics().density;
	}

	/**
	 * set whether tracker is enabled
	 * 
	 * @param soundEnabled
	 */
	public void setSound(boolean soundEnabled) {
		this.soundEnabled = soundEnabled;
	}

	/**
	 * set Sound
	 * 
	 * @param trackerEnabled
	 */
	public void setTracker(boolean trackerEnabled) {
		this.trackerEnabled = trackerEnabled;
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
		// draw title
		canvas.drawText("Options", (float) (scale * 150),
				(float) (scale * 175), titlePaint);

		// draw buttons to click when enabling/disabling tracker and sounds.
		// Which button is displayed is determined by the class
		// boolean for soundEnabled or trackerEnabled. Set by getting from
		// sharedPreferences
		if (soundEnabled) {
			canvas.drawBitmap(soundEnabledButton,
					(screenW - soundEnabledButton.getWidth()) / 2,
					(int) (screenH * 0.7), null);
		} else {
			canvas.drawBitmap(soundDisabledButton,
					(screenW - soundEnabledButton.getWidth()) / 2,
					(int) (screenH * 0.7), null);
		}

		if (trackerEnabled) {
			canvas.drawBitmap(trackerEnabledButton,
					(screenW - trackerEnabledButton.getWidth()) / 2,
					(int) (screenH * 0.85), null);
		} else {
			canvas.drawBitmap(trackerDisabledButton,
					(screenW - trackerEnabledButton.getWidth()) / 2,
					(int) (screenH * 0.85), null);

		}

	}

	public boolean onTouchEvent(MotionEvent event) {
		int eventaction = event.getAction();
		int X = (int) event.getX();
		int Y = (int) event.getY();

		switch (eventaction) {
		case MotionEvent.ACTION_DOWN:
			// if clicking sound button, then will change class boolean of
			// soundEnabled to false
			if (X > (screenW - soundEnabledButton.getWidth()) / 2
					&& X < (screenW - soundEnabledButton.getWidth()) / 2
					+ soundEnabledButton.getWidth()
					&& Y > (int) (screenH * 0.7)
					&& Y < (int) (screenH * 0.7)
					+ soundEnabledButton.getHeight()) {
				changeSoundPref(soundEnabled);
				if (soundEnabled) {
					sounds.play(blipSound, volume, volume, 1, 0, 1);

				}
			}
			// if clicking tracker button, then will change class boolean of
			// trackerEnabled to false
			if (X > (screenW - soundEnabledButton.getWidth()) / 2
			&& X < (screenW - soundEnabledButton.getWidth()) / 2
			+ soundEnabledButton.getWidth()
			&& Y > (int) (screenH * 0.85)
			&& Y < (int) (screenH * 0.85)
			+ soundEnabledButton.getHeight()) {
				changeTrackerPref(trackerEnabled);
				if (soundEnabled) {
					sounds.play(blipSound, volume, volume, 1, 0, 1);
				}
			}
			break;

		case MotionEvent.ACTION_MOVE:
			break;

		case MotionEvent.ACTION_UP:

		}
		invalidate();
		return true;
	}

}
