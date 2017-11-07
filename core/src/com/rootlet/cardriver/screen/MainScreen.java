package com.rootlet.cardriver.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by pavlenko on 11/7/17.
 */

public class MainScreen implements Screen {

    World word;
    Box2DDebugRenderer rend;
    OrthographicCamera camera;
    Body rect;

    @Override
    public void show() {
        //Задаем гравитацию
        word = new World(new Vector2(0, -10), false);
        camera = new OrthographicCamera(20, 15);
        camera.position.set(new Vector2(10, 7.5f), 0);
        rend = new Box2DDebugRenderer();

        createRect();


    }

    @Override
    public void render(float delta) {
    // Выведем в консоль количество кадров в секунду
        Gdx.app.log("GameScreen FPS", (1/delta) + "" );

        camera.update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        rend.render(word, camera.combined);

        word.step(delta, 4, 4);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) rect.applyForceToCenter(new Vector2(-50, 0), true);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) rect.applyForceToCenter(new Vector2(50, 0), true);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private void createRect() {
        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.DynamicBody;
        bDef.position.set(10, 7);

        rect = word.createBody(bDef);

        FixtureDef fDef = new FixtureDef();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(2, 2);

        fDef.shape = shape;
        fDef.density = 2;
        fDef.friction = 0.1f;

        rect.createFixture(fDef);
        shape.dispose();
        createWall();
    }

    private void createWall() {
        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.position.set(0, 0);

        Body w = word.createBody(bDef);

        FixtureDef fDef = new FixtureDef();

        ChainShape chainShape = new ChainShape();

        chainShape.createChain(new Vector2[] {new Vector2(0, 10), new Vector2(1, 0), new Vector2(19, 0), new Vector2(20, 15)});

        fDef.shape = chainShape;
        fDef.density = 2;
        fDef.restitution = 0.5f;
        fDef.friction = 0.1f;

        w.createFixture(fDef);
        chainShape.dispose();
    }
}
