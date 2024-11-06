package com.example.cse476.calculatorhw1.Calculator;

public class Calculator implements ICalculator {
    private StringBuilder _currentFormula;

    @Override
    public void SolveFormula(StringBuilder formula) throws ArithmeticException, NumberFormatException {
        this._currentFormula = formula;
        if (this._currentFormula.length() == 0)
            return;

        this.SolveExtraOperators();
        this.SolveMultiplicationAndDivision();
        this.SolveAdditionAndSubtraction();
    }

    private void SolveExtraOperators() {
        this.SolveCosOperator();
        this.SolveSinOperator();
        this.SolveLogOperator();
        this.SolveSqrtOperator();
    }

    private void SolveCosOperator() {
        var indexOfCosOperator = this._currentFormula.indexOf("cos");

        while (indexOfCosOperator >= 0) {
            var cosNumber = this.FindCosSinNumber(indexOfCosOperator);

            var result = Math.cos(cosNumber.Number);
            this.ValidateAndWriteResult(
                    result,
                    new MathOperationConstraints(
                            indexOfCosOperator, cosNumber.AfterOperatorIndex));

            indexOfCosOperator = this._currentFormula.indexOf("cos");
        }
    }

    private void SolveSinOperator() {
        var indexOfSinOperator = this._currentFormula.indexOf("sin");

        while (indexOfSinOperator >= 0) {
            var sinNumber = this.FindCosSinNumber(indexOfSinOperator);

            var result = Math.sin(sinNumber.Number);
            this.ValidateAndWriteResult(
                    result,
                    new MathOperationConstraints(
                            indexOfSinOperator, sinNumber.AfterOperatorIndex));

            indexOfSinOperator = this._currentFormula.indexOf("sin");
        }
    }

    private CosSinNumber FindCosSinNumber(int operatorIndex) {
        operatorIndex += 3;

        int afterOperatorIndex;
        var numberToAdd = 0;
        if (this._currentFormula.charAt(operatorIndex) == '-') {
            afterOperatorIndex = ICalculator.FindIndexOfFirstOperation(
                    this._currentFormula.substring(operatorIndex + 1));
            if (afterOperatorIndex != -1)
                numberToAdd = 4;
        }
        else {
            afterOperatorIndex = ICalculator.FindIndexOfFirstOperation(
                    this._currentFormula.substring(operatorIndex));
            if (afterOperatorIndex != -1)
                numberToAdd = 3;
        }

        if (numberToAdd == 0)
            afterOperatorIndex = this._currentFormula.length();
        else
            afterOperatorIndex += numberToAdd;

        var number = Double.parseDouble(
                this._currentFormula.substring(
                        operatorIndex, afterOperatorIndex));

        return new CosSinNumber(number, afterOperatorIndex);
    }

    private void SolveLogOperator() {
        var indexOfLogOperator = this._currentFormula.indexOf("log");

        while (indexOfLogOperator >= 0) {
            var startIndexOfOperatorNumber = indexOfLogOperator + 3;

            var afterOperatorIndex = ICalculator.FindIndexOfFirstOperation(
                    this._currentFormula.substring(startIndexOfOperatorNumber));

            if (afterOperatorIndex == -1)
                afterOperatorIndex = this._currentFormula.length();
            else
                afterOperatorIndex += 3;

            var number = Double.parseDouble(
                    this._currentFormula.substring(
                            startIndexOfOperatorNumber, afterOperatorIndex));

            var result = Math.log10(number);
            this.ValidateAndWriteResult(
                    result,
                    new MathOperationConstraints(indexOfLogOperator, afterOperatorIndex));

            indexOfLogOperator = this._currentFormula.indexOf("log");
        }
    }

    private void SolveSqrtOperator() {
        var indexOfSqrtOperator = this._currentFormula.indexOf("√");

        while (indexOfSqrtOperator >= 0) {
            var startIndexOfOperatorNumber = indexOfSqrtOperator + 1;

            var afterOperatorIndex = ICalculator.FindIndexOfFirstOperation(
                    this._currentFormula.substring(startIndexOfOperatorNumber));

            if (afterOperatorIndex == -1)
                afterOperatorIndex = this._currentFormula.length();
            else
                afterOperatorIndex++;

            var number = Double.parseDouble(
                    this._currentFormula.substring(
                            startIndexOfOperatorNumber, afterOperatorIndex));

            var result = Math.sqrt(number);
            this.ValidateAndWriteResult(
                    result,
                    new MathOperationConstraints(indexOfSqrtOperator, afterOperatorIndex));

            indexOfSqrtOperator = this._currentFormula.indexOf("√");
        }
    }

    private void SolveMultiplicationAndDivision() {
        var indexOfMultiplicationOperation = this._currentFormula.indexOf("x");
        var indexOfDivisionOperation = this._currentFormula.indexOf("/");

        while (indexOfMultiplicationOperation >= 0 || indexOfDivisionOperation >= 0) {
            var operationIndexToProcess = DetermineNextOperation(
                    indexOfMultiplicationOperation, indexOfDivisionOperation);
            var operationIsMultiply = operationIndexToProcess == indexOfMultiplicationOperation;

            var constraints = new MathOperationConstraints(operationIndexToProcess);

            var leftNumber = Double.parseDouble(
                    this._currentFormula.substring(
                            constraints.LeftNumberStartIndex, operationIndexToProcess));
            var rightNumber = Double.parseDouble(
                    this._currentFormula.substring(
                            operationIndexToProcess + 1, constraints.RightNumberEndIndex));

            double result;
            if (operationIsMultiply)
                result = leftNumber * rightNumber;
            else
                result = leftNumber / rightNumber;

            this.ValidateAndWriteResult(result, constraints);

            indexOfMultiplicationOperation = this._currentFormula.indexOf("x");
            indexOfDivisionOperation = this._currentFormula.indexOf("/");
        }
    }

