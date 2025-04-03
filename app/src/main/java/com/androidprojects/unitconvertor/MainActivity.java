package com.androidprojects.unitconvertor;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit Converter Application
 * This application allows users to convert between different units of measurement.
 * It supports conversions for Length, Weight, and Temperature.
 * The user can select the conversion type, input value, and choose the units for conversion.
 * The application performs the conversion and displays the result.
 * It also includes input validation to ensure the user enters valid data.
 * The conversion logic is implemented in separate methods for each type of conversion.
 * The application uses Android UI components such as Spinners, EditText, Button, and TextView.
 * The conversion results are formatted to 4 decimal places for better readability.
 */

public class MainActivity extends AppCompatActivity {

    // UI Components eg Spinner for selecting conversion type, EditText for input value, Button for conversion action, TextView for displaying result
    private Spinner spinnerConversionType;
    private Spinner spinnerFromUnit;
    private Spinner spinnerToUnit;
    private EditText etInputValue;
    private Button btnConvert;
    private TextView tvConversionResult;

    // Conversion Types 
    private final String[] conversionTypes = {"Length", "Weight", "Temperature"};

    // Unit Arrays for different conversion types
    private final String[] lengthUnits = {"Inch", "Foot", "Yard", "Mile", "Centimeter", "Kilometer"};
    private final String[] weightUnits = {"Pound", "Ounce", "Ton", "Kilogram", "Gram"};
    private final String[] temperatureUnits = {"Celsius", "Fahrenheit", "Kelvin"};

    // Current Unit Arrays for selected conversion type
    // These arrays will be updated based on the selected conversion type
    private String[] currentFromUnits;
    private String[] currentToUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();

        // Setup Spinner for conversion types
        setupConversionTypeSpinner();

