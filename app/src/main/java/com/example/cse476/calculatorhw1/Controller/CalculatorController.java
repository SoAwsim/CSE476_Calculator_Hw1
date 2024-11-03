package com.example.cse476.calculatorhw1.Controller;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.example.cse476.calculatorhw1.Calculator.Calculator;

@SuppressLint("SetTextI18n")
public class CalculatorController {
    private static final char[] operators = {'+', '-', 'x', '/'};
    private final Calculator _calculator;
    private final TextView _formulaView;
    private boolean _dotAllowed;
    private OperationState _operationState;

    public CalculatorController(Calculator calculator, TextView formulaView) {
        this._calculator = calculator;
        this._formulaView = formulaView;
        this._dotAllowed = false;
        this._operationState = OperationState.MINUS_ALLOWED;
    }

    public void EnterDigit(int digit) {
        var newFormula = this._formulaView.getText().toString() + digit;
        this.DetermineDotCanBePlaced(newFormula);
        this._operationState = OperationState.ALL_OPERATORS_ALLOWED;
        this._formulaView.setText(newFormula);
    }

    public void EnterOperation(char operation) {
        if (this._operationState == OperationState.ALL_OPERATORS_BANNED)
            return;

        if (this._operationState == OperationState.MINUS_ALLOWED && operation != '-')
            return;

        // At this point operation is valid
        this._dotAllowed = false;
        if (operation == '/' || operation == 'x')
            this._operationState = OperationState.MINUS_ALLOWED;
        else
            this._operationState = OperationState.ALL_OPERATORS_BANNED;

        this._formulaView.setText(this._formulaView.getText().toString() + operation);
    }

    public void EnterDot() {
        if (!this._dotAllowed)
            return;
        this._dotAllowed = false;
        this._operationState = OperationState.ALL_OPERATORS_BANNED;
        this._formulaView.setText(this._formulaView.getText().toString() + '.');
    }

    public void Delete() {
        var text = this._formulaView.getText();
        if (text.length() == 0)
            return;

        text = text.subSequence(0, text.length() - 1);
        this.DetermineDotCanBePlaced(text.toString());
        this.DetermineOperationState(text.toString());

        this._formulaView.setText(text);
    }

    public void Calculate() {

    }

    private void DetermineDotCanBePlaced(String formula) {
        if (formula.isEmpty()) {
            this._dotAllowed = false;
            return;
        }

        var indexOfLastOperator = Integer.MIN_VALUE;
        for (var c : operators) {
            var foundIndex = formula.lastIndexOf(c);
            if (foundIndex > indexOfLastOperator)
                indexOfLastOperator = foundIndex;
        }

        var indexOfLastDot = formula.lastIndexOf('.');
        this._dotAllowed = indexOfLastDot <= indexOfLastOperator;
    }

    private void DetermineOperationState(String formula) {
        if (formula.isEmpty()) {
            this._operationState = OperationState.MINUS_ALLOWED;
            return;
        }

        var lastChar = formula.charAt(formula.length() - 1);
        if (Character.isDigit(lastChar))
            this._operationState = OperationState.ALL_OPERATORS_ALLOWED;
        else if (lastChar == '/' || lastChar == 'x')
            this._operationState = OperationState.MINUS_ALLOWED;
        else
            this._operationState = OperationState.ALL_OPERATORS_BANNED;
    }
}
