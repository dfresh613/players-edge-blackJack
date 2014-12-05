package rohdez.blackjack.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class to pull static vars from when interacting with persistant storage and
 * pulling files and settings
 * 
 * @author Doug Rohde
 * 
 */
public class UserPreferences {
	public static final String SHARED_PREFS_FILE = "bjPrefsFile";
	public static final String SOUND_PREF = "soundPreference";
	public static final String TRACKER_PREF = "trackerPreference";
	public static final String reloadPrefs = "reloadPrefs";
	public boolean soundEnabled;
	public boolean trackerEnabled;

	public UserPreferences(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(
				SHARED_PREFS_FILE, Context.MODE_PRIVATE);
		this.soundEnabled = sharedPref.getBoolean(SOUND_PREF, true);
		this.trackerEnabled = sharedPref.getBoolean(TRACKER_PREF, true);
	}

	/**
	 * get value for whether the soundValue is enabled
	 * 
	 * @return
	 */
	public boolean getSoundEnabled() {
		return soundEnabled;
	}

	/**
	 * get value for whether the tracker value is enabled
	 * 
	 * @return trackerEnabled
	 */
	public boolean getTrackerEnabled() {
		return trackerEnabled;
	}
}
