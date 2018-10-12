package com.hitchtransporter.transporter.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.SmartButton;
import com.hitchtransporter.smart.customViews.SmartEditText;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.smart.weservice.SmartWebManager;
import com.hitchtransporter.transporter.AA_Classes.MasterActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.hitchtransporter.smart.framework.SmartUtils.focusEditTextRed;
import static com.hitchtransporter.smart.framework.SmartUtils.focusEditTextRed3;
import static com.hitchtransporter.smart.framework.SmartUtils.getLanguageCode;

public class ForgetPassword extends MasterActivity {

    private SmartEditText emailForgotEt;
    private SmartEditText passowrdForgotEt;
    private SmartEditText rePassowrdForgotEt;
    private SmartButton resetPasswordBtn;
    private ImageView backBtn;
    private LinearLayout signupForgotLl;


    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_forget_password;
    }

    @Override
    public void initComponents() {
        super.initComponents();

        emailForgotEt = findViewById(R.id.email_forgot_et);
        passowrdForgotEt = findViewById(R.id.passowrd_forgot_et);
        rePassowrdForgotEt = findViewById(R.id.re_passowrd_forgot_et);
        resetPasswordBtn = findViewById(R.id.reset_password_btn);
        signupForgotLl = findViewById(R.id.signup_forgot_Ll);
        backBtn = findViewById(R.id.back_btn);
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()) {
                    resestPassword();
                }
            }
        });

        signupForgotLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForgetPassword.this, Register.class);
                startActivity(i);
                supportFinishAfterTransition();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private boolean check() {
        boolean isValid = true;

        if (TextUtils.isEmpty(passowrdForgotEt.getText().toString())) {
            focusEditTextRed(ForgetPassword.this, passowrdForgotEt, true, null,0);
            isValid = false;
        } else if (TextUtils.isEmpty(rePassowrdForgotEt.getText().toString())) {
            focusEditTextRed(ForgetPassword.this, rePassowrdForgotEt, true, null,0);
            isValid = false;
        } else if (!rePassowrdForgotEt.getText().toString().equals(passowrdForgotEt.getText().toString())) {
            focusEditTextRed(ForgetPassword.this, passowrdForgotEt, false, null,0);
            focusEditTextRed(ForgetPassword.this, rePassowrdForgotEt, true, getString(R.string.password_mismatch),0);
            rePassowrdForgotEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    passowrdForgotEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            isValid = false;
        } else if (passowrdForgotEt.getText().toString().length() < 8) {
            focusEditTextRed(ForgetPassword.this, passowrdForgotEt, true, getString(R.string.validation_short_password),0);
            isValid = false;
        }
        if (TextUtils.isEmpty(emailForgotEt.getText().toString())) {
            focusEditTextRed3(ForgetPassword.this, emailForgotEt, true, getString(R.string.validation_email));
            isValid = false;
        }
        return isValid;
    }

    private void resestPassword() {
        SmartUtils.showLoadingDialog(ForgetPassword.this);
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(EMAIL, emailForgotEt.getText().toString());
        params.put(PASSWORD, passowrdForgotEt.getText().toString());

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "forgotPassword");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, ForgetPassword.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Toast.makeText(ForgetPassword.this, getString(R.string.password_reset), Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ForgetPassword.this, Login.class);
                    startActivity(i);
                    supportFinishAfterTransition();


                } else if (responseCode == 400) {
                    try {
                        SmartUtils.showSnackBar(ForgetPassword.this, response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseError() {
                SmartUtils.hideLoadingDialog();
            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, "", true);
    }

}
