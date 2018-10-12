package com.hitchtransporter.transporter.Activities;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.CustomClickListener;
import com.hitchtransporter.smart.customViews.SmartEditText;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.AlertMagnatic;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.smart.weservice.SmartWebManager;
import com.hitchtransporter.transporter.AA_Classes.MasterActivity;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.hitchtransporter.smart.framework.SmartUtils.dateFormatChanger;
import static com.hitchtransporter.smart.framework.SmartUtils.focusEditTextRed;
import static com.hitchtransporter.smart.framework.SmartUtils.getCurrentDate;
import static com.hitchtransporter.smart.framework.SmartUtils.getDateDialog;
import static com.hitchtransporter.smart.framework.SmartUtils.getLanguageCode;
import static com.hitchtransporter.smart.framework.SmartUtils.hideLoadingDialog;

public class BankAccount extends MasterActivity {


    private SmartEditText firstNameAccEt;
    private SmartEditText lastNameAccEt;
    private SmartEditText accNoEt;

    private SmartEditText postalCodeAccEt;
    private SmartEditText cityAccEt;
    private SmartEditText stateAccEt;
    private SmartEditText countryAccEt;
    private SmartEditText dobAccEt;
    private SmartTextView txtTransaction;
    //private SmartEditText ibanAccEt;
    private String birthDate;
    private boolean isEditing;
    private String firstNameResponse;
    private String lastNameResponse;
    private String accNoResponse;
    private String pinNoResponse;
    private String cityResponse;
    private String stateResponse;
    private String coutryResponse;
    private String birthDateResponse;
    private String countryCode;
    // private String ibanResponse;

    private MenuItem kMenuItem;

    @Override
    public int getLayoutID() {
        return R.layout.activity_bank_account;
    }

    @Override
    public void initComponents() {
        super.initComponents();
        setSwitch(true);
        setHeaderToolbar(getString(R.string.account_details));


        firstNameAccEt = findViewById(R.id.first_name_acc_et);
        lastNameAccEt = findViewById(R.id.last_name_acc_et);
        accNoEt = findViewById(R.id.acc_no_et);
        postalCodeAccEt = findViewById(R.id.postal_code_acc_et);
        cityAccEt = findViewById(R.id.city_acc_et);
        stateAccEt = findViewById(R.id.state_acc_et);
        countryAccEt = findViewById(R.id.country_acc_et);
        dobAccEt = findViewById(R.id.dob_acc_et);
        txtTransaction = findViewById(R.id.txtTransaction);

        // ibanAccEt = findViewById(R.id.iban_acc_et);
        /*String selected_locale = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SELECTED_LOCALE, ENGLISH_STR);
        if (selected_locale.equalsIgnoreCase(ENGLISH_STR)) {

        } else if (selected_locale.equalsIgnoreCase(FINNISH_STR)) {
            txtTransaction.setText(getString(R.string.transactions_will_occur_through_finland)+" "+getString(R.string.fennish));
        } else if (selected_locale.equalsIgnoreCase(SWEDISH_STR)) {
            txtTransaction.setText(getString(R.string.transactions_will_occur_through_finland)+" "+getString(R.string.swedish));
        } else {
            txtTransaction.setText(getString(R.string.transactions_will_occur_through_finland)+" "+getString(R.string.german));
        }*/

        getBankDetails();

    }

