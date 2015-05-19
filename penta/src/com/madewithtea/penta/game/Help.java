package com.madewithtea.penta.game;

import java.util.ArrayList;
import java.util.List;

import com.madewithtea.penta.R;
import com.madewithtea.penta.sound.SoundManager;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class Help {
	
	private SoundManager mSoundManager;
	private Resources res;
	private ImageView mTooltip;
	private int mCurrent;
	private int mAmountHelps;
	private boolean mIsHelpOpen;
	private GameFragment mParent; 
	private IOBar mBar;
	
	public Help(GameFragment fragment, View root, IOBar pBar, SoundManager pSoundManager) {
		
		mBar = pBar;
		mParent = fragment;
		mSoundManager = pSoundManager;
		res = mParent.getResources();
		
		mIsHelpOpen = false;
		mAmountHelps = 6;
		mTooltip = (ImageView) root.findViewById(R.id.help);
		mTooltip.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View pTooltip) {
				mSoundManager.playSystemClick();
				if(mCurrent > mAmountHelps) {
					hideHelp();
				} else {
					mCurrent++;
					if(mCurrent == 3) {
						List<Integer> guess = new ArrayList<Integer>();
						guess.add(2);
						guess.add(3);
						guess.add(1);
						guess.add(4);
						guess.add(5);
						
						List<Integer> mask = new ArrayList<Integer>();
						mask.add(2);
						mask.add(3);
						mask.add(1);
						mask.add(1);
						mask.add(1);
												
						mBar.showHint(guess, mask);
					}
					if(mCurrent == 4) {
						List<Integer> guess = new ArrayList<Integer>();
						guess.add(2);
						guess.add(3);
						guess.add(2);
						guess.add(4);
						guess.add(5);
						
						List<Integer> mask = new ArrayList<Integer>();
						mask.add(2);
						mask.add(3);
						mask.add(2);
						mask.add(1);
						mask.add(1);
												
						mBar.showHint(guess, mask);
					}
					if(mCurrent == 5) {
						List<Integer> guess = new ArrayList<Integer>();
						guess.add(2);
						guess.add(3);
						guess.add(7);
						guess.add(4);
						guess.add(5);
						
						List<Integer> mask = new ArrayList<Integer>();
						mask.add(2);
						mask.add(3);
						mask.add(3);
						mask.add(1);
						mask.add(1);
												
						mBar.showHint(guess, mask);
					}
					
					if(mCurrent == 6) {
						List<Integer> guess = new ArrayList<Integer>();
						guess.add(6);
						guess.add(3);
						guess.add(7);
						guess.add(8);
						guess.add(9);
						
						List<Integer> mask = new ArrayList<Integer>();
						mask.add(3);
						mask.add(3);
						mask.add(3);
						mask.add(3);
						mask.add(3);
												
						mBar.showHint(guess, mask);
					}
					
					mTooltip.setImageDrawable(getNumberDrawable(mCurrent));
				}
			}
		});
	}
	
	private Drawable getNumberDrawable(int number) {
		String fieldName = "@drawable/manual" + String.valueOf(number);
		String pkgName = mParent.getActivity().getPackageName();
		int identifier = res.getIdentifier(fieldName, null, pkgName);
		return res.getDrawable(identifier);
	}
	
	public boolean isHelpOpen() {
		return mIsHelpOpen;
	}
	
	public void hideHelp() {
		mBar.showBottomLine();
		mTooltip.setVisibility(View.GONE);
		mCurrent = 1;
		mIsHelpOpen = false;
		mParent.endMatch();
	}
	
	public void showHelp() {
		mBar.hideBottomLine();
		mParent.endMatch();
		mTooltip.setVisibility(View.VISIBLE);
		mCurrent = 1;
		mTooltip.setImageDrawable(getNumberDrawable(mCurrent));
		mIsHelpOpen = true;
	}
}
