package com.mygdx.starter.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.starter.Constants;
import com.mygdx.starter.MyGdxGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        float scale = 2f;
        config.x = (int) ((1920f - Constants.WindowWidth*scale) / 2f);
        config.y = (int) ((1080f - Constants.WindowHeight*scale) / 2f);
        config.title = "Beyond - a LD43 JAM game by MouthlessGames.com";
        config.resizable = false;
        config.width = (int) (Constants.WindowWidth * scale);
        config.height = (int) (Constants.WindowHeight * scale);
        config.vSyncEnabled = true;
        //config.fullscreen = true;
        new LwjglApplication(new MyGdxGame(), config);
    }
}
