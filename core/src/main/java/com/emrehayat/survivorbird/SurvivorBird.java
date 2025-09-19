package com.emrehayat.survivorbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class SurvivorBird extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    //Texture bird;  // Artık tek resim yerine dizi kullanıyoruz
    //Texture enemy1;
    //Texture enemy2;
    //Texture enemy3;

    // Kuş animasyonu
    Texture[] birds;
    int flapState = 0;
    float flapTime = 0;

    // Düşman kuş animasyonu
    Texture[] enemyBirds;
    int enemyFlapState = 0;
    float enemyFlapTime = 0;

    float birdX = 0;
    float birdY = 0;
    int gameState = 0;
    float velocity = 0;
    float gravity = 0.1f;
    float enemyVelocity = 10;
    Random random;

    int score = 0;
    int scoredEnemy = 0;

    BitmapFont font;
    BitmapFont font2;

    Circle birdCircle;

    ShapeRenderer shapeRenderer;

    int numberOfEnemies = 4;
    float [] enemyX = new float[numberOfEnemies];
    float [] enemyOffset = new float[numberOfEnemies];
    float [] enemyOffset2 = new float[numberOfEnemies];
    float [] enemyOffset3 = new float[numberOfEnemies];
    float distance = 0;

    Circle [] enemyCircles;
    Circle [] enemyCircles2;
    Circle [] enemyCircles3;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("background.png");

        // Kuş resimleri
        birds = new Texture[2];
        birds[0] = new Texture("bluebird.png");
        birds[1] = new Texture("bluebird2.png");

        // Düşman kuş resimleri
        enemyBirds = new Texture[2];
        enemyBirds[0] = new Texture("enemy1.png");
        enemyBirds[1] = new Texture("enemy2.png");

        distance = Gdx.graphics.getWidth() / 2;
        random = new Random();

        birdX = Gdx.graphics.getWidth() / 2 - birds[0].getHeight() / 2;
        birdY = Gdx.graphics.getHeight() / 3;

        shapeRenderer = new ShapeRenderer();

        birdCircle = new Circle();
        enemyCircles = new Circle[numberOfEnemies];
        enemyCircles2 = new Circle[numberOfEnemies];
        enemyCircles3 = new Circle[numberOfEnemies];

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        font2 = new BitmapFont();
        font2.setColor(Color.WHITE);
        font2.getData().setScale(12);

        for (int i = 0; i < numberOfEnemies; i++) {
            enemyOffset[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
            enemyOffset2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
            enemyOffset3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);

            enemyX[i] = Gdx.graphics.getWidth() - birds[0].getWidth() / 2 + i * distance;

            enemyCircles[i] = new Circle();
            enemyCircles2[i] = new Circle();
            enemyCircles3[i] = new Circle();
        }
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {
            // Ana kuş animasyonu
            flapTime += Gdx.graphics.getDeltaTime();
            if (flapTime > 0.2f) {
                flapState = 1 - flapState;
                flapTime = 0;
            }

            // Düşman animasyonu
            enemyFlapTime += Gdx.graphics.getDeltaTime();
            if (enemyFlapTime > 0.2f) {
                enemyFlapState = 1 - enemyFlapState;
                enemyFlapTime = 0;
            }

            if (enemyX[scoredEnemy] < Gdx.graphics.getWidth() / 2 - birds[0].getHeight() / 2) {
                score++;

                if (score % 10 == 0) {
                    enemyVelocity += 2; // her 10 puanda hız 2 artar
                }

                if (scoredEnemy < numberOfEnemies - 1) {
                    scoredEnemy++;
                } else {
                    scoredEnemy = 0;
                }
            }

            if (Gdx.input.justTouched()) {
                velocity = -7;
            }

            for (int i = 0; i < numberOfEnemies; i++) {
                if (enemyX[i] < 0) {
                    enemyX[i] = enemyX[i] + numberOfEnemies * distance;

                    enemyOffset[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
                    enemyOffset2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
                    enemyOffset3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
                } else {
                    enemyX[i] = enemyX[i] - enemyVelocity;
                }

                // Düşman kuşların animasyonlu çizimi
                batch.draw(enemyBirds[enemyFlapState], enemyX[i], Gdx.graphics.getHeight() / 2 + enemyOffset[i],
                    Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);
                batch.draw(enemyBirds[enemyFlapState], enemyX[i], Gdx.graphics.getHeight() / 2 + enemyOffset2[i],
                    Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);
                batch.draw(enemyBirds[enemyFlapState], enemyX[i], Gdx.graphics.getHeight() / 2 + enemyOffset3[i],
                    Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);

                enemyCircles[i] = new Circle(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffset[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 40);
                enemyCircles2[i] = new Circle(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffset2[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 40);
                enemyCircles3[i] = new Circle(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffset3[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 40);
            }

            if (birdY > 0) {
                if (birdY > Gdx.graphics.getHeight()) {
                    gameState = 2;
                }

                velocity = velocity + gravity;
                birdY = birdY - velocity;
            } else {
                gameState = 2;
            }

        } else if (gameState == 0){
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 2) {
            font2.draw(batch, "Game Over! Tap To Play Again!", 300, Gdx.graphics.getHeight() / 2);

            if (Gdx.input.justTouched()) {
                gameState = 1;

                birdY = Gdx.graphics.getHeight() / 3;

                for (int i = 0; i < numberOfEnemies; i++) {
                    enemyOffset[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
                    enemyOffset2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
                    enemyOffset3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);

                    enemyX[i] = Gdx.graphics.getWidth() - birds[0].getWidth() / 2 + i * distance;

                    enemyCircles[i] = new Circle();
                    enemyCircles2[i] = new Circle();
                    enemyCircles3[i] = new Circle();
                }

                velocity = 0;
                scoredEnemy = 0;
                score = 0;
            }
        }

        // Ana kuşun animasyonlu çizimi
        batch.draw(birds[flapState], birdX, birdY,
            Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);

        font.draw(batch, String.valueOf(score), 100, 200);

        batch.end();

        birdCircle.set(birdX + (Gdx.graphics.getWidth() / 15) / 2, birdY + (Gdx.graphics.getHeight() / 10) /2, Gdx.graphics.getWidth() / 40);

        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.setColor(Color.BLACK);
        //shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

        for (int i = 0; i < numberOfEnemies; i++) {
            //shapeRenderer.circle(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffset[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 40);
            //shapeRenderer.circle(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffset2[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 40);
            //shapeRenderer.circle(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffset3[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 40);

            if (Intersector.overlaps(birdCircle, enemyCircles[i]) ||
                Intersector.overlaps(birdCircle, enemyCircles2[i]) ||
                Intersector.overlaps(birdCircle, enemyCircles3[i])) {
                gameState = 2;
            }
        }
        //shapeRenderer.end();
    }

    @Override
    public void dispose() {
        background.dispose();

        birds[0].dispose();
        birds[1].dispose();

        enemyBirds[0].dispose();
        enemyBirds[1].dispose();

        font.dispose();
        font2.dispose();

        shapeRenderer.dispose();
    }
}
