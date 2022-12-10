package com.cha1se.picturefix;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.FileNotFoundException;
import java.math.BigDecimal;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private Button choose;
    private ImageView fBackground;
    private ImageView img;
    private ImageButton lockScreen;
    private ConstraintLayout mainLayout;
    private BigDecimal newScaleXY;
    private TextView textName;
    private Bitmap userImage;
    public AudioManager am;
    private AdView mAdView;

    private InterstitialAd mInterstatial;
    private boolean isChanged = false;
    private boolean f34k = false;
    public boolean lock = false;

    private float scaleXY = 1.0f;
    private float dX = 1.0f;
    private float dY = 1.0f;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags = 1024;
        getWindow().setAttributes(attrs);
        getWindow().getDecorView().setSystemUiVisibility(2);
        
        am =(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        am.setStreamMute(AudioManager.STREAM_MUSIC, false);

        if (getSharedPreferences("PREFERENCE", 0).getBoolean("isFirstRun", true)) {
            startActivity(new Intent(this, LauncherActivity.class));
        }
        getSharedPreferences("PREFERENCE", 0).edit().putBoolean("isFirstRun", false).apply();

        textName = findViewById(R.id.app_name);
        img = findViewById(R.id.image);
        choose = findViewById(R.id.choose_image);
        mainLayout = findViewById(R.id.mainLayout);
        fBackground = findViewById(R.id.fakeBackground);
        lockScreen = findViewById(R.id.lockScreen);
        img.setImageResource(R.drawable.cat);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

    }

    public void onClickImage(View view) {
        if (isChanged) {
            generateMenu(false);
            img.setImageBitmap(userImage);
            fBackground.setImageBitmap(userImage);
            return;
        }
        img.clearAnimation();
        img.setRotationX(0.0f);
        Animation catAnim = AnimationUtils.loadAnimation(this, R.anim.cat_animation1);
        img.setAnimation(catAnim);
        img.startAnimation(catAnim);
        img.animate().rotationXBy(360.0f).setDuration(700).start();
    }

    public void onClickChoose(View view) {

        //ad
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-8234852543216488/5661815866", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstatial = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstatial = null;
                    }
                });

        Intent photoPickerIntent = new Intent("android.intent.action.PICK");
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    public void onClickLockScreen(View view) {
        if (!f34k) {
            f34k = true;
        } else if (lock) {
            lockScreen.setImageResource(R.drawable.ic_lock_outlinedp);
            lock = false;
            f34k = false;
        } else {
            lockScreen.setImageResource(R.drawable.ic_lock_opendp);
            lock = true;
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == 1 && resultCode == -1) {
            try {
                if (mInterstatial != null) {
                    mInterstatial.show(this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
                dX = 1.0f;
                dY = 1.0f;
                fBackground.animate().x(dX).y(dY).setDuration(0).start();
                scaleXY = 1.0f;
                newScaleXY = new BigDecimal(scaleXY).setScale(1, 4);
                fBackground.setScaleX(newScaleXY.floatValue());
                fBackground.setScaleY(newScaleXY.floatValue());
                generateMenu(false);
                Bitmap selectedImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageReturnedIntent.getData()));
                fBackground.setImageBitmap(selectedImage);
                userImage = selectedImage;
                img.setImageBitmap(selectedImage);
                isChanged = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean onTouchEvent(MotionEvent event) {
        if (fBackground.getVisibility() == View.VISIBLE && lock) {
            int action = event.getAction();
            if (action == 0) {
                dX = fBackground.getX() - event.getRawX();
                dY = fBackground.getY() - event.getRawY();
            } else if (action != 1) {
                if (action != 2) {
                    return false;
                }
                fBackground.animate().x(event.getRawX() + dX).y(event.getRawY() + dY).setDuration(0).start();
            }
        }
        return true;
    }

    public void onBackPressed() {
        if (fBackground.getVisibility() == View.VISIBLE) {
            generateMenu(true);
        } else { // } else if (this.fBackground.getVisibility() == View.INVISIBLE) {
            super.onBackPressed();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 24) {
            if (fBackground.getVisibility() == View.VISIBLE) {
                scaleXY += 0.1f;
                newScaleXY = new BigDecimal(scaleXY).setScale(1, 4);
                fBackground.setScaleX(newScaleXY.floatValue());
                fBackground.setScaleY(newScaleXY.floatValue());
            }
            return true;
        } else if (keyCode != 25) {
            return super.onKeyDown(keyCode, event);
        } else {
            if (fBackground.getVisibility() == View.VISIBLE && (newScaleXY.floatValue()) > 0.2d) {
                scaleXY -= 0.1f;
                newScaleXY = new BigDecimal(scaleXY).setScale(1, 4);
                fBackground.setScaleX(newScaleXY.floatValue());
                fBackground.setScaleY(newScaleXY.floatValue());
            }
            return true;
        }
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == 24) {
            if (fBackground.getVisibility() == View.VISIBLE) {
                scaleXY += 0.1f;
                newScaleXY = new BigDecimal(scaleXY).setScale(1, 4);
                fBackground.setScaleX(newScaleXY.floatValue());
                fBackground.setScaleY(newScaleXY.floatValue());
            }
            return true;
        } else if (keyCode != 25) {
            return super.onKeyLongPress(keyCode, event);
        } else {
            if (fBackground.getVisibility() == View.VISIBLE && (newScaleXY.floatValue()) > 0.2d) {
                scaleXY -= 0.1f;
                newScaleXY = new BigDecimal(scaleXY).setScale(1, 4);
                fBackground.setScaleX(newScaleXY.floatValue());
                fBackground.setScaleY(newScaleXY.floatValue());
            }
            return true;
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(5894);
        }
    }

    public void generateMenu(boolean what) {
        if (what) {
            img.setBackgroundColor(Color.parseColor("#000000"));
            mainLayout.setBackgroundColor(Color.parseColor("#729092"));
            fBackground.setVisibility(View.INVISIBLE);
            textName.setVisibility(View.VISIBLE);
            img.setVisibility(View.VISIBLE);
            choose.setVisibility(View.VISIBLE);
            mAdView.setVisibility(View.VISIBLE);
            lockScreen.setImageResource(R.drawable.ic_lock_outlinedp);
            lockScreen.setVisibility(View.INVISIBLE);
            f34k = false;
            lock = false;
            mAdView.setVisibility(View.VISIBLE);
        } else {
            fBackground.setVisibility(View.VISIBLE);
            mainLayout.setBackgroundResource(R.color.black);
            textName.setVisibility(View.INVISIBLE);
            img.setVisibility(View.INVISIBLE);
            choose.setVisibility(View.INVISIBLE);
            mAdView.setVisibility(View.INVISIBLE);
            lockScreen.setVisibility(View.VISIBLE);
            mAdView.setVisibility(View.INVISIBLE);
        }
    }


}
