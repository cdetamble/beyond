package com.mygdx.starter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.starter.Constants;
import com.mygdx.starter.MediaManager;
import com.mygdx.starter.MyGdxGame;
import com.mygdx.starter.models.ICallback;
import com.mygdx.starter.models.LaptopCommand;
import com.mygdx.starter.utils.FontUtils;
import com.mygdx.starter.utils.MathUtils;
import com.mygdx.starter.utils.Particles;

import static com.mygdx.starter.screens.ResearchScene.NextGoal.OpenWindowAndSmoke;
import static com.mygdx.starter.screens.ResearchScene.NextGoal.ReadBayTimes;
import static com.mygdx.starter.screens.ResearchScene.NextGoal.ReadCrimeHistoryBook;
import static com.mygdx.starter.screens.ResearchScene.NextGoal.SearchForCranialNerves;
import static com.mygdx.starter.screens.ResearchScene.NextGoal.SearchForHeadlessVictimsCrimes;
import static com.mygdx.starter.screens.ResearchScene.NextGoal.SomeoneIsComing;

public class ResearchScene extends AbstractScreen implements InputProcessor {

    private boolean debug = false;

    private final Sprite bayAreaTimes;
    private final Sprite cranialNerves;
    private final Music musicNachdenklich;
    private final Music musicRain;

    private final Particles particles;
    private final Sprite water;
    private final Sprite coffeeCan;
    private final Sprite scheinwerfer;
    private final Sprite laptopScreen;
    private float elapsedTime;
    private Sprite vignette, room, sea, sky, table, sitz, human, cablePluggedIn, cableUnplugged, windowClosed, windowOpen;

    Rectangle laptopRect = new Rectangle(255, 155, 35, 35);
    Rectangle coffeeMachineRect = new Rectangle(255, 155, 40, 55);
    Rectangle cigRect = new Rectangle(550, 220, 10, 10);
    Rectangle book1Rect = new Rectangle(70, 220, 30, 40);
    Rectangle book2Rect = new Rectangle(85, 187, 40, 20);
    Rectangle book3Rect = new Rectangle(50, 180, 30, 40);
    Rectangle[] rects;

    private boolean book1Read = false;
    private boolean book2Read = false;
    private boolean book3Read = false;

    private BitmapFont font = FontUtils.getBellMt();

    // states
    private boolean isFireBurning = false;
    private boolean isWindowOpen = false;
    private boolean isCigaretteTaken;
    private boolean machineHasWater = false;
    private boolean machineHasCan = false;
    private boolean machineIsPluggedIn = false;

    // laptop commands
    private boolean isLaptopScreenShown = false;
    private boolean isReadingBayAreaTimes = false;
    private boolean isReadingCranialNerves = false;
    private Queue<String> notificationMessages = new Queue<>();
    private GlyphLayout layout;
    private Sprite itemPicked;
    private boolean hasReadThatItNeedsWater;
    private boolean hasReadThatItNeedsACan;
    private boolean hasReadThatItIsUnplugged;
    private boolean knowsToOpenWindow;
    private boolean isSmoking;
    private Music musicCreepy;

    long lastTimestamp;

    Array<Raindrop> raindrops = new Array<>();
    private boolean areScheinwerferShown = true;
    private boolean someoneIsToCome;
    private boolean drawHuman = true;
    private float globalAlpha;
    private Music breathing;
    private boolean whyLeave;
    private boolean leftTHedoor;
    private boolean machineIsReady;
    private boolean showRain = true;
    private float initialAlpha = 1;

    // next goal
    enum NextGoal {
        WarmUpTheLighthouse,
        ReadBayTimes,
        SearchForSurgeon,
        BrewSomeJuice,
        SearchForHeadlessVictimsCrimes,
        ReadCrimeHistoryBook,
        OpenWindowAndSmoke,
        SearchForCranialNerves,
        SomeoneIsComing
    }

    int someoneIsComing = -1;

    NextGoal nextGoal = NextGoal.WarmUpTheLighthouse;
    LaptopCommand laptopCommands[];

