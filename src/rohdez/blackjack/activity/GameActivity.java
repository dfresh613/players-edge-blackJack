package rohdez.blackjack.activity;

import rohdez.blackjack.view.GameView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


public class GameActivity extends Activity{
	//Called on first Activity Create
	private GameView gView;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.gView = new GameView(this);
		gView.setKeepScreenOn(true);
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(gView);
	}
	
	protected void onResume(){
		super.onResume();
	       gView.refreshPreferences(this);
	    }
	
}
