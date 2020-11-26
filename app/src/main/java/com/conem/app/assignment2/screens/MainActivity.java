package com.conem.app.assignment2.screens;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.conem.app.assignment2.R;
import com.conem.app.assignment2.adapter.GridAdapter;
import com.conem.app.assignment2.model.MinesModel;
import com.conem.app.assignment2.util.MinesUtil;
import com.conem.app.assignment2.util.ProjectUtil;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.Locale;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.conem.app.assignment2.screens.SettingsActivity.DIFFICULTY_CHANGED;
import static com.conem.app.assignment2.util.MinesUtil.COLUMN;
import static com.conem.app.assignment2.util.MinesUtil.Mines;
import static com.conem.app.assignment2.util.MinesUtil.ROW;
import static com.conem.app.assignment2.util.ProjectUtil.playSound;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 33;
    private static final int COUNT_DOWN_INTERVAL = 1000;
    private static final String TIME_FORMAT = "%02d:%02d";
    private static final String EMPTY = "";
    private static final int VOLUME = 30;
    private static final long SHOW_CASE_ONE = 22;

    AppCompatActivity mActivity;
    @BindView(R.id.grid)
    GridView mMinesGrid;
    @BindView(R.id.text_timer)
    TextView mTextTimer;
    @BindView(R.id.text_flag)
    TextView mTextFlag;
    @BindView(R.id.image_smile)
    ImageView mImageSmile;
    @BindView(R.id.image_settings)
    ImageView mImageSettings;
    @BindView(R.id.text_won_lost)
    TextView mTextWon;
    @BindView(R.id.text_best)
    TextView mTextBest;

    private int mCounter = 0;
    MinesModel mMinesModel = new MinesModel();
    GridAdapter mGridAdapter;
    private CountDownTimer countDownTimer;
    private boolean mGameStarted;
    private long timeElapsed = 0;
    private Vibrator mVibrate;
    private MediaPlayer mMediaPlayer;
    private MediaPlayer mMediaPlayerEffect;
    private ShowcaseView mShowcaseView;
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        mUnbinder = ButterKnife.bind(mActivity);

        mMediaPlayer = MediaPlayer.create(mActivity, R.raw.a_night_of_dizzy_spell);
        mMediaPlayer.setVolume(ProjectUtil.volume(VOLUME), ProjectUtil.volume(VOLUME));
        mMediaPlayer.setLooping(true);
        mVibrate = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        mTextTimer.setTypeface(ProjectUtil.getTypeface(mActivity, ProjectUtil.EIGHT_BIT_FONT));
        mTextFlag.setTypeface(ProjectUtil.getTypeface(mActivity, ProjectUtil.EIGHT_BIT_FONT));
        mTextBest.setTypeface(ProjectUtil.getTypeface(mActivity, ProjectUtil.EIGHT_BIT_FONT));
        mTextWon.setTypeface(ProjectUtil.getTypeface(mActivity, ProjectUtil.EIGHT_BIT_FONT));

        countDownTimer = new CountDownTimer(10000000, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long l) {
                timeElapsed += COUNT_DOWN_INTERVAL;
                mTextTimer.setText(ProjectUtil.getDisplayableTime(timeElapsed));
            }

            @Override
            public void onFinish() {
                mTextTimer.setText(String.format(Locale.ENGLISH, TIME_FORMAT, 0, 0));
            }
        };

        mGridAdapter = new GridAdapter(mActivity, mMinesModel);
        mMinesGrid.setAdapter(mGridAdapter);
        refreshGrid();

        mMinesGrid.setOnItemClickListener((adapterView, view, position, l) -> {
            startTimer();
            mMinesModel = MinesUtil.checkClickedPosition(mMinesModel, position / ROW,
                    position % COLUMN, false);
            if (mMinesModel.minesDiscovered) {
                mImageSmile.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.sad));
                mMediaPlayerEffect = playSound(mActivity, mMediaPlayerEffect, R.raw.bomb, 49);
                mMinesGrid.setEnabled(false);
            }
            mGridAdapter.notifyDataSetChanged(mMinesModel);
            if (mMinesModel.minesDiscovered) {
                countDownTimer.cancel();
                mTextWon.setText(getString(R.string.text_lost));
            } else if (mMinesModel.itemsShowing == MinesUtil.GRID_SIZE ||
                    mMinesModel.itemsShowing +
                            MinesUtil.numberOfMines(mMinesModel.minesDifficulty) == MinesUtil.GRID_SIZE) {
                mMinesModel.isWon = true;
                mGridAdapter.notifyDataSetChanged(mMinesModel);
                showWinning();
            }
        });

        mMinesGrid.setOnItemLongClickListener((adapterView, view, position, l) -> {
            startTimer();
            mGameStarted = true;
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            if (ProjectUtil.getSharedPreferencesBoolean(mActivity, SettingsActivity.PREF_VIBRATE, true)) {
                mVibrate.vibrate(50);
            }
            mMinesModel = MinesUtil.checkClickedPosition(mMinesModel, position / ROW,
                    position % COLUMN, true);
            mGridAdapter.notifyDataSetChanged(mMinesModel);
            mTextFlag.setText(String.valueOf(mMinesModel.flagsCount));
            if (mMinesModel.itemsShowing == MinesUtil.GRID_SIZE ||
                    mMinesModel.itemsShowing + MinesUtil.numberOfMines(mMinesModel.minesDifficulty)
                            == MinesUtil.GRID_SIZE) {
                mMinesModel.isWon = true;
                mGridAdapter.notifyDataSetChanged(mMinesModel);
                showWinning();
            }
            return true;
        });

        mImageSmile.setOnClickListener(view -> refreshGrid());

        mImageSettings.setOnClickListener(view ->
                startActivityForResult(new Intent(mActivity, SettingsActivity.class), REQUEST_CODE));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
        mUnbinder.unbind();
        mMediaPlayer.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ProjectUtil.getSharedPreferencesBoolean(mActivity, SettingsActivity.PREF_SOUND, true)) {
            mMediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ProjectUtil.getSharedPreferencesBoolean(mActivity, SettingsActivity.PREF_SOUND, true)) {
            mMediaPlayer.start();
        }
    }

    /**
     * Refresh The sweeper view and reinitialize paramters
     */
    private void refreshGrid() {
        mMinesGrid.setEnabled(true);
        timeElapsed = 0;
        mTextTimer.setText(ProjectUtil.getDisplayableTime(timeElapsed));
        mGameStarted = false;
        countDownTimer.cancel();
        mMinesModel = MinesUtil.refreshGrid(ProjectUtil.getSharedPreferencesString(mActivity,
                SettingsActivity.PREF_DIFFICULTY, "1"));
        mGridAdapter.notifyDataSetChanged(mMinesModel);
        mImageSmile.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.happy));
        mTextFlag.setText(String.valueOf(mMinesModel.minesCount));
        mTextWon.setText(EMPTY);
        mTextBest.setText(EMPTY);
    }

    /**
     * Show views in case of winning
     */
    private void showWinning() {
        countDownTimer.cancel();
        mTextWon.setText(getString(R.string.text_won));
        if (timeElapsed < ProjectUtil.getSharedPreferencesInt(mActivity,
                mMinesModel.minesDifficulty.name(), Integer.MAX_VALUE)) {
            mTextBest.setText(getString(R.string.text_best, mMinesModel.minesDifficulty,
                    String.valueOf(timeElapsed / COUNT_DOWN_INTERVAL)));
            ProjectUtil.setSharedPreferencesInt(mActivity,
                    mMinesModel.minesDifficulty.name(), (int) timeElapsed);
        }
        mMediaPlayerEffect = playSound(mActivity, mMediaPlayerEffect, R.raw.cheering, 49);
        mMinesGrid.setEnabled(false);
    }


    /**
     * Start the timer
     */
    private void startTimer() {
        if (!mGameStarted) {
            countDownTimer.start();
            mGameStarted = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK &&
                data.getBooleanExtra(DIFFICULTY_CHANGED, false)) {
            refreshGrid();
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mShowcaseView = new ShowcaseView.Builder(mActivity)
                .withMaterialShowcase()
                .blockAllTouches()
                .singleShot(SHOW_CASE_ONE)
                .setStyle(R.style.CustomShowcaseTheme)
                .setTarget(new ViewTarget(mMinesGrid))
                .setContentTitle(getString(R.string.show_grid))
                .setOnClickListener((View.OnClickListener) mActivity)
                .build();
    }


    @Override
    public void onClick(View v) {
        switch (mCounter) {
            case 0:
                mShowcaseView.setContentTitle(getString(R.string.show_flag));
                mShowcaseView.setContentText(getString(R.string.show_flag_description));
                mShowcaseView.setShowcase(new ViewTarget(mTextFlag), true);
                break;
            case 1:
                mShowcaseView.setContentTitle(getString(R.string.show_settings));
                mShowcaseView.setContentText(getString(R.string.show_settings_description));
                mShowcaseView.setShowcase(new ViewTarget(mImageSettings), true);
                break;
            case 2:
                mShowcaseView.setContentTitle(getString(R.string.show_smile));
                mShowcaseView.setContentText(EMPTY);
                mShowcaseView.setShowcase(new ViewTarget(mImageSmile), true);
                break;
            case 3:
                mShowcaseView.setContentTitle(getString(R.string.show_time));
                mShowcaseView.setContentText(getString(R.string.show_time_description));
                mShowcaseView.setShowcase(new ViewTarget(mTextTimer), true);
                mShowcaseView.setButtonText(getString(R.string.done));
                break;
            case 4:
                mShowcaseView.hide();
                break;
        }
        mCounter++;
    }
}
