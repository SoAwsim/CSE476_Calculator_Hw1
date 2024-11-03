package com.example.cse476.calculatorhw1.Calculator;

public class Calculator implements ICalculator {
    private StringBuilder _currentFormula;

    @Override
    public void SolveFormula(StringBuilder formula) throws ArithmeticException, NumberFormatException {
        this._currentFormula = formula;
        if (this._currentFormula.length() == 0)
            return;

        this.SolveMultiplicationAndDivision();
        this.SolveAdditionAndSubtraction();
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
        // throw ArithmeticException they are both treated as NaN
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

    private class MathOperationConstraints {
        public int LeftNumberStartIndex;
        public int RightNumberEndIndex;

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
