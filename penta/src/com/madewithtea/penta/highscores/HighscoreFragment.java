package com.madewithtea.penta.highscores;

import java.util.ArrayList;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.madewithtea.penta.R;
import com.madewithtea.penta.LocalStore;
import com.madewithtea.penta.MainActivity;
import com.madewithtea.penta.highscores.Highscore;
import com.madewithtea.penta.highscores.HighscoreAdapter;
import com.madewithtea.penta.network.NetworkManager;
import com.madewithtea.penta.sound.SoundManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HighscoreFragment extends Fragment {

	private static final String TAG = "HighscoreFragment";
	
	// UI Elements
	private TextView mRank, mPlayerName, mBestScore;
	
	// Activiy
	private MainActivity mActivity;
	
	// SoundManager
	private SoundManager mSoundManager;
	
	// Network Manager
	private NetworkManager mNetwork;
	private HighscoreAdapter mAdapter;
	
	// Store
	private LocalStore mStore;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mActivity = (MainActivity) getActivity();
		
		// Reference the sound manager, local store
		mSoundManager = mActivity.getSoundManger();
		mNetwork = mActivity.getNetworkManager();
		mStore = mActivity.getLocalStore();
		
		// Inflate the view
		View root = inflater.inflate(R.layout.fragment_highscore, container, false);
	
		mRank = (TextView) root.findViewById(R.id.myrank);
		mPlayerName = (TextView) root.findViewById(R.id.myName);
		mBestScore = (TextView) root.findViewById(R.id.myBestscore);
		
		// Set the List Adapter
		mAdapter = new HighscoreAdapter(getActivity(), mNetwork);
		ListView lv = (ListView) root.findViewById(R.id.highscore_list);
		lv.setAdapter(mAdapter);
		lv.setDivider(null);
		lv.setOnScrollListener(new HighscoreScrollerListener(mAdapter));
		
		if(mNetwork.isOnline()) {
			if(mStore.hasPlayerName()) {
				getHighscore();
				mAdapter.initOverNetwork();
			} else {
				// Check if player set otherwise dont show ranking layout
				LinearLayout ownRanking = (LinearLayout) root.findViewById(R.id.own_ranking);
				ownRanking.setVisibility(View.GONE);
			}
		} 
		
		return root;
	}

	public void getHighscore() {
		String username = mStore.getPlayerName();
		String password = mStore.getPassword();
		mNetwork.getAuthTokenH(username, password, this);
	}
	
	public void onAuthToken(String pAuthToken) {
		Tracker tracker = mActivity.getAppTracker();
		tracker.send(new HitBuilders.EventBuilder()
        .setCategory("Highscore")
        .setAction("got_auth_token")
        .build());
		
		mNetwork.getHighscore(pAuthToken, this);
	}
	
	public void onPageResult(final ArrayList<Highscore> scores) {
		Tracker tracker = mActivity.getAppTracker();
		tracker.send(new HitBuilders.EventBuilder()
        .setCategory("Highscore")
        .setAction("new_page_results")
        .build());
		
		mAdapter.clear();
		mAdapter.addAll(scores);
	}
	
	public void onHighscoreResult(int score, int rank) {
		Tracker tracker = mActivity.getAppTracker();
		tracker.send(new HitBuilders.EventBuilder()
        .setCategory("Highscore")
        .setAction("get_own_highscore")
        .build());
		
		mPlayerName.setText(mStore.getPlayerName());
		mRank.setText(String.valueOf(rank));
		mBestScore.setText(String.valueOf(score));
	}

	public void onSwitch() {
		if(mNetwork.isOnline()) {
			this.mAdapter.reloadOverNetwork();
			if(mStore.hasPlayerName()) {
				getHighscore();
			} 
		}
		return;
	}
}
