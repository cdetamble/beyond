package com.mygdx.starter.models;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.starter.utils.FontUtils;

public class LaptopCommand {

    public final String text;
    private final Rectangle rectangle;
    public boolean isDisabled = false;
    public float x, y;
    private float width, height;

    private final ICallback isVisibleCallback;

    public LaptopCommand(BitmapFont font, String text, ICallback isVisible) {
        this.text = text;
        this.isVisibleCallback = isVisible;
        GlyphLayout layout = FontUtils.getLayout(font, text);
        this.width = layout.width;
        this.height = layout.height;
        this.rectangle = new Rectangle();
    }

    public Rectangle getRectangle() {
        rectangle.set(x, y, width, height);
        return rectangle;
    }

    public boolean isVisible() {
        return isVisibleCallback.test();
    }

}
