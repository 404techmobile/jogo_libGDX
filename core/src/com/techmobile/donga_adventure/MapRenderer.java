
package com.techmobile.donga_adventure;

import static com.techmobile.donga_adventure.Bob.DYING;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Vector3;


public class MapRenderer {
	Map map;
	OrthographicCamera cam;
	SpriteCache cache;
	SpriteBatch batch = new SpriteBatch(8000);
	ImmediateModeRenderer20 renderer = new ImmediateModeRenderer20(false, true, 0);
	int[][] blocks;
	TextureRegion tile;
	Animation<TextureRegion> bobDead;
	Animation<TextureRegion> zap;
	TextureRegion cube;
	Animation<TextureRegion> cubeFixed;
	TextureRegion cubeControlled;
	TextureRegion dispenser;
	Animation<TextureRegion> spawn;
	TextureRegion spikes;
	Animation<TextureRegion> rocket;
	Animation<TextureRegion> rocketExplosion;
	TextureRegion rocketPad;
	TextureRegion endDoor;
	TextureRegion movingSpikes;
	TextureRegion laser;
	TextureRegion piso;
	TextureRegion elevador;
	TextureRegion vida;
	TextureRegion bonus;
	FPSLogger fps = new FPSLogger();

	//Animation<TextureRegion> inimigoDead = bobDead;

	Animation<TextureRegion> inimigospawn = spawn;

	Animation<TextureRegion> item;
	Animation<TextureRegion> porta;

	Animacao pirate = new Animacao("data/pirate.png", 774,496);
	Animacao heroi = new Animacao("data/sprite.png", 113, 136);
	Animacao candelabros = new Animacao("data/candelabros.png", 64,64);
	Animacao chamas = new Animacao("data/chama.png", 128, 51);

	public MapRenderer (Map map) {
		this.map = map;
		this.cam = new OrthographicCamera(24, 16);
		this.cam.position.set(map.bob.pos.x, map.bob.pos.y, 0);
		this.cache = new SpriteCache(this.map.tiles.length * this.map.tiles[0].length, false);
		this.blocks = new int[(int) Math.ceil(this.map.tiles.length / 24.0f)][(int) Math.ceil(this.map.tiles[0].length / 16.0f)];

		createAnimations();
		createBlocks();
		this.map.musica.setLooping(true);
		this.map.musica.play();

	}

	private void createBlocks () {
		int width = map.tiles.length;
		int height = map.tiles[0].length;
		for (int blockY = 0; blockY < blocks[0].length; blockY++) {
			for (int blockX = 0; blockX < blocks.length; blockX++) {
				cache.beginCache();
				for (int y = blockY * 16; y < blockY * 16 + 16; y++) {
					for (int x = blockX * 24; x < blockX * 24 + 24; x++) {
						if (x > width) continue;
						if (y > height) continue;
						int posX = x;
						int posY = height - y - 1;
						if (map.match(map.tiles[x][y], Map.TILE)) cache.add(tile, posX, posY, 1, 1);
						if (map.match(map.tiles[x][y], Map.SPIKES)) cache.add(spikes, posX, posY, 1, 1);
						if (map.match(map.tiles[x][y], Map.PISO)) cache.add(piso, posX, posY, 1, 1);
					}
				}
				blocks[blockX][blockY] = cache.endCache();
			}
		}
		Gdx.app.debug("Cubocy", "blocks created");
	}



