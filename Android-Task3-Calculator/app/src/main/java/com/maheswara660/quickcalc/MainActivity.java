package com.maheswara660.quickcalc;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvDisplay;
    private StringBuilder currentInput = new StringBuilder();
    private boolean isResultShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View mainLayout = findViewById(R.id.mainLayout);
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                int basePadding = (int) (16 * getResources().getDisplayMetrics().density);
                v.setPadding(
                        systemBars.left + basePadding,
                        systemBars.top + basePadding,
                        systemBars.right + basePadding,
                        systemBars.bottom + basePadding
                );
                return insets;
            });
        }

        tvDisplay = findViewById(R.id.tvDisplay);

        setupButtonListeners();
    }

    private void setupButtonListeners() {
        int[] buttonIds = new int[] {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot, R.id.btnClear, R.id.btnBackspace, R.id.btnPercent,
                R.id.btnDivide, R.id.btnMultiply, R.id.btnMinus, R.id.btnPlus,
                R.id.btnEquals
        };

        for (int id : buttonIds) {
            View view = findViewById(id);
            if (view != null) {
                view.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnClear) {
            clearAll();
        } else if (id == R.id.btnBackspace) {
            handleBackspace();
        } else if (id == R.id.btnEquals) {
            calculateResult();
        } else if (id == R.id.btnDot) {
            handleDecimalPoint();
        } else if (id == R.id.btnPlus || id == R.id.btnMinus ||
                id == R.id.btnMultiply || id == R.id.btnDivide || id == R.id.btnPercent) {
            MaterialButton btn = (MaterialButton) v;
            handleOperator(btn.getText().toString());
        } else {
            MaterialButton btn = (MaterialButton) v;
            handleDigit(btn.getText().toString());
        }
    }

    private void handleDigit(String digit) {
        if (isResultShown) {
            currentInput.setLength(0);
            isResultShown = false;
        }

        // Prevent leading redundant zeroes like "00"
        if (currentInput.toString().equals("0")) {
            currentInput.setLength(0);
        }

        currentInput.append(digit);
        updateDisplay();
    }

    private void handleDecimalPoint() {
        if (isResultShown) {
            currentInput.setLength(0);
            isResultShown = false;
        }

        String expr = currentInput.toString();

        // If expression is empty or ends with an operator, start new number with "0."
        if (expr.isEmpty() || isOperator(getLastCharAsString(expr))) {
            currentInput.append("0.");
            updateDisplay();
            return;
        }

        // Extract last token to check for existing decimal point
        String lastNumberToken = getLastNumberToken(expr);
        if (!lastNumberToken.contains(".")) {
            currentInput.append(".");
            updateDisplay();
        }
    }

    private void handleOperator(String op) {
        if (currentInput.length() == 0) {
            // Allow starting with minus for negative number
            if (op.equals("−") || op.equals("-")) {
                currentInput.append(op);
                isResultShown = false;
                updateDisplay();
            }
            return;
        }

        String expr = currentInput.toString();
        String lastChar = getLastCharAsString(expr);

        // If user presses another operator right after an existing operator, replace it
        if (isOperator(lastChar)) {
            currentInput.setCharAt(currentInput.length() - 1, op.charAt(0));
        } else {
            currentInput.append(op);
        }

        isResultShown = false;
        updateDisplay();
    }

    private void handleBackspace() {
        if (currentInput.length() > 0) {
            currentInput.deleteCharAt(currentInput.length() - 1);
            isResultShown = false;
            updateDisplay();
        }
    }

    private void clearAll() {
        currentInput.setLength(0);
        isResultShown = false;
        updateDisplay();
    }

    private void updateDisplay() {
        if (currentInput.length() == 0) {
            tvDisplay.setText("0");
        } else {
            tvDisplay.setText(currentInput.toString());
        }
    }

    private void calculateResult() {
        if (currentInput.length() == 0) {
            return;
        }

        String expr = currentInput.toString();

        // If expression ends with an operator, strip trailing operator for evaluation
        while (expr.length() > 0 && isOperator(getLastCharAsString(expr))) {
            expr = expr.substring(0, expr.length() - 1);
        }

        if (TextUtils.isEmpty(expr)) {
            return;
        }

        try {
            double result = evaluateExpression(expr);

            if (Double.isNaN(result) || Double.isInfinite(result)) {
                Toast.makeText(this, R.string.err_divide_by_zero, Toast.LENGTH_SHORT).show();
                return;
            }

            String formattedResult = formatResult(result);
            tvDisplay.setText(formattedResult);

            currentInput.setLength(0);
            currentInput.append(formattedResult);
            isResultShown = true;

        } catch (ArithmeticException e) {
            Toast.makeText(this, R.string.err_divide_by_zero, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.err_invalid_expression, Toast.LENGTH_SHORT).show();
        }
    }

    private String formatResult(double val) {
        // Format to prevent scientific notation for reasonable values & strip .0 for
        // integers
        DecimalFormat format = new DecimalFormat("#.##########");
        String result = format.format(val);
        if (result.equals("-0")) {
            return "0";
        }
        return result;
    }

    private boolean isOperator(String c) {
        return c.equals("+") || c.equals("−") || c.equals("-") ||
                c.equals("×") || c.equals("*") || c.equals("÷") || c.equals("/") || c.equals("%");
    }

    private String getLastCharAsString(String str) {
        if (str == null || str.isEmpty())
            return "";
        return str.substring(str.length() - 1);
    }

    private String getLastNumberToken(String expr) {
        int lastOpIndex = -1;
        for (int i = expr.length() - 1; i >= 0; i--) {
            char ch = expr.charAt(i);
            if (ch == '+' || ch == '−' || ch == '-' || ch == '×' ||
                    ch == '*' || ch == '÷' || ch == '/' || ch == '%') {
                lastOpIndex = i;
                break;
            }
        }
        if (lastOpIndex == -1) {
            return expr;
        } else {
            return expr.substring(lastOpIndex + 1);
        }
    }

    /**
     * Expression Evaluator supporting numbers and basic operators +, −, ×, ÷, %
     */
    private double evaluateExpression(String expression) throws ArithmeticException {
        List<String> tokens = tokenize(expression);
        return parseAndCompute(tokens);
    }

    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder numberBuffer = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                numberBuffer.append(c);
            } else {
                if (numberBuffer.length() > 0) {
                    tokens.add(numberBuffer.toString());
                    numberBuffer.setLength(0);
                }

                // Check for unary minus at beginning or after operator
                if ((c == '−' || c == '-') && (tokens.isEmpty() || isOperator(tokens.get(tokens.size() - 1)))) {
                    numberBuffer.append('-');
                } else {
                    tokens.add(String.valueOf(c));
                }
            }
        }

        if (numberBuffer.length() > 0) {
            tokens.add(numberBuffer.toString());
        }

        return tokens;
    }

    private double parseAndCompute(List<String> tokens) throws ArithmeticException {
        if (tokens.isEmpty())
            return 0;

        // Process postfix percentages first: e.g. "50 %" -> "0.5"
        List<String> processedTokens = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.equals("%")) {
                if (!processedTokens.isEmpty()) {
                    int lastIdx = processedTokens.size() - 1;
                    try {
                        double val = Double.parseDouble(processedTokens.get(lastIdx));
                        processedTokens.set(lastIdx, String.valueOf(val / 100.0));
                    } catch (NumberFormatException e) {
                        processedTokens.add(token);
                    }
                }
            } else {
                processedTokens.add(token);
            }
        }

        // Shunting yard algorithm for operators + - * /
        Stack<Double> values = new Stack<>();
        Stack<String> ops = new Stack<>();

        for (int i = 0; i < processedTokens.size(); i++) {
            String token = processedTokens.get(i);

            try {
                double val = Double.parseDouble(token);
                values.push(val);
            } catch (NumberFormatException e) {
                // Token is an operator
                while (!ops.isEmpty() && hasPrecedence(token, ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(token);
            }
        }

        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        return values.isEmpty() ? 0 : values.pop();
    }

    private boolean hasPrecedence(String op1, String op2) {
        if ((op1.equals("×") || op1.equals("*") || op1.equals("÷") || op1.equals("/")) &&
                (op2.equals("+") || op2.equals("−") || op2.equals("-"))) {
            return false;
        }
        return true;
    }

    private double applyOp(String op, double b, double a) throws ArithmeticException {
        if (op.equals("+")) {
            return a + b;
        } else if (op.equals("−") || op.equals("-")) {
            return a - b;
        } else if (op.equals("×") || op.equals("*")) {
            return a * b;
        } else if (op.equals("÷") || op.equals("/")) {
            if (Math.abs(b) < 1e-12) {
                throw new ArithmeticException("Divide by zero");
            }
            return a / b;
        }
        return 0;
    }
}
