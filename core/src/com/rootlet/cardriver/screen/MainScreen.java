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
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.rootlet.cardriver.helpers.Control;
import com.rootlet.cardriver.helpers.FixtureUserDataType;
import com.rootlet.cardriver.helpers.MyContactListener;
import com.rootlet.cardriver.objects.Car;
import com.rootlet.cardriver.objects.FixtureUserData;
import com.rootlet.cardriver.objects.GroundAreaFUD;
import com.rootlet.cardriver.objects.Tire;

/**
 * Created by pavlenko on 11/7/17.
 */

public class MainScreen implements Screen {

    final float DEGTORAD = 0.0174532925199432957f;
    final float RADTODEG = 57.295779513082320876f;

    World world;
    Box2DDebugRenderer rend;
    OrthographicCamera camera;
    Body rect;
    PolygonShape wheelFL;
    PolygonShape shape;
    ChainShape chainShape;

    Batch batch = new SpriteBatch();
    BitmapFont font24;

    Tire tire;
    Car car;
    Body groundBody;
    PolygonShape polygonShape;

    float value;


    @Override
    public void show() {
        //Задаем гравитацию
        //world = new World(new Vector2(0, -10), false);
        world = new World(new Vector2(0, 0), false);
        world.setContactListener(new MyContactListener(this));
        camera = new OrthographicCamera(200, 150);
        camera.position.set(new Vector2(10, 7.5f), 0);
        rend = new Box2DDebugRenderer();

        createCar();
        createGround();
        initFont();

    }

    @Override
    public void render(float delta) {
    // Выведем в консоль количество кадров в секунду
        //Gdx.app.log("GameScreen FPS", (1/delta) + "" );

        camera.update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        rend.render(world, camera.combined);

        world.step(delta, 4, 4);

        Control control = Control.NONE;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) control = Control.LEFT;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) control = Control.RIGHT;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) control = Control.UP;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) control = Control.DOWN;

        /*tire.updateFriction();
        tire.updateDrive(control);
        tire.updateTurn(control);*/

        car.update(control);

        batch.begin();
        font24.draw(batch, "CAR DRIVER " + value, 50, 50);
//        font24.draw(batch, "currentTraction = " + tire.getCurrentTraction(), 50, 100);
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
        tire.getTireShape();
        polygonShape.dispose();
    }

    private void createWall() {
        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.position.set(0, 0);

        Body w = world.createBody(bDef);

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

        //tire = new Tire(world);
        car = new Car(world);
    }

    public void createGround() {
        BodyDef bodyDef = new BodyDef();
        groundBody = world.createBody(bodyDef);

        polygonShape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;

        polygonShape.setAsBox( 9, 7, new Vector2(-10,15), 20*DEGTORAD );
        Fixture groundAreaFixture = groundBody.createFixture(fixtureDef);
        groundAreaFixture.setUserData( new GroundAreaFUD( 0.5f, false ) );

        polygonShape.setAsBox( 9, 5, new Vector2(5,20), -40*DEGTORAD );
        groundAreaFixture = groundBody.createFixture(fixtureDef);
        groundAreaFixture.setUserData( new GroundAreaFUD( 0.2f, false ) );
    }

    public void tire_vs_groundArea(Fixture tireFixture, Fixture groundAreaFixture, boolean began) {
        Tire tire = (Tire)tireFixture.getBody().getUserData();
        GroundAreaFUD gaFud = (GroundAreaFUD)groundAreaFixture.getUserData();
        if ( began )
            tire.addGroundArea( gaFud );
        else
            tire.removeGroundArea( gaFud );
    };

    public void handleContact(Contact contact, boolean began)
    {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        FixtureUserData fudA = (FixtureUserData) a.getUserData();
        FixtureUserData fudB = (FixtureUserData) b.getUserData();

        System.out.println(fudA == null);
        System.out.println(fudB == null);
        if ( fudA == null || fudB == null ) return;



        if (fudA.getType() == FixtureUserDataType.CAR_TIRE && fudB.getType() == FixtureUserDataType.GROUND_AREA)
            tire_vs_groundArea(a, b, began);
        else if (fudA.getType() == FixtureUserDataType.GROUND_AREA && fudB.getType() == FixtureUserDataType.CAR_TIRE )
            tire_vs_groundArea(b, a, began);
    }


    // Пример построения автомобиля))
    //http://www.iforce2d.net/b2dtut/top-down-car
    //http://badlogicgames.com/forum/viewtopic.php?t=4531&p=21844
}