	private void createAnimations () {
		this.tile = new TextureRegion(new Texture(Gdx.files.internal("data/tile.png")), 0, 0, 20, 20);
		Texture bobTexture = new Texture(Gdx.files.internal("data/bob.png"));

		Texture itens = new Texture(Gdx.files.internal("data/itens.png"));
		TextureRegion[] itens_textures = new TextureRegion(itens).split(32, 32)[5];

		Texture objects = new Texture(Gdx.files.internal("data/porta.png"));
		TextureRegion[] objects_textures = new TextureRegion(objects).split(79, 85)[0];

		TextureRegion[] split = new TextureRegion(bobTexture).split(20, 20)[0];
		TextureRegion[] mirror = new TextureRegion(bobTexture).split(20, 20)[0];
		for (TextureRegion region : mirror)
			region.flip(true, false);

		spikes = split[5];
		item = new Animation(0.1f, itens_textures[0], itens_textures[1],itens_textures[2],itens_textures[3],itens_textures[4],itens_textures[5],itens_textures[6],itens_textures[7],itens_textures[8],itens_textures[9]);
		porta = new Animation(0.5f, objects_textures[0], objects_textures[1],objects_textures[2],objects_textures[3],objects_textures[4]);

		bobDead = new Animation(0.2f, split[0]);
		split = new TextureRegion(bobTexture).split(20, 20)[1];
		cube = split[0];
		cubeFixed = new Animation(1, split[1], split[2], split[3], split[4], split[5]);
		split = new TextureRegion(bobTexture).split(20, 20)[2];
		cubeControlled = split[0];
		spawn = new Animation(0.1f, split[4], split[3], split[2], split[1]);

		dispenser = split[5];
		split = new TextureRegion(bobTexture).split(20, 20)[3];
		rocket = new Animation(0.1f, split[0], split[1], split[2], split[3]);
		rocketPad = split[4];
		vida = split[6];
		bonus = split[5];
		split = new TextureRegion(bobTexture).split(20, 20)[4];
		rocketExplosion = new Animation(0.1f, split[0], split[1], split[2], split[3], split[4], split[5]);
		split = new TextureRegion(bobTexture).split(20, 20)[5];
		endDoor = split[2];
		movingSpikes = split[0];
		laser = split[1];
		piso = split[3];
		elevador = split[5];
	}

	float stateTime = 0;
	Vector3 lerpTarget = new Vector3();

	public void render (float deltaTime) {
		//Movimentação da câmera seguindo o personagem ou o cubo
		if (map.cube.state != Cube.CONTROLLED)
			cam.position.lerp(lerpTarget.set(map.bob.pos.x, map.bob.pos.y, 0), 2f * deltaTime);
		else
			cam.position.lerp(lerpTarget.set(map.cube.pos.x, map.cube.pos.y, 0), 2f * deltaTime);
		cam.update();

		renderLaserBeams();

		cache.setProjectionMatrix(cam.combined);
		Gdx.gl.glDisable(GL20.GL_BLEND);

		cache.begin();
		int b = 0;
		for (int blockY = 0; blockY < 4; blockY++) {
			for (int blockX = 0; blockX < 6; blockX++) {
				cache.draw(blocks[blockX][blockY]);
				b++;
			}
		}
		cache.end();

		stateTime += deltaTime;
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		renderDispensers();
		if (map.endDoor != null) batch.draw(endDoor, map.endDoor.bounds.x, map.endDoor.bounds.y, 1, 1);
		//if (map.inimigo != null) batch.draw(inimigo, map.inimigo.bounds.x, map.inimigo.bounds.y, 1, 1);
		//if(map.vida != null) batch.draw(vida, map.vida.bounds.x, map.vida.bounds.y, 1, 1);
		renderLasers();
		renderMovingSpikes();
		renderBob();
		renderCube();
		renderRockets();
		renderVidas();
		renderBonus();
		renderItem();
		renderPorta();
		renderInimigo();
		renderChamas();
		renderCandelabro();
		batch.end();
		renderLaserBeams();


		//Mostra a palavra 'Pontuação:' na tela

		//Posição x, y
		//font.draw(batch2, "Posição x: " + map.bob.pos.x + "\n" + "Posição y: " + map.bob.pos.y, Gdx.graphics.getWidth() * .25f, Gdx.graphics.getHeight());

		fps.log();
	}

	private void renderBob () {
		int[] heroi_frames_correndo = {0,1,2,3,4,5,6,7};
		int[] heroi_frames_parado = {3,6};
		int[] heroi_frames_pulo = {4,5};
		int[] heroi_frames_morrendo = {0,1,2,3,4};

		Animation<TextureRegion> anim = null;
		boolean loop = true;
		if (map.bob.state == Bob.RUN) {
			if (map.bob.dir == Bob.LEFT)
				anim = heroi.esquerda(0.1f, 0, heroi_frames_correndo);
			else
				anim = heroi.direita(0.1f, 0, heroi_frames_correndo);
		}
		if (map.bob.state == Bob.IDLE) {
			if (map.bob.dir == Bob.LEFT)
				anim = heroi.parado(0.8f, 2, heroi_frames_parado);
			else
				anim = heroi.parado(0.8f, 2, heroi_frames_parado);
		}
		if (map.bob.state == Bob.JUMP) {
			if (map.bob.dir == Bob.LEFT)
				anim = heroi.esquerda(0.6f, 2, heroi_frames_pulo);
			else
				anim = heroi.direita(0.6f, 2, heroi_frames_pulo);
		}
		if (map.bob.state == Bob.SPAWN) {
			anim = spawn;
			loop = false;
		}
		if (map.bob.state == DYING) {
			anim = heroi.parado(0.8f, 2, heroi_frames_parado);
			loop = false;
		}
		batch.draw(anim.getKeyFrame(map.bob.stateTime, loop), map.bob.pos.x - 0.5f, map.bob.pos.y, 2, 3);
	}

