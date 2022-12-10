package com.cha1se.picturefix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class LauncherActivity extends AppCompatActivity {
    Button guideOneButton;
    Button guideTwoButton;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags = 1024;
        getWindow().setAttributes(attrs);
        getWindow().getDecorView().setSystemUiVisibility(2);
        setContentView((int) R.layout.activity_launcher);
        createPage(0);
        guideOneButton = findViewById(R.id.fragmentOneButton);
        guideTwoButton = findViewById(R.id.fragmentTwoButton);
    }

    public static class MyAdapter extends FragmentPagerAdapter {
        MyAdapter(FragmentManager fm) {
            super(fm);
        }

        public int getCount() {
            return 2;
        }

        public Fragment getItem(int position) {
            if (position == 1) {
                return new FragmentTwo();
            }
            return new FragmentOne();
        }
    }

    public void createPage(int a) {
        MyAdapter adapter = new MyAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(a);
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            public void transformPage(View v, float pos) {
                v.setAlpha(Math.abs(Math.abs(pos) - 1.0f));
            }
        });
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    public void onClickOneButton(View view) {
        createPage(1);
    }

    public void onClickToHome(View v) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
