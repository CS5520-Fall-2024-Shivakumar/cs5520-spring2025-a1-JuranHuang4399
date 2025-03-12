package com.example.numad25sp_juranhuang;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PrimeActivity extends AppCompatActivity {
    private Button findPrimesButton, terminateButton;
    private CheckBox pacifierSwitch;
    private TextView currentNumberText, latestPrimeText;

    private Thread primeThread; // Worker thread
    private int currentNumber;     // Start from 3
    private int latestPrime; // Last found prime
    private boolean isRestarting; // Flag to track if restart search needed
    private boolean wasSearching; // Flag to track if search started
    private boolean isRunning; // Flag to track if program is already running

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Restore all basic UI states
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prime);

        // Initialize UI components
        findPrimesButton = findViewById(R.id.findPrimesButton);
        terminateButton = findViewById(R.id.terminateButton);
        pacifierSwitch = findViewById(R.id.pacifierSwitchCheckBox);
        currentNumberText = findViewById(R.id.currentNumberText);
        latestPrimeText = findViewById(R.id.latestPrimeText);

        // getOnBackPressedDispatcher acts as a event listener
        // .addCallback tells android we need to define the behavior
        // this = current activity
        // OnBackPressedCallback is the behavior we will define, true means it is now active
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Triggers only when the search is still running
                if (isRunning) {
                    // show dialog to confirm if user wants to exit now
                    new AlertDialog.Builder(PrimeActivity.this)
                            .setTitle("Exit Confirmation")
                            .setMessage("The search is still running, still exit?")
                            // Yes button, stop search and close activity when clicked
                            .setPositiveButton("Yes", (dialog, which) -> {
                                stopPrimeSearch();
                                finish();
                            })
                            // No button, do nothing when clicked
                            .setNegativeButton("No", ((dialog, which) -> dialog.dismiss()))
                            .show();
                } else {
                    finish();  // Close activity if no search is running

                }
            }
        });

        isRestarting = true;

        // If the storage is not empty, meaning there was data saved here
        // Restore all the values saved in this instance
        if (savedInstanceState != null) {
            // Restore saved values
            // values are used as default values, only apply when there was no data saved
            // Default value is replaced when the key finds a stored value
            wasSearching = savedInstanceState.getBoolean("wasSearching", false);
            currentNumber = savedInstanceState.getInt("currentNumber", 3);
            latestPrime = savedInstanceState.getInt("latestPrime", 0);
            pacifierSwitch.setChecked(savedInstanceState.getBoolean("pacifierState", false));
            isRestarting = savedInstanceState.getBoolean("isRestarting", true);

            latestPrimeText.setText("Latest Prime " + latestPrime);
            currentNumberText.setText("Current Number: " + currentNumber);

            if (wasSearching) {
                startPrimeSearch();
            }
        }

        // Set click listeners for buttons
        findPrimesButton.setOnClickListener(v -> startPrimeSearch());
        terminateButton.setOnClickListener(v -> stopPrimeSearch());
    }

    private boolean isPrime(int num) {
        if (num < 2) {
            return false;  // any number smaller than 2 is not a prime number
        }
        for (int i = 2; i < num; i++) {
            if (num % i == 0) {
                return false;  // can be divided by any number larger than 1, so not a prime
            }
        }
        return true;  // prime number confirmed
    }

    private void startPrimeSearch() {
        if (isRunning) {
            return;  // if program is already running, do not start another search
        }
        wasSearching = true; // Signals the search has started
        if (isRestarting) {
            currentNumber = 3;
            latestPrime = 0;
            latestPrimeText.setText("Latest Prime: None");
            currentNumberText.setText("Current Number: 3");  // Start search from 3
        }
        primeThread = new Thread(() -> {
            while(wasSearching) {
                if (isPrime(currentNumber)) {
                    latestPrime = currentNumber;

                    // Gain access to UI on the main thread and update
                    runOnUiThread(() -> latestPrimeText.setText("Latest Prime: " + latestPrime));
                }
                runOnUiThread(() -> currentNumberText.setText("Current Number: " + currentNumber));
                currentNumber += 2; // Increment the current number by 2 every loop (odd numbers only)
            }
        });
        isRunning = true;     // Signals that the program is now running
        primeThread.start();  // Start the worker thread
    }

    private void stopPrimeSearch() {
        wasSearching = false; // The search is now stopped
        isRunning = false;     // The program is now halted
        isRestarting = true; // program is now allow to start over the search
        if (primeThread != null) {
            primeThread.interrupt(); // Stop the worker thread
        }
    }

    // Save data before the activity is killed or rotated
    // Android can use this to restore data when activity was recreated
    @Override
    protected void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);  // save all Android built-in UI elements
        // Then save all current state
        outstate.putBoolean("wasSearching", wasSearching);  // key: "wasSearching", value: true or false
        outstate.putInt("currentNumber", currentNumber); // Key: "currentNumber", value: the actual value as number
        outstate.putInt("latestPrime", latestPrime);
        outstate.putBoolean("pacifierState", pacifierSwitch.isChecked());
        outstate.putBoolean("isRestarting", false);  // prevent resetting values
    }
}
