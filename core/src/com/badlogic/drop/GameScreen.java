package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;

public class GameScreen implements Screen {
    final Drop game;
    private Array<Rectangle> raindrops;
    private long lastDropTime;
    private Texture dropImage;
    private Rectangle bucket;
    private Texture bucketImage;
    private Sound dropSound;
    private OrthographicCamera camera;
    private int score;
    private int lives;
    private int record;
    private int lastRecord;
    boolean isLocalAvailable;
    Preferences pref;

    public GameScreen(final Drop game) {
        this.game = game;
        isLocalAvailable = Gdx.files.isLocalStorageAvailable();
        // load the images for the droplet and the bucket, 64x64 pixels eac
        dropImage = new Texture(Gdx.files.internal("images/droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("images/bucket.png"));
        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("sounds/drop.wav"));

        bucket = new Rectangle();
        bucket.x = 800f / 2 - 64f / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        game.batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        raindrops = new Array<>();
        pref = Gdx.app.getPreferences("Save");
        record = 0;
        if(pref.contains("record"))
            record = pref.getInteger("record");
        score = 0;
        lives = 3;
        lastRecord = 0;

        spawnRaindrop();
    }

    @Override
    public void render (float delta) {
        ScreenUtils.clear(0,0,0.2f,1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(game.backTexture, 0,0);
        game.font.draw(game.batch, "Score: " + score, 20, 460);
        game.font.draw(game.batch, "Record: " + record, 20, 440);
        game.font.draw(game.batch, "Last record: " + lastRecord, 20, 420);
        game.batch.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height);
        for(Rectangle raindrop: raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        game.batch.end();

        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64f / 2;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
            bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            bucket.x += 200 * Gdx.graphics.getDeltaTime();
        if(bucket.x < 0) bucket.x = 0;
        if(bucket.x > 800 - 64) bucket.x = 800 - 64;
        long spawnDelta = 1000000000L - (5000000L * score);
        if(spawnDelta < 0) spawnDelta = 0;
        if(TimeUtils.nanoTime() - lastDropTime > spawnDelta)
            spawnRaindrop();

        for (Iterator<Rectangle> iterator = raindrops.iterator(); iterator.hasNext(); ) {
            Rectangle raindrop = iterator.next();
            raindrop.y -= (200 + score) * Gdx.graphics.getDeltaTime();
            if(raindrop.y + 64 < 0){
                iterator.remove();
                damage();
            }
            if(raindrop.overlaps(bucket)) {
                dropSound.play();
                iterator.remove();
                score++;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    private void endGame(){
        if(score > record){
            record = score;
        }
        lastRecord = score;
    }

    private void damage(){

        if(lives == 1){
            endGame();
        }
        lives--;
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800-64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void dispose () {
        endGame();
        pref.putInteger("record", record).flush();
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        pref.putInteger("record", record).flush();
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        pref.putInteger("record", record).flush();
    }
}
