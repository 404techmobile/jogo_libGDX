package com.techmobile.donga_adventure;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Itens {
    Rectangle bounds = new Rectangle();
    Map map;
    InformacoesTela info;
    Vector2 pos = new Vector2();
    float initPos;
    float stateTime = 0;

    public Itens(Map map, float x, float y){
        this.bounds.x = x;
        this.bounds.y = y;
        bounds.width = bounds.height = 1;
        this.map = map;
        this.info = new InformacoesTela();
        this.pos.set(x, y);
        this.initPos = this.pos.y;
        stateTime = 0;
    }

    public void update(int i, float delta){
        stateTime += delta;
    }

    public void updateVidas(int i, float delta){

        if((Vector2.dst(pos.x, pos.y, map.bob.pos.x, map.bob.pos.y) < 1)|((Vector2.dst(pos.x, pos.y, map.bob.pos.x, map.bob.pos.y+3) < 1))){
            map.vidas.removeIndex(i);
            info.pontos +=10;
            if(info.life <10) info.life++;
            map.bonus_sound.play(1.0f);
            //map.bonus_sound.stop(id);
        }
        this.pos.y+=.5*delta;
        stateTime += delta;

        /*
        //Sobe e depois some
        if(this.pos.y >= this.initPos + 2){
          vidaBratch.dispose();
        }

        //Sobe, pára e volta pra posição inicial
        if(this.pos.y >= this.initPos + 2){
            this.pos.y = this.initPos;
        }*/
    }

    public void updateBonus(int i, float delta){
        if(Vector2.dst(pos.x, pos.y, map.bob.pos.x, map.bob.pos.y) < 1){
            map.bonus.removeIndex(i);
            info.pontos +=10;
            if(info.life <10) info.life++;
            map.bonus_sound.play(1.0f);

        }
        this.pos.y+=.2*delta;
    }
}
