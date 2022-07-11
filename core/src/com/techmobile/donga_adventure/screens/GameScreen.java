
package com.techmobile.donga_adventure.screens;

import com.techmobile.donga_adventure.InformacoesTela;
import com.techmobile.donga_adventure.Map;
import com.techmobile.donga_adventure.MapRenderer;
import com.techmobile.donga_adventure.OnscreenControlRenderer;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen extends CubocScreen {
	Map map;
	MapRenderer renderer;
	OnscreenControlRenderer controlRenderer;
	InformacoesTela info;
	String[] niveis = {"data/levels.png", "data/levels2.png", "data/levels3.png"};
	static int nivel;
	private float delta;

	public GameScreen (Game game, int nivel) {
		super(game);
		this.nivel = nivel;
	}

	@Override
	public void show () {
		map = new Map(niveis[nivel]);
		renderer = new MapRenderer(map);
		controlRenderer = new OnscreenControlRenderer(map);
		info = new InformacoesTela();
	}


	@Override
	public void render (float delta) {
		this.delta = delta;
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());

		//Evita a tela preta quando sai e volta pra o jogo
		if (delta == 0){
			delta = .1f;
		}

		map.update(delta);
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		renderer.render(delta);
		controlRenderer.render();
		info.render();

		if(info.life == 0){
		    game.setScreen(new GameOverScreen(game));
		    map.musica.stop();
		    info.life = 10;
        }

		if (map.bob.bounds.overlaps(map.porta.bounds)) {
			nivel++;
			if(nivel == 3) {
				game.setScreen(new GameOverScreen(game));
				return;
			}
			game.setScreen(new GameScreen(game, nivel));
		}

		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			game.setScreen(new MainMenu(game));
		}
	}

	@Override
	public void hide () {
		Gdx.app.debug("Cubocy", "dispose game screen");
		renderer.dispose();
		controlRenderer.dispose();
		info.dispose();
	}

}
