package com.hitchtransporter.smart.framework;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.SwipeableTextView;

import java.util.ArrayList;

public abstract class SmartActivity extends AppCompatActivity implements Constants, SmartActivityHandler {

    /*Parent Containers*/
    private FrameLayout childViewContainer;
    private FrameLayout drawerContainer;
    public Toolbar toolbar;
    private SwipeableTextView txtNetworkInfo;

    public DrawerLayout drawerLayout;

    private LayoutInflater layoutInflater;
    private ActionBarDrawerToggle mDrawerToggle;
    private OnDrawerStateListener drawerStateListener;

    private NetworkStateListener networkStateListener;
    private ArrayList<KeyboardStateListener> keyboardStateListeners = new ArrayList<>();
    private KillReceiver clearActivityStack;

    public int orientation;

    public void setSnackbar(Snackbar snackbar) {

        this.snackbar = snackbar;
    }

    private Snackbar snackbar;

    private CoordinatorLayout snackBarContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimations();
        }
        super.onCreate(savedInstanceState);

        preOnCreate();

        setContentView(R.layout.smart_activity);

        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerContainer = (FrameLayout) findViewById(R.id.drawerContainer);
        childViewContainer = (FrameLayout) findViewById(R.id.lytChildViewContainer);
        clearActivityStack = new KillReceiver();

        registerReceiver(clearActivityStack, IntentFilter.create("clearStackActivity", "text/plain"));

        addChildViews();

        addSnackBarContainer();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    if (drawerStateListener != null) {
                        drawerStateListener.onDrawerOpen(drawerView);
                    }
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    if (drawerStateListener != null) {
                        drawerStateListener.onDrawerClose(drawerView);
                    }
                }

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    super.onDrawerSlide(drawerView, slideOffset);
                    childViewContainer.setTranslationX(slideOffset * drawerView.getWidth());
                    drawerLayout.bringChildToFront(drawerView);
                    drawerLayout.requestLayout();
                }
            };
            drawerLayout.addDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();

            setDrawer();
        }

        addDrawerLayout();

        initComponents();

        prepareViews();

        setActionListeners();

        if (toolbar != null) {
            manageAppBar(getSupportActionBar(), toolbar, mDrawerToggle);
        }

        postOnCreate();
    }

    private void setDrawer(){
        DrawerLayout.LayoutParams lp = new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT);
        if (SmartUtils.getCurrentLanguage().equals("ar")) {
            lp.gravity = Gravity.RIGHT;
        } else {
            lp.gravity = Gravity.LEFT;
        }
        drawerContainer.setLayoutParams(lp);
    }

    private void addDrawerLayout() {
        if (getDrawerLayoutID() != 0) {
            layoutInflater.inflate(getDrawerLayoutID(), drawerContainer);
        } else {
            disableSideMenu();
        }
    }

    protected void addSnackBarContainer() {
        layoutInflater.inflate(R.layout.snackbar_container, childViewContainer);
        snackBarContainer = (CoordinatorLayout) findViewById(R.id.snackBarPosition);
    }

    protected void closeDrawer() {
        drawerLayout.closeDrawer(drawerContainer);
    }

    private void setNetworkInfoProperties() {
        if (txtNetworkInfo != null) {
            txtNetworkInfo.setVisibility(SmartUtils.isNetworkAvailable() ? View.GONE : View.VISIBLE);
            txtNetworkInfo.setText(getString(R.string.network_not_available));
        }
    }

    protected void disableSideMenu() {
        if (drawerLayout != null && mDrawerToggle != null) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }
    }

    public void addChildViews() {
        if (getLayoutView() != null) {
            childViewContainer.addView(getLayoutView());
        } else {
            layoutInflater.inflate(getLayoutID(), childViewContainer);
        }

        txtNetworkInfo = (SwipeableTextView) findViewById(R.id.txtNetworkInfo);
        setNetworkInfoProperties();

        layoutInflater.inflate(R.layout.smart_transparent_frame, childViewContainer);
        childViewContainer.getViewTreeObserver().addOnGlobalLayoutListener(keyboardObserveListener);

    }

    public void setDrawerStateListener(OnDrawerStateListener drawerStateListener) {
        this.drawerStateListener = drawerStateListener;
    }

    protected View getScreenRootView() {
        return childViewContainer;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int color = getResources().getColor(R.color.textSecondary);
        MenuColorizer.colorMenu(this, menu, color, 255);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawerContainer)) {

            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private class NetworkStateListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            setNetworkInfoProperties();
        }
    }

    private final class KillReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    public int getDeviceWidth() {
        return getWindowManager().getDefaultDisplay().getWidth();
    }

    public int getDeviceHeight() {
        return getWindowManager().getDefaultDisplay().getHeight();
    }

    public View getSnackBarContainer() {
        return snackBarContainer;
    }

    public Snackbar getSnackbar() {
        return snackbar;
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkStateListener = new NetworkStateListener();
        registerReceiver(networkStateListener, new IntentFilter("NetworkState"));
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(networkStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(clearActivityStack);
    }

    public void setKeyboardStateListener(KeyboardStateListener keyboardStateListener) {
        keyboardStateListeners.add(keyboardStateListener);
    }

    ViewTreeObserver.OnGlobalLayoutListener keyboardObserveListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            childViewContainer.getRootView().getWindowVisibleDisplayFrame(r);
            int screenHeight = childViewContainer.getRootView().getRootView().getHeight();

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            int keypadHeight = screenHeight - r.bottom;

            final FrameLayout transparentFrame = (FrameLayout) findViewById(R.id.lytTransparentFrame);

            if (keypadHeight > screenHeight * 0.15) {
                // keyboard is opened
                transparentFrame.setVisibility(View.VISIBLE);

                for (int i = 0; i < keyboardStateListeners.size(); i++) {

                    if (keyboardStateListeners.get(i) != null) {
                        keyboardStateListeners.get(i).onKeyboardOpen();
                    }
                }

                transparentFrame.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (shouldKeyboardHideOnOutsideTouch()) {
                            SmartUtils.hideSoftKeyboard(SmartActivity.this);
                            return true;
                        } else {
                            return false;
                        }

                    }
                });
            } else {
                // keyboard is closed
                for (int i = 0; i < keyboardStateListeners.size(); i++) {

                    if (keyboardStateListeners.get(i) != null) {
                        keyboardStateListeners.get(i).onKeyboardClose();
                    }
                }
                transparentFrame.setVisibility(View.GONE);
                transparentFrame.setOnTouchListener(null);
            }
        }
    };

    public interface OnDrawerStateListener {
        void onDrawerOpen(View drawerView);

        void onDrawerClose(View drawerView);
    }
}
