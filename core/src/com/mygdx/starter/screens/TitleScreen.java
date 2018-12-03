package com.mygdx.starter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.starter.Constants;
import com.mygdx.starter.MediaManager;
import com.mygdx.starter.MyGdxGame;
import com.mygdx.starter.utils.FontUtils;
import com.mygdx.starter.utils.MathUtils;

import static com.mygdx.starter.screens.TitleScreen.FadeState.Done;
import static com.mygdx.starter.screens.TitleScreen.FadeState.FadingIn;
import static com.mygdx.starter.screens.TitleScreen.FadeState.FadingOut;
import static com.mygdx.starter.screens.TitleScreen.FadeState.Idle;

public class TitleScreen extends AbstractScreen {

    private final Music music;
    private final BitmapFont font;
    private Sprite bg;
    private Sprite title;
    private float elapsedTime;
    private float alpha;

    enum FadeState {FadingIn, Idle, FadingOut, Done}

    FadeState fadeState = FadingIn;

    public TitleScreen(MyGdxGame myGdxGame) {
        super(Constants.WindowWidth, Constants.WindowHeight, myGdxGame);

        bg = new Sprite(new Texture(Gdx.files.internal("title/bg.png")));
        bg.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        bg.setBounds(0, 0, bg.getWidth() / 2, bg.getHeight() / 2);

        title = new Sprite(new Texture(Gdx.files.internal("title/title.png")));
        title.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        title.setBounds(
                (Constants.WindowWidth - title.getWidth() / 2) / 2 + 100,
                (Constants.WindowHeight - title.getHeight() / 2) / 2,
                title.getWidth() / 2, title.getHeight() / 2);

        font = FontUtils.getBellMt();
        music = MediaManager.playMusic("music/title.ogg", true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        update(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (fadeState == Done) {
            font.draw(batch, "Prologue", 100,100);
        } else {
            bg.draw(batch, alpha);
            title.draw(batch, alpha);

            if (fadeState == Idle) {
                font.draw(batch, "Touch to Start", 350,100);
            }
        }
        batch.end();
    }

    private void update(float delta) {
        elapsedTime += delta;

        if (fadeState != Done) {
            bg.setScale(MathUtils.oscilliate(elapsedTime, 1, 1.04f, 9f));
            title.setScale(MathUtils.oscilliate(elapsedTime, 1, 1.05f, 8f));
        }

        switch (fadeState) {
            case FadingIn:
                if (alpha > 1) {
                    alpha = 1;
                    fadeState = Idle;
                } else {
                    alpha += 0.0025f;
                }
                break;
            case FadingOut:
                if (alpha <= 0) {
                    music.stop();
                    fadeState = Done;
                } else {
                    alpha -= 0.0025f;
                    alpha = Math.max(0, alpha);
                    music.setVolume(Math.max(0, alpha));
                }
                break;
            case Idle:
                if (Gdx.input.justTouched()) {
                    fadeState = FadingOut;
                }
                break;
            case Done:
                if (Gdx.input.justTouched()) {
                    myGdxGame.setScreen(new SnowScreen(myGdxGame));
                }
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
