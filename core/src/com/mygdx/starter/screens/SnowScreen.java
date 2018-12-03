package com.mygdx.starter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.mygdx.starter.Constants;
import com.mygdx.starter.MediaManager;
import com.mygdx.starter.MyGdxGame;
import com.mygdx.starter.models.Snowflake;
import com.mygdx.starter.utils.FontUtils;
import com.mygdx.starter.utils.MathUtils;

public class SnowScreen extends AbstractScreen {

    private final Sprite[] sentences;
    private final BitmapFont font;
    private final Music music;
    private Sprite vignette;
    private Sprite sky;
    private Sprite moon;
    private Sprite stars;
    private Sprite bigBerg;
    private Sprite smallBerg;
    private Sprite lake;
    private Sprite bergOhneHuman;
    private Sprite bergMitHuman;
    private Sprite sentence8;

    public Array<Snowflake> snowflakes = new Array<>();

    float risingMoonSpeed = 0.05f;
    private float elapsedTime;

    Color bgColor = Color.valueOf("#476075");
    private float vignetteAlpha = 1;
    private float bergAlpha = 1;
    private float sentenceAlpha;
    private long lastTimestamp = System.currentTimeMillis();
    private float sentenceFadingSpeed = 0.025f;
    private int currentSentence;
    private boolean fadeOut;
    private float globalAlpha = 1;
    private Color fadeColor = new Color();
    private CharSequence finalSentence1 = "One has to find out,";
    private CharSequence finalSentence2 = "but one lifetime is not enough for that.";
    private GlyphLayout finalSentenceLayout1;
    private GlyphLayout finalSentenceLayout2;
    private float fontAlpha = 1;
    private boolean isLastSentenceShowing;
    private boolean isLastSentenceFadingOut;
    private boolean fadeIn = true;

    enum SentenceState {FadingIn, IdleShowing, FadingOut, IdleNothing}

    SentenceState sentenceState = SentenceState.IdleNothing;