        // Setup button click listener
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    performConversion();
                }
            }
        });
    }

    private void initializeComponents() {
        spinnerConversionType = findViewById(R.id.spinnerConversionType);
        spinnerFromUnit = findViewById(R.id.spinnerFromUnit);
        spinnerToUnit = findViewById(R.id.spinnerToUnit);
        etInputValue = findViewById(R.id.etInputValue);
        btnConvert = findViewById(R.id.btnConvert);
        tvConversionResult = findViewById(R.id.tvConversionResult);
    }

    private void setupConversionTypeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, conversionTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConversionType.setAdapter(adapter);

        spinnerConversionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = conversionTypes[position];
                updateUnitSpinners(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void updateUnitSpinners(String conversionType) {
        switch (conversionType) {
            case "Length":
                currentFromUnits = lengthUnits;
                currentToUnits = lengthUnits;
                break;
            case "Weight":
                currentFromUnits = weightUnits;
                currentToUnits = weightUnits;
                break;
            case "Temperature":
                currentFromUnits = temperatureUnits;
                currentToUnits = temperatureUnits;
                break;
            default:
                // Default to length units if something goes wrong
                currentFromUnits = lengthUnits;
                currentToUnits = lengthUnits;
                Toast.makeText(this, "Unknown conversion type. Defaulting to Length.", Toast.LENGTH_SHORT).show();
                break;
        }

        // Setup From Unit Spinner
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currentFromUnits);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFromUnit.setAdapter(fromAdapter);

        // Setup To Unit Spinner
        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currentToUnits);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerToUnit.setAdapter(toAdapter);

        // Ensure the "To Unit" spinner starts with a different unit than the "From Unit"
        spinnerFromUnit.setSelection(0); // Set the first item as default for "From Unit"
        if (currentToUnits.length > 1) {
            spinnerToUnit.setSelection(1); // Set the second item as default for "To Unit"
        }

        // Clear previous result
        tvConversionResult.setText("0");
        etInputValue.setText("");
    }

    private boolean validateInput() {
        // Check if input field is empty
        if (TextUtils.isEmpty(etInputValue.getText())) {
            Toast.makeText(this, "Please enter a value to convert", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if input is a valid number
        try {
            Double.parseDouble(etInputValue.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Specific validation for temperature - Kelvin can't be negative
        String conversionType = conversionTypes[spinnerConversionType.getSelectedItemPosition()];
        if ("Temperature".equals(conversionType)) {
            String fromUnit = currentFromUnits[spinnerFromUnit.getSelectedItemPosition()];
            double value = Double.parseDouble(etInputValue.getText().toString());

            if ("Kelvin".equals(fromUnit) && value < 0) {
                Toast.makeText(this, "Kelvin cannot be negative", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Check if resulting temperature in Kelvin would be negative
            if ("Celsius".equals(fromUnit) && value < -273.15) {
                Toast.makeText(this, "Temperature below absolute zero (-273.15°C)", Toast.LENGTH_SHORT).show();
                return false;
            }

            if ("Fahrenheit".equals(fromUnit) && value < -459.67) {
                Toast.makeText(this, "Temperature below absolute zero (-459.67°F)", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void performConversion() {
        try {
            double inputValue = Double.parseDouble(etInputValue.getText().toString());
            String fromUnit = currentFromUnits[spinnerFromUnit.getSelectedItemPosition()];
            String toUnit = currentToUnits[spinnerToUnit.getSelectedItemPosition()];
            String conversionType = conversionTypes[spinnerConversionType.getSelectedItemPosition()];

            // Check if units are the same
            if (fromUnit.equals(toUnit)) {
                tvConversionResult.setText(new DecimalFormat("#.####").format(inputValue));
                Toast.makeText(this, "Source and destination units are the same", Toast.LENGTH_SHORT).show();
                return;
            }

            double result = 0;

            // Perform conversion based on conversion type
            switch (conversionType) {
                case "Length":
                    result = convertLength(inputValue, fromUnit, toUnit);
                    break;
                case "Weight":
                    result = convertWeight(inputValue, fromUnit, toUnit);
                    break;
                case "Temperature":
                    result = convertTemperature(inputValue, fromUnit, toUnit);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown conversion type: " + conversionType);
            }

            // Format the result to 4 decimal places
            DecimalFormat df = new DecimalFormat("#.####");
            tvConversionResult.setText(df.format(result));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input. Please enter a valid number.", Toast.LENGTH_SHORT).show();
            tvConversionResult.setText("Error");
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            tvConversionResult.setText("Error");
        } catch (Exception e) {
            Toast.makeText(this, "Error during conversion: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            tvConversionResult.setText("Error");
        }
    }

    private double convertLength(double value, String fromUnit, String toUnit) {
        // Convert everything to centimeters first, then to target unit
        double valueInCm = 0;

        // Convert from unit to cm
        switch (fromUnit) {
            case "Inch":
                valueInCm = value * 2.54;
                break;
            case "Foot":
                valueInCm = value * 30.48;
                break;
            case "Yard":
                valueInCm = value * 91.44;
                break;
            case "Mile":
                valueInCm = value * 160934;
                break;
            case "Centimeter":
                valueInCm = value;
                break;
            case "Kilometer":
                valueInCm = value * 100000;
                break;
            default:
                throw new IllegalArgumentException("Unknown length unit: " + fromUnit);
        }

        // Convert from cm to target unit
        switch (toUnit) {
            case "Inch":
                return valueInCm / 2.54;
            case "Foot":
                return valueInCm / 30.48;
            case "Yard":
                return valueInCm / 91.44;
            case "Mile":
                return valueInCm / 160934;
            case "Centimeter":
                return valueInCm;
            case "Kilometer":
                return valueInCm / 100000;
            default:
                throw new IllegalArgumentException("Unknown length unit: " + toUnit);
        }
    }

    private double convertWeight(double value, String fromUnit, String toUnit) {
        // Convert everything to grams first, then to target unit
        double valueInGrams = 0;

        // Convert from unit to grams
        switch (fromUnit) {
            case "Pound":
                valueInGrams = value * 453.592;
                break;
            case "Ounce":
                valueInGrams = value * 28.3495;
                break;
            case "Ton":
                valueInGrams = value * 907185;
                break;
            case "Kilogram":
                valueInGrams = value * 1000;
                break;
            case "Gram":
                valueInGrams = value;
                break;
            default:
                throw new IllegalArgumentException("Unknown weight unit: " + fromUnit);
        }

        // Convert from grams to target unit
        switch (toUnit) {
            case "Pound":
                return valueInGrams / 453.592;
            case "Ounce":
                return valueInGrams / 28.3495;
            case "Ton":
                return valueInGrams / 907185;
            case "Kilogram":
                return valueInGrams / 1000;
            case "Gram":
                return valueInGrams;
            default:
                throw new IllegalArgumentException("Unknown weight unit: " + toUnit);
        }
    }

    private double convertTemperature(double value, String fromUnit, String toUnit) {
        // First convert to Celsius
        double valueInCelsius = 0;

        switch (fromUnit) {
            case "Celsius":
                valueInCelsius = value;
                break;
            case "Fahrenheit":
                valueInCelsius = (value - 32) / 1.8;
                break;
            case "Kelvin":
                valueInCelsius = value - 273.15;
                break;
            default:
                throw new IllegalArgumentException("Unknown temperature unit: " + fromUnit);
        }

        // Convert from Celsius to target unit
        switch (toUnit) {
            case "Celsius":
                return valueInCelsius;
            case "Fahrenheit":
                return (valueInCelsius * 1.8) + 32;
            case "Kelvin":
                return valueInCelsius + 273.15;
            default:
                throw new IllegalArgumentException("Unknown temperature unit: " + toUnit);
        }
    }
}