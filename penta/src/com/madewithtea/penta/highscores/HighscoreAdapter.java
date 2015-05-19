package com.madewithtea.penta.highscores;

import java.util.ArrayList;

import com.madewithtea.penta.R;
import com.madewithtea.penta.network.NetworkManager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HighscoreAdapter extends ArrayAdapter<Highscore> {

	private static final String TAG = "HighscoreAdapter";
	
	public int mCount;
	private int mPage;
	private int mAmountPages;
	private boolean busyGetAmountPages, busy, busyGetHighscorePage;
	private NetworkManager mNetwork;

	
	public HighscoreAdapter(Context context, NetworkManager pNetwork) {
		super(context, 0);
		mNetwork = pNetwork;
		mPage = 0;
		mCount = 0;
		busy = true;
		busyGetAmountPages = true;
		busyGetHighscorePage = true;
		
	}

	public void initOverNetwork() {
		busyGetAmountPages = true;
		mNetwork.getAmountPages(this);

		clear();
		mCount = 0;
		mPage = 0;

		// Asynchronous
		busyGetHighscorePage = true;
		mNetwork.getHighscorePage(0, this);
	}

	public void reloadOverNetwork() {
		initOverNetwork();
	}

	public void onPageResult(ArrayList<Highscore> scores) {
		Log.v(TAG, "new page result" + String.valueOf(mPage));
		mCount += scores.size();
		addAll(scores);
		mPage++;
		busyGetHighscorePage = false;
	}

	public void onInfScroll() {
		Log.v(TAG, "onScroll triggerd");
		if(!busyGetAmountPages && !busyGetHighscorePage) {
			Log.v(TAG, "get page while infinity scroll " + String.valueOf(mPage));
			if(mPage <= mAmountPages) {
				busyGetHighscorePage = true;
				mNetwork.getHighscorePage(mPage, this);
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Highscore score = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_highscore, parent, false);
		}

		TextView rankv = (TextView) convertView.findViewById(R.id.tvRank);
		TextView playerv = (TextView) convertView.findViewById(R.id.tvPlayer);
		TextView scorev = (TextView) convertView.findViewById(R.id.tvHighscore);

		// Rank is off by one.
		rankv.setText(String.valueOf(position + 1));
		playerv.setText(score.player);
		scorev.setText(score.score);

		return convertView;
	}

	public void onAmountPagesResult(int pages) {
		mAmountPages = pages;
		Log.v(TAG, "Got amount of pages " + String.valueOf(pages));
		busyGetAmountPages = false;
	}

}
