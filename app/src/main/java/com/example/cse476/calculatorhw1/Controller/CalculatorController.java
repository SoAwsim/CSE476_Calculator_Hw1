package com.example.cse476.calculatorhw1.Controller;

import android.annotation.SuppressLint;
import android.widget.TextView;

@SuppressLint("SetTextI18n")
public class CalculatorController {
    private static final char[] OPERATORS = {'+', '-', 'x', '/'};
    private final TextView _formulaView;
    private boolean _inErrorState;
    private boolean _dotAllowed;
    private OperationState _operationState;

    public CalculatorController(TextView formulaView) {
        this._formulaView = formulaView;
        this._inErrorState = false;
        this._dotAllowed = false;
        this._operationState = OperationState.MINUS_ALLOWED;
    }

    public void EnterDigit(int digit) {
        this.ClearErrorState();
        var newFormula = this._formulaView.getText().toString() + digit;
        this.DetermineDotCanBePlaced(newFormula);
        this._operationState = OperationState.ALL_OPERATORS_ALLOWED;
        this._formulaView.setText(newFormula);
    }

    public void EnterOperation(char operation) {
        this.ClearErrorState();
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
        this.ClearErrorState();
        if (!this._dotAllowed)
            return;
        this._dotAllowed = false;
        this._operationState = OperationState.ALL_OPERATORS_BANNED;
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

        this._formulaView.setText(text);
    }

    public void DeleteAll() {
        this.ClearErrorState();
        this._dotAllowed = false;
        this._operationState = OperationState.MINUS_ALLOWED;
        this._formulaView.setText("");
    }

    public void Calculate() {
        ClearErrorState();
        var formula = new StringBuilder(this._formulaView.getText().toString());
        if (formula.length() == 0)
            return;

        try {
            SolveMultiplicationAndDivision(formula);
            SolveAdditionAndSubtraction(formula);
        } catch (ArithmeticException e) {
            this._inErrorState = true;
            this._formulaView.setText("NaN");
            return;
        } catch (NumberFormatException e) {
            this._inErrorState = true;
            this._formulaView.setText("Syntax Error");
            return;
        }

        this._formulaView.setText(formula);
    }

    private void ClearErrorState() {
        if (!this._inErrorState)
            return;

        this._inErrorState = false;
        this._formulaView.setText("");
    }

    private static void SolveMultiplicationAndDivision(StringBuilder formula) throws ArithmeticException, NumberFormatException {
        var indexOfMultiplicationOperation = formula.indexOf("x");
        var indexOfDivisionOperation = formula.indexOf("/");

        while (indexOfMultiplicationOperation >= 0 || indexOfDivisionOperation >= 0) {
            int operationIndexToProcess;
            boolean operationIsMultiply;

            if (indexOfMultiplicationOperation >= 0 && indexOfDivisionOperation < 0) {
                operationIndexToProcess = indexOfMultiplicationOperation;
                operationIsMultiply = true;
            }
            else if (indexOfMultiplicationOperation < 0) {
                operationIndexToProcess = indexOfDivisionOperation;
                operationIsMultiply = false;
            }
            else if (indexOfMultiplicationOperation < indexOfDivisionOperation) {
                operationIndexToProcess = indexOfMultiplicationOperation;
                operationIsMultiply = true;
            }
            else {
                operationIndexToProcess = indexOfDivisionOperation;
                operationIsMultiply = false;
            }

            var leftNumberStart = FindIndexOfLastOperation(
                    formula.substring(0, operationIndexToProcess)
            ) + 1;

            var rightNumberEnd = FindIndexOfFirstOperation(formula.substring(operationIndexToProcess + 1));
            if (rightNumberEnd == -1)
                rightNumberEnd = formula.length();
            else
                rightNumberEnd = rightNumberEnd + operationIndexToProcess + 1;

            var leftNumber = Double.parseDouble(
                    formula.substring(leftNumberStart, operationIndexToProcess));
            var rightNumber = Double.parseDouble(
                    formula.substring(operationIndexToProcess + 1, rightNumberEnd));

            double result;
            if (operationIsMultiply)
                result = leftNumber * rightNumber;
            else
                result = leftNumber / rightNumber;

            // throw ArithmeticException they are both treated as NaN
            if (Double.isNaN(result) || Double.isInfinite(result))
                throw new ArithmeticException();

            Object resultAsObject;
            if (Math.floor(result) == result)
                resultAsObject = (int) result;
            else
                resultAsObject = result;

            formula.replace(leftNumberStart, rightNumberEnd, resultAsObject.toString());

            indexOfMultiplicationOperation = formula.indexOf("x");
            indexOfDivisionOperation = formula.indexOf("/");
        }
    }

    private static void SolveAdditionAndSubtraction(StringBuilder formula) {

    }

    private static int FindIndexOfLastOperation(String formula) {
        var indexOfLastOperator = Integer.MIN_VALUE;
        for (var c : OPERATORS) {
            var foundIndex = formula.lastIndexOf(c);
            if (foundIndex > indexOfLastOperator)
                indexOfLastOperator = foundIndex;
        }
        return indexOfLastOperator;
    }

    private static int FindIndexOfFirstOperation(String formula) {
        var indexOfFirstOperator = -1;
        for (var c : OPERATORS) {
            var foundIndex = formula.indexOf(c);
            if (foundIndex == -1)
                continue;

            if (indexOfFirstOperator == -1 || foundIndex < indexOfFirstOperator)
                indexOfFirstOperator = foundIndex;
        }
        return indexOfFirstOperator;
    }

    private void DetermineDotCanBePlaced(String formula) {
        if (formula.isEmpty()) {
            this._dotAllowed = false;
            return;
        }

        var indexOfLastOperator = FindIndexOfLastOperation(formula);

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
