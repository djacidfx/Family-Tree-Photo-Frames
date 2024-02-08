package com.demo.example.PictureFrames;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.demo.example.AdAdmob;
import com.demo.example.R;

import com.google.android.material.navigation.NavigationView;


public class StartActivity extends Activity {
    static boolean onlyFirstTime = true;
    boolean doubleBackToExitPressedOnce = false;
    DrawerLayout drawer;
    NavigationView nav_view;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(128);
        setContentView(R.layout.usenavigation);


        AdAdmob adAdmob = new AdAdmob(this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner1), this);

        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.nav_view = navigationView;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.nav_invitefriend:
                        StartActivity.this.drawer.closeDrawer(GravityCompat.START);
                        Intent intent2 = new Intent("android.intent.action.SEND");
                        intent2.setType("text/plain");
                        intent2.putExtra("android.intent.extra.TEXT", "I just love " + StartActivity.this.getResources().getString(R.string.app_name) + " App and hope you will love it too. \n https://play.google.com/store/apps/details?id=" + StartActivity.this.getPackageName());
                        StartActivity.this.startActivity(Intent.createChooser(intent2, "Share App Via"));
                        return true;

                    case R.id.nav_privacypolicy:
                        StartActivity.this.drawer.closeDrawer(GravityCompat.START);
                        StartActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://mywild.work/privacy")));
                        return true;
                    case R.id.nav_rateus:
                        StartActivity.this.drawer.closeDrawer(GravityCompat.START);
                        String packageName = StartActivity.this.getPackageName();
                        try {
                            StartActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + packageName)));
                            return true;
                        } catch (ActivityNotFoundException unused2) {
                            StartActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                            return true;
                        }
                    default:
                        return true;
                }
            }
        });

    }

    public void gotoNext(View view) {
        startActivity(new Intent(getApplicationContext(), FrameActivity.class));
    }

    public void clickMenubar(View view) {
        this.drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        } else if (!this.doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getResources().getString(R.string.pressback), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    StartActivity.this.doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            System.exit(0);
        }
    }
}
