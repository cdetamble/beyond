package com.mygdx.starter.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.starter.MyGdxGame;

/**
 * Created by Christian on 26.02.2018.
 */

public abstract class AbstractScreen extends ScreenAdapter {

    protected final SpriteBatch batch;
    protected final OrthographicCamera camera;
    protected final FitViewport viewport;
    protected final ShapeRenderer sr;
    protected final MyGdxGame myGdxGame;
    protected Vector2 unprojected = new Vector2();

    public AbstractScreen(int width, int height, MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        camera = new OrthographicCamera();
        viewport = new FitViewport(width, height, camera);
        camera.position.set(width / 2f, height / 2f, 0f);
        camera.update();

        batch = new SpriteBatch();
        sr = new ShapeRenderer();
        sr.setAutoShapeType(true);
    }

    protected void unproject(Viewport viewport, int screenX, int screenY) {
        unprojected.set(screenX, screenY);
        unprojected = viewport.unproject(unprojected);
    }
}
