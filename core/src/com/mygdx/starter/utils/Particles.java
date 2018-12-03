/*
 * Copyright (C) 2016  Christian DeTamble
 *
 * This file is part of nO mooRe.
 *
 * nO mooRe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nO mooRe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nO mooRe.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mygdx.starter.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;


public class Particles {

    private final ParticleEffectPool bigFirePool, smallFirePool;
    private final ParticleEffectPool.PooledEffect[] bigFireEffect, smallFireEffect;

    public Particles(TextureAtlas textureAtlas) {
        bigFireEffect = new ParticleEffectPool.PooledEffect[5];
        smallFireEffect = new ParticleEffectPool.PooledEffect[5];
        //TextureAtlas textureAtlas = new TextureAtlas("textures/textures.pack");

        ParticleEffect fireworksEffect = new ParticleEffect();
        fireworksEffect.load(Gdx.files.internal("particles/fire.p"), textureAtlas);

        ParticleEffect smallFire = new ParticleEffect();
        smallFire.load(Gdx.files.internal("particles/fire_small.p"), textureAtlas);

        // if particle effect includes additive or pre-multiplied particle emitters
        // you can turn off blend function clean-up to save a lot of draw calls
        // but remember to switch the Batch back to GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA
        // before drawing "regular" sprites or your Stage.
        fireworksEffect.setEmittersCleanUpBlendFunction(false);
        smallFire.setEmittersCleanUpBlendFunction(false);

        bigFirePool = new ParticleEffectPool(fireworksEffect, 1, 5);
        smallFirePool = new ParticleEffectPool(smallFire, 1, 5);

        for (int i = 0; i < bigFireEffect.length; i++) {
            ParticleEffectPool.PooledEffect effect = bigFirePool.obtain();
            resetFireworksEffect(effect);
            bigFireEffect[i] = effect;
        }

        for (int i = 0; i < smallFireEffect.length; i++) {
            ParticleEffectPool.PooledEffect effect = smallFirePool.obtain();
            resetFireworksEffect(effect);
            smallFireEffect[i] = effect;
        }
    }

    private void resetBlendFunction(SpriteBatch batch) {
        batch.setBlendFunction(-1, -1);
        Gdx.gl20.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_DST_ALPHA);
    }

    private void render(SpriteBatch batch, float delta, float x, float y, ParticleEffectPool.PooledEffect[] pooledEffect) {
        for (ParticleEffectPool.PooledEffect effect : pooledEffect) {
            effect.setPosition(x, y);
            effect.draw(batch, delta);
            if (effect.isComplete()) {
                resetFireworksEffect(effect);
            }
        }
        resetBlendFunction(batch);
    }

    public void renderSmallFire(SpriteBatch batch, float delta, float x, float y) {
        render(batch, delta, x, y, smallFireEffect);
    }

    public void renderBigFire(SpriteBatch batch, float delta, float x, float y) {
        render(batch, delta, x, y, bigFireEffect);
    }

    /**
     * Resets the position, start color and duration of the given firework effects to random values.
     *
     * @param effect
     */
    private void resetFireworksEffect(ParticleEffect effect) {
        effect.reset();
        effect.setDuration(MathUtils.randomWithin(180, 250));
//        effect.setPosition(Utils.randomWithin(0, WINDOW_WIDTH), Utils.randomWithin(0, WINDOW_HEIGHT));
    }

    /**
     * Frees all allocated resources.
     */
    public void dispose() {
        for (ParticleEffectPool.PooledEffect effect : bigFireEffect) {
            effect.free();
            effect.dispose();
        }
        bigFirePool.clear();
        for (ParticleEffectPool.PooledEffect effect : smallFireEffect) {
            effect.free();
            effect.dispose();
        }
        smallFirePool.clear();
    }

}
