package com.hitchtransporter.smart.framework;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.common.Iso2Phone;
import com.hitchtransporter.smart.common.Object_Image;
import com.hitchtransporter.smart.customViews.CustomClickListener;
import com.hitchtransporter.smart.customViews.SmartDatePicker;
import com.hitchtransporter.smart.customViews.SmartEditText;
import com.hitchtransporter.smart.customViews.SmartTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmartUtils implements Constants {

    private static final String TAG = "SmartUtil";
    private static boolean isNetworkAvailable;
    private static ProgressDialog progressDialog;
    private static Dialog loadingDialog;
    private static Geocoder geocoder;
    private static int count = 0;

    public static boolean isReloadRequired() {
        return isReloadRequired;
    }

    public static void setIsReloadRequired(boolean isReloadRequired) {
        SmartUtils.isReloadRequired = isReloadRequired;
    }

    private static boolean isReloadRequired = false;

    public static boolean isNetworkAvailable() {
        return isNetworkAvailable;
    }

    public static void setNetworkStateAvailability(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {

                        isNetworkAvailable = true;
                        return;
                    }
                }
            }
        }
        isNetworkAvailable = false;
    }

    // Validation

    /**
     * This method used to email validator.
     *
     * @param mailAddress represented email
     * @return represented {@link Boolean}
     */
    public static boolean emailValidator(final String mailAddress) {
        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(mailAddress);
        return matcher.matches();
    }

    /**
     * This method used to birth date validator.
     *
     * @param birthDate represented birth date
     * @return represented {@link Boolean}
     */
    public static boolean birthdateValidator(String birthDate, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            Date date = dateFormat.parse(birthDate);
            Calendar bdate = Calendar.getInstance();
            bdate.setTime(date);
            Calendar today = Calendar.getInstance();

            if (bdate.compareTo(today) == 1) {
                return false;
            } else {
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }


    //Dates

    /**
     * This method used to get date from string.
     *
     * @param strDate represented date
     * @return represented {@link Date}
     */
    public static Calendar getDateFromString(String strDate, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Calendar calnder = Calendar.getInstance();
        Date date;
        try {
            date = dateFormat.parse(strDate);
            calnder.setTime(date);
            return calnder;
        } catch (Throwable e) {
            return Calendar.getInstance();
        }
    }

    /**
     * This method used to get time from string.
     *
     * @param strTime represented time
     * @return represented {@link Date}
     */
    public static Calendar getTimeFromString(String strTime, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date;
        Calendar calnder = Calendar.getInstance();
        try {
            date = dateFormat.parse(strTime);
            calnder.setTime(date);
            return calnder;
        } catch (Throwable e) {
            return Calendar.getInstance();
        }
    }

    public static long getTimeStampFromString(String time, String timeFormat) {
        long returnTimeStamp = 0;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(timeFormat);
            Date parsedDate = dateFormat.parse(time);
            returnTimeStamp = parsedDate.getTime();
        } catch (Exception e) {
            Log.e("@@ERROR_TimeStamp", e.getMessage());
        }
        return returnTimeStamp;
    }

    /**
     * This method used to get date dialog.
     *
     * @param selectedDate represented date
     */
    public static void getDateDialog(Context context, final String selectedDate, final CustomClickListener target, final String format, boolean setMinimum) {
        Calendar date = getDateFromString(selectedDate, format);
        Calendar today = Calendar.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SmartDatePicker dateDlg = new SmartDatePicker(context, R.style.AppDatePicketStyle,
                    new DatePickerDialog.OnDateSetListener() {

                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Time chosenDate = new Time();
                            chosenDate.set(dayOfMonth, monthOfYear, year);
                            long dt = chosenDate.toMillis(true);
                            CharSequence strDate = DateFormat.format(format, dt);
                            target.onClick(strDate.toString());
                        }
                    }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), false);

            if (setMinimum) {
                Calendar calendar = Calendar.getInstance();
                dateDlg.getDatePicker().setMinDate(calendar.getTimeInMillis());
            }
            dateDlg.show();
        } else {
            SmartDatePicker dateDlg = new SmartDatePicker(context, new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Time chosenDate = new Time();
                    chosenDate.set(dayOfMonth, monthOfYear, year);
                    long dt = chosenDate.toMillis(true);
                    CharSequence strDate = DateFormat.format(format, dt);
                    target.onClick(strDate.toString());
                }
            }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), false);
            dateDlg.getDatePicker().setMaxDate(date.getTimeInMillis());

            dateDlg.show();
        }
    }

    /**
     * This method used to get time dialog.
     *
     * @param strTime represented time
     * @param target  represented {@link CustomClickListener}
     */
    public static void getTimeDialog(Context context, final String strTime, final CustomClickListener target, final String format) {
        Calendar date = getTimeFromString(strTime, format);
        TimePickerDialog timeDialog = new TimePickerDialog(context, R.style.AppDatePicketStyle,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar date = Calendar.getInstance();
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        CharSequence strDate = DateFormat.format(format, date);
                        target.onClick(strDate.toString());
                    }
                }, date.get(Calendar.HOUR), date.get(Calendar.MINUTE), true);

        timeDialog.show();
    }


    /**
     * This method used to get date-time dialog.
     *
     * @param strDate represented date-time
     * @param target  represented {@link CustomClickListener}
     */
    public static void getDateTimeDialog(final Context context, final String strDate, boolean isCurent, final CustomClickListener target, final String format) {
        final Calendar date = getDateFromString(strDate, format);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DatePickerDialog dateDialog = new DatePickerDialog(context, R.style.AppDatePicketStyle,
                    new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            final int y = year;
                            final int m = monthOfYear;
                            final int d = dayOfMonth;

                            new TimePickerDialog(context, R.style.AppDatePicketStyle,
                                    new TimePickerDialog.OnTimeSetListener() {

                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                            Time chosenDate = new Time();
                                            chosenDate.set(0, minute, hourOfDay, d, m, y);
                                            long dt = chosenDate.toMillis(true);
                                            CharSequence strDate = DateFormat.format(format, dt);
                                            target.onClick(strDate.toString());

                                        }
                                    }, date.get(Calendar.HOUR), date.get(Calendar.MINUTE), true).show();

                        }
                    }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));

            if (isCurent) {
                Calendar calendar = Calendar.getInstance();
                dateDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            }
            dateDialog.show();
        } else {
            DatePickerDialog dateDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    final int y = year;
                    final int m = monthOfYear;
                    final int d = dayOfMonth;

                    if (count == 1) {

                        new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Time chosenDate = new Time();
                                chosenDate.set(0, minute, hourOfDay, d, m, y);
                                long dt = chosenDate.toMillis(true);
                                CharSequence strDate = DateFormat.format(format, dt);
                                target.onClick(strDate.toString());
                                count = 0;
                            }


                        }, date.get(Calendar.HOUR), date.get(Calendar.MINUTE), false).show();
                    }
                    count++;
                }
            }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
            if (isCurent) {
                Calendar calendar = Calendar.getInstance();
                dateDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            }
            dateDialog.show();
        }
    }

    /**
     * This method used to get date-time dialog.
     *
     * @param strDate represented date-time
     * @param target  represented {@link CustomClickListener}
     */
    public static void getStartEndDateTimeDialog(final Context context, final String strDate, final CustomClickListener target, final String format) {
        final Calendar date = getDateFromString(strDate, format);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DatePickerDialog dateDialog = new DatePickerDialog(context, R.style.AppDatePicketStyle,
                    new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            final int y = year;
                            final int m = monthOfYear;
                            final int d = dayOfMonth;

                            new TimePickerDialog(context, R.style.AppDatePicketStyle,
                                    new TimePickerDialog.OnTimeSetListener() {

                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                            Time chosenDate = new Time();
                                            chosenDate.set(0, minute, hourOfDay, d, m, y);
                                            long dt = chosenDate.toMillis(true);
                                            CharSequence strDate = DateFormat.format(format, dt);
                                            target.onClick(strDate.toString());

                                        }
                                    }, date.get(Calendar.HOUR), date.get(Calendar.MINUTE), false).show();

                        }
                    }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
            dateDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dateDialog.show();
        } else {
            DatePickerDialog dateDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    final int y = year;
                    final int m = monthOfYear;
                    final int d = dayOfMonth;

                    if (count == 1) {

                        new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                Time chosenDate = new Time();
                                chosenDate.set(0, minute, hourOfDay, d, m, y);
                                long dt = chosenDate.toMillis(true);
                                CharSequence strDate = DateFormat.format(format, dt);
                                target.onClick(strDate.toString());
                                count = 0;
                            }


                        }, date.get(Calendar.HOUR), date.get(Calendar.MINUTE), false).show();
                    }
                    count++;
                }
            }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
            dateDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dateDialog.show();
        }
    }


    public static void showLoadingDialog(final Context context) {
        if (context != null) {


            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
            loadingDialog = new Dialog(context);
            loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            View view = View.inflate(context, R.layout.loading_dialog, null);
            loadingDialog.setContentView(view);
            loadingDialog.setCancelable(false);
            loadingDialog.setCanceledOnTouchOutside(false);
            Window window = loadingDialog.getWindow();
            assert window != null;
            window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            loadingDialog.show();
        } else {
            Log.d("No Loading Dailog", "No Context");
        }
    }

    public static void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    /**
     * This method will show the progress dialog with given message in the given
     * activity's context.<br>
     * The progress dialog can be set cancellable by passing appropriate flag in
     * parameter. User can dismiss the current progress dialog by pressing back
     * SmartButton if the flag is set to <b>true</b>; This method can also be
     * called from non UI threads.
     *
     * @param context = Context context will be current activity's context.
     *                <b>Note</b> : A new progress dialog will be generated on
     *                screen each time this method is called.
     */
    public static void showProgressDialog(final Context context, String msg, final boolean isCancellable) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        }
        progressDialog = ProgressDialog.show(context, "", "");

        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(isCancellable);
        ((ProgressBar) progressDialog.findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.primaryColor), PorterDuff.Mode.SRC_ATOP);
        ((SmartTextView) progressDialog.findViewById(R.id.txtMessage)).setText(msg == null || msg.trim().length() <= 0 ? context.getString(R.string.dialog_loading_please_wait) : msg);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * This method will hide existing progress dialog.<br>
     * It will not throw any Exception if there is no progress dialog on the
     * screen and can also be called from non UI threads.
     */
    static public void hideProgressDialog() {
        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = null;
        } catch (Throwable e) {
            progressDialog = null;
        }
    }

    public static ProgressDialog getProgressDialog() {
        return progressDialog;
    }


    /**
     * This method will generate and show the Ok dialog with given message and
     * single message SmartButton.<br>
     *
     * @param title  = String title will be the title of OK dialog.
     * @param msg    = String msg will be the message in OK dialog.
     * @param target = String target is AlertNewtral callback for OK SmartButton
     *               click action.
     */
    static public void getConfirmDialog(Context context, String title, String msg, String positiveBtnCaption,
                                        String negativeBtnCaption, boolean isCancelable, final AlertMagnatic target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title).setMessage(msg).setCancelable(false)
                .setPositiveButton(positiveBtnCaption, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        target.PositiveMethod(dialog, id);
                    }
                })
                .setNegativeButton(negativeBtnCaption, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        target.NegativeMethod(dialog, id);
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setCancelable(isCancelable);
        alert.show();
    }

    /**
     * This method will generate and show the Ok dialog with given message and
     * single message SmartButton.<br>
     *
     * @param msg    = String msg will be the message in OK dialog.
     * @param target = String target is AlertNewtral callback for OK SmartButton
     *               click action.
     */
    static public void getConfirmDialog(Context context, String msg, String positiveBtnCaption,
                                        String negativeBtnCaption, boolean isCancelable, final AlertMagnatic target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(msg).setCancelable(false)
                .setPositiveButton(positiveBtnCaption, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        target.PositiveMethod(dialog, id);
                    }
                })
                .setNegativeButton(negativeBtnCaption, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        target.NegativeMethod(dialog, id);
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setCancelable(isCancelable);
        alert.show();
    }

    /**
     * This method will generate and show the Ok dialog with given message and
     * single message SmartButton.<br>
     *
     * @param msg           = String msg will be the message in OK dialog.
     * @param buttonCaption = String SmartButtonCaption will be the name of OK
     *                      SmartButton.
     * @param target        = String target is AlertNewtral callback for OK SmartButton
     *                      click action.
     */
    static public void getOKDialog(Context context, String msg, String buttonCaption,
                                   boolean isCancelable, final AlertNeutral target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(buttonCaption, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        target.NeutralMathod(dialog, id);
                    }
                });

        AlertDialog alert = builder.create();
        alert.setCancelable(isCancelable);
        alert.show();
    }

    /**
     * This method will generate and show the Ok dialog with given message and
     * single message SmartButton.<br>
     *
     * @param title         = String title will be the title of OK dialog.
     * @param msg           = String msg will be the message in OK dialog.
     * @param buttonCaption = String SmartButtonCaption will be the name of OK
     *                      SmartButton.
     * @param target        = String target is AlertNewtral callback for OK SmartButton
     *                      click action.
     */
    static public void getOKDialog(Context context, String title, String msg, String buttonCaption,
                                   boolean isCancelable, final AlertNeutral target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(buttonCaption, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        target.NeutralMathod(dialog, id);
                    }
                });

        AlertDialog alert = builder.create();
        alert.setCancelable(isCancelable);
        alert.show();
    }

    /**
     * This method will show short length Toast message with given string.
     *
     * @param msg = String msg to be shown in Toast message.
     */
    static public void ting(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method will show long length Toast message with given string.
     *
     * @param msg = String msg to be shown in Toast message.
     */
    static public void tong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * This method will show Center with given String
     **/

    static public void toCenter(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    // Audio, Image and Video

    /**
     * This method used to decode file from string path.
     *
     * @param path represented path
     * @return represented {@link Bitmap}
     */
    static public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    //General Methods

    /**
     * This method will return android device UDID.
     *
     * @return DeviceID = String DeviceId will be the Unique Id of android
     * device.
     */
    /*static public String getDeviceUDID(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }*/


    public static String getB64Auth(String userName, String password) {
        String source = userName + ":" + password;
        String ret = "Basic " + Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        return ret;
    }

    /**
     * This method used to hide soft keyboard.
     */
    static public void hideSoftKeyboard(Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    /**
     * This method used to show soft keyboard.
     */
    static public void showSoftKeyboard(Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception e) {
        }
    }

    /**
     * This method used to convert json to map.
     *
     * @param object represented json object
     * @return represented {@link Map <String, String>}
     * @throws JSONException represented {@link JSONException}
     */
    static public Map<String, String> jsonToMap(JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)).toString());
        }
        return map;
    }

    /**
     * This method used to convert json to Object.
     *
     * @param json represented json object
     * @return represented {@link Object}
     * @throws JSONException represented {@link JSONException}
     */
    static public Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return jsonToMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }

    /**
     * This method used to convert json array to List.
     *
     * @param array represented json array
     * @return represented {@link List}
     * @throws JSONException represented {@link JSONException}
     */
    static public List toList(JSONArray array) throws JSONException {
        List list = new ArrayList();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    /**
     * This method used to string array from string with (,) separated.
     *
     * @param value represented value
     * @return represented {@link String} array
     */
    static public String[] getStringArray(final String value) {
        try {
            if (value.length() > 0) {
                final JSONArray temp = new JSONArray(value);
                int length = temp.length();
                if (length > 0) {
                    final String[] recipients = new String[length];
                    for (int i = 0; i < length; i++) {
                        recipients[i] = temp.getString(i).equalsIgnoreCase("null") ? "1" : temp.getString(i);
                    }
                    return recipients;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * This method used to string array from arraylist.
     *
     * @param value represented value
     * @return represented {@link String} array
     */
    static public String[] getStringArray(final ArrayList<String> value) {
        try {
            String[] array = new String[value.size()];
            for (int i = 0; i < value.size(); i++) {
                array[i] = value.get(i);
            }
            return array;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray concatJsonArrays(JSONArray... arrs)
            throws JSONException {
        JSONArray result = new JSONArray();
        for (JSONArray arr : arrs) {
            for (int i = 0; i < arr.length(); i++) {
                result.put(arr.get(i));
            }
        }
        return result;
    }

    static public boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    static public String createExternalDirectory(String directoryName) {

        if (SmartUtils.isExternalStorageAvailable()) {

            File file = new File(Environment.getExternalStorageDirectory(), directoryName);
            if (!file.mkdirs()) {
                Log.e(TAG, "Directory may exist");
            }
            return file.getAbsolutePath();
        } else {

            Log.e(TAG, "External storage is not available");
        }
        return null;
    }

    static public void clearActivityStack(Activity currentActivity, Intent intent) {

    /*    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(currentActivity);
        Intent mainIntent = IntentCompat.makeRestartActivityTask(intent.getComponent());
        ActivityCompat.startActivity(currentActivity, mainIntent, options.toBundle());*/
    }

    static public int convertSizeToDeviceDependent(Context context, int value) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return ((dm.densityDpi * value) / 160);
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    static public void
    showSnackBar(Context context, String message, int length) {
        Snackbar snackbar = Snackbar.make(((SmartActivity) context).getSnackBarContainer(), message, length);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.snackBar_bg));
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextSize(15);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
        ((SmartActivity) context).setSnackbar(snackbar);
    }

    static public void hideSnackBar(Context context) {
        if (((SmartActivity) context).getSnackbar() != null) {
            ((SmartActivity) context).getSnackbar().dismiss();
        }
    }

    public static boolean isOSPreLollipop() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    private static Date parseDate(String date, String hours24) {
        SimpleDateFormat inputParser = new SimpleDateFormat(hours24);
        try {
            return inputParser.parse(date);
        } catch (ParseException e) {
            return new Date(0);
        }
    }

    public static String getCurrentDay() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("EEE");
        return df.format(c.getTime());
    }

    public static int getMonth(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
        Date parse = null;
        try {
            parse = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(parse);
        return c.get(Calendar.MONTH);
    }

    public static int getYear(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
        Date parse = null;
        try {
            parse = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(parse);
        return c.get(Calendar.YEAR);
    }

    public static String getTime(String dateString) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-HH HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("h:mm a");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat1.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat2.format(convertedDate);
    }

    public static String getDate(String dateString) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yyyy");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat1.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat2.format(convertedDate);
    }

    public static String formatTimeStampDate(Context context, long date) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(date * 1000);
        return DateFormat.format(context.getString(R.string.date_format_2), cal).toString();
    }

    public static String formatTimeStampDate2(Context context, long date) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(date * 1000);
        return DateFormat.format(context.getString(R.string.date_format_3), cal).toString();
    }

    public static String formatDate(String dateString) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yyyy - h:mm a");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat1.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat2.format(convertedDate);
    }

    public static String formatSearchDate(String dateString) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yyyy");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat1.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat2.format(convertedDate);
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c.getTime());
    }

    public static String getCurrentDateTime(Context context) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(context.getString(R.string.date_time_format));
        return df.format(c.getTime());
    }

    public static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    static public void removeCookie() {
        SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().edit().remove(Constants.SP_COOKIES);
        SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().edit().commit();
    }

    static public String validateResponse(JSONObject response, String errorMessage) {
        if (response.has("status")) {
            try {
                if (response.has("data") && response.getJSONObject("data").length() > 0) {
                    errorMessage = response.getJSONObject("data").toString();
                }
            } catch (Throwable e) {
            }
        } else {
            errorMessage = "There is some problem on server, Please contact to server guys!";
        }
        return errorMessage;
    }


    static public String validateResponse2(JSONObject response, String errorMessage) {
        if (response.has("status")) {
            try {
                if (response.getJSONObject("data").has("message") && response.getJSONObject("data").getString("message").length() > 0) {
                    errorMessage = response.getJSONObject("data").getString("message");
                }
            } catch (Throwable e) {
            }
        } else {
            errorMessage = "There is some problem on server, Please contact to server guys!";
        }
        return errorMessage;
    }

    static public int getResponseCode(JSONObject response) {
        if (response.has("status")) {
            try {
                return response.getInt("status");
            } catch (JSONException e) {
                return 108;
            }
        }
        return 108;
    }

    static public String getCountryPhoneCode(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimCountryIso() != null && tm.getSimCountryIso().length() > 0) {
            return Iso2Phone.getPhone(tm.getSimCountryIso());
        } else {
            return Iso2Phone.getPhone(tm.getNetworkCountryIso());
        }
    }

    public static boolean isPhoneValid(String input) {
        return input.length() != 10 ? false : android.util.Patterns.PHONE.matcher(input).matches();
    }

    /**
     * This method used to get address list from latitude-longitude
     *
     * @param lat represented latitude (0-for current latitude)
     * @param lng represented longitude (0-for current longitude)
     * @return represented {@link Address}
     */
    public static Address getAddressFromLatLong(Context context, double lat, double lng) {
        geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(lat, lng, 1);
            Log.v("@@@@ADDRESS", list.toString());
            return list.get(0);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method used to get latitude-longitude from address.
     *
     * @param address represented address
     * @return represented {@link Address}
     */
    public static Address getLatLongFromAddress(Context context, String address) {
        if (address != null && address.length() > 0) {
            geocoder = new Geocoder(context);

            List<Address> list = null;
            try {
                list = geocoder.getFromLocationName(address, 1);
                Log.v("@@@@ADDRESS", list.toString());
                return list.get(0);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getLatitude() {
        return SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_USER_LATITUDE, "41.8268");
    }

    public static void setLatitude(String lat) {
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_USER_LATITUDE, lat);
    }

    public static String getLongitude() {
        return SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_USER_LONGITUDE, "-71.4025");
    }

    public static void setLongitude(String lng) {
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_USER_LONGITUDE, lng);
    }

    public static Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }

    public static String getCommaSeparated(ArrayList<String> selectId) {
        if (selectId.size() > 0) {
            return TextUtils.join(",", selectId);
        } else
            return "";
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap, Context context) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        Bitmap resizedBitmap = getResizedBitmap(bitmap, (int) SmartUtils.convertDpToPixel(35, context)
                , (int) SmartUtils.convertDpToPixel(35, context));
        Bitmap output = Bitmap.createBitmap(resizedBitmap.getWidth(),
                resizedBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(resizedBitmap.getWidth() / 2, resizedBitmap.getHeight() / 2,
                resizedBitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(resizedBitmap, rect, rect, paint);
        return output;
    }

    public static boolean getDisplayLanguage(String locale) {
        String language = Locale.getDefault().getLanguage();
        return language.equals(locale);
    }

    public static String getCurrentLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static void ForcefullyLocaleChange(Context context, String locale) {
        Locale.setDefault(new Locale(locale));
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(locale));
        } else {
            conf.locale = new Locale(locale);
        }
        res.updateConfiguration(conf, dm);
        Log.e("@@@Locale", Locale.getDefault().getLanguage());
    }

    public static JSONObject getUser() {
        try {
            Log.e("@@USER_INFO===", SmartApplication.REF_SMART_APPLICATION.
                    readSharedPreferences().getString(SP_LOGGED_USER, null));
            return new JSONObject(SmartApplication.REF_SMART_APPLICATION.
                    readSharedPreferences().getString(SP_LOGGED_USER, null));


        } catch (Exception e) {
            Log.e("@@NO_USER_ERROR===", e.toString());
        }
        return null;
    }

    public static String setGender(String genderCode) {
        if (genderCode.equalsIgnoreCase("0")) {
            return "Male";
        } else {
            return "Female";
        }
    }

    public static void logLargeString(String str) {
        if (str.length() > 3000) {
            Log.i(TAG, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(TAG, str); // continuation
        }
    }

    public static long dayDiff(Context context, String start_date, String end_date) {
        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat(context.getString(R.string.date_format));

        Date d1 = null;
        Date d2 = null;
        long diffDays = 0;

        try {
            d1 = format.parse(start_date);
            d2 = format.parse(end_date);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            diffDays = diff / (24 * 60 * 60 * 1000);

            // Log.d(TAG, "diffDays==" + diffDays);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return diffDays;
    }


    public static String timeFormatChanger(String time) {
        String[] split_time_array = time.split(":");
        String am_pm = "AM";
        int hrs_int, min, sec;
        hrs_int = Integer.parseInt(split_time_array[0]);
        if (hrs_int > 12) {
            hrs_int = hrs_int - 12;
            am_pm = "PM";
        } else if (hrs_int == 12) {
            am_pm = "PM";
        }
        return hrs_int + ":" + split_time_array[1] + " " + am_pm;

    }

    public static String timeStampFormatChanger(String time) {

        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        sdf.setTimeZone(tz);//set time zone.
        String localTime = sdf.format(new Date(Long.parseLong(time) * 1000));
        Date date = new Date();
        String formattedDate = "";
        try {
            date = sdf.parse(localTime);//get local date
            formattedDate = new SimpleDateFormat("MMM,dd yyyy hh:mm a").format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] str = date.toString().split(" ");
//        return str[0] + " " + str[1]+ " " + str[2]+ " " + str[3]+ " " + str[4];
        return formattedDate;
    }


    public static String timeFormatChanger2(String time) {
        String[] split_time_array = time.split(":");
        String am_pm = "AM";
        int hrs_int, min, sec;
        hrs_int = Integer.parseInt(split_time_array[0]);
        if (hrs_int > 12) {
            hrs_int = hrs_int - 12;
            am_pm = "PM";
        } else if (hrs_int == 12) {
            am_pm = "PM";
        }
        return hrs_int + ":" + split_time_array[1] + " " + am_pm;

    }


    public static String timeFormatChanger3(String time) {
        String[] split_time_array = time.split(":");
        return split_time_array[0] + ":" + split_time_array[1];
    }

    public static String extractDate(String date_time) {
        //   Log.d("@@date_time", date_time.split(" ")[0]);
        return date_time.split(" ")[0];
    }

    public static String extractTime(String date_time) {
        //   Log.d("@@date_time", date_time.split(" ")[0]);
        return date_time.split(" ")[1];
    }


    public static String dateFormatChanger(String date_str) {
        String formattedDate = "";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(date_str);
            formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static String dateFormatChanger2(String date_str) {
        String formattedDate = "";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(date_str);
            formattedDate = new SimpleDateFormat("MMM,dd yyyy EEEE").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static String dateTimeFormatChanger(String date_time_str) {
        String[] date_time_array = date_time_str.split(" ");
        String formattedDateTime = "";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(date_time_array[0]);
            formattedDateTime = new SimpleDateFormat("MMM,dd yyyy").format(date) + " " + timeFormatChanger(date_time_array[1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDateTime;
    }

    public static String setCandidacy(String candidacy) {
        String result;
        try {
            if (Integer.parseInt(candidacy) == 0) {
                result = "New Candidate";
            } else {
                result = candidacy + " years";
            }
        } catch (NumberFormatException e) {
            result = "0";
        }

        return result;
    }


    public static String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }

    public static Object_Image getImageFileUri(Context context) {

        File imagePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "");
        if (!imagePath.exists()) {
            if (!imagePath.mkdirs()) {
                return null;
            } else {
                //create new folder
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "MyProject_" + timeStamp + ".jpg";
        File image = new File(imagePath, imageName);

        if (!image.exists()) {
            try {
                image.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Object_Image(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", image), imageName);

    }

    public static String getCurrentTimeInHHmmss() {
        String delegate = "HH:mm:ss";
        return (String) DateFormat.format(delegate, Calendar.getInstance().getTime());
    }

    public static void focusEditTextRed(Context context, final SmartEditText focusedView, Boolean showSnackBar, String errorMsg, final int drawable) {
        focusedView.requestFocus();
        //  focusedView.setBackgroundResource(R.drawable.bg_round_bordered_red);
        if (showSnackBar) {
            if (errorMsg != null) {
                SmartUtils.showSnackBar(context, errorMsg, Snackbar.LENGTH_LONG);
            } else {
                SmartUtils.showSnackBar(context, context.getString(R.string.please_fill_the_empty_fields), Snackbar.LENGTH_LONG);
            }
        }
        // focusedView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_1, 0);
        if (drawable != 0) {
            focusedView.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, R.drawable.ic_error_1, 0);
        } else {
            focusedView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_1, 0);
        }

        focusedView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //   focusedView.setBackgroundResource(R.drawable.bg_round_bordered_grey);
                if (drawable != 0) {
                    focusedView.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
                } else {
                    focusedView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static void focusTextViewRed(Context context, final SmartTextView focusedView, Boolean showSnackBar, String errorMsg) {
        focusedView.requestFocus();
        //  focusedView.setBackgroundResource(R.drawable.bg_round_bordered_red);
        if (showSnackBar) {
            if (errorMsg != null) {
                SmartUtils.showSnackBar(context, errorMsg, Snackbar.LENGTH_LONG);
            } else {
                SmartUtils.showSnackBar(context, context.getString(R.string.please_fill_the_empty_fields), Snackbar.LENGTH_LONG);
            }
        }
        focusedView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_1, 0);

        focusedView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //   focusedView.setBackgroundResource(R.drawable.bg_round_bordered_grey);
                focusedView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public static void focusEditTextRed2(Context context, final SmartEditText focusedView, Boolean showSnackBar, String errorMsg) {
        focusedView.requestFocus();

        if (showSnackBar) {
            if (errorMsg != null) {
                SmartUtils.showSnackBar(context, errorMsg, Snackbar.LENGTH_LONG);
            } else {
                SmartUtils.showSnackBar(context, context.getString(R.string.please_fill_the_empty_fields), Snackbar.LENGTH_LONG);
            }
        }
        // focusedView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_1, 0);

        focusedView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                focusedView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static void focusEditTextRed3(Context context, final SmartEditText focusedView, Boolean showSnackBar, String errorMsg) {
        focusedView.requestFocus();
        //    focusedView.setBackgroundResource(R.drawable.bg_round_bordered_red);
        if (showSnackBar) {
            if (errorMsg != null) {
                SmartUtils.showSnackBar(context, errorMsg, Snackbar.LENGTH_LONG);
            } else {
                SmartUtils.showSnackBar(context, context.getString(R.string.please_fill_the_empty_fields), Snackbar.LENGTH_LONG);
            }
        }
        focusedView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_1, 0);

        focusedView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //    focusedView.setBackgroundResource(R.drawable.bg_round_white);
                focusedView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public static void focusEditTextRed4(Context context, Boolean showSnackBar, String errorMsg, final int drawable, final SmartEditText... focusedView) {
        //focusedView.requestFocus();
        //  focusedView.setBackgroundResource(R.drawable.bg_round_bordered_red);
        if (showSnackBar) {
            if (errorMsg != null) {
                SmartUtils.showSnackBar(context, errorMsg, Snackbar.LENGTH_LONG);
            } else {
                SmartUtils.showSnackBar(context, context.getString(R.string.please_fill_the_empty_fields), Snackbar.LENGTH_LONG);
            }
        }
        // focusedView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_1, 0);
        for (final SmartEditText et : focusedView) {

            if (drawable != 0) {
                et.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, R.drawable.ic_error_1, 0);
            } else {
                et.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_1, 0);
            }

            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //   focusedView.setBackgroundResource(R.drawable.bg_round_bordered_grey);
                    for (final SmartEditText et : focusedView) {
                        if (drawable != 0) {
                            et.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
                        } else {
                            et.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }


    public static void checkSetStoragePermission(Context context, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
        }
    }

    public static String decimalFormatter(String value) {
        value = value.replace(",", ".");
        return new DecimalFormat("0.00").format(Float.parseFloat(value));
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static String getLanguageCode() {
        String selectedLocale = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SELECTED_LOCALE, NO_DATA);
        String codeToReturn;
        switch (selectedLocale) {
            case ENGLISH_STR:
                codeToReturn = ENGLISH_CODE;
                break;
            case GERMAN_STR:
                codeToReturn = GERMAN_CODE;
                break;
            case FINNISH_STR:
                codeToReturn = FINNISH_CODE;
                break;
            case SWEDISH_STR:
                codeToReturn = SWEDISH_CODE;
                break;
            default:
                codeToReturn = ENGLISH_CODE;
                break;
        }
        return codeToReturn;
    }

    public static String getUserType() {
        return SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_TYPE, USER_CODE);
    }

    public static boolean checkIfTransporter() {
        return getUserType().equalsIgnoreCase(TRANSPORTER_CODE);
    }

    public static boolean checkIfUser() {
        return getUserType().equalsIgnoreCase(USER_CODE);
    }

}
