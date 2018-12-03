package com.mygdx.starter.screens;

import com.mygdx.starter.utils.MathUtils;


class Raindrop {
    private final float xMin, xMax, yMin, yMax;
    public float x, y;
    public float grayness = MathUtils.randomWithin(0.3f, 1f);

    float xFallingSpeed = MathUtils.randomWithin(2f, 4f);
    float yFallingSpeed = MathUtils.randomWithin(4f, 7f);

    public Raindrop(float x, float y,
                    float xMin, float xMax,
                    float yMin, float yMax) {
        this.x = x;
        this.y = y;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;

    }

    public void update() {
        x -= xFallingSpeed;
        if (x < xMin) {
            x = xMax;
        }
        y -= yFallingSpeed;
        if (y < yMin) {
            y = yMax;
        }
    }
}