    public ResearchScene(MyGdxGame myGdxGame) {
        super(Constants.WindowWidth, Constants.WindowHeight, myGdxGame);

        musicNachdenklich = MediaManager.playMusic("music/nachdenklich.ogg", true);
        musicRain = MediaManager.playMusic("music/rain.ogg", true);
        musicRain.setVolume(0.7f);

        font.getData().setScale(0.8f);

        TextureAtlas textureAtlas = new TextureAtlas("research/pack.atlas");
        particles = new Particles(textureAtlas);

        vignette = new Sprite(textureAtlas.findRegion("vignette"));
        room = new Sprite(textureAtlas.findRegion("room"));
        sea = new Sprite(textureAtlas.findRegion("sea"));
        table = new Sprite(textureAtlas.findRegion("table"));

        windowClosed = new Sprite(textureAtlas.findRegion("window_closed"));
        windowClosed.setPosition(186, 225);

        windowOpen = new Sprite(textureAtlas.findRegion("window_open"));
        windowOpen.setPosition(145, 220);

        sitz = new Sprite(textureAtlas.findRegion("sitz"));
        sitz.setX(210);

        human = new Sprite(textureAtlas.findRegion("human"));
        human.setX(195);

        cableUnplugged = new Sprite(textureAtlas.findRegion("kabel_off"));
        cableUnplugged.setY(125);

        cablePluggedIn = new Sprite(textureAtlas.findRegion("kabel_on"));
        cablePluggedIn.setY(cableUnplugged.getY());

        water = new Sprite(textureAtlas.findRegion("water"));
        water.setY(85);

        coffeeCan = new Sprite(textureAtlas.findRegion("coffecan"));
        coffeeCan.setY(155);

        scheinwerfer = new Sprite(textureAtlas.findRegion("scheinwerfer"));
        scheinwerfer.setPosition(-100, Constants.WindowHeight - scheinwerfer.getHeight());

        sky = new Sprite(textureAtlas.findRegion("sky"));
        sky.setY(Constants.WindowHeight - sky.getHeight());


        laptopScreen = new Sprite(textureAtlas.findRegion("laptop_screen"));
        laptopScreen.setPosition(
                (Constants.WindowWidth - laptopScreen.getWidth()) / 2, 0
        );

        bayAreaTimes = new Sprite(textureAtlas.findRegion("surgeon_strikes"));
        bayAreaTimes.setPosition(laptopScreen.getX() + 30, 0);

        cranialNerves = new Sprite(textureAtlas.findRegion("cranial_nerves"));
        cranialNerves.setPosition(bayAreaTimes.getX(), bayAreaTimes.getY());

        //MediaManager.playMusic("music/snow.ogg", false);

        rects = new Rectangle[]{
                laptopRect,
                coffeeMachineRect,
                cigRect,
                book1Rect,
                book2Rect,
                book3Rect,
        };

        initializeLaptopCommands();
        initializeRaindrops();

        notificationMessages.addLast("Puh it's cold. I better warm up this place.");

        if (debug) {
            nextGoal = SearchForCranialNerves;
            isWindowOpen = true;
        }
    }

    private void initializeRaindrops() {
        for (int i = 0; i < 20; i++) {
            raindrops.add(new Raindrop(
                    MathUtils.randomWithin(windowClosed.getX(), windowClosed.getX() + windowClosed.getWidth()),
                    MathUtils.randomWithin(windowClosed.getY(), windowClosed.getY() + windowClosed.getHeight()),
                    windowClosed.getX(),
                    windowClosed.getX() + windowClosed.getWidth(),
                    windowClosed.getY(),
                    windowClosed.getY() + windowClosed.getHeight())
            );
        }
    }

