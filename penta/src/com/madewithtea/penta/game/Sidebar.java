package com.madewithtea.penta.game;

import com.madewithtea.penta.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.internal.mf;
import com.madewithtea.penta.MainActivity;
import com.madewithtea.penta.sound.SoundManager;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Sidebar {
	
	private static final String TAG = "Sidebar";
	
	private GameFragment mParent;
	private SoundManager mSoundManager;
	private Resources mRes;
	private Help mHelp;
	private IOBar mBar;
	
	public Sidebar(GameFragment fragment, View root, IOBar pBar, Help pHelp) {	

		mBar = pBar;
		mParent = fragment;
		mRes = fragment.getResources();
		
		MainActivity activity = ((MainActivity) fragment.getActivity());
		mSoundManager = activity.getSoundManger();
		mHelp = pHelp;
		
		final ImageButton muteButton;
		
		// Mute button
		muteButton = (ImageButton) root.findViewById(R.id.mute_btn);
		
		if(!mSoundManager.getMuteState()) {
			Drawable sound = mRes.getDrawable(R.drawable.sound);
			muteButton.setImageDrawable(sound);
		} else {
			Drawable sound = mRes.getDrawable(R.drawable.sound2);
			muteButton.setImageDrawable(sound);
		}
		
		// Get previous mute state
		muteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Tracker tracker = ((MainActivity) mParent.getActivity()).getAppTracker();
				tracker.send(new HitBuilders.EventBuilder()
		        .setCategory("Game")
		        .setAction("pressed_mute")
		        .build());
				
				mSoundManager.playSystemClick();
				boolean state = mSoundManager.muteunmute();
				mSoundManager.playSystemClick();
				if(!state) {
					Drawable sound = mRes.getDrawable(R.drawable.sound);
					muteButton.setImageDrawable(sound);
				} else {
					Drawable sound = mRes.getDrawable(R.drawable.sound2);
					muteButton.setImageDrawable(sound);
				}
			}
		});
		
		// Help Button
		ImageButton helpBtn = (ImageButton) root.findViewById(R.id.help_btn);
		helpBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Tracker tracker = ((MainActivity) mParent.getActivity()).getAppTracker();
				tracker.send(new HitBuilders.EventBuilder()
		        .setCategory("Game")
		        .setAction("pressed_help")
		        .build());
				
				mSoundManager.playSystemClick();
				Log.v("Sidebar", "help button pressed");
				if(mHelp.isHelpOpen()) {
					mHelp.hideHelp();
					mBar.showBottomLine();
					mParent.endMatch();
				} else {
					mParent.endMatch();
					mHelp.showHelp();
					mBar.hideBottomLine();
				}
			}
		});
		
		// Restart game
		ImageButton initGameBtn = (ImageButton) root.findViewById(R.id.initGameBtn);
		initGameBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Tracker tracker = ((MainActivity) mParent.getActivity()).getAppTracker();
				tracker.send(new HitBuilders.EventBuilder()
		        .setCategory("Game")
		        .setAction("pressed_restart")
		        .build());
				
				mSoundManager.playSystemClick();
				mParent.endMatch();
				mHelp.hideHelp();
				mBar.showBottomLine();
				mParent.endMatch();
			}
		});
		
		// Highscore button
		ImageButton btn = (ImageButton) root.findViewById(R.id.highscore_btn);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Tracker tracker = ((MainActivity) mParent.getActivity()).getAppTracker();
				tracker.send(new HitBuilders.EventBuilder()
		        .setCategory("Game")
		        .setAction("pressed_highscore")
		        .build());
				
				mSoundManager.playSystemClick();
				mParent.endMatch();
				mHelp.hideHelp();
				mBar.showBottomLine();
				((MainActivity) mParent.getActivity()).changeToHighscore();
			}
		});
	}
}
