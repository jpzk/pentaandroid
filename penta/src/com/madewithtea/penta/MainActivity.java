package com.madewithtea.penta;

import java.io.IOException;

import com.madewithtea.penta.R;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.madewithtea.penta.highscores.HighscoreFragment;
import com.madewithtea.penta.network.NetworkManager;
import com.madewithtea.penta.sound.SoundManager;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	// Network
	NetworkManager mNetwork;

	// LocalStore
	LocalStore mStore;

	// SoundManager
	SoundManager mSoundManager;

	// Pager Adapter
	GamePagerAdapter mPagerAdapter;

	// ViewPager
	ViewPager mViewPager;

	// AdView
	private AdView mAdView;
	private boolean mAdActive;

	// Tracker specific stuff
	private Tracker mAppTracker = null;

	// Get the application wide tracker
	public synchronized Tracker getAppTracker() {
		if(mAppTracker == null) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			mAppTracker = analytics.newTracker(R.xml.app_tracker);
		}
		return mAppTracker;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);

		// Initialize local storage
		this.mStore = new LocalStore(this);

		// Initialize SoundManager
		this.mSoundManager = new SoundManager(getApplicationContext(),
				getAssets(), mStore);
		try {
			this.mSoundManager.load();
		} catch (IOException e) {
			e.printStackTrace();
		} // load sound, pool etc.

		this.mSoundManager.startSound(); // start thread

		// Initialize the network manager
		this.mNetwork = new NetworkManager(this);

		// ViewPager and its adapters use support library
		// fragments, so use getSupportFragmentManager.
		FragmentManager fm = getSupportFragmentManager();
		mPagerAdapter = new GamePagerAdapter(fm, this.mSoundManager);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mPagerAdapter);

		// mAdActive= false;
		//
		// if(mAdActive) {
		// // Set Ads
		// mAdView = new AdView(this);
		// mAdView.setAdUnitId("ca-app-pub-2989103197995605/5567369774");
		// mAdView.setAdSize(AdSize.SMART_BANNER);
		//
		// // Initiate a generic request.
		// AdRequest request = new AdRequest.Builder()
		// .addTestDevice(AdRequest.DEVICE_ID_EMULATOR) // All emulators
		// .addTestDevice("EAC7E252F6AAEFA378521AC1ECE3829B") // Moto G
		// .build();
		//
		// mAdView.loadAd(request);
		//
		// // Lookup your LinearLayout assuming it's been given
		// // the attribute android:id="@+id/mainLayout".
		// LinearLayout layout = (LinearLayout) findViewById(R.id.container);
		// layout.addView(mAdView);
		// }
		
	}

	// Standard Activity Lifecyle
	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Stop sound engine
		this.mSoundManager.stopSound();

		// Stop advertisement
		if (mAdActive) {
			mAdView.destroy();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mAdActive)
			mAdView.pause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mAdActive)
			mAdView.resume();
	}

	@Override
	public void onBackPressed() {
		changeToGame();
	}

	public void changeToRegister() {
		mViewPager.setCurrentItem(0);
	}

	public void changeToGame() {
		Tracker tracker = getAppTracker();
        tracker.setScreenName("PentaApp GameView");
        tracker.send(new HitBuilders.AppViewBuilder().build());
		
		mViewPager.setCurrentItem(1);
	}

	public void onRegistered() {
		mPagerAdapter.onRegistered();
	}

	public void changeToHighscore() {
		Tracker tracker = getAppTracker();
        tracker.setScreenName("PentaApp HighscoreView");
        tracker.send(new HitBuilders.AppViewBuilder().build());
		
		if (!mNetwork.isOnline()) {
			Context context = getApplicationContext();
			CharSequence text = "You have to be online.";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		} else {
			HighscoreFragment frag = (HighscoreFragment) mPagerAdapter
					.getItem(2);
			frag.onSwitch();
			mViewPager.setCurrentItem(2);
		}
	}

	public SoundManager getSoundManger() {
		return mSoundManager;
	}

	public LocalStore getLocalStore() {
		return mStore;
	}

	public NetworkManager getNetworkManager() {
		return mNetwork;
	}
}
