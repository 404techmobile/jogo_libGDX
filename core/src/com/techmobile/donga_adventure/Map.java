
package com.techmobile.donga_adventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;

public class Map {
	static int EMPTY = 0;
	static int TILE = 0xffffff;
	static int START = 0xff0000;
	static int END = 0xff00ff;
	static int DISPENSER = 0xff0100;
	static int SPIKES = 0x00ff00;
	static int ROCKET = 0x0000ff;
	static int MOVING_SPIKES = 0xffff00;
	static int LASER = 0x00ffff;
	static int PISO = 0x800080;
	static int VIDA = 0x707070;
	static int BONUS = 0xA0A0A0;
	static int INIMIGO = 0xD0D0D0;
    static int ITEM = 0xff00a0;
    static int PORTA = 0xff00b0;
    static int CHAMA_DIREITA = 0x00ffbb;
	static int CHAMA_ESQUERDA = 0xC9C636;
	static int CANDELABRO = 0xF9F636;

	int[][] tiles;
	public Bob bob;
	Cube cube;
	Array<Dispenser> dispensers = new Array<Dispenser>();
	Dispenser activeDispenser = null;
	Array<Rocket> rockets = new Array<Rocket>();
	Array<MovingSpikes> movingSpikes = new Array<MovingSpikes>();
	Array<Laser> lasers = new Array<Laser>();
	Array<Itens> vidas = new Array<Itens>();
	Array<Itens> bonus = new Array<Itens>();
	Array<Itens> coin = new Array<Itens>();
	public Objetos porta;
	Array<Inimigo> inimigo = new Array<Inimigo>();
	Array<Objetos> chama = new Array<Objetos>();
	Array<Objetos> candelabro = new Array<Objetos>();
	public EndDoor endDoor;
    public Sound bonus_sound;
    public Sound fire;
    public Music musica;

	public Map (String level) {
		loadBinary(level);
	}

	private void loadBinary (String level) {
        bonus_sound = Gdx.audio.newSound(Gdx.files.internal("data/bonus.mp3"));
        fire = Gdx.audio.newSound(Gdx.files.internal("data/fire1.wav"));
        musica = Gdx.audio.newMusic(Gdx.files.internal("data/music1.wav"));

        Pixmap pixmap = new Pixmap(Gdx.files.internal(level));
		tiles = new int[pixmap.getWidth()][pixmap.getHeight()];
		for (int y = 0; y < 160; y++) {
			for (int x = 0; x < 240; x++) {
				int pix = (pixmap.getPixel(x, y) >>> 8) & 0xffffff;
				if (match(pix, START)) {
					Dispenser dispenser = new Dispenser(x, pixmap.getHeight() - 1 - y);
					dispensers.add(dispenser);
					activeDispenser = dispenser;
					bob = new Bob(this, activeDispenser.bounds.x, activeDispenser.bounds.y);
					bob.state = Bob.SPAWN;
					cube = new Cube(this, activeDispenser.bounds.x, activeDispenser.bounds.y);
					cube.state = Cube.DEAD;
				} else if (match(pix, DISPENSER)) {
					Dispenser dispenser = new Dispenser(x, pixmap.getHeight() - 1 - y);
					dispensers.add(dispenser);
				} else if (match(pix, ROCKET)) {
					Rocket rocket = new Rocket(this, x, pixmap.getHeight() - 1 - y);
					rockets.add(rocket);
				} else if (match(pix, MOVING_SPIKES)) {
					movingSpikes.add(new MovingSpikes(this, x, pixmap.getHeight() - 1 - y));
				} else if (match(pix, LASER)) {
					lasers.add(new Laser(this, x, pixmap.getHeight() - 1 - y));
				} else if (match(pix, END)) {
					endDoor = new EndDoor(x, pixmap.getHeight() - 1 - y);
				} else if (match(pix, PISO)) {
					tiles[x][y] = pix;
				} else if (match(pix, VIDA)) {
					vidas.add(new Itens(this, x, pixmap.getHeight() - 1 - y));
				} else if (match(pix, BONUS)) {
					bonus.add(new Itens(this, x, pixmap.getHeight() - 1 - y));
				} else if (match(pix, ITEM)) {
					coin.add(new Itens(this, x, pixmap.getHeight() - 1 - y));
				} else if (match(pix, INIMIGO)) {
                    inimigo.add(new Inimigo(this, x, pixmap.getHeight() - 1 - y));
                } else if(match(pix, PORTA)){
                	porta = new Objetos(this, x, pixmap.getHeight()-1-y, 4, 2, 3);
				} else if(match(pix, CHAMA_DIREITA)){
					chama.add(new Objetos(this, x, pixmap.getHeight()-1-y, 0, 2, 1));
				} else if(match(pix, CHAMA_ESQUERDA)){
					chama.add(new Objetos(this, x, pixmap.getHeight()-1-y, 1, 2,1 ));
				} else if(match(pix, CANDELABRO)){
                    candelabro.add(new Objetos(this, x, pixmap.getHeight()-1-y, 4, 1,1));
                }
				else{
					tiles[x][y] = pix;
				}
			}
		}

		for (int i = 0; i < movingSpikes.size; i++) {
			movingSpikes.get(i).init();
		}
		for (int i = 0; i < lasers.size; i++) {
			lasers.get(i).init();
		}
	}

	boolean match (int src, int dst) {
		return src == dst;
	}

	public void update (float deltaTime) {
		bob.update(deltaTime);
		if (bob.state == Bob.DEAD) bob = new Bob(this, activeDispenser.bounds.x, activeDispenser.bounds.y);
		cube.update(deltaTime);
		if (cube.state == Cube.DEAD) cube = new Cube(this, bob.bounds.x, bob.bounds.y);
		for (int i = 0; i < dispensers.size; i++) {
			if (bob.bounds.overlaps(dispensers.get(i).bounds)) {
				activeDispenser = dispensers.get(i);
			}
		}

		for (int i = 0; i < rockets.size; i++) {
			Rocket rocket = rockets.get(i);
			rocket.update(deltaTime);
		}
		for (int i = 0; i < movingSpikes.size; i++) {
			MovingSpikes spikes = movingSpikes.get(i);
			spikes.update(deltaTime);
		}
		for (int i = 0; i < lasers.size; i++) {
			lasers.get(i).update();
		}

		for (int i = 0; i < vidas.size; i++) {
			vidas.get(i).updateVidas(i, deltaTime);
		}

		for (int i = 0; i < bonus.size; i++) {
			bonus.get(i).updateBonus(i, deltaTime);
		}

		for (int i = 0; i < coin.size; i++) {
			coin.get(i).update(i, deltaTime);
		}

		if(inimigo != null){
			for (int i = 0; i < inimigo.size; i++) {
				inimigo.get(i).update(deltaTime, i);
			}
		}


		porta.update(0, deltaTime);

		for(int i = 0; i<chama.size; i++){
			chama.get(i).update(i, deltaTime);
		}

        for(int i = 0; i<candelabro.size; i++){
            candelabro.get(i).update(i, deltaTime);
        }
	}

	public boolean isDeadly (int tileId) {
		return tileId == SPIKES;
	}
}
