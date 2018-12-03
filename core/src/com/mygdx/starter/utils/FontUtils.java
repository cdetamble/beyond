package com.mygdx.starter.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

/**
 * Created by Christian on 28.02.2018.
 */

public class FontUtils {
    private static GlyphLayout glyphLayout = new GlyphLayout();

    public static BitmapFont getBellMt() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/bell_mt.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return font;
    }

    public static GlyphLayout getLayout(BitmapFont font, String text) {
        glyphLayout.setText(font, text);
        return glyphLayout;
    }
}
