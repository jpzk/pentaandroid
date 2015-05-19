package com.madewithtea.penta.game;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.madewithtea.penta.R;
import com.madewithtea.penta.sound.SoundManager;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Timer {
	
	private static final String TAG = "Timer";
	
	private GameFragment mParent;
	
	// UI
	private ImageView mBar;
	private TextView mCounter;
	
	// State
	boolean isMeasuring = false;
	
	// Thread Pool
	private ScheduledThreadPoolExecutor mStpe;

	// Semaphore for TimeBudget
	private final Semaphore mSemaphore;
	
	// Time per Match
	private float mTimePerMatch = 90.0f * 1000.0f;
	private float mTimeBudgetCap = mTimePerMatch;
	private float mTimeBudgetAdd = 10.0f * 1000.0f;
	private long mTimeBudgetClock = 100;
	private float mTimeBudget = mTimePerMatch;
	private TimeUnit unit = TimeUnit.MILLISECONDS;
	
	// Sound Manager
	private SoundManager mSoundManager;
	
	/**
	 * 
	 * @param activity
	 * @param fragment
	 * @param root
	 */
	public Timer(GameFragment fragment, SoundManager pSoundManager, View root) {
		mSemaphore = new Semaphore(1);
		mCounter = (TextView) root.findViewById(R.id.timeCounter);
		mBar = (ImageView) root.findViewById(R.id.timebar);
		mSoundManager = pSoundManager;
		mParent = fragment;
	}
	
	/**
	 * Start measuring, start thread.
	 */
	public void startMeasuring() {
		Log.v(TAG, "start measuring");
		
		mStpe = new ScheduledThreadPoolExecutor(2);
		Runnable thread = new TimerRunnable(this);
		mStpe.scheduleAtFixedRate(thread, 0, mTimeBudgetClock, unit);
		isMeasuring = true;
	}
	
	/**
	 * Stop timer thread and measuring, reset.
	 */
	public void stopMeasuring() {
		Log.v(TAG, "stop measuring");
		
		if(mStpe != null) {
			mStpe.shutdown();
			mStpe = null;
		}
		mTimeBudget = mTimePerMatch;
		updateUISignal();
		isMeasuring = false;
	}
	
	/**
	 * Increase when successful but cap.
	 */
	public void increaseTimeBudgetCap() {
		Log.v(TAG, "increase time budget");
		
		float newTime = getTimeBudget() + mTimeBudgetAdd;
		if(newTime > mTimeBudgetCap) {
			setTimeBudget(mTimeBudgetCap);
		} else {
			setTimeBudget(newTime);
		}
	}
	
	/**
	 * Get the time budget.
	 * 
	 * @return
	 */
	public float getTimeBudget() {
		return mTimeBudget;
	}
	
	/**
	 * Called for decreasing and increasing.
	 * 
	 * @param time
	 */
	public void setTimeBudget(float time) {
		try {
			mSemaphore.acquire(); // blocking until acquired.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mTimeBudget = time;
		mSemaphore.release();
	}
	
	/**
	 * Update the UI
	 */
	public void updateUISignal() {
		// Update Counter
		mCounter.post(new Runnable() {
			@Override
			public void run() {
				float seconds = mTimeBudget / 1000.0f;
				String time = String.format("%.2f", seconds);
				mCounter.setText(time + " s");
			}
		});
		
		// Update Bar
		final float pf = mTimeBudget / mTimePerMatch;
		mBar.post(new Runnable() {
			@Override
			public void run() {
				mBar.setScaleX(pf);
				float offset = (mBar.getWidth() * pf) / 2;
				mBar.setTranslationX(offset - (mBar.getWidth() / 2));
			}
		});
	}
	
	public void endMatchSignal() {
		playTimeOutSound();
		mParent.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mParent.endMatchByTimeout();
			}
		});
	}
	
	public boolean isMeasuring() {
		return isMeasuring;
	}

	public void playTickSound() {
		mSoundManager.playTick();
	}
	
	public void playTimeOutSound() {
		mSoundManager.playTimeOut();
	}
}