    @Override
    public void onBackPressed() {
        SmartUtils.getConfirmDialog(this, getString(R.string.quit_app), getString(R.string.quit_message),
                getString(R.string.yes), getString(R.string.no), true, new AlertMagnatic() {
                    @Override
                    public void PositiveMethod(DialogInterface dialog, int id) {
                        finish();
                    }

                    @Override
                    public void NegativeMethod(DialogInterface dialog, int id) {

                    }
                });
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();
        dobAccEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDateDialog(BankAccount.this, getCurrentDate(), new CustomClickListener() {
                    @Override
                    public void onClick(String value) {
                        birthDate = value;
                        dobAccEt.setText(dateFormatChanger(value));
                    }
                }, getString(R.string.date_format), false);
            }
        });
    }


    private boolean check() {
        boolean isValid = true;
      /*  if (TextUtils.isEmpty(ibanAccEt.getText().toString())) {
            isValid = false;
            focusEditTextRed(BankAccount.this, ibanAccEt, true, null,0);
        }*/
        if (TextUtils.isEmpty(dobAccEt.getText().toString())) {
            isValid = false;
            focusEditTextRed(BankAccount.this, dobAccEt, true, null, 0);
        }
        if (TextUtils.isEmpty(countryAccEt.getText().toString())) {
            isValid = false;
            focusEditTextRed(BankAccount.this, countryAccEt, true, null, 0);
        }
        if (TextUtils.isEmpty(stateAccEt.getText().toString())) {
            isValid = false;
            focusEditTextRed(BankAccount.this, stateAccEt, true, null, 0);
        }
        if (TextUtils.isEmpty(cityAccEt.getText().toString())) {
            isValid = false;
            focusEditTextRed(BankAccount.this, cityAccEt, true, null, 0);
        }
        if (TextUtils.isEmpty(postalCodeAccEt.getText().toString())) {
            isValid = false;
            focusEditTextRed(BankAccount.this, postalCodeAccEt, true, null, 0);
        }
        if (TextUtils.isEmpty(accNoEt.getText().toString())) {
            isValid = false;
            focusEditTextRed(BankAccount.this, accNoEt, true, null, 0);
        }
        if (TextUtils.isEmpty(lastNameAccEt.getText().toString())) {
            isValid = false;
            focusEditTextRed(BankAccount.this, lastNameAccEt, true, null, 0);
        }

        if (TextUtils.isEmpty(firstNameAccEt.getText().toString())) {
            isValid = false;
            focusEditTextRed(BankAccount.this, firstNameAccEt, true, null, 0);
        }

        return isValid;
    }


    private void getBankDetails() {
        SmartUtils.showLoadingDialog(BankAccount.this);
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "getBankDetail");

        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, BankAccount.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Log.d("@@@account_success", response.toString());
                    try {
                        firstNameResponse = response.getJSONObject(DATA).getString(FIRST_NAME);
                        lastNameResponse = response.getJSONObject(DATA).getString(LAST_NAME);
                        accNoResponse = response.getJSONObject(DATA).getString(ACCOUNT_NUMBER);
                        pinNoResponse = response.getJSONObject(DATA).getString(POSTAL_CODE);
                        cityResponse = response.getJSONObject(DATA).getString(CITY);
                        stateResponse = response.getJSONObject(DATA).getString(STATE);
                        coutryResponse = response.getJSONObject(DATA).getString(COUNTRY);
                        countryCode  = response.getJSONObject(DATA).getString(COUNTRY_CODE);
                        birthDate = response.getJSONObject(DATA).getString(DOB);
                        birthDateResponse = birthDate;
                        //  ibanResponse = response.getJSONObject(DATA).getString(IBAN);


                        firstNameAccEt.setText(firstNameResponse);
                        lastNameAccEt.setText(lastNameResponse);
                        accNoEt.setText(accNoResponse);
                        postalCodeAccEt.setText(pinNoResponse);
                        cityAccEt.setText(cityResponse);
                        stateAccEt.setText(stateResponse);
                        if (countryCode.equalsIgnoreCase("FI")) {
                            countryAccEt.setText(getString(R.string.finland));
                            txtTransaction.setText(getString(R.string.transactions_will_occur_through_finland)+" "+getString(R.string.finland));
                        }else if(countryCode.equalsIgnoreCase(SWEDEN_CODE)){
                            countryAccEt.setText(getString(R.string.sweden));
                            txtTransaction.setText(getString(R.string.transactions_will_occur_through_finland)+" "+getString(R.string.sweden));
                        }else {
                            countryAccEt.setText(getString(R.string.germany));
                            txtTransaction.setText(getString(R.string.transactions_will_occur_through_finland)+" "+getString(R.string.germany));
                        }
                        dobAccEt.setText(SmartUtils.dateFormatChanger(birthDateResponse));
                        //   ibanAccEt.setText(ibanResponse);

                        setDetails();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (responseCode == 400) {
                    editDetails();
                    /*try {
                        SmartUtils.showSnackBar(BankAccount.this, response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                }
            }

            @Override
            public void onResponseError() {
                SmartUtils.hideLoadingDialog();
            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, "", true);

    }


    private void sendBackDetails(String token) {
        SmartUtils.showLoadingDialog(BankAccount.this);
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));
        params.put(FIRST_NAME, firstNameAccEt.getText().toString());
        params.put(LAST_NAME, lastNameAccEt.getText().toString());
//
        params.put(ACCOUNT_NUMBER, accNoEt.getText().toString());
//        params.put(ACCOUNT_NUMBER, "FI89370400440532013000");
        params.put(POSTAL_CODE, postalCodeAccEt.getText().toString());
        params.put(CITY, cityAccEt.getText().toString());
        params.put(STATE, stateAccEt.getText().toString());
        params.put(COUNTRY, countryCode);
        params.put(DOB, birthDate);
        params.put(IBAN, "no Iban");
        params.put(CURRENCY, EUR_CURRENCY);
        params.put(BANK_TOKEN, token);
//        params.put(BANK_TOKEN, "tok_visa");


        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
//        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "addBankAccount");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "addBankAccount");

        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, BankAccount.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Log.d("@@@account_success", response.toString());
                    Toast.makeText(BankAccount.this, R.string.bank_accounted_created, Toast.LENGTH_LONG).show();
                    try {
                        JSONObject userData = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER, NO_DATA));
                        userData.put(IS_BANK_DETAIL, "1");
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_USER, userData.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //supportFinishAfterTransition();
                    setDetails();
                    kMenuItem.setIcon(R.drawable.ic_edit);
                } else if (responseCode == 400) {
                    try {
                        SmartUtils.showSnackBar(BankAccount.this, response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);
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

    private void generateTokenAndAddAccount(String accNo) {
        SmartUtils.showLoadingDialog(BankAccount.this);
        Stripe stripe = new Stripe(this);
        stripe.setDefaultPublishableKey(getString(R.string.stripe_key_test));
//        stripe.setDefaultPublishableKey(getString(R.string.stripe_key));
//        BankAccount bankAccount = new BankAccount("FI89370400440532013000", "FI", "EUR", "");
        com.stripe.android.model.BankAccount bankAccount = new com.stripe.android.model.BankAccount(accNo, countryCode, EUR_CURRENCY, "");
        stripe.createBankAccountToken(bankAccount, new TokenCallback() {
            @Override
            public void onError(Exception error) {
                hideLoadingDialog();
               // kMenuItem.setIcon(R.drawable.ic_edit);
                Log.d("@@Stripe_Error", error.getMessage());
                Toast.makeText(BankAccount.this, "ERROR:" + error.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(com.stripe.android.model.Token token) {
                hideLoadingDialog();
                Log.d("@@Bank_Token", token.getId());
                sendBackDetails(token.getId());
            }
        });


    }

    private void editDetails() {
        isEditing = true;
        firstNameAccEt.setEnabled(true);
        lastNameAccEt.setEnabled(true);
        accNoEt.setEnabled(true);
        postalCodeAccEt.setEnabled(true);
        stateAccEt.setEnabled(true);
        cityAccEt.setEnabled(true);
        countryAccEt.setEnabled(true);
        dobAccEt.setEnabled(true);
        // ibanAccEt.setEnabled(true);
        String country = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_HITCHER_COUNTRY, NO_USER_ID);
        if(country.equalsIgnoreCase("Finland") || country.equalsIgnoreCase("Finland")|| country.equalsIgnoreCase("Suomi")){
            countryAccEt.setText(getString(R.string.finland));

            if(country.equalsIgnoreCase("Suomi")){
                txtTransaction.setText("Maksuliikenne tapahtuu "+getString(R.string.finland)+" kautta");
            }else {
                txtTransaction.setText(getString(R.string.transactions_will_occur_through_finland)+" "+getString(R.string.finland));
            }
            countryCode = FINLAND_CODE;
        }else if(country.equalsIgnoreCase("Sweden") || country.equalsIgnoreCase("Sverige")|| country.equalsIgnoreCase("Ruotsi")){
            countryAccEt.setText(R.string.sweden);
            if(country.equalsIgnoreCase("Ruotsi")){
                txtTransaction.setText("Maksuliikenne tapahtuu "+getString(R.string.sweden)+" kautta");
            }else {
                txtTransaction.setText(getString(R.string.transactions_will_occur_through_finland)+" "+getString(R.string.sweden));
            }

            countryCode = SWEDEN_CODE;
        }else if(country.equalsIgnoreCase("Germany") || country.equalsIgnoreCase("Saksa")|| country.equalsIgnoreCase("Tyskland")){
            countryAccEt.setText(R.string.germany);
            if(country.equalsIgnoreCase("Tyskland")){
                txtTransaction.setText("Maksuliikenne tapahtuu "+getString(R.string.germany)+" kautta");
            }else {
                txtTransaction.setText(getString(R.string.transactions_will_occur_through_finland)+" "+getString(R.string.germany));
            }

            countryCode = GERMANY_CODE;
        }else {
            countryAccEt.setText(getString(R.string.finland));
            if(country.equalsIgnoreCase("Suomi")){
                txtTransaction.setText("Maksuliikenne tapahtuu "+getString(R.string.finland)+" kautta");
            }else {
                txtTransaction.setText(getString(R.string.transactions_will_occur_through_finland)+" "+getString(R.string.finland));
            }
            countryCode = FINLAND_CODE;
        }
        kMenuItem.setIcon(R.drawable.ic_save);

    }

    private void setDetails() {
        isEditing = false;
        firstNameAccEt.setEnabled(false);
        lastNameAccEt.setEnabled(false);
        accNoEt.setEnabled(false);
        postalCodeAccEt.setEnabled(false);
        stateAccEt.setEnabled(false);
        cityAccEt.setEnabled(false);
        countryAccEt.setEnabled(false);
        dobAccEt.setEnabled(false);
        //  ibanAccEt.setEnabled(false);


        kMenuItem.setIcon(R.drawable.ic_edit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_bank, menu);
        kMenuItem = menu.findItem(R.id.edit);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit) {
            if (isEditing) {
                if (check()) {
                    if (!firstNameAccEt.getText().toString().equalsIgnoreCase(firstNameResponse) ||
                            !lastNameAccEt.getText().toString().equalsIgnoreCase(lastNameResponse) ||
                            !accNoEt.getText().toString().equalsIgnoreCase(accNoResponse) ||
                            !postalCodeAccEt.getText().toString().equalsIgnoreCase(pinNoResponse) ||
                            !cityAccEt.getText().toString().equalsIgnoreCase(cityResponse) ||
                            !stateAccEt.getText().toString().equalsIgnoreCase(stateResponse) ||
                            !countryAccEt.getText().toString().equalsIgnoreCase(coutryResponse) ||
                            !birthDate.equalsIgnoreCase(birthDateResponse)
                        // ||!ibanAccEt.getText().toString().equalsIgnoreCase(ibanResponse)) {

                            ) {
                        Log.d("@@ACC_NO", accNoEt.getText().toString());
                        generateTokenAndAddAccount(accNoEt.getText().toString());
//                        sendBackDetails("12164");

                    } else {
                        Log.e("@@@BANK_CHECK1", "BANK_CHECK1");
                        setDetails();
                    }
                }
            } else {
                editDetails();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle
            actionBarDrawerToggle) {
        super.manageAppBar(actionBar, toolbar, actionBarDrawerToggle);
        toolbar.setNavigationIcon(R.drawable.menu);
    }
}
