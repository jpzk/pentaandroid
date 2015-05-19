package com.madewithtea.penta.sound;

import java.io.IOException;

import com.madewithtea.penta.LocalStore;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundManager {
	
	private boolean mute;
	private float volume;
	private SoundThread st;
	private SoundPool pool;
	private AssetManager manager;
	private int onClick, onFail, onSuccess, onSystemClick, onTick, onTimeOut;
	private Context context;

	private LocalStore mStore;
	
	public SoundManager(Context context, AssetManager manager, LocalStore store) {
		this.context = context;
		this.manager = manager;
		this.mute = store.getDefaultMute();
		this.mStore = store;
		
		if(this.mute) {
			this.mute();
		} else {
			this.unmute();
		}
		
	}

	public void load() throws IOException {
		AssetFileDescriptor sfd;

		this.pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		this.st = new SoundThread(this.pool);
		
		sfd = this.manager.openFd("digitsbase.mp3");
		this.onClick = this.pool.load(sfd, 1);

		sfd = this.manager.openFd("s_failure3.mp3");
		this.onFail = this.pool.load(sfd, 1);
		
		sfd = this.manager.openFd("success12.mp3");
		this.onSuccess = this.pool.load(sfd, 1);
		
		sfd = this.manager.openFd("b1.mp3");
		this.onSystemClick = this.pool.load(sfd, 1);
		
		sfd = this.manager.openFd("s_timer3.mp3");
		this.onTick = this.pool.load(sfd, 1);
		
		sfd = this.manager.openFd("s_timeout4.mp3");
		this.onTimeOut = this.pool.load(sfd, 1);
		
	}

	public boolean muteunmute() {
		if(getMuteState()) {
			unmute();
		} else {
			mute();
		}
		return getMuteState();
	}
	
	public boolean getMuteState() {
		return this.mute;
	}
	
	public void mute() {
		this.mute = true;
		this.volume = 0.0f;
		this.mStore.putDefaultMute(true);
	}
	
	public void unmute() {
		this.mute = false;
		this.volume = 1.0f;
		this.mStore.putDefaultMute(false);
	}
	
	public void stopSound() {
		this.st.sounds.add(new SoundItem(true)); // sending kill event
		this.pool.release(); 
	}
	
	public void playTick() {
		this.st.sounds.add(new SoundItem(this.onTick, this.volume));
	}
	
	public void playTimeOut() {
		this.st.sounds.add(new SoundItem(this.onTimeOut, this.volume));
	}
	
	public void startSound() {
		this.st.start(); // start thread
	}
	
	public void playClick() {
		this.st.sounds.add(new SoundItem(this.onClick, this.volume));
	}
	
	public void playSystemClick() {
		this.st.sounds.add(new SoundItem(this.onSystemClick, this.volume));
	}
	
	public void playSuccess() {
		this.st.sounds.add(new SoundItem(this.onSuccess, this.volume));
	}
	
	public void playFail() {
		this.st.sounds.add(new SoundItem(this.onFail, this.volume));
	}
}
