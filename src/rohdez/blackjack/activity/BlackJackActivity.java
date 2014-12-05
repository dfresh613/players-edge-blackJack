package rohdez.blackjack.activity;

import rohdez.blackjack.R;
import rohdez.blackjack.view.GameView;
import rohdez.blackjack.view.TitleView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class BlackJackActivity extends Activity{
private TitleView tView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		this.tView = new TitleView(this);
		tView.setKeepScreenOn(true);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(tView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.black_jack, menu);
		return true;
	}
	
	
	protected void onResume(){
		super.onResume();
	       tView.refreshPreferences(this);
	    }
	
	
}
