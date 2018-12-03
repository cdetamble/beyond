package com.mygdx.starter;

import com.badlogic.gdx.Game;
import com.mygdx.starter.screens.TitleScreen;

public class MyGdxGame extends Game {

    @Override
    public void create() {
        TitleScreen screen = new TitleScreen(this);
        //SnowScreen screen = new SnowScreen(this);
        //LighthouseScene screen = new LighthouseScene(this);
        //ResearchScene screen = new ResearchScene(this);
        //CellarScene screen = new CellarScene(this);
        //PlanetScene screen = new PlanetScene(this);
        //OutroScreen screen = new OutroScreen(this);
        setScreen(screen);
    }

}
