package com.emrehayat.survivorbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.Preferences;

import java.util.Random;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class SurvivorBird extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture background2;

    // Kuş animasyonu
    Texture[] birds;
    int flapState = 0;
    float flapTime = 0;

    // Düşman kuş animasyonu
    Texture[] enemyBirds;
    Texture[] grumpyBeeBirds;
    Texture[] redEnemyBirds;
    Texture[] blueBatBirds;
    Texture[] deathsHeadBirds;
    Texture[] blackEnemyBirds;

    int enemyFlapState = 0;
    float enemyFlapTime = 0;

    float birdX = 0;
    float birdY = 0;
    int gameState = 0;

    float velocity = 0;
    float gravity = 0;
    float enemyVelocity = 0;
    float initialEnemyVelocity = 0;

    private static final float jumpVelocityFactor = 0.6f;
    private static final float gravityAccelFactor = 1.5f;
    private static final float enemyVelocityBaseFactor = 0.3f;
    private static final float enemySpeedIncrementFactor = 0.02f;

    Random random;

    int score = 0;
    int scoredEnemy = 0;

    BitmapFont font;
    BitmapFont font2;

    Circle birdCircle;

    ShapeRenderer shapeRenderer;

    int highScore = 0;
    Preferences preferences;

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
        background2 = new Texture("background2.png");

        // Kuş resimleri
        birds = new Texture[2];
        birds[0] = new Texture("bluebird.png");
        birds[1] = new Texture("bluebird2.png");

        // Düşman kuş resimleri
        enemyBirds = new Texture[2];
        enemyBirds[0] = new Texture("enemy1.png");
        enemyBirds[1] = new Texture("enemy2.png");

        grumpyBeeBirds = new Texture[2];
        grumpyBeeBirds[0] = new Texture("grumpybee1.png");
        grumpyBeeBirds[1] = new Texture("grumpybee2.png");

        redEnemyBirds = new Texture[2];
        redEnemyBirds[0] = new Texture("redenemy1.png");
        redEnemyBirds[1] = new Texture("redenemy2.png");

        blueBatBirds = new Texture[2];
        blueBatBirds[0] = new Texture("bluebat1.png");
        blueBatBirds[1] = new Texture("bluebat2.png");

        deathsHeadBirds = new Texture[2];
        deathsHeadBirds[0] = new Texture("deathshead1.png");
        deathsHeadBirds[1] = new Texture("deathshead2.png");

        blackEnemyBirds = new Texture[2];
        blackEnemyBirds[0] = new Texture("blackenemy1.png");
        blackEnemyBirds[1] = new Texture("blackenemy2.png");

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

        font2 = new BitmapFont();
        font2.setColor(Color.WHITE);

        float fontScale = Gdx.graphics.getWidth() / 300f;
        font.getData().setScale(fontScale);
        font2.getData().setScale(fontScale * 1.2f);

        gravity = Gdx.graphics.getHeight() * gravityAccelFactor;
        initialEnemyVelocity = Gdx.graphics.getWidth() * enemyVelocityBaseFactor;
        enemyVelocity = initialEnemyVelocity;


        for (int i = 0; i < numberOfEnemies; i++) {
            enemyOffset[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
            enemyOffset2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
            enemyOffset3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);

            enemyX[i] = Gdx.graphics.getWidth() - birds[0].getWidth() / 2 + i * distance;

            enemyCircles[i] = new Circle();
            enemyCircles2[i] = new Circle();
            enemyCircles3[i] = new Circle();
        }

        preferences = Gdx.app.getPreferences("SurvivorBirdHighScore");
        highScore = preferences.getInteger("highScore", 0); // varsayılan 0
    }

    @Override
    public void render() {
        if (score >= 60) {
            batch.begin();
            batch.draw(background2, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else {
            batch.begin();
            batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

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
                    enemyVelocity += Gdx.graphics.getWidth() * enemySpeedIncrementFactor;
                }

                if (scoredEnemy < numberOfEnemies - 1) {
                    scoredEnemy++;
                } else {
                    scoredEnemy = 0;
                }

                // Rekor güncelleme kontrolü
                if (score > highScore) {
                    highScore = score;
                    preferences.putInteger("highScore", highScore);
                    preferences.flush(); // kalıcı olarak kaydeder
                }
            }

            if (Gdx.input.justTouched()) {
                velocity = -Gdx.graphics.getHeight() * jumpVelocityFactor;
            }

            // Düşman Değişimi
            Texture[] currentEnemyBirds;
            if (score >= 100) {
                currentEnemyBirds = blackEnemyBirds;
            } else if (score >= 80) {
                currentEnemyBirds = deathsHeadBirds;
            } else if (score >= 60) {
                currentEnemyBirds = blueBatBirds;
            } else if (score >= 40) {
                currentEnemyBirds = redEnemyBirds;
            } else if (score >= 20) {
                currentEnemyBirds = grumpyBeeBirds;
            } else {
                currentEnemyBirds = enemyBirds;
            }

            float deltaTime = Gdx.graphics.getDeltaTime();

            for (int i = 0; i < numberOfEnemies; i++) {
                if (enemyX[i] < -currentEnemyBirds[0].getWidth()) { // Düşman ekran dışına çıktıysa
                    enemyX[i] = enemyX[i] + numberOfEnemies * distance;

                    enemyOffset[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
                    enemyOffset2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
                    enemyOffset3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
                } else {
                    enemyX[i] -= enemyVelocity * deltaTime;

                }

                // Düşman kuşların animasyonlu çizimi
                batch.draw(currentEnemyBirds[enemyFlapState], enemyX[i], Gdx.graphics.getHeight() / 2 + enemyOffset[i],
                    Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);
                batch.draw(currentEnemyBirds[enemyFlapState], enemyX[i], Gdx.graphics.getHeight() / 2 + enemyOffset2[i],
                    Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);
                batch.draw(currentEnemyBirds[enemyFlapState], enemyX[i], Gdx.graphics.getHeight() / 2 + enemyOffset3[i],
                    Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);

                enemyCircles[i] = new Circle(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffset[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 40);
                enemyCircles2[i] = new Circle(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffset2[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 40);
                enemyCircles3[i] = new Circle(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffset3[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 40);
            }

            if (birdY > 0) {
                if (birdY > Gdx.graphics.getHeight()) {
                    gameState = 2;
                }

                velocity += gravity * deltaTime;
                birdY -= velocity * deltaTime;

            } else {
                gameState = 2;
            }

        } else if (gameState == 0){
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 2) {
            String gameOverText = "Game Over! Tap To Play Again!";
            GlyphLayout layout = new GlyphLayout(font, gameOverText);
            float x = (Gdx.graphics.getWidth() - layout.width) / 2;
            float y = Gdx.graphics.getHeight() / 2;

            font.draw(batch, gameOverText, x, y);

            font.draw(batch, "Record: " + highScore, Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 3.5f);

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
                // Başlangıç hızına geri dön. Bu değer create() içinde zaten ölçeklenmiştir.
                enemyVelocity = initialEnemyVelocity;
            }
        }

        // Ana kuşun animasyonlu çizimi
        batch.draw(birds[flapState], birdX, birdY,
            Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);

        font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 6);

        batch.end();

        birdCircle.set(birdX + (Gdx.graphics.getWidth() / 15) / 2, birdY + (Gdx.graphics.getHeight() / 10) /2, Gdx.graphics.getWidth() / 40);

        for (int i = 0; i < numberOfEnemies; i++) {
            if (Intersector.overlaps(birdCircle, enemyCircles[i]) ||
                Intersector.overlaps(birdCircle, enemyCircles2[i]) ||
                Intersector.overlaps(birdCircle, enemyCircles3[i])) {
                gameState = 2;
            }
        }
    }

    @Override
    public void dispose() {
        background.dispose();
        background2.dispose();

        birds[0].dispose();
        birds[1].dispose();

        enemyBirds[0].dispose();
        enemyBirds[1].dispose();

        grumpyBeeBirds[0].dispose();
        grumpyBeeBirds[1].dispose();

        redEnemyBirds[0].dispose();
        redEnemyBirds[1].dispose();

        blueBatBirds[0].dispose();
        blueBatBirds[1].dispose();

        deathsHeadBirds[0].dispose();
        deathsHeadBirds[1].dispose();

        blackEnemyBirds[0].dispose();
        blackEnemyBirds[1].dispose();

        font.dispose();
        font2.dispose();

        shapeRenderer.dispose();
    }
}
