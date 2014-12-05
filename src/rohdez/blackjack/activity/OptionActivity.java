package rohdez.blackjack.activity;

import rohdez.blackjack.R;
import rohdez.blackjack.storage.UserPreferences;
import rohdez.blackjack.view.OptionView;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class OptionActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OptionView oView = new OptionView(this);
		oView.setKeepScreenOn(true);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(oView);
		
	}	
	
}
