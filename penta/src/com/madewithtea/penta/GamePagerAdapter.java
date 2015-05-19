package com.madewithtea.penta;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.madewithtea.penta.game.GameFragment;
import com.madewithtea.penta.game.RegisterFragment;
import com.madewithtea.penta.highscores.HighscoreFragment;
import com.madewithtea.penta.sound.SoundManager;

public class GamePagerAdapter extends FragmentStatePagerAdapter {
	
	private RegisterFragment mRegisterFragment;
	private GameFragment mGameFragment;
	private HighscoreFragment mHighscoresFragment;
	
    public GamePagerAdapter(FragmentManager fm, SoundManager soundManager) {
        super(fm);
        
		// Initialize fragments
        mRegisterFragment = new RegisterFragment();
		mGameFragment = new GameFragment();
		mHighscoresFragment = new HighscoreFragment();
    }
    
    @Override
    public Fragment getItem(int i) {
    	if(i == 0) {
    		return mRegisterFragment;
    	} 
    	if(i == 1) {
    		return mGameFragment;
    	}
    	else {
    		return mHighscoresFragment;
    		
    	}
    }
    
    @Override
    public int getCount() {
        return 3;
    }
    
    public void onRegistered() {
    	mGameFragment.onRegistered();
    }
}
