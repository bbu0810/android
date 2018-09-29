package com.hitchtransporter.transporter.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hitchtransporter.transporter.AA_Classes.MasterActivity;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.SmartButton;
import com.hitchtransporter.smart.customViews.SmartEditText;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.smart.weservice.SmartWebManager;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

import static com.hitchtransporter.smart.framework.SmartUtils.focusEditTextRed;
import static com.hitchtransporter.smart.framework.SmartUtils.focusEditTextRed3;
import static com.hitchtransporter.smart.framework.SmartUtils.getLanguageCode;
import static java.lang.Boolean.TRUE;

public class Login extends MasterActivity implements GoogleApiClient.OnConnectionFailedListener {

    private SmartEditText usernameLoginEt;
    private SmartEditText passowrdLoginEt;
    private SmartButton loginBtn;
    private ImageView loginFbIv;
    private ImageView loginGoogleIv;
    private ImageView loginTwitterIv;
    private Button btnChangeLang;
    private LinearLayout loginSignupLl;
    private SmartTextView forgotPasswordTv;
    private GoogleApiClient googleApiClient;
    private RadioButton englishRb, germanRb, fennishRb, swedishRb;
    private static final int REQ_CODE = 9001;
    private Dialog setLanguageDialog;
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private TwitterAuthClient mTwitterAuthClient;

