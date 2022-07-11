
package com.techmobile.donga_adventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.HttpParametersUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;

public class Bob {
	static final int IDLE = 0;
	static final int RUN = 1;
	static final int JUMP = 2;
	static final int SPAWN = 3;
	static final int DYING = 4;
	static final int DEAD = 5;
	static final int LEFT = -1;
	static final int RIGHT = 1;
	static final float ACCELERATION = 20f;
	static final float JUMP_VELOCITY = 10;
	static final float GRAVITY = 20.0f;
	static final float MAX_VEL = 4f;
	static final float DAMP = 0.90f;

	Vector2 pos = new Vector2();
	Vector2 accel = new Vector2();
	Vector2 vel = new Vector2();
	public Rectangle bounds = new Rectangle();

	int state = SPAWN;
	float stateTime = 0;
	int dir = LEFT;
	Map map;
	boolean grounded = false;

	InformacoesTela info;

	//Socket socket = Gdx.net.newClientSocket(Net.Protocol.TCP, "191.252.202.55", 8082, null);
	SocketAddress socketAddress = new InetSocketAddress("191.252.202.55", 8082);
	Socket socket = new Socket();


	public Bob (Map map, float x, float y) {
		this.map = map;
		pos.x = x;
		pos.y = y;
		//bounds.width = 0.6f;
		//bounds.height = 0.8f;
		bounds.width = 0.6f;
		bounds.height = 2.9f;
		bounds.x = pos.x + 0.2f;
		bounds.y = pos.y;
		state = SPAWN;
		stateTime = 0;
		this.info = new InformacoesTela();


		//request_get();
		//request_post();
	}

	/*public void Thread() {
		new Thread(new Runnable() {
			public void run() {
				try {
					socket.getOutputStream().write((int) pos.x);
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					Gdx.app.log("Servidor: ", String.valueOf(socket.getInputStream().read()));
					info.life = socket.getInputStream().read();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}*/


