package com.example.cse476.calculatorhw1.Controller;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.example.cse476.calculatorhw1.Calculator.Calculator;

public class CalculatorController {
    private final Calculator _calculator;
    private final TextView _formulaView;

    public CalculatorController(Calculator calculator, TextView formulaView) {
        this._calculator = calculator;
        this._formulaView = formulaView;
    }

    @SuppressLint("SetTextI18n")
    public void EnterDigit(int digit) {
        this._formulaView.setText(this._formulaView.getText().toString() + digit);
    }

    public void EnterOperation(char operation) {

    }

    public void EnterDot() {

    }

    public void Delete() {
        var text = this._formulaView.getText();
        if (text.length() == 0)
            return;

        text = text.subSequence(0, text.length() - 1);
        this._formulaView.setText(text);
    }

    public void Calculate() {

    }
}
