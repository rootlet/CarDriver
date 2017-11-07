package com.rootlet.cardriver;

import com.badlogic.gdx.Game;
import com.rootlet.cardriver.screen.MainScreen;

public class CarDriver extends Game {

    @Override
    public void create() {
        setScreen(new MainScreen());
    }



    /*SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}*/
}
