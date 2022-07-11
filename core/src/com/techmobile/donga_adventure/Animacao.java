package com.techmobile.donga_adventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animacao {

    Animation<TextureRegion> animation;
    Texture texture;
    TextureRegion[] textureRegions;
    TextureRegion[] texture_frames;
    int largura;
    int altura;

    public Animacao(String pasta_da_imagem, int largura, int altura){
        this.texture = new Texture(Gdx.files.internal(pasta_da_imagem));
        this.largura = largura;
        this.altura = altura;

    }
    public Animation direita(float duracao_da_animacao, int linha_onde_recortar, int[] frames){
        this.textureRegions = new TextureRegion(texture).split(largura, altura)[linha_onde_recortar];
        this.texture_frames = new TextureRegion[frames.length];
        for (int i = 0; i < frames.length; i++) {
            texture_frames[i] = textureRegions[frames[i]];
        }
        this.animation = new Animation<TextureRegion>(duracao_da_animacao, texture_frames);
        return this.animation;
    }

    public Animation esquerda(float duracao_da_animacao, int linha_onde_recortar, int[] frames){
        this.textureRegions = new TextureRegion(texture).split(largura, altura)[linha_onde_recortar];
        for (TextureRegion region : textureRegions)
            region.flip(true, false);
        this.texture_frames = new TextureRegion[frames.length];

        for (int i = 0; i < frames.length; i++) {
            texture_frames[i] = textureRegions[frames[i]];
        }
        this.animation = new Animation<TextureRegion>(duracao_da_animacao, texture_frames);
        return this.animation;
    }

    public Animation parado(float duracao_da_animacao, int linha_onde_recortar, int[] frames){
        this.textureRegions = new TextureRegion(texture).split(largura, altura)[linha_onde_recortar];
        this.texture_frames = new TextureRegion[frames.length];
        for (int i = 0; i < frames.length; i++) {
            texture_frames[i] = textureRegions[frames[i]];
        }
        this.animation = new Animation<TextureRegion>(duracao_da_animacao, texture_frames);
        return this.animation;
    }
}