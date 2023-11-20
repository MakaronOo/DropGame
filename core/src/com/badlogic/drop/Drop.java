package com.badlogic.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Drop extends Game {
	private Music rainMusic;
	public SpriteBatch batch;
	public BitmapFont font;
	static public Skin skin;
	public Texture backTexture;
	public Image background;

	@Override
	public void create () {
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("musics/rain.mp3"));
		skin = new Skin(Gdx.files.internal("skins/glassy-ui.json"));

		batch = new SpriteBatch();
		font = new BitmapFont();

		// start the playback of the background music immediately
		rainMusic.setLooping(true);
		rainMusic.play();

		backTexture = new Texture(Gdx.files.internal("images/background.png"));
		background = new Image(backTexture);
		background.setPosition(0,0);
		background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		rainMusic.dispose();
		batch.dispose();
		font.dispose();
		skin.dispose();
	}
}
