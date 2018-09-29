package com.hitchtransporter.smart.Instagram;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hitchtransporter.smart.framework.SmartUtils;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class InstagramApp {

	private InstagramSession mSession;
	private InstagramDialog mDialog;
	private OAuthAuthenticationListener mListener;
	private ProgressDialog mProgress;
	private HashMap<String, String> userInfo = new HashMap<String, String>();
	private String userInfo_str;
	private String mAuthUrl;
	private String mAccessToken;
	private Context mCtx;

	private String mClientId;
	private String mClientSecret;

	public static int WHAT_FINALIZE = 0;
	public static int WHAT_ERROR = 1;
	public static int WHAT_FETCH_INFO = 2;

	/**
	 * Callback url, as set in 'Manage OAuth Costumers' page
	 */
	public static String mCallbackUrl = "";
	private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
	private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
	private static final String API_URL = "https://api.instagram.com/v1";

	private static final String TAG = "InstagramAPI";

	public InstagramApp(Context context, String clientId, String clientSecret,
			String callbackUrl) {

		mClientId = clientId;
		mClientSecret = clientSecret;
		mCtx = context;
		mSession = new InstagramSession(context);
		mAccessToken = mSession.getAccessToken();
		mCallbackUrl = callbackUrl;
		mAuthUrl = AUTH_URL
				+ "?client_id="
				+ clientId
				+ "&redirect_uri="
				+ mCallbackUrl
				+ "&response_type=code&display=touch&scope=likes+comments+relationships";
		InstagramDialog.OAuthDialogListener listener = new InstagramDialog.OAuthDialogListener() {
			@Override
			public void onComplete(String code) {
				getAccessToken(code);
			}

			@Override
			public void onError(String error) {
				mListener.onFail("Authorization failed");
			}
		};
		mDialog = new InstagramDialog(context, mAuthUrl, listener);
		mProgress = new ProgressDialog(context);
		mProgress.setCancelable(false);
	}

	private void getAccessToken(final String code) {
		mProgress.setMessage("Getting access token ...");
		mProgress.show();
		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Getting access token");
				int what = WHAT_FETCH_INFO;
				try {
					URL url = new URL(TOKEN_URL);
					Log.i(TAG, "Opening Token URL " + url.toString());
					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();
					urlConnection.setRequestMethod("POST");
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);
					OutputStreamWriter writer = new OutputStreamWriter(
							urlConnection.getOutputStream());
					writer.write("client_id=" + mClientId + "&client_secret="
							+ mClientSecret + "&grant_type=authorization_code"
							+ "&redirect_uri=" + mCallbackUrl + "&code=" + code);
					writer.flush();
					String response = SmartUtils.streamToString(urlConnection
							.getInputStream());
					Log.i(TAG, "response " + response);

					JSONObject jsonObj = (JSONObject) new JSONTokener(response)
							.nextValue();
					mAccessToken = jsonObj.getString("access_token");
					Log.i(TAG, "Got access token: " + mAccessToken);
					String id = jsonObj.getJSONObject("user").getString("id");
					String user = jsonObj.getJSONObject("user").getString(
							"username");
					String name = jsonObj.getJSONObject("user").getString(
							"full_name");
					mSession.storeAccessToken(mAccessToken, id, user, name);
				} catch (Exception ex) {
					what = WHAT_ERROR;
					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
			}
		}.start();
	}

    public void fetchUserName(final Handler handler) {
        mProgress = new ProgressDialog(mCtx);
        mProgress.setMessage("Loading ...");
        mProgress.show();
        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Fetching user info");
                int what = WHAT_FINALIZE;
                try {
                    URL url = new URL(API_URL + "/users/" + mSession.getId()
                            + "/?access_token=" + mAccessToken);

                    Log.d(TAG, "Opening URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.connect();
                    String response = SmartUtils.streamToString(urlConnection
                            .getInputStream());
                    System.out.println(response);
                    userInfo_str = response;

                } catch (Exception ex) {
                    what = WHAT_ERROR;
                    ex.printStackTrace();
                }
                mProgress.dismiss();
                handler.sendMessage(handler.obtainMessage(what, 2, 0));
            }
        }.start();
    }

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == WHAT_ERROR) {
				mProgress.dismiss();
				if (msg.arg1 == 1) {
					mListener.onFail("Failed to get access token");
				} else if (msg.arg1 == 2) {
					mListener.onFail("Failed to get user information");
				}
			} else if (msg.what == WHAT_FETCH_INFO) {
				// fetchUserName();
				mProgress.dismiss();
				mListener.onSuccess();
			}
		}
	};

	public HashMap<String, String> getUserInfo() {
		return userInfo;
	}

	public String getUserInfo_str() {
		return userInfo_str;
	}

	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}

	public void setListener(OAuthAuthenticationListener listener) {
		mListener = listener;
	}

	public String getUserName() {
		return mSession.getUsername();
	}

	public String getId() {
		return mSession.getId();
	}

	public String getName() {
		return mSession.getName();
	}

	public String getToken() {
		return mSession.getAccessToken();
	}

	public void authorize() {
		mDialog.show();
	}

	public void resetAccessToken() {
		if (mAccessToken != null) {
			mSession.resetAccessToken();
			mAccessToken = null;
		}
	}

	public interface OAuthAuthenticationListener {
		void onSuccess();

		void onFail(String error);
	}
}