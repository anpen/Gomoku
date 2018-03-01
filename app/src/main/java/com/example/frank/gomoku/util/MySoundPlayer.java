package com.example.frank.gomoku.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.example.frank.gomoku.R;

import java.util.HashMap;

/**
 * Created by Frank on 2016/1/29.
 */
public class MySoundPlayer {

    public static final int S1 = 1;
    public static final int S2 = 1;
    public static final int S3 = 1;

    private static SoundPool soundPool;
    private static HashMap soundPoolMap;

    /** Populate the SoundPool */
    public static void initSounds(Context context) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,100);
        soundPoolMap = new HashMap(3);

        soundPoolMap.put(S1,soundPool.load(context, R.raw.chesspiecesfall,1));
    }

    public static void playSound(Context context, int soundID) {
        if (soundPool == null || soundPoolMap == null) {
            initSounds(context);
        }

        float volume = 0.5f;
        soundPool.play((Integer) soundPoolMap.get(soundID),volume,volume,1,1,1f);
    }
}
