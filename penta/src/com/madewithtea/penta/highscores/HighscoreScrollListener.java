package com.madewithtea.penta.highscores;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

class HighscoreScrollerListener implements OnScrollListener {

	private HighscoreAdapter mAdapter;
	
	public HighscoreScrollerListener(HighscoreAdapter adapter) {
		mAdapter = adapter;
	}
	
	@Override
	public void onScroll(AbsListView view, 
			int firstV, int visCount, int totalCount) {
		
		boolean loadMore = firstV + visCount >= totalCount;
		if(loadMore) {
			mAdapter.onInfScroll();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {}
}
