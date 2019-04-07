package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class BirdGdxGame extends ApplicationAdapter implements ApplicationListener, InputProcessor {

    private static int gameState = 0;

    private Texture backGroundTexture;
    //UI
    private SpriteBatch UI;
    private Texture CarBar;
    private Rectangle carRect;
    private Texture BirdBar;
    private Rectangle birdRect;
    //PLAYER 1
    private int health = 100;
    private SpriteBatch batch;

    private OrthographicCamera camera;
    private Texture player1Texture;
    private Rectangle player1Rectangle;
    private Vector3 touchPos;

    private static final float SPEED = 300f; //world units per second
    private final Vector2 tmp = new Vector2();
    static float prevx;
    static  float prevy;

    //PLAYER 2
    private Texture player2Texture;

    private Rectangle player2Rectangle;


    //Client side stuff
    static int i = 1;

    private static Socket birdSocket;
    private static DataOutputStream birdout;
    private static DataInputStream birdin;

    public BirdGdxGame() {
    }


    @Override
    public void create() {
        UI = new SpriteBatch();
        batch = new SpriteBatch();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());// sets camera for app
        // create UI here
        CarBar = new Texture("carCard.png");
        carRect = new Rectangle();
        carRect.set(0, 300, CarBar.getWidth(), CarBar.getHeight());
        BirdBar = new Texture("birdCard.png");
        birdRect = new Rectangle();
        birdRect.set(0, -500, BirdBar.getWidth(), BirdBar.getHeight());
        //
        backGroundTexture = new Texture("BackGround.png");


        //create bird here
        player1Texture = new Texture("Bird.png");
        player1Rectangle = new Rectangle();
        player1Rectangle.set(240, 0, 64, 64);

        player2Texture = new Texture("car.png");
        player2Rectangle = new Rectangle();
        player2Rectangle.set(240, 0, 64, 64);

        touchPos = new Vector3();


                    // Create a socket
                    try {
                     birdSocket = Gdx.net.newClientSocket(Net.Protocol.TCP,"pi.cs.oswego.edu",2720, null);

                    // Read data from the socket into a BufferedReader
                     birdout = new DataOutputStream(birdSocket.getOutputStream());
                    birdin = new DataInputStream(birdSocket.getInputStream());

                        // Read to the next newline (\n) and display that text on labelMessage

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

}

    @Override
    public void render() {
        prevx = player1Rectangle.x;
        prevy = player1Rectangle.y;

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


            batch.setProjectionMatrix(camera.combined);


            batch.begin();

            if (Gdx.input.isTouched()) {    // this is for BIRD movment
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                //how far the player can move this frame (distance = speed * time):
                float maxDistance = SPEED * Gdx.graphics.getDeltaTime();

//a vector from the player to the touch point:
                tmp.set(touchPos.x, touchPos.y).sub(player1Rectangle.x, player1Rectangle.y);

                if (tmp.len() <= maxDistance) {// close enough to just set the player at the target
                    player1Rectangle.x = touchPos.x;
                    player1Rectangle.y = touchPos.y;
                } else { // need to move along the vector toward the target
                    tmp.nor().scl(maxDistance); //reduce vector length to the distance traveled this frame
                    player1Rectangle.x += tmp.x; //move rectangle by the vector length
                    player1Rectangle.y += tmp.y;
                }

                if (touchPos.x > player1Rectangle.x) {
                    player1Rectangle.x += 5;
                    player2Rectangle.x += 5;    //testing until
                } else {
                    player1Rectangle.x -= 5;
                    player2Rectangle.x -= 5;
                }
                if (touchPos.y > player1Rectangle.y)
                    player1Rectangle.y += 5;
                else
                    player1Rectangle.y -= 5;

            }

            // System.out.println(Gdx.graphics.getHeight());
            batch.draw(backGroundTexture, -1000, -300);
            batch.draw(player1Texture, player1Rectangle.x - 32, player1Rectangle.y - 32);

            if (prevx != player1Rectangle.x || prevy !=player1Rectangle.y) {


                try {


                    birdout.writeFloat(player1Rectangle.x - 32);
                    birdout.writeFloat(player1Rectangle.y - 32);
                    birdout.writeInt(0);// the poop function
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {


                if (birdin.available() != 0) {

                    batch.draw(player2Texture, birdin.readFloat(), Gdx.graphics.getHeight() - Gdx.graphics.getHeight() + -300);// testing needs input from server
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            batch.end();



    }




    @Override
    public void dispose() {
        UI.dispose();
        batch.dispose();
        player1Texture.dispose();

        backGroundTexture.dispose();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

 //   @Override

