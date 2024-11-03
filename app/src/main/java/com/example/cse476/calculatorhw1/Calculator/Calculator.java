package com.example.cse476.calculatorhw1.Calculator;

public class Calculator implements ICalculator {
    private StringBuilder _currentFormula;

    @Override
    public void SolveFormula(StringBuilder formula) throws ArithmeticException, NumberFormatException {
        this._currentFormula = formula;
        this.SolveMultiplicationAndDivision();
        this.SolveAdditionAndSubtraction();
    }

    private void SolveMultiplicationAndDivision() {
        var indexOfMultiplicationOperation = this._currentFormula.indexOf("x");
        var indexOfDivisionOperation = this._currentFormula.indexOf("/");

        while (indexOfMultiplicationOperation >= 0 || indexOfDivisionOperation >= 0) {
            var operationIndexToProcess = DetermineNextOperationIsMultiplyOrDivision(
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

    private static int DetermineNextOperationIsMultiplyOrDivision(
            int indexOfMultiplicationOperation,
            int indexOfDivisionOperation) {
        if (indexOfMultiplicationOperation >= 0 && indexOfDivisionOperation < 0) {
            return indexOfMultiplicationOperation;
        }

        if (indexOfMultiplicationOperation < 0) {
            return indexOfDivisionOperation;
        }

        return Math.min(indexOfMultiplicationOperation, indexOfDivisionOperation);
    }

    private class MathOperationConstraints {
        public int LeftNumberStartIndex;
        public int RightNumberEndIndex;

        public MathOperationConstraints(int operationIndex) {
            this.LeftNumberStartIndex = ICalculator.FindIndexOfLastOperation(
                    _currentFormula.substring(0, operationIndex)) + 1;
            if (this.LeftNumberStartIndex != 0 && _currentFormula.charAt(this.LeftNumberStartIndex - 1) == '-')
                this.LeftNumberStartIndex--;

            this.RightNumberEndIndex = ICalculator.FindIndexOfFirstOperation(
                    _currentFormula.substring(operationIndex + 1));
            if (this.RightNumberEndIndex == -1) {
                this.RightNumberEndIndex = _currentFormula.length();
            }
            else {
                this.RightNumberEndIndex = this.RightNumberEndIndex + operationIndex + 1;
                if (
                        _currentFormula.charAt(this.RightNumberEndIndex) == '-' &&
                        (this.RightNumberEndIndex - 1) == operationIndex
                ) {
                    var newIndex = ICalculator.FindIndexOfFirstOperation(
                            _currentFormula.substring(this.RightNumberEndIndex + 1));
                    if (newIndex == -1)
                        this.RightNumberEndIndex = _currentFormula.length();
                    else
                        this.RightNumberEndIndex = newIndex + this.RightNumberEndIndex + 1;
                }
            }
        }
    }
}
