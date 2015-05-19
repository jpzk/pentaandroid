package com.madewithtea.penta;

import android.app.Activity;
import android.content.SharedPreferences;

public class LocalStore {
	
	public static final String PREFS_NAME = "Pentaxxxx";
	
	// Keys
	public static final String PLAYER_KEY = "playerName";
	public static final String BESTSCORE_KEY = "bestScore";
	public static final String MUTE_KEY = "defaultMute";
	public static final String PW_KEY = "password";
	public static final String FIRST_PLAY_KEY = "firstplay";
	
	// Defaults
	public static final int BESTSCORE_DEFAULT = 0;
	public static final String PLAYER_DEFAULT = "player";
	public static final boolean MUTE_DEFAULT = false;
	public static final boolean FIRST_PLAY_DEFAULT = true;
	
	private SharedPreferences mSettings;
	
	public LocalStore(Activity activity) {
		mSettings = activity.getSharedPreferences(PREFS_NAME, 0);
	}
	
	public boolean getDefaultMute() {
		if(!mSettings.contains(MUTE_KEY)) {
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putBoolean(MUTE_KEY, MUTE_DEFAULT);
			editor.commit();
			return false;
		} else {
			return mSettings.getBoolean(MUTE_KEY, false);
		}
	}
	
	public int getBestScore() {
		if(!mSettings.contains(BESTSCORE_KEY)) {
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putInt(BESTSCORE_KEY, BESTSCORE_DEFAULT);
			editor.commit();
			return BESTSCORE_DEFAULT;
		} else {
			return mSettings.getInt(BESTSCORE_KEY, 0);
		}
	}
	
	public void putBestScore(int score) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putInt(BESTSCORE_KEY, score);
		editor.commit();
	}
	
	public String getPassword() {
		return mSettings.getString(PW_KEY, "");
	}
	
	public void putPassword(String pKey) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString(PW_KEY, pKey);
		editor.commit();
	}
	
	public String getPlayerName() {
		return mSettings.getString(PLAYER_KEY, PLAYER_DEFAULT);
	}
	
	public boolean hasPlayerName() {
		return mSettings.contains(PLAYER_KEY);
	}
	
	public boolean isFirstPlay() {	
		if(!mSettings.contains(FIRST_PLAY_KEY)) {
			return true;
		}
		return mSettings.getBoolean(FIRST_PLAY_KEY, false);
	}
	
	public void putFirstPlay(boolean firstPlay) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean(FIRST_PLAY_KEY, firstPlay);
		editor.commit();
	}
	
	public void putPlayerName(String pPlayerName) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString(PLAYER_KEY, pPlayerName);
		editor.commit();
	}

	public void putDefaultMute(boolean mute) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean(MUTE_KEY, mute);
		editor.commit();
	}
}