	private void renderInimigo () {
		if(map.inimigo == null){
			return;
		}
		int[] pirate_frames = {0,1,2,3,4,5,6,7,8,9};
		int[] pirate_idle_frames = {0,1,2,3,4,5,6};
		Animation<TextureRegion> anim = null;
		boolean loop = true;
		for (int i = 0; i < map.inimigo.size; i++) {
			if (map.inimigo.get(i).state == Inimigo.RUN) {
				if (map.inimigo.get(i).dir == Inimigo.LEFT)
					anim = pirate.esquerda(0.1f, 0, pirate_frames);
				else
					anim = pirate.direita(0.1f, 0, pirate_frames);
			}
			if (map.inimigo.get(i).state == Inimigo.IDLE) {
				if (map.inimigo.get(i).dir == Inimigo.LEFT)
					anim = pirate.parado(0.1f, 1, pirate_idle_frames);
				else
					anim = pirate.parado(0.1f, 0, pirate_idle_frames);
			}
			if (map.inimigo.get(i).state == Inimigo.JUMP) {
				if (map.inimigo.get(i).dir == Inimigo.LEFT)
					pirate.parado(0.1f, 0, pirate_idle_frames);
				else
					pirate.parado(0.1f, 0, pirate_idle_frames);
			}
			if (map.inimigo.get(i).state == Inimigo.SPAWN) {
				anim = inimigospawn;
				loop = false;
			}
			if (map.bob.state == Inimigo.DYING) {
				pirate.parado(0.1f, 0, pirate_idle_frames);
				loop = false;
			}
			batch.draw(anim.getKeyFrame(map.inimigo.get(i).stateTime, loop), map.inimigo.get(i).pos.x, map.inimigo.get(i).pos.y, 3, 3);
		}
	}


	private void renderCube () {
		if (map.cube.state == Cube.FOLLOW) batch.draw(cube, map.cube.pos.x, map.cube.pos.y, 1.5f, 1.5f);
		if (map.cube.state == Cube.FIXED)
			batch.draw(cubeFixed.getKeyFrame(map.cube.stateTime, false), map.cube.pos.x, map.cube.pos.y, 1.5f, 1.5f);
		if (map.cube.state == Cube.CONTROLLED) batch.draw(cubeControlled, map.cube.pos.x, map.cube.pos.y, 1.5f, 1.5f);
	}

	private void renderRockets () {
		for (int i = 0; i < map.rockets.size; i++) {
			Rocket rocket = map.rockets.get(i);
			if (rocket.state == Rocket.FLYING) {
				TextureRegion frame = this.rocket.getKeyFrame(rocket.stateTime, true);
				batch.draw(frame, rocket.pos.x, rocket.pos.y, 0.5f, 0.5f, 1, 1, 1, 1, rocket.vel.angle());
			} else {
				TextureRegion frame = this.rocketExplosion.getKeyFrame(rocket.stateTime, false);
				batch.draw(frame, rocket.pos.x, rocket.pos.y, 1, 1);
			}
			batch.draw(rocketPad, rocket.startPos.x, rocket.startPos.y, 1, 1);
		}
	}

	private void renderDispensers () {
		for (int i = 0; i < map.dispensers.size; i++) {
			Dispenser dispenser = map.dispensers.get(i);
			batch.draw(this.dispenser, dispenser.bounds.x, dispenser.bounds.y, 1, 1);
		}
	}

	private void renderMovingSpikes () {
		for (int i = 0; i < map.movingSpikes.size; i++) {
			MovingSpikes spikes = map.movingSpikes.get(i);
			batch.draw(movingSpikes, spikes.pos.x, spikes.pos.y, 0.5f, 0.5f, 1, 1, 1, 1, spikes.angle);
		}
	}

	private void renderLasers () {
		for (int i = 0; i < map.lasers.size; i++) {
			Laser laser = map.lasers.get(i);
			batch.draw(this.laser, laser.pos.x, laser.pos.y, 0.5f, 0.5f, 1, 1, 1, 1, laser.angle);
		}
	}

