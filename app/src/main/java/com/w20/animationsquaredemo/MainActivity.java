package com.w20.animationsquaredemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    ImageView square;
    RelativeLayout root;
    ViewGroup.MarginLayoutParams lp;

    Button btnPlus, btnMinus, btnPause, btnPlay;
    int speedAdded = 0;

    int height, width;

    int seekBarPosition;

    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnPlus = findViewById(R.id.btn_plus);
        btnMinus = findViewById(R.id.btn_minus);
        btnPause = findViewById(R.id.btn_pause);
        btnPlay = findViewById(R.id.btn_restart);

        square = findViewById(R.id.square);

        final SeekBar seekBar = findViewById(R.id.seekBar);
        seekBarPosition = seekBar.getProgress();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarPosition = progress;
                Log.d(TAG, "onProgressChanged: " + progress);
                if (progress == 0) {
                    animation.setDuration(height * 1000 / 150);
                    Log.d(TAG, "onProgressChanged: " + progress);
                    return;
                }
                if (height / 150 > seekBarPosition + speedAdded / 2) {
                    animation.setDuration(height * 1000 / 150 - (seekBarPosition + speedAdded/2) * 1000);
                    Log.d(TAG, "onProgressChanged2: " + progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnPlay.setOnClickListener(this);
        btnPause.setOnClickListener(this);

        btnPlus.setOnClickListener(this);
        btnMinus.setOnClickListener(this);

        final Button btnDir = findViewById(R.id.btn_anim_dir);

        root = findViewById(R.id.root_layout);
        root.post(new Runnable() {
            @Override
            public void run() {
                btnDir.setOnClickListener(new View.OnClickListener() {
                    boolean isHorizontal = false;

                    @Override
                    public void onClick(View v) {
                        Button btn = (Button) v;
                        seekBar.setProgress(0);
                        if (isHorizontal == false)
                            btn.setText("vertical");
                        else
                            btn.setText("horizontal");
                        isHorizontal = !isHorizontal;
                        initAnimation(isHorizontal);
                    }
                });

                initAnimation(false);
            }
        });

        lp = (ViewGroup.MarginLayoutParams) root.getLayoutParams();


    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        switch (v.getId()) {
            case R.id.btn_plus:
                if (height / 150 > seekBarPosition + speedAdded / 2) {
                    speedAdded++;
                    animation.setDuration(height * 1000 / 150 - (seekBarPosition + speedAdded/2) * 1000);
                }
                Log.d(TAG, "onClick: " + (seekBarPosition + speedAdded/2));
                break;
            case R.id.btn_minus:
                if (speedAdded > 0) {
                    speedAdded--;
                    animation.setDuration(height * 1000 / 150 - (seekBarPosition + speedAdded/2) * 1000);
                }

                Log.d(TAG, "onClick: " + (seekBarPosition + speedAdded/2));
                break;
            case R.id.btn_restart:
                animateSquare();
                break;
            case R.id.btn_pause:
                animation.cancel();
                break;
        }
    }

    private void initAnimation(boolean directionHorizontal) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        height = displayMetrics.heightPixels
                - getNavigationBarHeight(this)
                - square.getHeight()
                - getTitleBarHeight()
                - lp.bottomMargin - lp.topMargin;

        Log.d(TAG, "animateSquare: " + height);

        width = displayMetrics.widthPixels - square.getWidth()
                - lp.leftMargin - lp.rightMargin;

        if (!directionHorizontal) {
            animation = new TranslateAnimation(
                    Animation.ABSOLUTE, width / 2,
                    Animation.ABSOLUTE, width / 2,
                    Animation.ABSOLUTE, 0,
                    Animation.ABSOLUTE, height);
        } else {
            animation = new TranslateAnimation(
                    Animation.ABSOLUTE, 0,
                    Animation.ABSOLUTE, width,
                    Animation.ABSOLUTE, height / 2,
                    Animation.ABSOLUTE, height / 2);
        }

        animation.setDuration(height * 1000 / 150);
        animateSquare();

    }

    private void animateSquare() {
        animation.setFillAfter(true);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        square.startAnimation(animation);
    }

    public void changeColor(View view) {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        square.setBackgroundColor(color);
    }

    /*
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            result = getResources().getDimensionPixelSize(resourceId);
        Log.d(TAG, "getStatusBarHeight: " + result);
        return result;
    }
     */

    private int getStatusBarHeight() {
        Rect r = new Rect();
        Window w = getWindow();
        w.getDecorView().getWindowVisibleDisplayFrame(r);
        return r.top;
    }

    private int getTitleBarHeight() {
        int viewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        return (viewTop - getStatusBarHeight());
    }

    private int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int result = 0;
        int id = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        Log.d(TAG, "getNavigationBarHeight: " + resources.getDimensionPixelSize(id));
        if (id > 0)
            result = resources.getDimensionPixelSize(id);
        return result;
    }
}
