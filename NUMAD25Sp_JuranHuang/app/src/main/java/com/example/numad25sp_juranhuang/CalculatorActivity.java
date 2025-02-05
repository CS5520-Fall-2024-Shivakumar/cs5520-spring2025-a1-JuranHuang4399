package com.example.numad25sp_juranhuang;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CalculatorActivity extends AppCompatActivity {

    // helper method used to set numbers on listener
    private void setUpNumberButtons(Button button, TextView calcTextView, String number) {
        button.setOnClickListener(v -> {
            String currentText = calcTextView.getText().toString();
            if (currentText.equals("CALC") || currentText.equals("ERROR")) {
                calcTextView.setText(number);
            } else {
                calcTextView.setText(currentText + number);
            }
        });
    }
    // helper method used to set operators on listener
    private void setUpOperatorButtons(Button button, TextView calcTextView, String operator) {
        button.setOnClickListener(v -> {
            String currentText = calcTextView.getText().toString();
            if (currentText.equals("CALC") || currentText.equals("ERROR") || currentText.isEmpty()) {
                return; // Do nothing when operator tries go to the front and when there is an error
            }
            else if (!currentText.isEmpty() && "+-".contains(currentText.substring(currentText.length()-1))) {
                return; // Do nothing when there is already an operator in the previous index, to prevent multiple operators exist next to each other
            } else {
                calcTextView.setText(currentText + operator);
            }
        });
    }
    // helper method used to set delete button on listener
    private void setUpDeleteButtons(Button button, TextView calcTextView) {
        button.setOnClickListener(v -> {
            String currentText = calcTextView.getText().toString();
            if (!currentText.isEmpty()) {
                calcTextView.setText(currentText.substring(0, currentText.length() - 1));
            }
        });
    }
    // helper method used to set equals button on listener
    private void setUpEqualsButtons(Button button, TextView calcTextView){
        button.setOnClickListener(v -> {
            String currentText = calcTextView.getText().toString();
            String result = evaluateExpression(currentText);
            calcTextView.setText(result);
        });
    }
    // helper method used to evaluate expression
    private String evaluateExpression (String expression) {
        try {
            // expression ends with operators should not be evaluated
            if (expression.endsWith("+") || expression.endsWith("-")) {
                return expression; // stay unchanged
            }
            // split all numbers into separate strings
            String [] numbers = expression.split("[-+]");
            // split all operators into separate strings
            String [] operators = expression.split("[0-9]+");
            // start with the first number
            double result = Double.parseDouble(numbers[0]);

            int numIndex = 0;
            // split() creates a empty first element for operators since number is always at the front in expression
            // start from 1 to skip the empty element
            for (int i = 1; i < operators.length; i++) {
                double nextNum = Double.parseDouble(numbers[++numIndex]);
                result =  ((operators[i].equals("+")) ? (result + nextNum) : (result - nextNum));

            }
            // type cast back to Integer when returned
            return String.valueOf((int)result);
        } catch (Exception e) {
            return "Error"; // In case of invalid input
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView calcTextView = findViewById(R.id.calcTextView);
        // set all number buttons on listener
        setUpNumberButtons(findViewById(R.id.buttonOne), calcTextView, "1");
        setUpNumberButtons(findViewById(R.id.buttonTwo), calcTextView, "2");
        setUpNumberButtons(findViewById(R.id.buttonThree), calcTextView, "3");
        setUpNumberButtons(findViewById(R.id.buttonFour), calcTextView, "4");
        setUpNumberButtons(findViewById(R.id.buttonFive), calcTextView, "5");
        setUpNumberButtons(findViewById(R.id.buttonSix), calcTextView, "6");
        setUpNumberButtons(findViewById(R.id.buttonSeven), calcTextView, "7");
        setUpNumberButtons(findViewById(R.id.buttonEight), calcTextView, "8");
        setUpNumberButtons(findViewById(R.id.buttonNine), calcTextView, "9");
        setUpNumberButtons(findViewById(R.id.buttonZero), calcTextView, "0");
        // set all operator buttons on listener
        setUpOperatorButtons(findViewById(R.id.buttonPlus), calcTextView, "+");
        setUpOperatorButtons(findViewById(R.id.buttonMinus), calcTextView, "-");
        // set delete button on listener
        setUpDeleteButtons(findViewById(R.id.buttonRemove), calcTextView);
        // set equals button on listener
        setUpEqualsButtons(findViewById(R.id.buttonEquals), calcTextView);
    }
}