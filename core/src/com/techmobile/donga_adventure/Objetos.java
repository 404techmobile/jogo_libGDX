package com.techmobile.donga_adventure;
import static com.techmobile.donga_adventure.Bob.DYING;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Objetos {
    public Rectangle bounds = new Rectangle();
    Map map;
    InformacoesTela info;
    Vector2 pos = new Vector2();
    float initPos;
    float stateTime = 0;
    static final int DIREITA = 0;
    static final int ESQUERDA = 1;
    static final int CIMA = 2;
    static final int BAIXO = 3;
    static final int NULO = 4;
    float id=0.0f;

    int orientacao = 0;

    public Objetos(Map map, float x, float y, int orientacao, int largura_retangulo_colisao, int altura_retangulo_colisao){
        this.bounds.x = x;
        this.bounds.y = y;
        //bounds.width = bounds.height = 1;
        bounds.width = largura_retangulo_colisao;
        bounds.height = altura_retangulo_colisao;
        this.map = map;
        this.info = new InformacoesTela();
        this.pos.set(x, y);
        this.initPos = this.pos.y;
        stateTime = 0;
        this.orientacao = orientacao;
    }

    public void update(int i, float delta){
        stateTime += delta;
/*

        if(Vector2.dst(pos.x, pos.y, map.bob.pos.x, map.bob.pos.y) < 2){
            if(id==0.0f){
                id = map.fire.play();
                stateTime = 0;
            }
        }else {
            id=0.0f;
        }
*/

    }

}
