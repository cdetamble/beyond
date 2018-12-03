package com.mygdx.starter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.starter.Constants;
import com.mygdx.starter.MediaManager;
import com.mygdx.starter.MyGdxGame;
import com.mygdx.starter.utils.FontUtils;
import com.mygdx.starter.utils.MathUtils;

public class OutroScreen extends AbstractScreen implements InputProcessor {

    private final Music music;
    private final Sprite spriteDownload;
    private final Sprite rateGame;
    private long lastTimestamp;
    private float elapsedTime;
    private BitmapFont font = FontUtils.getBellMt();
    private int currentLine = -1;
    private GlyphLayout layout = new GlyphLayout();
    private boolean done = false;
    private boolean debug = false;

    Rectangle downloadSoundtrackRect = new Rectangle(40, 30, 400, 50);
    Rectangle rateGameRect = new Rectangle(40, 90, 400, 50);

    public OutroScreen(MyGdxGame myGdxGame) {
        super(Constants.WindowWidth, Constants.WindowHeight, myGdxGame);
        font.getData().setScale(0.8f);

        music = MediaManager.playMusic("music/outro.ogg", false);
        music.setOnCompletionListener(new Music.OnCompletionListener() {

            @Override
            public void onCompletion(Music music) {
                done = true;
                MediaManager.playMusic("music/police.ogg", false);
            }
        });

        spriteDownload = new Sprite(new Texture("download.png"));
        spriteDownload.setPosition(50, 40);

        rateGame = new Sprite(new Texture("star.png"));
        rateGame.setPosition(50, 100);

        lastTimestamp = System.currentTimeMillis() - 1250;
    }

    private String[] lines = new String[]{
            "Thank you for playing!",
            "",
            "Beyond was made with <3 in 3 days.",
            "",
            " For the Ludum Dare 43 game development competition.",
            "",
            "The game, music and voice acting\nhave been made within that time.",
            "Equipment used:\n- Rode NT-USB microphone\n- ASUS Zenbook",
            "Equipment used:\n- My acoustic guitar\n- Audacity\n- and my coffee cup :)",
            "",
            "THE END",
    };

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        update(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (currentLine != -1) {
            font.draw(batch, lines[currentLine],
                    (Constants.WindowWidth - layout.width) / 2f,
                    (Constants.WindowHeight + layout.height) / 2f
            );
        }

        if (done) {
            font.draw(batch, "Rate the Game", 90, 125);
            font.draw(batch, "Download Soundtrack", 90, 65);

            batch.draw(rateGame, rateGame.getX(), rateGame.getY(), 0, 0,
                    rateGame.getWidth() / 2f, rateGame.getHeight() / 2f,
                    MathUtils.oscilliate(elapsedTime, 0.9f, 1.1f, 1.8f),
                    MathUtils.oscilliate(elapsedTime, 0.9f, 1.1f, -1.8f),
                    MathUtils.oscilliate(elapsedTime, -5f, 5f, 1.8f));

            batch.draw(spriteDownload, spriteDownload.getX(), spriteDownload.getY(), 0, 0,
                    spriteDownload.getWidth() / 2f, spriteDownload.getHeight() / 2f,
                    MathUtils.oscilliate(elapsedTime, 0.9f, 1.1f, 1.8f),
                    MathUtils.oscilliate(elapsedTime, 0.9f, 1.1f, -1.8f),
                    MathUtils.oscilliate(elapsedTime, -5f, 5f, 1.8f));
        }

        batch.end();

        if (debug) {
            sr.setProjectionMatrix(camera.combined);
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(Color.RED);
            sr.rect(downloadSoundtrackRect.x, downloadSoundtrackRect.y, downloadSoundtrackRect.width, downloadSoundtrackRect.height);
            sr.rect(rateGameRect.x, rateGameRect.y, rateGameRect.width, rateGameRect.height);
            sr.end();
        }

    }


    private void update(float delta) {
        elapsedTime += delta;

        if (elapsedTime > 0.2f) {
            if (lastTimestamp + 3000 < System.currentTimeMillis()) {
                proceedToNextLine();
                lastTimestamp = System.currentTimeMillis();
            }
        }
    }

    private void proceedToNextLine() {
        if (currentLine < lines.length - 1) {
            currentLine++;
            layout = FontUtils.getLayout(font, lines[currentLine]);
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

        if (done) {
            if (downloadSoundtrackRect.contains(unprojected)) {
                Gdx.net.openURI("https://youtu.be/YV7OEH0_mrs");
                return true;
            }

            if (rateGameRect.contains(unprojected)) {
                Gdx.net.openURI("https://ldjam.com/events/ludum-dare/43/beyond");
                return true;
            }
        }

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
