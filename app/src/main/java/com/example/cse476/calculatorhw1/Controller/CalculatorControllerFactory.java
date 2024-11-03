package com.example.cse476.calculatorhw1.Controller;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.example.cse476.calculatorhw1.Calculator.Calculator;
import com.example.cse476.calculatorhw1.R;

public class CalculatorControllerFactory {
    private final Activity _activity;
    private CalculatorController _calculatorController;
    
    public CalculatorControllerFactory(Activity activity) {
        this._activity = activity;
    }
    
    public void DefaultCalculatorController() {
        var calculator = new Calculator();
        TextView formulaView = this._activity.findViewById(R.id.formulaView);
        this._calculatorController = new CalculatorController(calculator, formulaView);

        HorizontalScrollView scrollView = this._activity.findViewById(R.id.formulaScroller);
        formulaView
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> scrollView.fullScroll(View.FOCUS_RIGHT));

        this.InitializeDigitButtons();
        this.InitializeOperatorButtons();
    }

    private void InitializeDigitButtons() {
        Button button;
        button = this._activity.findViewById(R.id.buttonNumber7);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(7));

        button = this._activity.findViewById(R.id.buttonNumber8);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(8));

        button = this._activity.findViewById(R.id.buttonNumber9);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(9));

        button = this._activity.findViewById(R.id.buttonNumber4);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(4));

        button = this._activity.findViewById(R.id.buttonNumber5);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(5));

        button = this._activity.findViewById(R.id.buttonNumber6);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(6));

        button = this._activity.findViewById(R.id.buttonNumber1);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(1));

        button = this._activity.findViewById(R.id.buttonNumber2);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(2));

        button = this._activity.findViewById(R.id.buttonNumber3);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(3));

        button = this._activity.findViewById(R.id.buttonNumberDot);
        button.setOnClickListener(v -> this._calculatorController.EnterDot());

        button = this._activity.findViewById(R.id.buttonNumber0);
        button.setOnClickListener(v -> this._calculatorController.EnterDigit(0));
    }
    
    private void InitializeOperatorButtons() {
        Button button;
        button = this._activity.findViewById(R.id.buttonOperationAdd);
        button.setOnClickListener(v -> this._calculatorController.EnterOperation('+'));

        button = this._activity.findViewById(R.id.buttonOperationDivide);
        button.setOnClickListener(v -> this._calculatorController.EnterOperation('/'));

        button = this._activity.findViewById(R.id.buttonOperationMultiply);
        button.setOnClickListener(v -> this._calculatorController.EnterOperation('x'));

        button = this._activity.findViewById(R.id.buttonOperationSubtract);
        button.setOnClickListener(v -> this._calculatorController.EnterOperation('-'));

        button = this._activity.findViewById(R.id.buttonOperationSum);
        button.setOnClickListener(v -> this._calculatorController.Calculate());

        button = this._activity.findViewById(R.id.buttonOperationDelete);
        button.setOnClickListener(v -> this._calculatorController.Delete());
        button.setOnLongClickListener(v -> {
            this._calculatorController.DeleteAll();
            return true;
        });
    }
}
