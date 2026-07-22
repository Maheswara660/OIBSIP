package com.maheswara660.uniconv;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private View rootView;
    private EditText etInput;
    private Spinner spinnerCategory;
    private Spinner spinnerSourceUnit;
    private Spinner spinnerTargetUnit;
    private Button btnConvert;
    private ImageButton btnSwap;
    private TextView tvResult;

    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootView = findViewById(R.id.rootView);
        etInput = findViewById(R.id.etInput);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerSourceUnit = findViewById(R.id.spinnerSourceUnit);
        spinnerTargetUnit = findViewById(R.id.spinnerTargetUnit);
        btnConvert = findViewById(R.id.btnConvert);
        btnSwap = findViewById(R.id.btnSwap);
        tvResult = findViewById(R.id.tvResult);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            Insets navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(
                    v.getPaddingLeft(),
                    statusBarInsets.top + 16,
                    v.getPaddingRight(),
                    navBarInsets.bottom + 16);
            return insets;
        });

        decimalFormat = new DecimalFormat("#.######");

        setupCategorySpinner();

        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performConversion();
            }
        });

        btnSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapUnits();
            }
        });
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories_array,
                R.layout.spinner_item);
        categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUnitSpinners(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void updateUnitSpinners(int categoryIndex) {
        int arrayResId;
        switch (categoryIndex) {
            case 0: // Length
                arrayResId = R.array.length_units_array;
                break;
            case 1: // Weight
                arrayResId = R.array.weight_units_array;
                break;
            case 2: // Temperature
                arrayResId = R.array.temperature_units_array;
                break;
            default:
                arrayResId = R.array.length_units_array;
                break;
        }

        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                this,
                arrayResId,
                R.layout.spinner_item);
        unitAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinnerSourceUnit.setAdapter(unitAdapter);
        spinnerTargetUnit.setAdapter(unitAdapter);

        if (unitAdapter.getCount() > 1) {
            spinnerTargetUnit.setSelection(1);
        }
    }

    private void swapUnits() {
        int sourcePos = spinnerSourceUnit.getSelectedItemPosition();
        int targetPos = spinnerTargetUnit.getSelectedItemPosition();

        spinnerSourceUnit.setSelection(targetPos);
        spinnerTargetUnit.setSelection(sourcePos);

        if (!TextUtils.isEmpty(etInput.getText().toString().trim())) {
            performConversion();
        }
    }

    private void performConversion() {
        String inputText = etInput.getText().toString().trim();

        // 1. Input Validation: Check empty input
        if (TextUtils.isEmpty(inputText)) {
            Toast.makeText(this, getString(R.string.toast_invalid_input), Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Input Validation: Check valid double format
        double inputValue;
        try {
            inputValue = Double.parseDouble(inputText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.toast_invalid_input), Toast.LENGTH_SHORT).show();
            return;
        }

        int categoryIndex = spinnerCategory.getSelectedItemPosition();
        String sourceUnit = spinnerSourceUnit.getSelectedItem().toString();
        String targetUnit = spinnerTargetUnit.getSelectedItem().toString();

        // 3. Category specific physical domain validation
        if (categoryIndex == 0 || categoryIndex == 1) { // Length or Weight
            if (inputValue < 0) {
                Toast.makeText(this, getString(R.string.toast_negative_value), Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (categoryIndex == 2) { // Temperature
            if ((sourceUnit.equals("Celsius") && inputValue < -273.15) ||
                    (sourceUnit.equals("Fahrenheit") && inputValue < -459.67) ||
                    (sourceUnit.equals("Kelvin") && inputValue < 0)) {
                Toast.makeText(this, getString(R.string.toast_invalid_temperature), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        double result;
        switch (categoryIndex) {
            case 0:
                result = convertLength(inputValue, sourceUnit, targetUnit);
                break;
            case 1:
                result = convertWeight(inputValue, sourceUnit, targetUnit);
                break;
            case 2:
                result = convertTemperature(inputValue, sourceUnit, targetUnit);
                break;
            default:
                result = inputValue;
                break;
        }

        String formattedInput = decimalFormat.format(inputValue);
        String formattedResult = decimalFormat.format(result);

        String resultDisplayText = formattedInput + " " + sourceUnit + " = " + formattedResult + " " + targetUnit;
        tvResult.setText(resultDisplayText);
    }

    // Length Conversion (Base Unit: Meters)
    private double convertLength(double value, String from, String to) {
        double meters;
        switch (from) {
            case "cm":
                meters = value / 100.0;
                break;
            case "inch":
                meters = value * 0.0254;
                break;
            case "m":
            default:
                meters = value;
                break;
        }

        switch (to) {
            case "cm":
                return meters * 100.0;
            case "inch":
                return meters / 0.0254;
            case "m":
            default:
                return meters;
        }
    }

    // Weight Conversion (Base Unit: Kilograms)
    private double convertWeight(double value, String from, String to) {
        double kg;
        switch (from) {
            case "g":
                kg = value / 1000.0;
                break;
            case "pound":
                kg = value * 0.45359237;
                break;
            case "kg":
            default:
                kg = value;
                break;
        }

        switch (to) {
            case "g":
                return kg * 1000.0;
            case "pound":
                return kg / 0.45359237;
            case "kg":
            default:
                return kg;
        }
    }

    // Temperature Conversion (Celsius ↔ Fahrenheit ↔ Kelvin)
    private double convertTemperature(double value, String from, String to) {
        double celsius;
        switch (from) {
            case "Fahrenheit":
                celsius = (value - 32.0) * 5.0 / 9.0;
                break;
            case "Kelvin":
                celsius = value - 273.15;
                break;
            case "Celsius":
            default:
                celsius = value;
                break;
        }

        switch (to) {
            case "Fahrenheit":
                return (celsius * 9.0 / 5.0) + 32.0;
            case "Kelvin":
                return celsius + 273.15;
            case "Celsius":
            default:
                return celsius;
        }
    }
}