	private void renderLaserBeams () {
		cam.update(false);
		renderer.begin(cam.combined, GL20.GL_LINES);
		for (int i = 0; i < map.lasers.size; i++) {
			Laser laser = map.lasers.get(i);
			float sx = laser.startPoint.x, sy = laser.startPoint.y;
			float ex = laser.cappedEndPoint.x, ey = laser.cappedEndPoint.y;
			//float ex = map.bob.pos.x;
			//float ey = map.bob.pos.y;
			renderer.color(1, 0, 0, 1);
			renderer.vertex(sx, sy, 0);
			renderer.color(1, 0, 0, 1);
			renderer.vertex(ex, ey, 0);
		}
		renderer.end();
	}

	public void renderVidas(){
		for (int i = 0; i < map.vidas.size; i++) {
			Itens vida = map.vidas.get(i);
				batch.draw(this.vida, vida.pos.x, vida.pos.y, 1, 1);
		}
	}

	public void renderBonus(){
		for (int i = 0; i < map.bonus.size; i++) {
			Itens itens = map.bonus.get(i);
			batch.draw(this.bonus, itens.pos.x, itens.pos.y, 1, 1);
		}
	}

/*	public void renderCoin(){
		for (int i = 0; i < map.item.size; i++) {
			Itens item = map.item.get(i);
			batch.draw(this.item, item.pos.x, item.pos.y, 1, 1);
		}
	}*/

	private void renderItem () {
		if(map.coin == null){
			return;
		}

		Animation<TextureRegion> anim = item;
		boolean loop = true;
		for (int i = 0; i < map.coin.size; i++) {
			batch.draw(anim.getKeyFrame(map.coin.get(i).stateTime, loop), map.coin.get(i).pos.x, map.coin.get(i).pos.y, 1, 1);
		}

		if(anim.getKeyFrameIndex(stateTime) == 9){
			stateTime = .1f;
		}
		Gdx.app.log("Itens", String.valueOf(anim.getKeyFrameIndex(stateTime)));


	}

	private void renderPorta() {
		if(map.porta == null){
			return;
		}

		Animation<TextureRegion> anim = porta;
		boolean loop = true;
		batch.draw(anim.getKeyFrame(map.porta.stateTime, loop), map.porta.pos.x, map.porta.pos.y, 2, 3);
	}

	private void renderChamas() {
		if(map.chama == null){
			return;
		}

		int[] chamas_frame = {0,1,2,3,4,5,6,7,8,9};
		Animation<TextureRegion> anim = null;
		boolean loop = true;
		for (int i = 0; i < map.chama.size; i++) {
			if(map.chama.get(i).orientacao == 0) {
				anim = chamas.direita(0.1f, 0, chamas_frame);
				batch.draw(anim.getKeyFrame(map.chama.get(i).stateTime, loop), map.chama.get(i).pos.x, map.chama.get(i).pos.y, 2, 1);
			}
			if(map.chama.get(i).orientacao == 1) {
				anim = chamas.esquerda(0.1f, 0, chamas_frame);
				batch.draw(anim.getKeyFrame(map.chama.get(i).stateTime, loop), map.chama.get(i).pos.x-1, map.chama.get(i).pos.y, 2, 1);
			}

			if(map.chama.get(i).bounds.overlaps(map.bob.bounds) & anim.getKeyFrameIndex(stateTime) == 4){
				map.bob.state = DYING;
				map.bob.stateTime = 0;
				map.chama.get(i).stateTime = 0;
				map.fire.play();
			}
		}
	}

	private void renderCandelabro(){
		if(map.candelabro == null){
			return;
		}

		int[] candelabros_frames = {0,1,2,3};
		Animation<TextureRegion> anim = null;
		boolean loop = true;
		for (int i = 0; i < map.candelabro.size; i++) {
				anim = candelabros.parado(0.1f, 2, candelabros_frames);
				batch.draw(anim.getKeyFrame(map.candelabro.get(i).stateTime, loop), map.candelabro.get(i).pos.x, map.candelabro.get(i).pos.y, 1, 2);
		}
	}

	public void dispose () {
		cache.dispose();
		batch.dispose();
		tile.getTexture().dispose();
		cube.getTexture().dispose();
		map.bonus_sound.dispose();
		map.fire.dispose();
	}
}
