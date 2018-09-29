package com.hitchtransporter.transporter.AA_Classes;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;

import com.hitchtransporter.smart.framework.SmartSuperMaster;

/**
 * Created by ebiztrait on 22/8/16.
 */
public abstract class CoreMaster extends SmartSuperMaster {

    @Override
    public View getFooterLayoutView() {
        return null;
    }

    @Override
    public int getFooterLayoutID() {
        return 0;
    }

    @Override
    public View getHeaderLayoutView() {
        return null;
    }

    @Override
    public int getHeaderLayoutID() {
        return 0;
    }

    @Override
    public void preOnCreate() {
    }

    @Override
    public View getLayoutView() {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setAnimations() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setAllowReturnTransitionOverlap(true);
    }

    @Override
    public void initComponents() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public void postOnCreate() {
    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return true;
    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {

    }
}
