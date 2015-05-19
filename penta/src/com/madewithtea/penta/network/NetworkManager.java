package com.madewithtea.penta.network;

import com.loopj.android.http.*;
import com.madewithtea.penta.game.RegisterFragment;
import com.madewithtea.penta.game.Scorer;
import com.madewithtea.penta.highscores.Highscore;
import com.madewithtea.penta.highscores.HighscoreAdapter;
import com.madewithtea.penta.highscores.HighscoreFragment;

import java.util.ArrayList;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NetworkManager {

	private static final String TAG = "NetworkManager";

	private String URL = "https://highscore.madewithtea.com/";
	private Activity mActivity;
	private AsyncHttpClient mClient;

	public NetworkManager(Activity activity) {
		mActivity = activity;
		mClient = new AsyncHttpClient(80, 443);

		// for not checking the SSL certs signature
		mClient.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
	}

	public boolean isOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) mActivity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
	
	public void getAuthTokenH(String pUsername, String pPassword,
			final HighscoreFragment caller) {
		
		Log.v(TAG, "called getAuthTokenH");
		
		String url = URL + "/oauth2/access_token/";
		
		// Get the Auth Token
		RequestParams params = new RequestParams();
		params.put("username", pUsername);
		params.put("password", pPassword);
		params.put("grant_type", "password");
		params.put("client_id", pUsername);
		
		mClient.post(url, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					String authToken = response.getString("access_token");
					Log.v(TAG, "Got AuthToken: " + authToken);
					caller.onAuthToken(authToken);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				Log.e(TAG, "Error when fetching AuthToken");
				Log.e(TAG, responseString);
			}
		});	
		
	}
	
	public void getAuthToken(final String pUsername, final String pPassword,
			final int score, final Scorer caller) {
		
		Log.v(TAG, "called getAuthToken");
		
		String url = URL + "/oauth2/access_token/";
		
		// Get the Auth Token
		RequestParams params = new RequestParams();
		params.put("username", pUsername);
		params.put("password", pPassword);
		params.put("grant_type", "password");
		params.put("client_id", pUsername);
		
		mClient.post(url, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					String authToken = response.getString("access_token");
					Log.v(TAG, "Got AuthToken: " + authToken);
					caller.onAuthToken(authToken, score);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Log.e(TAG, "Error when fetching AuthToken");
				Log.e(TAG, errorResponse.toString());
			}
		});	
	}
	
	public void sendMatch(final String pAuthToken, final int score, 
			final Scorer caller) {
		
		Log.v(TAG, "called sendMatch");
		
		String url = URL + "user/matches/";
		
		RequestParams params = new RequestParams();
		params.put("score", score);
		
		mClient.removeAllHeaders();
		mClient.addHeader("Authorization", " bearer " + pAuthToken);
		mClient.post(url, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
					Log.v(TAG, String.valueOf(statusCode));
			}
		});
	}
	
	public void registerPlayer(final String pPlayerName,
			final RegisterFragment caller) {

		Log.v(TAG, "called registerPlayer");		
		
		String url = URL + "registration/";

		RequestParams params = new RequestParams();
		params.put("username", pPlayerName);

		mClient.post(url, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {

				if (response.has("password")) {
					String pw;
					try {
						pw = response.getString("password");
						caller.onRegistered(pPlayerName, pw);

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject response) {
				if (response.has("error")) {
					try {
						int code = response.getInt("error");
						if(code == 0) {
							caller.onUsernameTaken(pPlayerName);
							Log.v(TAG, "Playername is taken.");
						}
						if(code == 3 || code == 2) {
							caller.onInvalid();
							Log.v(TAG, "Playername is invalid.");
						}
						Log.v(TAG, response.getString("error"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return;
				}
			}
		});
	}

	public void getHighscore(String pAuthToken, final HighscoreFragment caller) {
		
		Log.v(TAG, "called getHighscore");	
		
		String url = URL + "user/highscore/";
		
		mClient.removeAllHeaders();
		mClient.addHeader("Authorization", " bearer " + pAuthToken);
		
		JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, JSONObject obj) {
				try {
					Log.v(TAG, String.valueOf(arg0));
					Log.v(TAG, obj.toString());
					final int score = obj.getInt("score");
					final int rank = obj.getInt("rank");
					caller.onHighscoreResult(score, rank);		
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				Log.e(TAG, "Failure when retrieving own highscore.");
				Log.e(TAG,String.valueOf(statusCode));
			}
		};
		
		mClient.get(url, handler);
		
		return;
	}
	
	public void getAmountPages(final HighscoreAdapter mAdapter) {

		Log.v(TAG, "called getAmountPages");	
		
		String url = URL + "highscores/";
		
		AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				Log.e(TAG, "getAmountPages:" + String.valueOf(arg2));
			}
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] json) {
				Log.v(TAG, String.valueOf(arg0));
 				try {
					JSONObject jsonObject = new JSONObject(new String(json));
					int pages = jsonObject.getInt("pages");
					Log.v(TAG, "Got amount of pages:" + String.valueOf(pages));
					mAdapter.onAmountPagesResult(pages);
					return;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		mClient.get(url, handler);
		
	}
	
	public void getHighscorePage(int page, final HighscoreAdapter mAdapter) {

		Log.v(TAG, "called getHighscorePage");	
		
		String url = URL + "highscores/" + String.valueOf(page) + "/";

		AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				Log.e(TAG, String.valueOf(arg2));
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] json) {
				Log.v(TAG, String.valueOf(arg0));
				ArrayList<Highscore> scores = new ArrayList<Highscore>();
 				try {
					JSONArray jsonArray = new JSONArray(new String(json));
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject obj = jsonArray.getJSONObject(i);
						String name = obj.getString("player_name");
						String score = obj.getString("score");
						scores.add(new Highscore(name, score));
					}
					mAdapter.onPageResult(scores);
					return;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		mClient.get(url, handler);

		return;
	}
}
