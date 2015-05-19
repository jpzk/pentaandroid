package com.madewithtea.penta.game;

import android.os.SystemClock;
import android.util.Log;

public class TimerRunnable implements Runnable {
	
	private Timer mTimer;
	private long mLastRealtime = 0;
	private long mElapsedRealtime = 0;
	private float begin = 9000.0f;
	private float end = 10000.0f;
	
	private static final String TAG = "TimerRunnable";
	
	public TimerRunnable(Timer timer) {
		mTimer = timer;
		mLastRealtime = SystemClock.elapsedRealtime();
	}
	
	@Override
	public void run() {
		// Check if time is up.
		long now = SystemClock.elapsedRealtime();
		if(mTimer.getTimeBudget() - (now - mLastRealtime) < 0) {
			Log.v(TAG, "Time is up, sending end match signal");
			mTimer.endMatchSignal();
			return;
		}
		
		now = SystemClock.elapsedRealtime();
		mElapsedRealtime = now - mLastRealtime;
		float timeBudget = mTimer.getTimeBudget() - mElapsedRealtime;
		mTimer.setTimeBudget(timeBudget);
		mTimer.updateUISignal();
		
		// if time budget is out of critical time
		if(timeBudget > 10000.0f) {
			begin = 9000.0f;
			end = 10000.0f;
		}
		
		if(begin < timeBudget && timeBudget < end) {
			mTimer.playTickSound();
			begin -= 1000.0f;
			end -= 1000.0f;
		}
		
		mLastRealtime = SystemClock.elapsedRealtime();
	}
	

}
