package com.madewithtea.penta.game;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.madewithtea.penta.R;
import com.madewithtea.penta.LocalStore;
import com.madewithtea.penta.MainActivity;
import com.madewithtea.penta.network.NetworkManager;
import com.madewithtea.penta.sound.SoundManager;

public class RegisterFragment extends Fragment {

	private static final String TAG = "RegisterFragment";
	
	// UI elements
	private EditText mPlayerEditText;
	private TextView mOutput;
	private Button mSkip, mGo;
	
	// Activiy
	private MainActivity mActivity;

	// SoundManager
	private SoundManager mSoundManager;

	// Local store
	private LocalStore mStore;

	private View mRoot;

	public boolean register() {
		MainActivity activity = ((MainActivity) getActivity());
		NetworkManager network = activity.getNetworkManager();

		String playerName = mPlayerEditText.getText().toString();
		if (playerName.equals("")) {
			mOutput.setText("What is your Player Name?");
			return false;
		}
		if (!network.isOnline()) {
			mOutput.setText("You have to be online to register.");
			return false;
		}	
		network.registerPlayer(playerName, RegisterFragment.this);
		
		return true;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Reference the sound manager, local store
		mActivity = (MainActivity) getActivity();
		mSoundManager = mActivity.getSoundManger();
		mStore = mActivity.getLocalStore();

		// Inflate the view
		int resId = R.layout.fragment_register;
		mRoot = inflater.inflate(resId, container, false);

		// References UI elements
		mPlayerEditText = (EditText) mRoot.findViewById(R.id.playerbox);
		
		mOutput = (TextView) mRoot.findViewById(R.id.register_output);
		mGo = (Button) mRoot.findViewById(R.id.go_btn);
		mSkip = (Button) mRoot.findViewById(R.id.skip_btn);
		
		// Skip button
		mSkip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Tracker tracker = mActivity.getAppTracker();
				tracker.send(new HitBuilders.EventBuilder()
		        .setCategory("Registration")
		        .setAction("skip")
		        .build());
				
				mSoundManager.playClick();

				LinearLayout reg = (LinearLayout) mRoot.findViewById(R.id.registration);
				reg.removeView(mPlayerEditText);
				
			    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
			    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				
				((MainActivity) getActivity()).changeToGame();
			}
		});
		
		// Setting the Go Buttons Listener
		mGo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Tracker tracker = mActivity.getAppTracker();
				tracker.send(new HitBuilders.EventBuilder()
		        .setCategory("Registration")
		        .setAction("register")
		        .build());
				
				mSoundManager.playClick();
				register();
			}
		});
		
		mPlayerEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				mSoundManager.playClick();
				return register();
			}
		});
		
		// Skip to game if network is not available or player registered.
		NetworkManager network = mActivity.getNetworkManager();
		if (!network.isOnline() || mStore.hasPlayerName()) {
			mActivity.changeToGame();
		}

		return mRoot;
	}
	
	public void onUsernameTaken(final String pPlayerName) {
		Tracker tracker = mActivity.getAppTracker();
		tracker.send(new HitBuilders.EventBuilder()
        .setCategory("Registration")
        .setAction("username_taken")
        .setLabel("error")
        .build());
		
		mOutput.setText("The player name is already taken.");
	}
	
	public void onInvalid() {
		Tracker tracker = mActivity.getAppTracker();
		tracker.send(new HitBuilders.EventBuilder()
        .setCategory("Registration")
        .setAction("invalid_username")
        .setLabel("error")
        .build());
		
		mOutput.setText("The player name must be longer than 4" +
		" and at most 8 characters.");
	}
	
	public void onRegistered(final String pPlayerName, final String pPassword) {
		Tracker tracker = mActivity.getAppTracker();
		tracker.send(new HitBuilders.EventBuilder()
        .setCategory("Registration")
        .setAction("registered")
        .build());
		
		// Store password etc.
		mStore.putPassword(pPassword);
		mStore.putPlayerName(pPlayerName);
		
	    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(mPlayerEditText.getWindowToken(), 0);
		
	    // Remove the text field 
		LinearLayout reg = (LinearLayout) mRoot.findViewById(R.id.registration);
		reg.removeView(mPlayerEditText);
	    
		// Change to the Game Fragment
		mActivity.changeToGame();	
		
		// Update the Player Name
		mActivity.onRegistered();	
	}
}
