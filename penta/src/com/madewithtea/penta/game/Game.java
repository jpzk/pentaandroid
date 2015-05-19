package com.madewithtea.penta.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {
	
	private static final String TAG = "Game";
	
	private List<Integer> secret;
	
	public Game() {
		this.secret = new ArrayList<Integer>();
		nextNumber();
	}
	
	public void nextNumber() {
		long seed = System.nanoTime();
		secret.clear();
		for (int i = 1; i < 10; i++) {
			secret.add(Integer.valueOf(i));
		}
		Collections.shuffle(this.secret, new Random(seed));
		secret = secret.subList(0, 5);
	}
	
	public List<Integer> getHint(List<Integer> guess) {
		List<Integer> mask = new ArrayList<Integer>();

		for (int i = 0; i < guess.size(); i++) {
			boolean exist = false;
			for (int k = 0; k < secret.size(); k++) {
				// exists
				if(guess.get(i) == secret.get(k)) {
					exist = true;
					// right position
					if(i == k) {
						mask.add(Integer.valueOf(3));
					} else { // exists but not right position
						mask.add(Integer.valueOf(2));
					}
					// found, so skip every other
					break;
				}
			}
			// not found in secret
			if(!exist) {
				mask.add(Integer.valueOf(1));
			}
		}
		
		return mask;
	}
	
	public boolean performCheck(List<Integer> guess) {
		return secret.equals(guess);
	}
}