    @Override
    public int getLayoutID() {
        return R.layout.activity_login;
    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    public void initComponents() {
        super.initComponents();

      /*  englishRb = findViewById(R.id.english_login_rb);
        germanRb = findViewById(R.id.german_login_rb);
        fennishRb = findViewById(R.id.fennish_login_rb);
        swedishRb = findViewById(R.id.swedish_login_rb);*/
        btnChangeLang = findViewById(R.id.btnChangelang);
        usernameLoginEt = findViewById(R.id.username_login_et);
        passowrdLoginEt = findViewById(R.id.passowrd_login_et);
        forgotPasswordTv = findViewById(R.id.forgot_password_tv);
        loginBtn = findViewById(R.id.login_btn);
        loginFbIv = findViewById(R.id.login_fb_iv);
        loginGoogleIv = findViewById(R.id.login_google_iv);
        loginTwitterIv = findViewById(R.id.login_twitter_iv);
        loginSignupLl = findViewById(R.id.login_signup_ll);

       /* String selected_locale = SmartApplication.REF_SMART_APPLICATION.
                readSharedPreferences().getString(SELECTED_LOCALE, ENGLISH_STR);
        if (selected_locale.equalsIgnoreCase(ENGLISH_STR)) {
            englishRb.setChecked(true);
            germanRb.setChecked(false);
            fennishRb.setChecked(false);
            swedishRb.setChecked(false);
        } else if (selected_locale.equalsIgnoreCase(GERMAN_STR)) {
            englishRb.setChecked(false);
            germanRb.setChecked(true);
            fennishRb.setChecked(false);
            swedishRb.setChecked(false);
        } else if (selected_locale.equalsIgnoreCase(FENNISH_STR)) {
            englishRb.setChecked(false);
            germanRb.setChecked(false);
            fennishRb.setChecked(true);
            swedishRb.setChecked(false);
        } else if (selected_locale.equalsIgnoreCase(SWEDISH_STR)) {
            englishRb.setChecked(false);
            germanRb.setChecked(false);
            fennishRb.setChecked(false);
            swedishRb.setChecked(true);
        }*/


        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();

        callbackManager = CallbackManager.Factory.create();
        setUpFacebookSignUp();

        Twitter.initialize(this);
        mTwitterAuthClient = new TwitterAuthClient();
    }


    @Override
    public void setActionListeners() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()) {
                    login(null);
                }
            }
        });
        btnChangeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage();
            }
        });
        loginSignupLl.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
            }
        });

        forgotPasswordTv.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, ForgetPassword.class);
                startActivity(i);
            }
        });

        loginGoogleIv.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

        loginFbIv.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile", EMAIL));
            }
        });

        loginTwitterIv.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                setUpTwitterSignUp();
            }

        });


       /* englishRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                englishRb.setChecked(true);
                germanRb.setChecked(false);
                SmartUtils.ForcefullyLocaleChange(Login.this, ENGLISH_STR);
                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SELECTED_LOCALE, ENGLISH_STR);
                restartActivity();
            }
        });

        germanRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                germanRb.setChecked(true);
                englishRb.setChecked(false);
                SmartUtils.ForcefullyLocaleChange(Login.this, GERMAN_STR);
                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SELECTED_LOCALE, GERMAN_STR);
                restartActivity();
            }
        });
        fennishRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                germanRb.setChecked(false);
                englishRb.setChecked(false);
                fennishRb.setChecked(true);
                swedishRb.setChecked(false);
                SmartUtils.ForcefullyLocaleChange(Login.this, FENNISH_STR);
                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SELECTED_LOCALE, FENNISH_STR);
                restartActivity();
            }
        });
        swedishRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                germanRb.setChecked(false);
                englishRb.setChecked(false);
                fennishRb.setChecked(false);
                swedishRb.setChecked(true);
                SmartUtils.ForcefullyLocaleChange(Login.this, SWEDISH_STR);
                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SELECTED_LOCALE, SWEDISH_STR);
                restartActivity();
            }
        });
*/
    }

    private void setLanguage() {
        setLanguageDialog = new Dialog(this);
        setLanguageDialog.setContentView(R.layout.dialog_set_language);

        RadioGroup languageRg = setLanguageDialog.findViewById(R.id.language_rg);
        RadioButton englishRb = setLanguageDialog.findViewById(R.id.english_rb);
        RadioButton germanRb = setLanguageDialog.findViewById(R.id.german_rb);
        RadioButton fennishRb = setLanguageDialog.findViewById(R.id.fennih_rb);
        RadioButton swedishRb = setLanguageDialog.findViewById(R.id.swedish_rb);

        String selected_locale = SmartApplication.REF_SMART_APPLICATION.
                readSharedPreferences().getString(SELECTED_LOCALE, ENGLISH_STR);
        if (selected_locale.equalsIgnoreCase(ENGLISH_STR)) {
            englishRb.setChecked(true);
            germanRb.setChecked(false);
        } else if (selected_locale.equalsIgnoreCase(GERMAN_STR)) {
            englishRb.setChecked(false);
            germanRb.setChecked(true);
        } else if (selected_locale.equalsIgnoreCase(FINNISH_STR)) {
            englishRb.setChecked(false);
            germanRb.setChecked(false);
            fennishRb.setChecked(true);
            swedishRb.setChecked(false);
        } else if (selected_locale.equalsIgnoreCase(SWEDISH_STR)) {
            englishRb.setChecked(false);
            germanRb.setChecked(false);
            fennishRb.setChecked(false);
            swedishRb.setChecked(true);
        }

        languageRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.english_rb) {
                    SmartUtils.ForcefullyLocaleChange(Login.this, ENGLISH_STR);
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SELECTED_LOCALE, ENGLISH_STR);
                    restartActivity(false);
                } else if (checkedId == R.id.german_rb) {
                    SmartUtils.ForcefullyLocaleChange(Login.this, GERMAN_STR);
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SELECTED_LOCALE, GERMAN_STR);
                    restartActivity(false);
                } else if (checkedId == R.id.fennih_rb) {
                    SmartUtils.ForcefullyLocaleChange(Login.this, FINNISH_STR);
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SELECTED_LOCALE, FINNISH_STR);
                    restartActivity(false);
                } else if (checkedId == R.id.swedish_rb) {
                    SmartUtils.ForcefullyLocaleChange(Login.this, SWEDISH_STR);
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SELECTED_LOCALE, SWEDISH_STR);
                    restartActivity(false);
                }
            }
        });
        if (setLanguageDialog != null && !setLanguageDialog.isShowing()) {
            setLanguageDialog.show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (setLanguageDialog != null && setLanguageDialog.isShowing()) {
            setLanguageDialog.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (setLanguageDialog != null && setLanguageDialog.isShowing()) {
            setLanguageDialog.cancel();
        }
    }

    private boolean check() {
        boolean isValid = true;

        if (TextUtils.isEmpty(passowrdLoginEt.getText().toString())) {
            focusEditTextRed(Login.this, passowrdLoginEt, true, getString(R.string.password_empty), 0);
            isValid = false;
        }
        if (TextUtils.isEmpty(usernameLoginEt.getText().toString())) {
            focusEditTextRed3(Login.this, usernameLoginEt, true, getString(R.string.validation_email));
            isValid = false;
        }
        return isValid;
    }

    private void login(final HashMap<String, String> social_data) {
        SmartUtils.showLoadingDialog(Login.this);
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        if (social_data != null) {
            params.put(SOCIAL_ID, social_data.get(SOCIAL_ID));
            params.put(SOCIAL_TYPE, social_data.get(SOCIAL_TYPE));
        } else {
            params.put(EMAIL, usernameLoginEt.getText().toString());
            params.put(PASSWORD, passowrdLoginEt.getText().toString());
        }
        params.put(DEVICE_TOKEN, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_FIREBASE_REGID, "no_device_token"));
        params.put(DEVICE_TYPE, ANDROID);
        params.put(LONGITUDE, SmartUtils.getLongitude());
        params.put(LATITUDE, SmartUtils.getLatitude());

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        if (social_data != null) {
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "socialLogin");
        } else {
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "login");
        }
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, Login.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Log.d("@@@Login_success", ": true");
                    try {

                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(USER_TYPE, response.getJSONObject(DATA).getString(USER_TYPE));
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(USER_ID, response.getJSONObject(DATA).getString(USER_ID));
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(EMAIL, usernameLoginEt.getText().toString());
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(PASSWORD, passowrdLoginEt.getText().toString());

                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_USER, response.getJSONObject(DATA).toString());
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(IS_LOGGED_IN, TRUE);

                        quickBloxLogin(response.getJSONObject(DATA).getString(EMAIL));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (responseCode == 400) {
                    if (social_data == null) {
                        try {
                            SmartUtils.showSnackBar(Login.this, response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);
                            usernameLoginEt.setText("");
                            usernameLoginEt.requestFocus();
                            passowrdLoginEt.setText("");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent i = new Intent(Login.this, Register.class);
                        i.putExtra(SOCIAL_DATA, social_data);
                        startActivity(i);

                    }
                }
            }

            @Override
            public void onResponseError() {

                SmartUtils.hideLoadingDialog();
            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams, true, false);
    }

    //GOOGLE SIGNIN/SIGNUP---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void googleSignIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    private void handleGoogleResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()) {

            GoogleSignInAccount account = googleSignInResult.getSignInAccount();

            HashMap<String, String> googleData = new HashMap<>();
            googleData.put(SOCIAL_TYPE, GOOGLE_CODE);
            googleData.put(SOCIAL_ID, account.getId());
            if (account.getDisplayName() != null) {
                googleData.put(FIRST_NAME, account.getDisplayName().split(" ")[0]);
                googleData.put(LAST_NAME, account.getDisplayName().split(" ")[1]);

            }
            googleData.put(EMAIL, account.getEmail());
            if (account.getPhotoUrl() != null) {
                googleData.put(PROFILE_IMAGE, account.getPhotoUrl().toString());
            }
            login(googleData);

        } else {
            Toast.makeText(this, getString(R.string.google_fail), Toast.LENGTH_SHORT).show();
            Log.d("@@google_login_status==", "failed");
        }
    }


    //FACEBOOK SIGNIN/SIGNUP--------------------------------------------------------------------------------------------------------------------------------------------------------------------


    private void setUpFacebookSignUp() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (object != null) {
                            Log.d("@@facebook", object.toString());
                            try {
                                HashMap<String, String> facebookData = new HashMap<>();
                                facebookData.put(SOCIAL_TYPE, FACEBOOK_CODE);
                                facebookData.put(SOCIAL_ID, object.getString(ID));
                                facebookData.put(FIRST_NAME, object.getString(FIRST_NAME));
                                facebookData.put(LAST_NAME, object.getString(LAST_NAME));
                                if (object.has(EMAIL)) {
                                    facebookData.put(EMAIL, object.getString(EMAIL));
                                }
                                String profilePicUrl = "https://graph.facebook.com/" + object.getString(ID) + "/picture?type=large";
                                facebookData.put(PROFILE_IMAGE, profilePicUrl);

                                login(facebookData);
                            } catch (Exception e) {
                                Toast.makeText(Login.this, "Error1", Toast.LENGTH_SHORT).show();
                                Log.e("@@facebook_error", e.toString());
                                e.printStackTrace();
                            }

                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, first_name,last_name, email,gender,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(Login.this, getString(R.string.facebook_cancel), Toast.LENGTH_LONG).show();
                LoginManager.getInstance().logOut();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("@@facebook_error", error.getMessage());
            }
        });
    }

    //TWITTER SIGNIN/SIGNUP----------------------------------------------------------------------------------------------------------------------------------------------------------------------


    private void setUpTwitterSignUp() {

        Call<User> user = TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(false, false, true);
        user.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {

                HashMap<String, String> twitterData = new HashMap<>();
                twitterData.put(SOCIAL_TYPE, TWITTER_CODE);
                twitterData.put(SOCIAL_ID, userResult.data.idStr);
                twitterData.put(FIRST_NAME, userResult.data.name.split(" ")[0]);
                twitterData.put(LAST_NAME, userResult.data.name.split(" ")[0]);
                twitterData.put(EMAIL, userResult.data.email);
                String photoUrlOriginalSize = userResult.data.profileImageUrl.replace("_normal", "");
                twitterData.put(PROFILE_IMAGE, photoUrlOriginalSize);

                login(twitterData);

            }

            @Override
            public void failure(TwitterException exc) {
                Log.d("TwitterKit", "Verify Credentials Failure", exc);
                mTwitterAuthClient.authorize(Login.this, new com.twitter.sdk.android.core.Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
                        Call<User> user = TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(false, false, true);
                        user.enqueue(new Callback<User>() {
                            @Override
                            public void success(Result<User> userResult) {
                                HashMap<String, String> twitterData = new HashMap<>();
                                twitterData.put(SOCIAL_TYPE, TWITTER_CODE);
                                twitterData.put(SOCIAL_ID, userResult.data.idStr);
                                twitterData.put(FIRST_NAME, userResult.data.name.split(" ")[0]);
                                twitterData.put(LAST_NAME, userResult.data.name.split(" ")[0]);
                                twitterData.put(EMAIL, userResult.data.email);
                                String photoUrlOriginalSize = userResult.data.profileImageUrl.replace("_normal", "");
                                twitterData.put(PROFILE_IMAGE, photoUrlOriginalSize);

                                login(twitterData);

                            }

                            @Override
                            public void failure(TwitterException exc) {
                                Log.d("TwitterKit", "Verify Credentials Failure", exc);
                            }
                        });
                    } // Success

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });
            }
        });

    }


    //QUICK BLOX----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void quickBloxLogin(final String user) {
        SmartUtils.showLoadingDialog(Login.this);

        QBUser qbUser = new QBUser(user, QUICK_BLOX_PASSWORD);

        QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                SmartUtils.hideLoadingDialog();
                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(QUICK_BLOX_USER_ID, user);
                Intent intent = new Intent(Login.this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                supportFinishAfterTransition();
            }

            @Override
            public void onError(QBResponseException e) {
                SmartUtils.hideLoadingDialog();
                if (e.getMessage() != null) {
                    Toast.makeText(Login.this, "Quickblox : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("@@ERR_LOGIN", e.getMessage());
                } else {
                    Log.e("@@ERR_LOGIN", "NULL FROM QUICKBLOX");
                }
            }
        });

    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            handleGoogleResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));

        } else if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            callbackManager.onActivityResult(requestCode, resultCode, data);

        } else if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
    }
}
