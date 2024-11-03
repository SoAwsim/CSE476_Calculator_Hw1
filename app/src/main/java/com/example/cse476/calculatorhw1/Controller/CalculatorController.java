package com.example.cse476.calculatorhw1.Controller;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.TextView;

import com.example.cse476.calculatorhw1.Calculator.ICalculator;

@SuppressLint("SetTextI18n")
public class CalculatorController {
    private final ICalculator _calculator;
    private final TextView _formulaView;
    private boolean _inErrorState;
    private boolean _dotAllowed;
    private OperationState _operatorState;
    private boolean _extraOperatorAllowed;

    public CalculatorController(ICalculator calculator, TextView formulaView) {
        this._calculator = calculator;
        this._formulaView = formulaView;
        this._inErrorState = false;
        this._dotAllowed = false;
        this._operatorState = OperationState.MINUS_ALLOWED;
        this._extraOperatorAllowed = true;
    }

    public void EnterDigit(int digit) {
        this.ClearErrorState();
        this._extraOperatorAllowed = false;
        var newFormula = this._formulaView.getText().toString() + digit;
        this.DetermineDotCanBePlaced(newFormula);
        this._operatorState = OperationState.ALL_OPERATORS_ALLOWED;
        this._formulaView.setText(newFormula);
    }

    public void EnterOperation(char operation) {
        this.ClearErrorState();
        if (this._operatorState == OperationState.ALL_OPERATORS_BANNED)
            return;

        if (this._operatorState == OperationState.MINUS_ALLOWED && operation != '-')
            return;

        // At this point operation is valid
        this._extraOperatorAllowed = true;
        this._dotAllowed = false;
        if (operation == '/' || operation == 'x')
            this._operatorState = OperationState.MINUS_ALLOWED;
        else
            this._operatorState = OperationState.ALL_OPERATORS_BANNED;

        this._formulaView.setText(this._formulaView.getText().toString() + operation);
    }

    public void EnterDot() {
        this.ClearErrorState();
        if (!this._dotAllowed)
            return;
        this._dotAllowed = false;
        this._operatorState = OperationState.ALL_OPERATORS_BANNED;
        this._formulaView.setText(this._formulaView.getText().toString() + '.');
    }

    public void Delete() {
        this.ClearErrorState();
        var text = this._formulaView.getText();
        if (text.length() == 0)
            return;

        text = text.subSequence(0, text.length() - 1);
        this.DetermineDotCanBePlaced(text.toString());
        this.DetermineOperationState(text.toString());
        this.DetermineExtraOperatorCanBePlaced(text.toString());

        this._formulaView.setText(text);
    }

    public void DeleteAll() {
        this.ClearErrorState();
        this._dotAllowed = false;
        this._extraOperatorAllowed = true;
        this._operatorState = OperationState.MINUS_ALLOWED;
        this._formulaView.setText("");
    }

    public void Calculate() {
        ClearErrorState();
        var formula = new StringBuilder(this._formulaView.getText().toString());
        if (formula.length() == 0)
            return;

        try {
            this._calculator.SolveFormula(formula);
        } catch (ArithmeticException e) {
            this._inErrorState = true;
            this._formulaView.setText("NaN");
            return;
        } catch (NumberFormatException e) {
            this._inErrorState = true;
            this._formulaView.setText("Syntax Error");
            return;
        } catch (Exception e) {
            this._inErrorState = true;
            Log.wtf("Unhandled exception", e);
            this._formulaView.setText("Err");
            return;
        }

        this._formulaView.setText(formula);
    }

    public void EnterExtraOperation(String operator) {
        ClearErrorState();
        if (!this._extraOperatorAllowed)
            return;

        this._extraOperatorAllowed = false;
        var text = this._formulaView.getText();

        if (!operator.equals("sin") && !operator.equals("cos"))
            this._operatorState = OperationState.ALL_OPERATORS_BANNED;
        else
            this._operatorState = OperationState.MINUS_ALLOWED;

        this._formulaView.setText(text + operator);
    }

    private void ClearErrorState() {
        if (!this._inErrorState)
            return;

        this._inErrorState = false;
        this._formulaView.setText("");
    }

    private void DetermineDotCanBePlaced(String formula) {
        if (formula.isEmpty()) {
            this._dotAllowed = false;
            return;
        }

        var indexOfLastOperator = ICalculator.FindIndexOfLastOperation(formula);

        var indexOfLastDot = formula.lastIndexOf('.');
        this._dotAllowed = indexOfLastDot <= indexOfLastOperator;
    }

    private void DetermineOperationState(String formula) {
        if (formula.isEmpty()) {
            this._operatorState = OperationState.MINUS_ALLOWED;
            return;
        }

        var lastChar = formula.charAt(formula.length() - 1);
        if (Character.isDigit(lastChar))
            this._operatorState = OperationState.ALL_OPERATORS_ALLOWED;
        else if (lastChar == '/' || lastChar == 'x')
            this._operatorState = OperationState.MINUS_ALLOWED;
        else
            this._operatorState = OperationState.ALL_OPERATORS_BANNED;
    }

    private void DetermineExtraOperatorCanBePlaced(String formula) {
        if (formula.isEmpty()) {
            this._extraOperatorAllowed = true;
            return;
        }

        var lastChar = formula.charAt(formula.length() - 1);
        if (ICalculator.IsOperator(lastChar)) {
            this._extraOperatorAllowed = true;
            return;
        }

        this._extraOperatorAllowed = false;
    }
}
