package com.madewithtea.penta.game;

import java.util.ArrayList;
import java.util.List;

import com.madewithtea.penta.R;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class IOBar {
	
	private static final String TAG = "IOBar";
	
	private Fragment parent;
	private Resources res;
	
	// UI helper variables
	private boolean topLine = true;
	private int cursor = 0;
	private List<ImageView> cellsTopView, cellsBottomView;
	private ImageView topIndicator, bottomIndicator;
	private List<ViewGroup> rows;
	
	/**
	 * 
	 * @param fragment
	 * @param root
	 */
	public IOBar(GameFragment fragment, View root) {
		
		rows = new ArrayList<ViewGroup>();
		parent = fragment;
		res = parent.getResources();
		
		cellsTopView = new ArrayList<ImageView>();
	    cellsBottomView = new ArrayList<ImageView>();
		
		ViewGroup lines = (ViewGroup) root.findViewById(R.id.input);
		int amountOfRows = 2;
		int cellsInRow = 5;
		
		// Each line
		for(int i = 0; i < amountOfRows; i++) {
			ViewGroup row;
			
			rows.add((ViewGroup) ((ViewGroup) lines.getChildAt(i)));
			
			row = (ViewGroup) ((ViewGroup) lines.getChildAt(i)).getChildAt(1);
			
			// Each cell
			for(int k = 0; k < cellsInRow; k++) {
				ImageView cell = (ImageView) row.getChildAt(k);
				List<ImageView> list = (i == 0) ? cellsTopView : cellsBottomView;
				list.add(cell);
			}
		}
		
		// Initialize Indicator
		// Each line
		for(int i = 0; i < amountOfRows; i++) {
			ViewGroup row = (ViewGroup) lines.getChildAt(i);
			ImageView indicatorRef = (ImageView) row.getChildAt(0);
			if(i == 0) {
				this.topIndicator = indicatorRef;
			} else {
				this.bottomIndicator = indicatorRef;
			}
		}
	}
	
	/**
	 * 
	 * @param number
	 */
	public void writeNumber(int number) {
		// Get current line
		List<ImageView> line = topLine ? cellsTopView : cellsBottomView;
		
		// Get cell and set the number
		ImageView view = line.get(cursor);
		view.setImageDrawable(getNumberDrawable(number, 1));
		
		// Cursor for writing in cells
		cursor += 1;
	}
	
	/**
	 * Show indicator for IO bar
	 * 
	 * @param top
	 */
	private void showIndicator(boolean top) {
		ImageView indicatorShow = (top) ? topIndicator : bottomIndicator;
		ImageView indicatorHide = (!top) ? topIndicator : bottomIndicator;
		indicatorShow.setVisibility(View.VISIBLE);
		indicatorHide.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * Show the hint in the IO bar.
	 * 
	 * @param mask
	 */
	public void showHint(List<Integer> guess, List<Integer> mask) {
		List<ImageView> line = topLine ? cellsTopView : cellsBottomView;
		for (int i = 0; i < guess.size(); i++) {
			int gnum = guess.get(i);
			int type = mask.get(i);
			Drawable rDrawable = getNumberDrawable(gnum, type);
			line.get(i).setImageDrawable(rDrawable);
		}
	}

	/**
	 * Initialize new.
	 */
	public void prepareGame() {
		this.topLine = true;
		this.cursor = 0;

		// Emptify all cells
		for (ImageView cell : this.cellsTopView) {
			Drawable empty = res.getDrawable(R.drawable.empty);
			cell.setImageDrawable(empty);
		}
		// Emptify all cells
		for (ImageView cell : this.cellsBottomView) {
			Drawable empty = res.getDrawable(R.drawable.empty);
			cell.setImageDrawable(empty);
		}
		
		// Set the indicator
		showIndicator(true);
	}
	
	
	/**
	 * Helper function to get number drawable.
	 * 
	 * @param number
	 * @param type
	 * @return
	 */
	private Drawable getNumberDrawable(int number, int type) {
		String fieldName = "@drawable/n" + String.valueOf(number) + "_"
				+ String.valueOf(type);
		
		String pkgName = parent.getActivity().getPackageName();
		int identifier = res.getIdentifier(fieldName, null, pkgName);
		return res.getDrawable(identifier);
	}
	
	/**
	 * Get number drawable.
	 */
	public void prepareNext() {
		cursor = 0;
		// Switch lines, show indicator and erase content
		topLine = topLine ? false : true;
		showIndicator(topLine);
		
		List<ImageView> line = topLine ? cellsTopView : cellsBottomView;
		for (ImageView cell : line) {
			cell.setImageDrawable(res.getDrawable(R.drawable.empty));
		}
	}
	
	public void hideBottomLine() {
		this.rows.get(1).setVisibility(View.GONE);
	}
	
	public void showBottomLine() {
		this.rows.get(1).setVisibility(View.VISIBLE);
	}
	
	/**
	 * Get the cursor
	 */
	public int getCursor() {
		return cursor;
	}
}
