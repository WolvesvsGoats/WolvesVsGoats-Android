/**
 * Wolves Vs Goats by Andr� Rosa and Fernando Alves is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 * Based on a work at http://wvg.i3portal.net.
 * <p>
 * Learn how to share your work with existing communities that have enabled Creative Commons licensing.
 * <p>
 * Creative Commons is a non-profit organization.
 *
 * @author Andr� Rosa
 * @author Fernando Alves
 * @version 0.1
 */
package com.wolvesvsgoats.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.wolvesvsgoats.R;

import java.util.HashMap;

public class Audio {
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap;
    private Context ctx;

    public Audio(Context ctx) {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundPoolMap = new HashMap<Integer, Integer>();
        this.ctx = ctx;
        loadAudioFiles();
    }

    private void loadAudioFiles() {
        soundPoolMap.put(0, soundPool.load(ctx, R.raw.chinada, 0));
        soundPoolMap.put(1, soundPool.load(ctx, R.raw.pistol, 0));
        soundPoolMap.put(2, soundPool.load(ctx, R.raw.shotgun, 0));
        soundPoolMap.put(3, soundPool.load(ctx, R.raw.submachine_gun, 0));
        soundPoolMap.put(4, soundPool.load(ctx, R.raw.machine_gun, 0));
        soundPoolMap.put(5, soundPool.load(ctx, R.raw.sniper, 0));
        soundPoolMap.put(6, soundPool.load(ctx, R.raw.chinada_spin, 0));
    }

    public void playSound(int sound) {
        /* Volume calculations */
        AudioManager mgr = (AudioManager) ctx
                .getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;

		/* Play the sound with the correct volume */
        soundPool.play(soundPoolMap.get(sound), volume, volume, 1, 0, 1f);
    }
}