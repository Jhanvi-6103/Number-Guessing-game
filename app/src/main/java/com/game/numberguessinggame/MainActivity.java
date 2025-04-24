package com.game.numberguessinggame;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide; // Import Glide
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView timerText, instruction, attemptsText, resultMessage, gameTitle;
    private EditText userInput;
    private Button submitButton, resetButton;
    private ImageView animatedImageView;  // ImageView for the animated GIF
    private int numberToGuess;
    private int attempts;
    private long startTime; // Track the start time
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        timerText = findViewById(R.id.timerText);
        instruction = findViewById(R.id.instruction);
        attemptsText = findViewById(R.id.attempts);
        userInput = findViewById(R.id.userInput);
        submitButton = findViewById(R.id.submitButton);
        resetButton = findViewById(R.id.resetButton);
        resultMessage = findViewById(R.id.resultMessage);
        gameTitle = findViewById(R.id.gameTitle);  // Added TextView for game title

        // Fade-in animation for the game title
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(gameTitle, "alpha", 0f, 1f);
        fadeIn.setDuration(4000); // 1 second fade-in
        fadeIn.start();

        // Initialize ImageView for GIF
        animatedImageView = findViewById(R.id.animatedImageView);  // Add this line to initialize ImageView

        // Load the GIF into the ImageView using Glide
        Glide.with(this)
                .load(R.drawable.number_guessing)  // Replace with the name of your GIF file in res/drawable
                .into(animatedImageView);

        // Fade-in animation for the TextViews when the game starts
        Animation fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        timerText.startAnimation(fadeInAnim);
        instruction.startAnimation(fadeInAnim);

        // Start the game and timer
        resetGame();

        // Button animations
        submitButton.setOnClickListener(v -> {
            Animation scaleUp = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_up);
            submitButton.startAnimation(scaleUp);
            handleGuess();
        });

        resetButton.setOnClickListener(v -> {
            Animation scaleUp = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_up);
            resetButton.startAnimation(scaleUp);
            resetGame();
        });
    }

    private void handleGuess() {
        String userGuessString = userInput.getText().toString().trim();
        if (userGuessString.isEmpty()) {
            resultMessage.setText("❗ Please enter a number.");
            return;
        }

        int userGuess;
        try {
            userGuess = Integer.parseInt(userGuessString);
        } catch (NumberFormatException e) {
            resultMessage.setText("❗ Invalid input! Please enter a valid number.");
            return;
        }

        if (userGuess < 1 || userGuess > 100) {
            resultMessage.setText("⚠️ Enter a number between 1 and 100.");
            return;
        }

        attempts++;
        attemptsText.setText("Attempts: " + attempts);

        if (userGuess == numberToGuess) {
            resultMessage.setText("🎉 Congratulations! You guessed it right!");
            stopTimerAndShowTime();
        } else if (userGuess < numberToGuess) {
            resultMessage.setText("🔼 Try a higher number!");
        } else {
            resultMessage.setText("🔽 Try a lower number!");
        }
    }

    private void stopTimerAndShowTime() {
        // Stop the timer and calculate the time taken
        countDownTimer.cancel();

        long elapsedTime = System.currentTimeMillis() - startTime; // in ms
        long timeTakenInSeconds = elapsedTime / 1000;

        // Show success message
        resultMessage.setText("🎉 Congratulations! You guessed it in " + timeTakenInSeconds + " seconds!");

        // 🎉 Load celebration GIF
        Glide.with(this)
                .asGif()
                .load(R.drawable.celebratiom) // your GIF in drawable
                .into(animatedImageView);

        disableInput();
    }

    private void startTimer() {
        startTime = System.currentTimeMillis(); // Store the start time when the timer starts

        countDownTimer = new CountDownTimer(40000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time Left: ⏳" + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                timerText.setText("Time's up!");
                resultMessage.setText("⏰ You ran out of time! The number was " + numberToGuess + ".");
                disableInput();
            }
        };
        countDownTimer.start();
    }

    private void disableInput() {
        userInput.setEnabled(false);
        submitButton.setEnabled(false);
    }

    private void resetGame() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        numberToGuess = new Random().nextInt(100) + 1;
        attempts = 0;

        userInput.setEnabled(true);
        submitButton.setEnabled(true);
        userInput.setText("");
        attemptsText.setText("Attempts: 0");
        resultMessage.setText("");

        // Reload the initial GIF when the game restarts
        Glide.with(this)
                .load(R.drawable.number_guessing)  // This is the initial GIF file
                .into(animatedImageView);

        startTimer();
    }
}
