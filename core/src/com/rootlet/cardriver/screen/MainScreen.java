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

import org.jbox2d.common.Settings;

/**
 * Created by pavlenko on 11/7/17.
 */

public class MainScreen implements Screen {

    World word;
    Box2DDebugRenderer rend;
    OrthographicCamera camera;
    Body rect;
    Body car;
    PolygonShape wheelFL;
    PolygonShape shape;
    ChainShape chainShape;

    Batch batch = new SpriteBatch();
    BitmapFont font24;

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
        Gdx.app.log("GameScreen FPS", (1/delta) + "" );

        camera.update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        rend.render(word, camera.combined);

        word.step(delta, 4, 4);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) car.applyForceToCenter(new Vector2(-100, 0), true);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) car.applyForceToCenter(new Vector2(100, 0), true);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) car.applyForceToCenter(new Vector2(0, 100), true);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) car.applyForceToCenter(new Vector2(0, -100), true);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) car.setAngularVelocity(0.5f);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) car.setAngularVelocity(-0.5f);

        updateFriction();


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
    }

    private void createRect() {
        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.DynamicBody;
        bDef.position.set(10, 7);

        rect = word.createBody(bDef);

        FixtureDef fDef = new FixtureDef();



        shape = new PolygonShape();

        shape.setAsBox(2,2);
        fDef.shape = shape;
        fDef.density = 2;
        fDef.friction = 0.1f;

        rect.createFixture(fDef);


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

    private void createCar() {
        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.DynamicBody;
        //bDef.position.set(10, 10);

        car = word.createBody(bDef);
        shape = new PolygonShape();
        shape.setAsBox(1,2);
        car.createFixture(shape, 1);
        car.setUserData(this);

        FixtureDef fDef = new FixtureDef();






        // Колеса автомобиля
        wheelFL = new PolygonShape();
        PolygonShape wheelFR = new PolygonShape();
        PolygonShape wheelBL = new PolygonShape();
        PolygonShape wheelBR = new PolygonShape();
        wheelFL.setAsBox(0.2f, 0.4f, new Vector2(-1,1), 0);
        wheelFR.setAsBox(0.2f, 0.4f, new Vector2(1,1), 0);
        wheelBL.setAsBox(0.2f, 0.4f, new Vector2(-1,-1), 0);
        wheelBR.setAsBox(0.2f, 0.4f, new Vector2(1,-1), 0);

        fDef.shape = shape;
        fDef.density = 2;
        fDef.friction = 0.1f;

        //car.createFixture(fDef);
        car.createFixture(wheelFL, 2);
        car.createFixture(wheelFR, 2);
        car.createFixture(wheelBL, 2);
        car.createFixture(wheelBR, 2);

    }

    public Vector2 getLeteralVelocity() {
        Vector2 currentRightNormal = car.getWorldVector(new Vector2(1,0));
        return currentRightNormal.scl(currentRightNormal.dot(car.getLinearVelocity()));
    }

    public Vector2 getForwardVelocity() {
        Vector2 currentForwardNormal = car.getWorldVector(new Vector2(0,1));
        return currentForwardNormal.scl(currentForwardNormal.dot(car.getLinearVelocity()));
    }

    public void updateFriction() {
        Vector2 impulse = getLeteralVelocity().scl(-car.getMass());
        car.applyLinearImpulse(impulse, car.getWorldCenter(), false);

        car.applyAngularImpulse(0.1f * car.getInertia() * -car.getAngularVelocity(), false);

        Vector2 currentForwardNormal = getForwardVelocity();
        float currentForwardSpeed = normalize(currentForwardNormal);
        value = currentForwardNormal.len();
        float dragForceMagnitude = -2 * currentForwardSpeed;
        car.applyForce( currentForwardNormal.scl(dragForceMagnitude ), car.getWorldCenter(), false );
    }

    /// Convert this vector into a unit vector. Returns the length.
    private float normalize(Vector2 vector2)
    {
        float length = vector2.len();
        if (length < Settings.EPSILON)
        {
            return 0.0f;
        }
        float invLength = 1.0f / length;
        vector2.x *= invLength;
        vector2.y *= invLength;

        return length;
    }

    // Пример построения автомобиля))
    //http://www.iforce2d.net/b2dtut/top-down-car
    //http://badlogicgames.com/forum/viewtopic.php?t=4531&p=21844
}
