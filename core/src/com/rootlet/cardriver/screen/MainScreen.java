package com.rootlet.cardriver.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.rootlet.cardriver.world.Car;

import org.jbox2d.common.Settings;

/**
 * Created by pavlenko on 11/7/17.
 */

public class MainScreen implements Screen {

    World word;
    Box2DDebugRenderer rend;
    OrthographicCamera camera;
    Body rect;
    Body tire;
    PolygonShape wheelFL;
    PolygonShape shape;
    ChainShape chainShape;

    Batch batch = new SpriteBatch();
    BitmapFont font24;

    Car car;

    float value;


    @Override
    public void show() {
        //Задаем гравитацию
        //word = new World(new Vector2(0, -10), false);
        word = new World(new Vector2(0, 0), false);
        camera = new OrthographicCamera(20, 15);
        camera.position.set(new Vector2(10, 7.5f), 0);
        rend = new Box2DDebugRenderer();

        createCar();
        //createWall();
        initFont();

    }

    @Override
    public void render(float delta) {
    // Выведем в консоль количество кадров в секунду
        //Gdx.app.log("GameScreen FPS", (1/delta) + "" );

        camera.update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        rend.render(word, camera.combined);

        word.step(delta, 4, 4);

       /* if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) car.getTire().applyForceToCenter(new Vector2(-100, 0), true);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) car.getTire().applyForceToCenter(new Vector2(100, 0), true);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) car.getTire().applyForceToCenter(new Vector2(0, 100), true);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) car.getTire().applyForceToCenter(new Vector2(0, -100), true);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) car.getTire().setAngularVelocity(0.5f);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) car.getTire().setAngularVelocity(-0.5f);*/

        Car.Control control = Car.Control.NONE;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) control = Car.Control.LEFT;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) control = Car.Control.RIGHT;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) control = Car.Control.UP;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) control = Car.Control.DOWN;

        car.updateFriction();
        car.updateDrive(control);
        car.updateTurn(control);

        batch.begin();
        font24.draw(batch, "CAR DRIVER " + value, 50, 50);
        batch.end();
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
        font24.dispose();
        chainShape.dispose();
        shape.dispose();
        car.getTireShape();
    }

    private void createWall() {
        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.position.set(0, 0);

        Body w = word.createBody(bDef);

        FixtureDef fDef = new FixtureDef();

        chainShape = new ChainShape();

        chainShape.createChain(new Vector2[] {new Vector2(0, 10), new Vector2(1, 0), new Vector2(19, 0), new Vector2(20, 15)});

        fDef.shape = chainShape;
        fDef.density = 2;
        fDef.restitution = 0.5f;
        fDef.friction = 0.1f;

        w.createFixture(fDef);


    }

    private void initFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/roboto/Roboto-Black.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 24;
        parameter.color = Color.CORAL;
        //parameter.shadowColor = Color.CYAN;
        //parameter.shadowOffsetX = (1);
        //parameter.borderColor = Color.BROWN;
        //parameter.borderWidth = 1;

        font24 = generator.generateFont(parameter);
        font24.getData().setScale(2f);

    }

    public void createCar() {
        car = new Car(word);
    }

    // Пример построения автомобиля))
    //http://www.iforce2d.net/b2dtut/top-down-car
    //http://badlogicgames.com/forum/viewtopic.php?t=4531&p=21844
}
