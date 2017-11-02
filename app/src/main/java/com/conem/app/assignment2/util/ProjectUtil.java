package com.conem.app.assignment2.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

import com.conem.app.assignment2.R;
import com.conem.app.assignment2.screens.SettingsActivity;

import java.util.Hashtable;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Utility functions
 * Created by mj on 10/12/2017.
 */
public class ProjectUtil {

    public static final String EIGHT_BIT_FONT = "PressStart2P.ttf";
    private static final Hashtable<String, Typeface> font_cache = new Hashtable<>();

    /**
     * Create Assets
     *
     * @param activity activity reference
     * @return Typeface
     */
    public static Typeface getTypeface(Context activity, String path) {
        synchronized (font_cache) {
            if (!font_cache.containsKey(path)) {
                try {
                    Typeface ty = Typeface.createFromAsset(activity.getAssets(), path);
                    font_cache.put(path, ty);
                } catch (Exception e) {
                    return null;
                }
            }
            return font_cache.get(path);
        }
    }

    /**
     * Get Displayed time
     *
     * @param time time in long
     * @return Time in String displayed format
     */
    public static String getDisplayableTime(long time) {
        return String.format(Locale.ENGLISH, "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(time), TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }


    /**
     * Set Shared preference String
     *
     * @param context context reference
     * @param key     key value
     * @param value   value to set
     */
    public static void setSharedPreferencesString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(key, value).apply();
    }

    /**
     * Get Shared preference String with default
     *
     * @param context      context reference
     * @param key          key value
     * @param defaultValue default value
     * @return return stored value
     */
    public static String getSharedPreferencesString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, defaultValue);
    }

    /**
     * Set Shared preference int
     *
     * @param context context reference
     * @param key     key value
     * @param value   value to set
     */
    public static void setSharedPreferencesInt(Context context, String key, int value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(key, value).apply();
    }

    /**
     * Get Shared preference int with default
     *
     * @param context      context reference
     * @param key          key value
     * @param defaultValue default value
     * @return return stored value
     */
    public static int getSharedPreferencesInt(Context context, String key, int defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, defaultValue);
    }

    /**
     * Get Shared preference boolean with default
     *
     * @param context      context reference
     * @param key          key value
     * @param defaultValue default value
     * @return return stored value
     */
    public static boolean getSharedPreferencesBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Set volume
     *
     * @param volume volume level
     * @return sound in float
     */
    public static float volume(int volume) {
        return (1 - (float) (Math.log(50 - volume) / Math.log(50)));
    }

    /**
     * Play sound
     *
     * @param context     context reference
     * @param mediaPlayer media player reference
     * @param sound       sound reference
     * @return media player object
     */
    public static MediaPlayer playSound(Context context, MediaPlayer mediaPlayer, int sound, int volume) {
        if (ProjectUtil.getSharedPreferencesBoolean(context, SettingsActivity.PREF_SOUND, true)) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(context, sound);
            if (sound == R.raw.a_night_of_dizzy_spell) mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(volume(volume), volume(volume));
            mediaPlayer.start();
        }
        return mediaPlayer;
    }
}
