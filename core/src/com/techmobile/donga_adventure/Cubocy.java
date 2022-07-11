
package com.techmobile.donga_adventure;

import com.techmobile.donga_adventure.screens.GameScreen;
import com.badlogic.gdx.Game;
import com.techmobile.donga_adventure.screens.IntroScreen;
import com.techmobile.donga_adventure.screens.MainMenu;

public class Cubocy extends Game {

	@Override
	public void create () {
		//setScreen(new MainMenu(this));
		setScreen(new IntroScreen(this));
		//setScreen(new GameScreen(this, 0));
	}


}
