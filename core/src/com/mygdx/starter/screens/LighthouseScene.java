package com.mygdx.starter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.starter.Constants;
import com.mygdx.starter.MediaManager;
import com.mygdx.starter.MyGdxGame;
import com.mygdx.starter.utils.FontUtils;
import com.mygdx.starter.utils.MathUtils;

public class LighthouseScene extends AbstractScreen {

    private final Sprite sky;
    private final Sprite vignette;
    private final Sprite shore;
    private final Sprite sea;
    private final Sprite lighthouse;
    private final BitmapFont font;
    private final Music music;
    private float elapsedTime;
    Color bgColor = Color.valueOf("#476075");
    private boolean fadeOut;
    private boolean fadeIn = true;
    private Color fadeColor = new Color();
    private float globalAlpha = 1;
    private boolean pendguest;

    public LighthouseScene(MyGdxGame myGdxGame) {
        super(Constants.WindowWidth, Constants.WindowHeight, myGdxGame);
        TextureAtlas textureAtlas = new TextureAtlas("lighthouse/pack.atlas");

        sky = new Sprite(textureAtlas.findRegion("sky"));
        vignette = new Sprite(textureAtlas.findRegion("vignette"));
        shore = new Sprite(textureAtlas.findRegion("shore"));
        sea = new Sprite(textureAtlas.findRegion("sea"));
        lighthouse = new Sprite(textureAtlas.findRegion("lighthouse"));

        lighthouse.setScale(1.2f);
        sea.setScale(1.2f);

        font = FontUtils.getBellMt();
        font.getData().setScale(1);

        music = MediaManager.playMusic("sounds/waves.ogg", true);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        update(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        sky.draw(batch);
        sea.draw(batch);
        shore.draw(batch);
        lighthouse.draw(batch);

        vignette.draw(batch);


        font.draw(batch, "The Present, 8 p.m.", 60, 100);
        if (pendguest) {
            font.draw(batch, "at Detective Pendguest's Basement", 60, 60);
        }

        batch.end();

        if (fadeOut || fadeIn) {
            Gdx.gl.glEnable(GL30.GL_BLEND);
            Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
            sr.setProjectionMatrix(camera.combined);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(fadeColor);
            sr.rect(0, 0, Constants.WindowWidth, Constants.WindowHeight);
            sr.end();
            Gdx.gl.glDisable(GL30.GL_BLEND);
        }
    }

    private void update(float delta) {
        elapsedTime += delta;

        sea.setX(MathUtils.oscilliate(elapsedTime, 0, 10, 8));
        sea.setY(MathUtils.oscilliate(elapsedTime, -10, 0, 8));
        shore.setX(MathUtils.oscilliate(elapsedTime, 10, 0, 10));

        lighthouse.setX(MathUtils.oscilliate(elapsedTime, 280, 240, 8));
        lighthouse.setY(MathUtils.oscilliate(elapsedTime, 20, 0, 5));


        sky.setY(MathUtils.oscilliate(elapsedTime, 20, 0, 5));

        if (fadeIn) {
            if (globalAlpha >= 0) {
                globalAlpha -= 0.01f;
            } else {
                globalAlpha = 0;
                fadeIn = false;
            }
            fadeColor.set(0, 0, 0, Math.max(0, globalAlpha));
        }

        if (!fadeOut) {
            if (elapsedTime > 56) {
                fadeOut = true;
            }
        } else {
            if (globalAlpha < 1) {
                globalAlpha += 0.006f;
            } else {
                globalAlpha = 1;
            }
            fadeColor.set(0, 0, 0, Math.min(1, globalAlpha));
            music.setVolume(Math.max(0, 1 - globalAlpha));
            if (music.getVolume() <= 0) {
                music.stop();
            }
        }

        if (Gdx.input.justTouched()) {
            if (!pendguest) {
                pendguest = true;
            } else {
                fadeOut = true;
            }
        }

        if (fadeOut && globalAlpha == 1) {
            music.stop();
            myGdxGame.setScreen(new ResearchScene(myGdxGame));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
