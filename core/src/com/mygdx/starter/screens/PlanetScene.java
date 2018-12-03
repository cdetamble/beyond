package com.mygdx.starter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.starter.Constants;
import com.mygdx.starter.MyGdxGame;
import com.mygdx.starter.utils.FontUtils;
import com.mygdx.starter.utils.MathUtils;

public class PlanetScene extends AbstractScreen implements InputProcessor {

    private final Sprite bg;
    private long lastTimestamp;
    private float elapsedTime;
    private BitmapFont font = FontUtils.getBellMt();
    private int currentLine = -1;
    private GlyphLayout layout = new GlyphLayout();
    private float bgAlpha;
    private boolean fadeOut;
    private boolean switchToOutro;
    private float deltaX;


    public PlanetScene(MyGdxGame myGdxGame) {
        super(Constants.WindowWidth, Constants.WindowHeight, myGdxGame);
        font.getData().setScale(0.8f);

        lastTimestamp = System.currentTimeMillis() - 1250;

        bg = new Sprite(new Texture(Gdx.files.internal("title/bg.png")));
        //bg.flip(true, false);
        //bg.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        bg.setBounds(0, 0, bg.getWidth() / 2, bg.getHeight() / 2);
    }

    private String[] lines = new String[]{
            "Humanity needs this potion.",
            "",
            "Without it, we will never populate\nother planets.",
            "",
            "Neither travel to distant galaxies.",
            "",
            "We won't go beyond the limits of life.",
            "",
            "It seems so unbelievably hard\nto reach this goal.",
            "",
            "And yet the human brain has\nall we need inside.",
            "",
            "Consider yourself a sacrifice\nfor the greater good.",
            "",
    };

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        update(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        bg.draw(batch, bgAlpha);

        if (currentLine != -1) {
            font.draw(batch, lines[currentLine],
                    280f,
                    (Constants.WindowHeight + layout.height) / 3f + 30 - deltaX
            );
        }

        batch.end();
    }


    private void update(float delta) {
        elapsedTime += delta;

        deltaX += 0.15f;

        if (switchToOutro) {
            if (lastTimestamp + 2000L < System.currentTimeMillis()) {

                myGdxGame.setScreen(new OutroScreen(myGdxGame));
            }
        } else {

            if (fadeOut) {
                if (bgAlpha > 0) {
                    bgAlpha -= 0.01f;
                    bgAlpha = Math.max(0, bgAlpha);
                } else {
                    lastTimestamp = System.currentTimeMillis();
                    switchToOutro = true;
                }
            } else {

                if (elapsedTime > 0.2f) {
                    if (lastTimestamp + (currentLine % 2 == 0 ? 3000 : 500) < System.currentTimeMillis()) {
                        proceedToNextLine();
                        lastTimestamp = System.currentTimeMillis();
                    }
                }

                if (bgAlpha < 1) {
                    bgAlpha += 0.001f;
                    bgAlpha = Math.min(1, bgAlpha);
                }
            }
        }

        bg.setScale(MathUtils.oscilliate(elapsedTime, 1, 1.04f, 9f));
    }

    private void proceedToNextLine() {
        if (currentLine < lines.length - 1) {
            currentLine++;
            layout = FontUtils.getLayout(font, lines[currentLine]);
            deltaX = 0;
        } else {
            fadeOut = true;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        unproject(viewport, screenX, screenY);

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