    private void SolveAdditionAndSubtraction() {
        var indexOfAdditionOperation = this._currentFormula.indexOf("+");

        int indexOfSubtractionOperation;
        if (this._currentFormula.charAt(0) == '-')
            indexOfSubtractionOperation = this._currentFormula.indexOf("-", 1);
        else
            indexOfSubtractionOperation = this._currentFormula.indexOf("-");

        while (indexOfAdditionOperation >= 0 || indexOfSubtractionOperation >= 0) {
            var operationIndexToProcess = DetermineNextOperation(
                    indexOfAdditionOperation, indexOfSubtractionOperation);
            var operationIsAddition = operationIndexToProcess == indexOfAdditionOperation;

            var constraints = new MathOperationConstraints(operationIndexToProcess);

            var leftNumber = Double.parseDouble(
                    this._currentFormula.substring(
                            constraints.LeftNumberStartIndex, operationIndexToProcess));
            var rightNumber = Double.parseDouble(
                    this._currentFormula.substring(
                            operationIndexToProcess + 1, constraints.RightNumberEndIndex));

            double result;
            if (operationIsAddition)
                result = leftNumber + rightNumber;
            else
                result = leftNumber - rightNumber;

            this.ValidateAndWriteResult(result, constraints);

            indexOfAdditionOperation = this._currentFormula.indexOf("+");
            if (this._currentFormula.charAt(0) == '-')
                indexOfSubtractionOperation = this._currentFormula.substring(1).indexOf("-");
            else
                indexOfSubtractionOperation = this._currentFormula.indexOf("-");
        }
    }

    private void ValidateAndWriteResult(double result, MathOperationConstraints constraints) {
        // Throw ArithmeticException they are both treated as NaN
        if (Double.isNaN(result) || Double.isInfinite(result))
            throw new ArithmeticException();

        Object resultAsObject;
        if (Math.floor(result) == result)
            resultAsObject = (long) result;
        else
            resultAsObject = result;

        this._currentFormula.replace(
                constraints.LeftNumberStartIndex,
                constraints.RightNumberEndIndex,
                resultAsObject.toString());

        // Fix double minus operator
        if (constraints.LeftNumberStartIndex == 0)
            return;

        if (this._currentFormula.charAt(constraints.LeftNumberStartIndex) != '-')
            return;

        if (this._currentFormula.charAt(constraints.LeftNumberStartIndex - 1) != '-')
            return;

        var replace = "+";
        if (constraints.LeftNumberStartIndex - 2 >= 0 &&
                ICalculator.IsOperator(this._currentFormula.charAt(constraints.LeftNumberStartIndex)))
            replace = "";

        this._currentFormula.replace(
                constraints.LeftNumberStartIndex - 1,
                constraints.LeftNumberStartIndex + 1,
                replace);
    }

    private static int DetermineNextOperation(
            int indexOfOperation0,
            int indexOfOperation1) {
        if (indexOfOperation0 >= 0 && indexOfOperation1 < 0) {
            return indexOfOperation0;
        }

        if (indexOfOperation0 < 0) {
            return indexOfOperation1;
        }

        return Math.min(indexOfOperation0, indexOfOperation1);
    }

    private static class CosSinNumber {
        public double Number;
        public int AfterOperatorIndex;

        public CosSinNumber(double number, int after) {
            this.Number = number;
            this.AfterOperatorIndex = after;
        }
    }

    private class MathOperationConstraints {
        public int LeftNumberStartIndex;
        public int RightNumberEndIndex;

        public MathOperationConstraints(int left, int right) {
            this.LeftNumberStartIndex = left;
            this.RightNumberEndIndex = right;
        }

        public MathOperationConstraints(int operationIndex) {
            this.LeftNumberStartIndex = ICalculator.FindIndexOfLastOperation(
                    _currentFormula.substring(0, operationIndex)) + 1;
            if (
                    this.LeftNumberStartIndex != 0 &&
                    _currentFormula.charAt(this.LeftNumberStartIndex - 1) == '-'
            )
                this.LeftNumberStartIndex--;

            this.RightNumberEndIndex = ICalculator.FindIndexOfFirstOperation(
                    _currentFormula.substring(operationIndex + 1));
            if (this.RightNumberEndIndex == -1) {
                this.RightNumberEndIndex = _currentFormula.length();
            }
            else {
                this.RightNumberEndIndex += operationIndex + 1;
                if (
                        _currentFormula.charAt(this.RightNumberEndIndex) == '-' &&
                        (this.RightNumberEndIndex - 1) == operationIndex
                ) {
                    var newIndex = ICalculator.FindIndexOfFirstOperation(
                            _currentFormula.substring(this.RightNumberEndIndex + 1));
                    if (newIndex == -1)
                        this.RightNumberEndIndex = _currentFormula.length();
                    else
                        this.RightNumberEndIndex += newIndex + 1;
                }
            }
        }
    }
}