    public void request_get (){
        Gdx.app.log("funcao", "ok");
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        final String url = "https://www.invertexto.com/donga";
        request.setUrl(url);
        //request.setContent("requestJson");
        request.setHeader("Content-Type", "application/text");

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String responseJson = httpResponse.getResultAsString();

				Document doc = Jsoup.parse(responseJson);
				Elements conteudo = doc.select(".form-control");

                info.life = Integer.parseInt(conteudo.get(0).text());
                Gdx.app.log("conteudo", conteudo.get(0).text());
            }
            @Override public void failed(Throwable t) {
                Gdx.app.log("failed!", "Ok");
            }
            @Override
            public void cancelled() {
                Gdx.app.log("cancelled!", "Ok");
            }
        });
    }


	public void request_post (){
		HashMap parameters = new HashMap();
		parameters.put("userId","donga");
		parameters.put("token","18c92be6cc15e5d48d5071308734542a");
		parameters.put("saved_text", "texto enviado");

    	Gdx.app.log("funcao_post", "ok");
		Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
		final String url = "https://www.invertexto.com/ajax/notepad-save.php";

		request.setUrl(url);
		request.setContent(HttpParametersUtils.convertHttpParameters(parameters));
		//request.setContent();
		request.setHeader("Content-Type", "application/x-www-form-urlencoded");

		Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
			@Override
			public void handleHttpResponse(Net.HttpResponse httpResponse) {
				String responseJson = httpResponse.getResultAsString();
				Gdx.app.log("f_post", responseJson);
			}

			@Override
			public void failed(Throwable t) {
				Gdx.app.log("failed!", "Ok");
			}

			@Override
			public void cancelled() {
				Gdx.app.log("cancelled!", "Ok");
			}
		});
	}


	public void update (float deltaTime) {

/*
		try {
			socket.connect(socketAddress, 1);
			socket.getOutputStream().write(3);
		} catch (IOException e) {
			e.printStackTrace();
		}
*/

		processKeys();
		accel.y = -GRAVITY;
		accel.scl(deltaTime);
		vel.add(accel.x, accel.y);
		if (accel.x == 0) vel.x *= DAMP;
		if (vel.x > MAX_VEL) vel.x = MAX_VEL;
		if (vel.x < -MAX_VEL) vel.x = -MAX_VEL;
		vel.scl(deltaTime);
		tryMove();
		vel.scl(1.0f / deltaTime);

		if (state == SPAWN) {
			if (stateTime > 0.4f) {
				state = IDLE;
			}
		}

		if (state == DYING) {
			if (stateTime > 0.4f) {
				state = DEAD;
			}
		}

		stateTime += deltaTime;
	}

	private void processKeys () {
		if (map.cube.state == Cube.CONTROLLED || state == SPAWN || state == DYING) return;

		float x0 = (Gdx.input.getX(0) / (float)Gdx.graphics.getWidth()) * 480;
		float x1 = (Gdx.input.getX(1) / (float)Gdx.graphics.getWidth()) * 480;
		float y0 = 320 - (Gdx.input.getY(0) / (float)Gdx.graphics.getHeight()) * 320;

		boolean leftButton = (Gdx.input.isTouched(0) && x0 < 70) || (Gdx.input.isTouched(1) && x1 < 70);
		boolean rightButton = (Gdx.input.isTouched(0) && x0 > 70 && x0 < 134) || (Gdx.input.isTouched(1) && x1 > 70 && x1 < 134);
		boolean jumpButton = (Gdx.input.isTouched(0) && x0 > 416 && x0 < 480 && y0 < 64)
			|| (Gdx.input.isTouched(1) && x1 > 416 && x1 < 480 && y0 < 64);

		if ((Gdx.input.isKeyPressed(Keys.W) || jumpButton) && state != JUMP) {
			state = JUMP;
			vel.y = JUMP_VELOCITY;
			grounded = false;
		}

		if (Gdx.input.isKeyPressed(Keys.A) || leftButton) {
			if (state != JUMP) state = RUN;
			dir = LEFT;
			accel.x = ACCELERATION * dir;
		} else if (Gdx.input.isKeyPressed(Keys.D) || rightButton) {
			if (state != JUMP) state = RUN;
			dir = RIGHT;
			accel.x = ACCELERATION * dir;
		} else {
			if (state != JUMP) state = IDLE;
			accel.x = 0;
		}
	}

	Rectangle[] r = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};

	private void tryMove () {
		bounds.x += vel.x;
		fetchCollidableRects();
		for (int i = 0; i < r.length; i++) {
			Rectangle rect = r[i];
			if (bounds.overlaps(rect)) {
				if (vel.x < 0)
					bounds.x = rect.x + rect.width + 0.01f;
				else
					bounds.x = rect.x - bounds.width - 0.01f;
				vel.x = 0;
			}
		}

		bounds.y += vel.y;
		fetchCollidableRects();
		for (int i = 0; i < r.length; i++) {
			Rectangle rect = r[i];
			if (bounds.overlaps(rect)) {
				if (vel.y < 0) {
					bounds.y = rect.y + rect.height + 0.01f;
					grounded = true;
					if (state != DYING && state != SPAWN) state = Math.abs(accel.x) > 0.1f ? RUN : IDLE;
				} else
					bounds.y = rect.y - bounds.height - 0.01f;
				vel.y = 0;
			}
		}

		pos.x = bounds.x - 0.2f;
		pos.y = bounds.y;
	}

	private void fetchCollidableRects () {
		int p1x = (int)bounds.x;
		int p1y = (int)Math.floor(bounds.y);
		int p2x = (int)(bounds.x + bounds.width);
		int p2y = (int)Math.floor(bounds.y);
		int p3x = (int)(bounds.x + bounds.width);
		int p3y = (int)(bounds.y + bounds.height);
		int p4x = (int)bounds.x;
		int p4y = (int)(bounds.y + bounds.height);

		int[][] tiles = map.tiles;
		int tile1 = tiles[p1x][map.tiles[0].length - 1 - p1y];
		int tile2 = tiles[p2x][map.tiles[0].length - 1 - p2y];
		int tile3 = tiles[p3x][map.tiles[0].length - 1 - p3y];
		int tile4 = tiles[p4x][map.tiles[0].length - 1 - p4y];

		if (state != DYING && (map.isDeadly(tile1) || map.isDeadly(tile2) || map.isDeadly(tile3) || map.isDeadly(tile4))) {
			state = DYING;
			stateTime = 0;
			if(info.life >=1 ){
				info.life--;
			}
		}

		if (tile1 == Map.TILE || tile1 == Map.PISO)
			r[0].set(p1x, p1y, 1, 1);
		else
			r[0].set(-1, -1, 0, 0);
		if (tile2 == Map.TILE|| tile2 == Map.PISO)
			r[1].set(p2x, p2y, 1, 1);
		else
			r[1].set(-1, -1, 0, 0);
		if (tile3 == Map.TILE || tile3 == Map.PISO)
			r[2].set(p3x, p3y, 1, 1);
		else
			r[2].set(-1, -1, 0, 0);
		if (tile4 == Map.TILE || tile4 == Map.PISO)
			r[3].set(p4x, p4y, 1, 1);
		else
			r[3].set(-1, -1, 0, 0);

		if (map.cube.state == Cube.FIXED) {
			r[4].x = map.cube.bounds.x;
			r[4].y = map.cube.bounds.y;
			r[4].width = map.cube.bounds.width;
			r[4].height = map.cube.bounds.height;
		} else
			r[4].set(-1, -1, 0, 0);
	}
}
