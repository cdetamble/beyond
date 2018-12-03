package com.mygdx.starter.models;

import com.mygdx.starter.Constants;
import com.mygdx.starter.utils.MathUtils;

public class Snowflake {

    private float originX, velocityY;

    public Snowflake(int x, int y, float velocityY) {
        this.x = x;
        this.y = y;
        this.originX = x;
        this.velocityY = velocityY;
    }

    public float x, y;

    public void update(float delta, float elapsedTIme) {
        x = MathUtils.oscilliate(elapsedTIme, originX - 4f, originX + 4f, 10f);
        y += velocityY;
        if (y < -10) {
            y = Constants.WindowHeight;
        }
    }


}