    private void initializeLaptopCommands() {
        laptopCommands = new LaptopCommand[]{
                new LaptopCommand(font, "Read 'The Bay Area Times - Late Edition'", new ICallback() {
                    @Override
                    public boolean test() {
                        return nextGoal.ordinal() >= NextGoal.ReadBayTimes.ordinal();
                    }
                }),
                new LaptopCommand(font, "Search for 'The Surgeon'", new ICallback() {
                    @Override
                    public boolean test() {
                        return nextGoal.ordinal() >= NextGoal.SearchForSurgeon.ordinal();
                    }
                }),
                new LaptopCommand(font, "Search for 'Headless Victims Crimes'", new ICallback() {
                    @Override
                    public boolean test() {
                        return nextGoal.ordinal() >= SearchForHeadlessVictimsCrimes.ordinal();
                    }
                }),
                new LaptopCommand(font, "Search for 'Cranial Nerves'", new ICallback() {
                    @Override
                    public boolean test() {
                        return nextGoal.ordinal() >= NextGoal.SearchForCranialNerves.ordinal();
                    }
                }),
                new LaptopCommand(font, "Warm up the lighthouse!", new ICallback() {
                    @Override
                    public boolean test() {
                        return nextGoal.ordinal() <= NextGoal.WarmUpTheLighthouse.ordinal();
                    }
                }),
                new LaptopCommand(font, "Brew some juice!", new ICallback() {
                    @Override
                    public boolean test() {
                        return nextGoal.ordinal() >= NextGoal.BrewSomeJuice.ordinal();
                    }
                }),
        };
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        update(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        sky.draw(batch);
        sea.draw(batch);

        if (areScheinwerferShown) {
            scheinwerfer.draw(batch);
        }
        batch.end();

        if (showRain) {
            sr.setProjectionMatrix(camera.combined);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            for (Raindrop raindrop : raindrops) {
                sr.setColor(raindrop.grayness, raindrop.grayness, raindrop.grayness, 1);
                sr.line(raindrop.x, raindrop.y,
                        raindrop.x - 3, raindrop.y - 5
                );
            }
            sr.end();
        }

        batch.begin();
        room.draw(batch);
        table.draw(batch);

        if (machineIsPluggedIn) {
            cablePluggedIn.draw(batch);
        } else {
            cableUnplugged.draw(batch);
        }

        if (drawHuman) {
            human.draw(batch);
        }
        sitz.draw(batch);

        if (isWindowOpen) {
            windowOpen.draw(batch);
        } else {
            windowClosed.draw(batch);
        }

        if (isFireBurning) {
            particles.renderBigFire(batch, delta, 560, 83);
        }
        if (isSmoking) {
            particles.renderSmallFire(batch, delta,
                    human.getX() + human.getWidth() - 40,
                    human.getY() + human.getHeight() - 10
            );
        }

        // interactable objects
        water.draw(batch);
        coffeeCan.draw(batch);

        if (isLaptopScreenShown) {
            laptopScreen.draw(batch);

            if (isReadingBayAreaTimes) {
                bayAreaTimes.draw(batch);
            } else if (isReadingCranialNerves) {
                cranialNerves.draw(batch);
            } else {
                // just show the list of commands

                float spaceBetweenLines = 15;
                float currentY = laptopScreen.getY() + laptopScreen.getHeight() - 45;

                for (int i = 0; i < laptopCommands.length; i++) {
                    LaptopCommand laptopCommand = laptopCommands[i];
                    font.setColor(laptopCommand.isDisabled ? Color.GRAY : Color.BLACK);
                    if (laptopCommand.isVisible()) {
                        laptopCommand.x = laptopScreen.getX() + 50;
                        laptopCommand.y = currentY;
                        font.draw(batch, laptopCommand.text, laptopCommand.x, laptopCommand.y + laptopCommand.getRectangle().height);
                    }
                    currentY -= spaceBetweenLines + FontUtils.getLayout(font, laptopCommand.text).height;
                    if (i == 0 || i == 3) {
                        currentY -= 15;
                    }
                }
            }

        }

        vignette.draw(batch);

        if (itemPicked != null) {
            itemPicked.setPosition(Constants.WindowWidth / 2, Constants.WindowHeight - 30);
            itemPicked.draw(batch);
        }
        batch.end();

        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        if (isCigaretteTaken) {
            // render the taken cig on the jacket
            sr.setColor(Color.valueOf("#0B0B0B"));
            sr.rect(553, 224, 3, 3);

            if (!isWindowOpen || isSmoking) { // render a cigarette
                sr.setColor(Color.GRAY);
                sr.rectLine(human.getX() + human.getWidth() - 52,
                        human.getY() + human.getHeight() - 30,
                        human.getX() + human.getWidth() - 52 + 7,
                        human.getY() + human.getHeight() - 29, 2
                );
            }
        }
        if (globalAlpha > 0) {
            sr.setColor(0, 0, 0, globalAlpha);
            sr.rect(0, 0, Constants.WindowWidth, Constants.WindowHeight);
        }

        if (initialAlpha > 0) {
            sr.setColor(0, 0, 0, initialAlpha);
            sr.rect(0, 0, Constants.WindowWidth, Constants.WindowHeight);
        }

        sr.end();
        Gdx.gl.glDisable(GL30.GL_BLEND);

        if (notificationMessages.size > 0) {
            batch.begin();
            layout = FontUtils.getLayout(font, notificationMessages.first());
            font.setColor(isLaptopScreenShown ? Color.BLACK : Color.WHITE);
            font.draw(batch, notificationMessages.first(),
                    (Constants.WindowWidth - layout.width) / 2,
                    20 + layout.height);
            batch.end();
        }

        if (debug) {
            sr.begin();
            sr.set(ShapeRenderer.ShapeType.Line);
            sr.setColor(Color.RED);
            for (Rectangle rect : rects) {
                if (rect != null) {
                    sr.rect(rect.x, rect.y, rect.width, rect.height);
                }
            }
            Rectangle rect = coffeeCan.getBoundingRectangle();
            sr.rect(rect.x, rect.y, rect.width, rect.height);

            // laptop commands
            if (isLaptopScreenShown) {
                for (LaptopCommand laptopCommand : laptopCommands) {
                    Rectangle r = laptopCommand.getRectangle();
                    sr.rect(r.x, r.y, r.width, r.height);
                }
            }
            sr.end();
        }
    }


    private void update(float delta) {
        elapsedTime += delta;

        if (initialAlpha > 0) {
            initialAlpha -= 0.0025f;
            initialAlpha = Math.max(0, initialAlpha);
        }


        if (showRain) {
            for (Raindrop raindrop : raindrops) {
                raindrop.update();
            }
        }

        if (!someoneIsToCome && nextGoal.ordinal() < SomeoneIsComing.ordinal()) {

            vignette.setScale(MathUtils.oscilliate(elapsedTime, 1, 1.3f, 8));

            // same period
            sitz.setY(MathUtils.oscilliate(elapsedTime, 100, 108, 8));
            human.setY(MathUtils.oscilliate(elapsedTime, 80, 88, 8));
            // same period

            // same period
            // coffee can
            if (itemPicked == coffeeCan) {
                // do nothing
            } else if (machineHasCan) {
                coffeeCan.setX(MathUtils.oscilliate(elapsedTime, 358, 368, 10));
            } else {
                coffeeCan.setX(MathUtils.oscilliate(elapsedTime, 325, 335, 10));
            }

            // water
            if (itemPicked == water) {
                // do nothing
            } else if (machineHasWater) {
                water.setX(MathUtils.oscilliate(elapsedTime, 367, 377, 10));
            } else {
                water.setX(MathUtils.oscilliate(elapsedTime, 130, 140, 10));
            }

            table.setX(MathUtils.oscilliate(elapsedTime, 0, 10, 10));
            laptopRect.setX(MathUtils.oscilliate(elapsedTime, 260, 270, 10));
            coffeeMachineRect.setX(MathUtils.oscilliate(elapsedTime, 360, 370, 10));
            cableUnplugged.setX(MathUtils.oscilliate(elapsedTime, 410, 420, 10));
            cablePluggedIn.setX(cableUnplugged.getX());
            // same period

            sea.setX(MathUtils.oscilliate(elapsedTime, 0, 10, 10));
            sky.setY(MathUtils.oscilliate(elapsedTime,
                    Constants.WindowHeight - sky.getHeight(),
                    Constants.WindowHeight - sky.getHeight() + 10, 10));
        } else {
            if (nextGoal == SomeoneIsComing) {
                if (someoneIsComing == -1) {
                    MediaManager.playMusic("music/footsteps1.ogg", false);
                    someoneIsComing++;

                }
                if (someoneIsComing == 0) {
                    if (scheinwerfer.getX() < 150) {
                        scheinwerfer.setX(scheinwerfer.getX() + 1);
                        lastTimestamp = System.currentTimeMillis();
                    } else {
                        if (lastTimestamp + 1000 < System.currentTimeMillis()) {
                            areScheinwerferShown = false;
                            MediaManager.playSound("sounds/switch.ogg");
                            lastTimestamp = System.currentTimeMillis();
                            someoneIsComing++;
                        }
                    }
                } else if (someoneIsComing == 1) {
                    if (lastTimestamp + 1000 < System.currentTimeMillis()) {
                        notificationMessages.addLast("Oh god, who is that?");
                        someoneIsComing++;
                        lastTimestamp = System.currentTimeMillis();
                        musicNachdenklich.stop();

                    } else {
                        float volume = (float) (lastTimestamp + 1000 - System.currentTimeMillis()) / 3000.0f;
                        musicNachdenklich.setVolume(Math.max(0, volume));
                    }
                } else if (someoneIsComing == 2) {
                    if (lastTimestamp + 6000 < System.currentTimeMillis()) {
                        if (notificationMessages.size > 0) {
                            notificationMessages.clear();
                        }
                        someoneIsComing++;
                        notificationMessages.addLast("I better hide...");
                        lastTimestamp = System.currentTimeMillis();
                        drawHuman = false;

                    }
                } else if (someoneIsComing == 3) {
                    if (globalAlpha < 1) {
                        globalAlpha += 0.005f;
                        globalAlpha = Math.min(1, globalAlpha);

                    } else {
                        globalAlpha = 1;
                        someoneIsComing++;
                        lastTimestamp = System.currentTimeMillis();
                        Music approachingFootsteps = MediaManager.playMusic("music/approaching_footsteps.ogg", false);
                        approachingFootsteps.setOnCompletionListener(new Music.OnCompletionListener() {
                            @Override
                            public void onCompletion(Music music) {
                                someoneIsComing++;
                            }
                        });
                        lastTimestamp = System.currentTimeMillis();
                    }
                } else if (someoneIsComing == 4) {
                    // wait
                    if (!leftTHedoor && lastTimestamp + 2000 < System.currentTimeMillis()) {
                        if (notificationMessages.size > 0) {
                            notificationMessages.clear();
                        }
                        notificationMessages.addLast("Shit, I left the door open.");
                        leftTHedoor = true;
                    }
                } else if (someoneIsComing == 5) {
                    if (lastTimestamp + 2000 < System.currentTimeMillis()) {
                        breathing = MediaManager.playMusic("music/breathing.ogg", true);
                        someoneIsComing++;
                        lastTimestamp = System.currentTimeMillis();
                    }
                } else if (someoneIsComing == 6) {
                    if (lastTimestamp + 3000 < System.currentTimeMillis()) {
                        if (notificationMessages.size > 0) {
                            notificationMessages.clear();
                        }
                        notificationMessages.addLast("Who the hell is this? Damn...");
                        notificationMessages.addLast("I'm close to the window..");
                        notificationMessages.addLast("I better just jump outside...");
                        whyLeave = true;

                        someoneIsComing++;
                    }
                } else if (someoneIsComing == 7) {
                    // nothing, wait for messages to disappear
                } else if (someoneIsComing == 8) {
                    breathing.stop();
                    Music whyLeave = MediaManager.playMusic("music/why_leave.ogg", false);
                    whyLeave.setOnCompletionListener(new Music.OnCompletionListener() {
                        @Override
                        public void onCompletion(Music music) {
                            someoneIsComing++;
                        }
                    });
                    lastTimestamp = System.currentTimeMillis();
                    someoneIsComing++;
                } else if (someoneIsComing == 9) {
                    // nothing, wait for why leave to finish

                    if (lastTimestamp + 7000 < System.currentTimeMillis()) {
                        notificationMessages.addLast("ARGH...");
                        someoneIsComing++;
                    }

                } else if (someoneIsComing == 10) {
                    // wait
                } else if (someoneIsComing == 11) {
                    myGdxGame.setScreen(new CellarScene(myGdxGame));
                }
            }
        }
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
        unproject(viewport, screenX, screenY);

        if (notificationMessages.size > 0) {
            notificationMessages.removeFirst();

            if (notificationMessages.size == 0) {
                if (someoneIsToCome) {
                    nextGoal = SomeoneIsComing;
                    someoneIsToCome = false;
                } else if (whyLeave) {
                    someoneIsComing++;
                    whyLeave = false;
                }
            }
            return true;
        }

        if (someoneIsToCome || nextGoal == SomeoneIsComing) {
            return false;
        }

        // laptop is shown
        if (isLaptopScreenShown) {
            handleTouchOnLaptopCommands();
        } else {
            if (laptopRect.contains(unprojected)) {

                if (nextGoal == OpenWindowAndSmoke) {
                    if (!isCigaretteTaken) {
                        notificationMessages.addLast("I need a cigarette now.");
                    }
                    if (isCigaretteTaken && !isWindowOpen) {
                        notificationMessages.addLast("I shall open the window before lightning it.");
                        knowsToOpenWindow = true;
                    }
                } else {
                    isLaptopScreenShown = true;
                }

                return true;
            }

            switch (nextGoal) {
                case BrewSomeJuice:
                    if (handleInputForCoffeeMaking()) {
                        return true;
                    }
                    break;
                case OpenWindowAndSmoke:
                    if (!isCigaretteTaken) {

                        // cig touched
                        if (cigRect.contains(unprojected)) {
                            isCigaretteTaken = true;
                            MediaManager.playSound("sounds/clue_found.ogg");
                            return true;
                        }
                    } else if (!isWindowOpen) {
                        if (windowClosed.getBoundingRectangle().contains(unprojected)) {
                            if (knowsToOpenWindow) {
                                openTheWindow();
                                return true;
                            }
                        }
                    }
                    break;
                case ReadCrimeHistoryBook:
                    if (!book3Read) {
                        if (book3Rect.contains(unprojected)) {
                            notificationMessages.addLast("No, nothing interesting in here.");
                            MediaManager.playSound("sounds/book.ogg");
                            book3Read = true;
                        }

                    } else if (!book2Read) {
                        if (book2Rect.contains(unprojected)) {
                            notificationMessages.addLast("All sorts of stuff but no relevant information.");
                            MediaManager.playSound("sounds/book.ogg");
                            book2Read = true;
                        }

                    } else if (!book1Read) {
                        if (book1Rect.contains(unprojected)) {
                            readCrimeHistoryBook();
                        }
                    }
                    break;
            }
        }
        return false;
    }

    private void openTheWindow() {
        MediaManager.playSound("sounds/window_opened.ogg");
        isWindowOpen = true;
        notificationMessages.addLast("His goal was..");
        notificationMessages.addLast("..to brew a life lengthening potion.");
        notificationMessages.addLast("Hm I wonder... no that can't be.");
        notificationMessages.addLast("This time it must be a copycat killer.");
        notificationMessages.addLast("He tried to make it out of... cranial nerves?");
        notificationMessages.addLast("What are those anyway?");
        nextGoal = SearchForCranialNerves;
        isSmoking = true;
    }

    private boolean handleInputForCoffeeMaking() {

        if (!machineHasWater) {
            if (hasReadThatItNeedsWater) {

                // water touched
                if (water.getBoundingRectangle().contains(unprojected)) {
                    pickItem(water);
                    return true;
                }

                // coffee machine touched
                if (coffeeMachineRect.contains(unprojected)) {
                    if (itemPicked == water) {
                        water.setY(coffeeMachineRect.getY() + 25);
                        machineHasWater = true;
                        itemPicked = null;
                        MediaManager.playSound("sounds/water.ogg");
                        return true;
                    }
                }
            }
        } else if (!machineHasCan) {

            if (hasReadThatItNeedsACan) {
                // coffee can touched
                if (coffeeCan.getBoundingRectangle().contains(unprojected)) {
                    pickItem(coffeeCan);
                    return true;
                }

                // coffee machine touched
                if (coffeeMachineRect.contains(unprojected)) {
                    if (itemPicked == coffeeCan) {
                        coffeeCan.setY(coffeeMachineRect.getY() + 5);
                        machineHasCan = true;
                        itemPicked = null;
                        MediaManager.playSound("sounds/tusch.ogg");
                        return true;
                    }
                    return true;
                }
            }
        } else if (!machineIsPluggedIn) {
            if (hasReadThatItIsUnplugged) {

                // machine kabel touched
                if (cableUnplugged.getBoundingRectangle().contains(unprojected)) {
                    machineIsPluggedIn = true;
                    Music unplugged = MediaManager.playMusic("sounds/coffee_machine_plugged_in.ogg", false);
                    unplugged.setOnCompletionListener(new Music.OnCompletionListener() {
                        @Override
                        public void onCompletion(Music music) {
                            machineIsReady = true;
                        }
                    });
                    return true;
                }
            }
        }
        return false;
    }

    private void pickItem(Sprite sprite) {
        if (itemPicked != null) {
            notificationMessages.addLast("I already picked another item.");
            return;
        }
        itemPicked = sprite;
        sprite.setPosition(20, Constants.WindowHeight - 20 - sprite.getHeight());
    }

    private void handleTouchOnLaptopCommands() {


        if (isReadingBayAreaTimes) {
            isReadingBayAreaTimes = false;
            isLaptopScreenShown = false;

            notificationMessages.addLast("Another victim?");
            notificationMessages.addLast("That's incredible..");
            notificationMessages.addLast("Last time they didn't find the head either.");

            nextGoal = NextGoal.SearchForSurgeon;
        } else if (isReadingCranialNerves) {
            isReadingCranialNerves = false;
            isLaptopScreenShown = false;
            isFireBurning = false;


            musicRain.stop();
            showRain = false;

            notificationMessages.addLast("Oh god, I feel queasy.");
            notificationMessages.addLast("Who knows what these surgeon's victims must have endured.");
            notificationMessages.addLast("I have to stop this.");

            someoneIsToCome = true;
        } else {

            if (laptopCommands[0].getRectangle().contains(unprojected)) { // read the bay area times
                if (!laptopCommands[0].isDisabled && laptopCommands[0].isVisible()) {
                    readBayTimes();
                }

            } else if (laptopCommands[1].getRectangle().contains(unprojected)) { // search for the surgeon
                if (!laptopCommands[1].isDisabled && laptopCommands[1].isVisible()) {
                    searchForTheSurgeon();
                }

            } else if (laptopCommands[2].getRectangle().contains(unprojected)) { // search for headless victims
                if (!laptopCommands[2].isDisabled && laptopCommands[2].isVisible()) {
                    searchForHeadlessVictims();
                }

            } else if (laptopCommands[3].getRectangle().contains(unprojected)) { // search for cranial nerves
                if (!laptopCommands[3].isDisabled && laptopCommands[3].isVisible()) {
                    isReadingCranialNerves = true;
                    isSmoking = false;
                    MediaManager.playSound("sounds/keyboard.ogg");
                }

            } else if (laptopCommands[4].getRectangle().contains(unprojected)) { // warm up the lighthouse
                if (!laptopCommands[4].isDisabled && laptopCommands[4].isVisible()) {
                    warmUpTheLighthouse();
                }

            } else if (laptopCommands[5].getRectangle().contains(unprojected)) { // brew some joice
                if (!laptopCommands[5].isDisabled && laptopCommands[5].isVisible()) {
                    brewSomeJuice();
                }

            } else {
                if (laptopScreen.getBoundingRectangle().contains(unprojected)) {
                    // do nothing, the user just clicked on the screen
                } else {
                    // escape laptop
                    isLaptopScreenShown = false;
                }
            }
        }
    }

    private void readBayTimes() {
        if (nextGoal.ordinal() < NextGoal.BrewSomeJuice.ordinal()) {
            isReadingBayAreaTimes = true;
            MediaManager.playSound("sounds/switch.ogg");
        } else {
            notificationMessages.addLast("It says that a headless victim has been found.");
            notificationMessages.addLast("I wonder why exactly headless.");
        }
    }

    private void searchForHeadlessVictims() {
        if (nextGoal == SearchForHeadlessVictimsCrimes
                || nextGoal == ReadCrimeHistoryBook
                || nextGoal == OpenWindowAndSmoke) {
            MediaManager.playSound("sounds/keyboard.ogg");
            notificationMessages.addLast("Hm, I can't find any crimes with similar circumstances.");
            notificationMessages.addLast("Maybe I shall try another source of information.");
            isLaptopScreenShown = false;
            nextGoal = ReadCrimeHistoryBook;
        } else {
            notificationMessages.addLast("Nothing other than I already know.");
        }
    }

    private void readCrimeHistoryBook() {
        if (nextGoal == ReadCrimeHistoryBook) {
            MediaManager.playSound("sounds/clue_found.ogg");
            notificationMessages.addLast("Hm, it seems that someone committed\nsimilar crimes in the 18th century.");
            notificationMessages.addLast("The location was a cellar.");
            notificationMessages.addLast("The culprit was never caught though.");
            notificationMessages.addLast("According to the investigators, the believed motive was..");
            notificationMessages.addLast("..no, that can't be..");
            notificationMessages.addLast("I have to smoke a cig' to handle this.");
            isLaptopScreenShown = false;
            nextGoal = OpenWindowAndSmoke;
        }
    }

    private void searchForTheSurgeon() {
        if (nextGoal == NextGoal.SearchForSurgeon) {
            MediaManager.playSound("sounds/keyboard.ogg");
            isLaptopScreenShown = false;
            notificationMessages.addLast("Hm, all results point to the article I just read.");
            notificationMessages.addLast("I need a hot brew before continuing research.");
            nextGoal = NextGoal.BrewSomeJuice;
        } else {
            notificationMessages.addLast("Nothing other than I already know.");
        }
    }

    private void warmUpTheLighthouse() {
        if (nextGoal == NextGoal.WarmUpTheLighthouse) {
            isLaptopScreenShown = false;
            isFireBurning = true;
            notificationMessages.addLast("Ah much better. Now, for some online research...");
            nextGoal = ReadBayTimes;
            MediaManager.playSound("sounds/clue_found.ogg");
        } else {
            notificationMessages.addLast("It's already warm enough.");
        }
    }

    private void brewSomeJuice() {
        if (nextGoal == NextGoal.BrewSomeJuice) {
            isLaptopScreenShown = false;

            if (!machineHasWater) {
                notificationMessages.addLast("The machine needs water.");
                hasReadThatItNeedsWater = true;
            } else if (!machineHasCan) {
                notificationMessages.addLast("The machine needs a can.");
                hasReadThatItNeedsACan = true;
            } else if (!machineIsPluggedIn) {
                notificationMessages.addLast("The machine is unplugged.");
                hasReadThatItIsUnplugged = true;
            } else {
                if (machineIsReady) {
                    water.setY(-100); // make it disappear
                    MediaManager.playSound("sounds/coffe_zubereitet.ogg");
                    notificationMessages.addLast("Ah that was good.");
                    notificationMessages.addLast("I should carry on with research now.");
                    nextGoal = SearchForHeadlessVictimsCrimes;
                } else {
                    notificationMessages.addLast("The machine is not ready yet.");
                }
            }

        } else {
            notificationMessages.addLast("No, I'm not thirsty anymore.");
        }
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