    public SnowScreen(MyGdxGame myGdxGame) {
        super(Constants.WindowWidth, Constants.WindowHeight, myGdxGame);

        vignette = new Sprite(new Texture(Gdx.files.internal("snow/vignette.png")));
        vignette.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        sky = new Sprite(new Texture(Gdx.files.internal("snow/background.png")));
        sky.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        stars = new Sprite(new Texture(Gdx.files.internal("snow/stars.png")));
        stars.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        bigBerg = new Sprite(new Texture(Gdx.files.internal("snow/big_berg.png")));
        bigBerg.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        smallBerg = new Sprite(new Texture(Gdx.files.internal("snow/small_berg.png")));
        smallBerg.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        lake = new Sprite(new Texture(Gdx.files.internal("snow/lake.png")));
        lake.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        bergOhneHuman = new Sprite(new Texture(Gdx.files.internal("snow/berg_ohne_human.png")));
        bergOhneHuman.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        bergMitHuman = new Sprite(new Texture(Gdx.files.internal("snow/berg_mit_human.png")));
        bergMitHuman.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        bergMitHuman.setAlpha(0);

        moon = new Sprite(new Texture(Gdx.files.internal("snow/moon.png")));
        moon.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        moon.setPosition(150, 100);

        initializeSnowflakes();

        sentences = new Sprite[7];
        for (int i = 0; i < sentences.length; i++) {
            sentences[i] = new Sprite(new Texture(Gdx.files.internal("snow/" + (i + 1) + ".png")));
            sentences[i].setPosition(
                    (Constants.WindowWidth - sentences[i].getWidth()) / 2,
                    (Constants.WindowHeight - sentences[i].getHeight() - 30)
            );
            sentences[i].getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        font = FontUtils.getBellMt();
        finalSentenceLayout1 = new GlyphLayout(font, finalSentence1);
        finalSentenceLayout2 = new GlyphLayout(font, finalSentence2);

        music = MediaManager.playMusic("music/snow.ogg", false);
    }

    private void initializeSnowflakes() {
        for (int i = 0; i < 200; i++) {
            snowflakes.add(new Snowflake(
                    MathUtils.randomWithin(0, Constants.WindowWidth),
                    MathUtils.randomWithin(0, Constants.WindowHeight),
                    MathUtils.randomWithin(-0.1f, -0.2f)));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        update(delta);

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(bgColor);
        sr.rect(0, 0, Constants.WindowWidth, sky.getY());
        sr.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        sky.draw(batch);
        moon.draw(batch);

        bigBerg.draw(batch);
        smallBerg.draw(batch);
        lake.draw(batch);
        stars.draw(batch);

        bergOhneHuman.draw(batch);
        bergMitHuman.draw(batch);


        batch.end();

        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.WHITE);
        for (Snowflake snowflake : snowflakes) {
            sr.point(snowflake.x, snowflake.y, 0);
        }
        if (fadeOut || fadeIn) {
            sr.setColor(fadeColor);
            sr.rect(0, 0, Constants.WindowWidth, Constants.WindowHeight);
        }
        sr.end();
        Gdx.gl.glDisable(GL30.GL_BLEND);

        batch.begin();
        vignette.draw(batch);
        if (currentSentence < sentences.length) {
            sentences[currentSentence].draw(batch, sentenceAlpha);
        }

        if (isLastSentenceShowing) {
            font.setColor(1, 1, 1, fontAlpha);
            font.draw(batch, finalSentence1,
                    (Constants.WindowWidth - finalSentenceLayout1.width) / 2,
                    (Constants.WindowHeight - finalSentenceLayout1.height) / 2 + 20
            );
            font.draw(batch, finalSentence2,
                    (Constants.WindowWidth - finalSentenceLayout2.width) / 2,
                    (Constants.WindowHeight - finalSentenceLayout2.height) / 2 - 5
            );
        }

        batch.end();
    }

    private void update(float delta) {
        elapsedTime += delta;

        if (fadeIn) {
            if (globalAlpha >= 0) {
                globalAlpha -= 0.03f;
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
        }

        if (!isLastSentenceShowing) {
            //59.234f
            if (elapsedTime > 59.234f) {
                isLastSentenceShowing = true;
            }
        } else {
            if (Gdx.input.justTouched()) {
                isLastSentenceFadingOut = true;
            }
        }

        if (isLastSentenceFadingOut) {
            fontAlpha -= 0.009f;
            fontAlpha = Math.max(0, fontAlpha);

            if (fontAlpha <= 0) {
                music.stop();
                myGdxGame.setScreen(new LighthouseScene(myGdxGame));
            }
        }


        moon.setX(moon.getX() + risingMoonSpeed / 4);
        moon.setY(moon.getY() + risingMoonSpeed);
        sky.setY(sky.getY() + risingMoonSpeed);

        stars.setY(MathUtils.oscilliate(elapsedTime, 0, 10, 10));

        bigBerg.setX(MathUtils.oscilliate(elapsedTime, 0, 10, 10));
        smallBerg.setX(MathUtils.oscilliate(elapsedTime, 0, 10, 8));
        lake.setX(MathUtils.oscilliate(elapsedTime, 0, 5, 8));
        bergOhneHuman.setX(MathUtils.oscilliate(elapsedTime, -5, 0, 6));
        bergMitHuman.setX(MathUtils.oscilliate(elapsedTime, -5, 0, 6));

        if (currentSentence < sentences.length) {
            sentences[currentSentence].setX(sentences[currentSentence].getX() + sentenceFadingSpeed);
            sentences[currentSentence].setY(sentences[currentSentence].getY() + sentenceFadingSpeed);
        }

        for (Snowflake snowflake : snowflakes) {
            snowflake.update(delta, elapsedTime);
        }

        if (vignetteAlpha > 0)
            vignetteAlpha -= 0.00005f;
        else
            vignetteAlpha = 0;
        vignette.setAlpha(vignetteAlpha);


        if (bergAlpha > 0) {
            bergAlpha -= 0.005f;
        } else {
            bergAlpha = 0;
        }
        bergMitHuman.setAlpha(Math.min(1, 1 - bergAlpha));

        switch (sentenceState) {
            case FadingIn:
                sentenceAlpha += sentenceFadingSpeed;
                if (sentenceAlpha > 1) {
                    sentenceAlpha = 1;
                    sentenceState = SentenceState.IdleShowing;
                    lastTimestamp = System.currentTimeMillis();
                }
                break;
            case IdleShowing:
                if (System.currentTimeMillis() > lastTimestamp + 4500) {
                    sentenceState = SentenceState.FadingOut;
                }
                break;
            case FadingOut:
                sentenceAlpha -= sentenceFadingSpeed;
                if (sentenceAlpha < 0) {
                    sentenceAlpha = 0;
                    sentenceState = SentenceState.IdleNothing;
                    lastTimestamp = System.currentTimeMillis();
                    currentSentence++;
                }
                break;
            case IdleNothing:
                if (System.currentTimeMillis() > lastTimestamp + 2000) {
                    sentenceState = SentenceState.FadingIn;
                }
                break;
        }

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
