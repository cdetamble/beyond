package com.mygdx.starter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.starter.Constants;
import com.mygdx.starter.MediaManager;
import com.mygdx.starter.MyGdxGame;
import com.mygdx.starter.utils.FontUtils;
import com.mygdx.starter.utils.MathUtils;

public class CellarScene extends AbstractScreen implements InputProcessor {

    private final Sprite vignette;
    private final Sprite bg;
    private final Sprite feet;
    private final Sprite floor;
    private final Sprite halos;
    private final Music musicCreepy;
    private long lastTimestamp;
    private float elapsedTime;
    private Color fadeGray = Color.valueOf("#6B6B6B");

    private float fadeGrayAlpha = 1; // start with 1
    private float fadeBlackAlpha = 1; // start with 1

    private BitmapFont font = FontUtils.getBellMt();
    private int state = 0;

    private Queue<String> messages = new Queue<>();
    private boolean drawHalos;
    private boolean drawBg;
    private boolean redFlashes;
    private boolean renderBlack;
    private Music musicCreepy2;

    public CellarScene(MyGdxGame myGdxGame) {
        super(Constants.WindowWidth, Constants.WindowHeight, myGdxGame);

        TextureAtlas textureAtlas = new TextureAtlas("cellar/pack.atlas");

        vignette = new Sprite(textureAtlas.findRegion("vignette"));
        halos = new Sprite(textureAtlas.findRegion("halos"));
        bg = new Sprite(textureAtlas.findRegion("background"));

        floor = new Sprite(textureAtlas.findRegion("floor"));
        floor.setX((Constants.WindowWidth - floor.getWidth()) / 2);

        feet = new Sprite(textureAtlas.findRegion("feet"));
        feet.setX((Constants.WindowWidth - feet.getWidth()) / 2);

        font.getData().setScale(0.8f);

        musicCreepy = MediaManager.playMusic("music/creepy3.ogg", true);
        musicCreepy.setVolume(0.6f);


        lastTimestamp = System.currentTimeMillis();
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        update(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (!renderBlack) {
            bg.draw(batch);

            floor.draw(batch);
            batch.end();

            Gdx.gl.glEnable(GL30.GL_BLEND);
            Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
            sr.setProjectionMatrix(camera.combined);
            sr.begin(ShapeRenderer.ShapeType.Filled);

            // fade gray
            if (drawBg) {
                fadeGrayAlpha = MathUtils.oscilliate(elapsedTime, 0.85f, 1f, 3);
            }
            fadeGray.set(fadeGray.r, fadeGray.g, fadeGray.b, fadeGrayAlpha);
            sr.setColor(fadeGray);
            sr.rect(0, 0, Constants.WindowWidth, Constants.WindowHeight);

            sr.setColor(0, 0, 0, fadeBlackAlpha);
            sr.rect(0, 0, Constants.WindowWidth, Constants.WindowHeight);


            if (redFlashes) {
                if ((lastTimestamp - System.currentTimeMillis()) % 2 == 0) {
                    sr.setColor(Color.RED);
                    sr.rect(0, 0, Constants.WindowWidth, Constants.WindowHeight);
                }
            }
            sr.end();
            Gdx.gl.glDisable(GL30.GL_BLEND);


            batch.begin();
            feet.draw(batch);

            if (drawHalos) {
                halos.draw(batch, MathUtils.oscilliate(elapsedTime, 0f, 0.2f, 5));
            }


            vignette.draw(batch, MathUtils.oscilliate(elapsedTime, 0.9f, 1, 4));
        }

        if (messages.size > 0) {
            font.draw(batch, messages.first(), 80, 260);
        }
        batch.end();
    }

    private void update(float delta) {
        elapsedTime += delta;

        feet.setX(MathUtils.oscilliate(elapsedTime,
                (Constants.WindowWidth - feet.getWidth()) / 2 - 0,
                (Constants.WindowWidth - feet.getWidth()) / 2 + 5,
                7));
        feet.setY(MathUtils.oscilliate(elapsedTime, -10, 0, 4));

        floor.setX(MathUtils.oscilliate(elapsedTime,
                (Constants.WindowWidth - floor.getWidth()) / 2 - 10,
                (Constants.WindowWidth - floor.getWidth()) / 2 + 10,
                5));
        //floor.setY(MathUtils.oscilliate(elapsedTime, -15, 0, 7));


        switch (state) {
            case 0:
                if (secondsHavePassed(2)) {
                    messages.addLast("Where am I? I can't see a thing.");
                    messages.addLast("Shit, I also can't move.");
                    incrementState();
                }
                break;
            case 1:
                break;
            case 2:
                Music tryingToSpeka = MediaManager.playMusic("music/trying_to_speak.ogg", false);
                tryingToSpeka.setOnCompletionListener(new Music.OnCompletionListener() {
                    @Override
                    public void onCompletion(Music music) {
                        messages.addLast("Oh god...");
                        messages.addLast("What is wrong with me?");
                        messages.addLast("I can't speak properly anymore.");
                        messages.addLast("But there is nothing in or around my mouth.");
                        messages.addLast("No matter how hard I try...");
                        messages.addLast("It feels strange.");
                    }
                });
                incrementState();
                break;
            case 3:
                // nothing
                break;
            case 4:
                Music doorOpens = MediaManager.playMusic("music/door_opens.ogg", false);
                doorOpens.setOnCompletionListener(new Music.OnCompletionListener() {
                    @Override
                    public void onCompletion(Music music) {

                    }
                });
                incrementState();
                break;
            case 5:
                if (secondsHavePassed(7)) {
                    clearMessage();
                    if (fadeBlackAlpha > 0) {
                        fadeBlackAlpha -= 0.005f;
                        fadeBlackAlpha = Math.max(0, fadeBlackAlpha);
                    } else if (fadeGrayAlpha > 0) {
                        messages.addLast("Oh god, I'm on a surgery table.");

                    }
                }
                break;
            case 6:
                Music headOpen = MediaManager.playMusic("music/head_open.ogg", false);
                headOpen.setOnCompletionListener(new Music.OnCompletionListener() {
                    @Override
                    public void onCompletion(Music music) {
                        incrementState();

                    }
                });
                incrementState();
                break;
            case 7:
                if (secondsHavePassed(5)) {
                    messages.addLast("Shit SHIT SHH...");
                    incrementState();
                }
                break;
            case 8:
                // wait
                break;
            case 9:
                clearMessage();
                if (secondsHavePassed(3)) {
                    messages.addLast("This must be 'The Surgeon'.");
                    messages.addLast("Damn I have to send an emergency signal\nby touching my wisdom tooth with my tongue.");
                    incrementState();
                }
                break;
            case 10:
                //wait
                break;
            case 11:
                tryingToSpeka = MediaManager.playMusic("music/trying_to_speak.ogg", false);
                tryingToSpeka.setOnCompletionListener(new Music.OnCompletionListener() {
                    @Override
                    public void onCompletion(Music music) {
                        messages.addLast("Damn I can't feel my tongue anymore.");
                        messages.addLast("I have to somehow make him touch my tooth for me.");
                    }
                });
                incrementState();
                break;
            case 12:
                //wait
                break;
            case 13:
                Music dontSpeak = MediaManager.playMusic("music/dont_speak.ogg", false);
                dontSpeak.setOnCompletionListener(new Music.OnCompletionListener() {
                    @Override
                    public void onCompletion(Music music) {
                        messages.addLast("Humanity's most desired potion??");
                        messages.addLast("What the hell..");
                        messages.addLast("My eyesight seems strange..");
                        messages.addLast("What are those red things?");
                        messages.addLast("Veins?");
                        messages.addLast("And what's that disgusting taste?");
                        incrementState();
                    }
                });
                incrementState();
            case 14:
                if (secondsHavePassed(4)) {
                    drawHalos = true;
                }
                break;
            case 15:
                drawBg = true;
                break;
            case 16:
                Music whatSacrifice = MediaManager.playMusic("music/what_sacrifice.ogg", false);
                whatSacrifice.setOnCompletionListener(new Music.OnCompletionListener() {
                    @Override
                    public void onCompletion(Music music) {
                        musicCreepy2 = MediaManager.playMusic("music/creepy2.ogg", true);
                        musicCreepy2.setVolume(0.6f);
                        incrementState();
                    }
                });
                incrementState();
                break;
            case 17:
                // wait
                break;
            case 18:
                messages.addLast("MY SACRIFICE FOR THIS WORLD'S HEALING?");
                messages.addLast("I'm a detect.. damn..");
                messages.addLast("..what cold thing did he put into my ear?");
                messages.addLast("And what's that pulsating sound it emerges?");
                messages.addLast("Shit, if I keep loosing senses like this,\nI'll be gone in no time.");
                messages.addLast("I have no choice but to smash my head towards that\nextractor thing in my ear.");
                messages.addLast("Hopefully it pushes through and touches\nmy wisdom tooth.");
                incrementState();
                break;
            case 19:
                if (musicCreepy.getVolume() > 0) {
                    float volume = ((float) (lastTimestamp + 2000 - System.currentTimeMillis()) / 2000f);
                    musicCreepy.setVolume(Math.max(0, volume));
                }
                break;
            case 20:
                redFlashes = true;
                incrementState();
                MediaManager.playSound("sounds/aua.ogg");
                break;
            case 21:
                if (secondsHavePassed(0.5f)) {
                    redFlashes = false;
                    renderBlack = true;
                    incrementState();
                }
                break;
            case 22:
                if (secondsHavePassed(1)) {
                    messages.addLast("Argh... Shit, I'm blind.");
                    messages.addLast("But.. at least the signal..");
                    messages.addLast("I.. think it was sent..");
                    messages.addLast("...");
                    messages.addLast("Please!");
                    incrementState();
                }
                break;
            case 23:
                // wait
                break;
            case 24:
                if (musicCreepy2.getVolume() > 0) {
                    float volume = ((float) (lastTimestamp + 3000 - System.currentTimeMillis()) / 3000f);
                    musicCreepy2.setVolume(Math.max(0, volume));
                } else {
                    incrementState();
                }
                break;
            case 25:
                if (secondsHavePassed(1)) {
                    Music useless = MediaManager.playMusic("music/useless.ogg", false);
                    useless.setOnCompletionListener(new Music.OnCompletionListener() {
                        @Override
                        public void onCompletion(Music music) {
                            incrementState();
                        }
                    });
                    incrementState();
                }
                break;
            case 26:
                // wait
                break;
            case 27:
                Music finala = MediaManager.playMusic("music/final.ogg", false);
                finala.setOnCompletionListener(new Music.OnCompletionListener() {
                    @Override
                    public void onCompletion(Music music) {
                        incrementState();
                    }
                });
                incrementState();
                break;
            case 28:
                // wait
                break;
            case 29:
                if (secondsHavePassed(4)) {
                    myGdxGame.setScreen(new PlanetScene(myGdxGame));
                }
                break;
            default:
                // wait
                break;
        }
    }

    private void clearMessage() {
        if (messages.size > 0) {
            messages.clear();
        }
    }

    private void incrementState() {
        state++;
        lastTimestamp = System.currentTimeMillis();
    }

    private boolean secondsHavePassed(float seconds) {
        return lastTimestamp + (long) (seconds * 1000) < System.currentTimeMillis();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        if (messages.size > 0) {
            messages.removeFirst();
            if (messages.size == 0) {
                incrementState();
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

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(this);
    }
}
