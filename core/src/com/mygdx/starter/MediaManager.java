package com.mygdx.starter;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by Christian on 16.02.2018.
 */

public class MediaManager {

    public static AssetManager assetManager = new AssetManager();
    //public static TextureAtlas atlas = new TextureAtlas("textures.atlas");

    public static Music playMusic(String assetPath, boolean loop) {
        if (!assetManager.isLoaded(assetPath)) {
            assetManager.load(assetPath, Music.class);
            assetManager.finishLoading();
        }
        Music music = assetManager.get(assetPath, Music.class);
        music.setLooping(loop);
        music.play();
        return music;
    }

    public static Sound playSound(String assetPath) {
        if (!assetManager.isLoaded(assetPath)) {
            assetManager.load(assetPath, Sound.class);
            assetManager.finishLoading();
        }
        Sound sound = assetManager.get(assetPath, Sound.class);
        sound.play();
        return sound;
    }
}
