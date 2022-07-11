
package com.techmobile.donga_adventure.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class IntroScreen extends CubocScreen {
	TextureRegion intro;
	TextureRegion inicio;
	TextureRegion config;
	SpriteBatch batch;
	float time = 0;

	public IntroScreen (Game game) {
		super(game);
	}

	@Override
	public void show () {
		intro = new TextureRegion(new Texture(Gdx.files.internal("data/intro.png")), 0, 0, 480, 320);
		inicio = new TextureRegion(new Texture(Gdx.files.internal("data/inicio.png")), 0,0, 193, 54);
		config = new TextureRegion(new Texture(Gdx.files.internal("data/configuracoes.png")), 0,0, 193, 53);
		batch = new SpriteBatch();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 480, 320);
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(intro, 0, 0);
		batch.draw(inicio, 144, 246);
		batch.draw(config, 144, 178);
		batch.end();

		//time += delta;
		//if (time > .1f) {
			if (Gdx.input.isKeyPressed(Keys.ANY_KEY) || Gdx.input.justTouched()) {
				game.setScreen(new GameScreen(game, 0));
			}
		//}
	}

	@Override
	public void hide () {
		Gdx.app.debug("Cubocy", "dispose intro");
		batch.dispose();
		intro.getTexture().dispose();
	}

}
