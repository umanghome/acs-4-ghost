package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private static final int MIN_WORD_LENGTH = 4;
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private int whoWentFirst;

    TextView text;
    TextView label;
    Button challenge;
    Button restart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new SimpleDictionary(inputStream);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }

        text = (TextView) findViewById(R.id.ghostText);
        label = (TextView) findViewById(R.id.gameStatus);
        challenge = (Button) findViewById(R.id.challenge);
        restart = (Button) findViewById(R.id.restart);

        // When user taps on challenge
        challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {

                // Get word
                String word = text.getText().toString();

                // If length is less than min_word_length, return
                if (word.length() < MIN_WORD_LENGTH) {
                    return;
                }

                // User wins
                if (dictionary.isWord(word)) {
                    // Boast
                    Toast.makeText(getApplicationContext(), "You won!", Toast.LENGTH_LONG).show();
                    label.setText("You won!");

                    return;
                }

                // Get bigger word
                String biggerWord = dictionary.getAnyWordStartingWith(word);

                // If biggerWord does not exist
                if (biggerWord == null) {
                    // Boast
                    Toast.makeText(getApplicationContext(), "You won!", Toast.LENGTH_LONG).show();
                    label.setText("You won!");

                    // Disable challenge button
                    challenge.setEnabled(false);

                    return;
                }
                // If biggerWord exists
                else {
                    // Boast
                    Toast.makeText(getApplicationContext(), "Computer won!", Toast.LENGTH_LONG).show();

                    // Display biggerWord
                    label.setText("Computer won! A word that can be formed is: " + biggerWord);

                    // Disable challenge button
                    challenge.setEnabled(false);
                    restart.setEnabled(true);
                }


            }
        });

        restart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View v) {
                onStart(null);
            }

        });

        // Restore state
        if (savedInstanceState != null) {
            text.setText(savedInstanceState.getString("text"));
            label.setText(savedInstanceState.getString("label"));
            userTurn = savedInstanceState.getBoolean("userTurn");
            whoWentFirst = savedInstanceState.getInt("whoWentFirst");
            challenge.setEnabled(savedInstanceState.getBoolean("challenge"));
            restart.setEnabled(savedInstanceState.getBoolean("restart"));
        } else {
            onStart(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();

        whoWentFirst = userTurn ? 1 : 0;

        text.setText("");

        challenge.setEnabled(true);
        restart.setEnabled(true);

        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        // Disable challenge button
        challenge.setEnabled(false);

        // Get word
        String word = text.getText().toString();

        // If word exists
        if (word.length() >= MIN_WORD_LENGTH && dictionary.isWord(word)) {
            // Boast
            Toast.makeText(this, "Computer won!", Toast.LENGTH_LONG).show();
            label.setText("Computer won!");

            // Disable challenge button
            challenge.setEnabled(false);

            return;
        }

        // Get bigger word
        String biggerWord = dictionary.getAnyWordStartingWith(word);

        // If biggerWord does not exist
        if (biggerWord == null) {
            // Boast
            Toast.makeText(this, "Computer won!", Toast.LENGTH_LONG).show();
            label.setText("Computer won!");

            // Disable challenge button
            challenge.setEnabled(false);

            return;
        }
        // If biggerWord exists
        else {

            // Copy biggerWord
            String biggerWordCopy = biggerWord;

            // Get a good word
            biggerWord = dictionary.getGoodWordStartingWith(word, whoWentFirst);

            // If there is no good word, restore
            if (biggerWord == null) {
                biggerWord = biggerWordCopy;
            }

            // Create new word
            word += biggerWord.charAt(word.length());

            // Set new word
            text.setText(word);
        }

        // Enable challenge button
        challenge.setEnabled(true);

        // User's turn
        userTurn = true;
        label.setText(USER_TURN);
    }

    @Override
    public boolean onKeyUp (int keyCode, KeyEvent keyEvent) {

        // If key pressed is between 'a' and 'z'
        if (keyCode >= 29 && keyCode <= 54) {

            // Get keyCode from offset 0
            int kc = keyCode - 29;

            // Add offset to 'a'
            char c = (char) ((int) 'a' + kc);

            // Get game's text
            String gameText = text.getText().toString();

            // Add character
            gameText += c;

            // Set string
            text.setText(gameText);

            computerTurn();

        } else {
            return super.onKeyUp(keyCode, keyEvent);
        }

        return true;

    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {

        // Save text
        savedInstanceState.putString("text", text.getText().toString());
        savedInstanceState.putString("label", label.getText().toString());
        savedInstanceState.putBoolean("userTurn", userTurn);
        savedInstanceState.putInt("whoWentFirst", whoWentFirst);
        savedInstanceState.putBoolean("challenge", challenge.isEnabled());
        savedInstanceState.putBoolean("restart", restart.isEnabled());

        super.onSaveInstanceState(savedInstanceState);
    }


}
