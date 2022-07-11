
package com.techmobile.donga_adventure;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class InformacoesTela {
    public static int life = 10;

    BitmapFont font;
    SpriteBatch batch2;
    //BitmapFont font2;
    static int pontos = 0;

    public InformacoesTela () {
        loadAssets();
    }

    private void loadAssets () {
        //Mostra a Pontuação
        font = new BitmapFont();
        //font2 = new BitmapFont();
        batch2 = new SpriteBatch();
        font.getData().setScale(2f);
    }

    public void render () {
        if (Gdx.app.getType() != ApplicationType.Android && Gdx.app.getType() != ApplicationType.iOS) return;

            batch2.begin();
            font.draw(batch2, "Vidas: " + life, Gdx.graphics.getWidth() * .5f, Gdx.graphics.getHeight());
//            font2.draw(batch2, String.valueOf(pontos), Gdx.graphics.getWidth() * .1f, Gdx.graphics.getHeight());
            batch2.end();
        }

    public void dispose () {
        batch2.dispose();
        //font2.dispose();
    }
}
