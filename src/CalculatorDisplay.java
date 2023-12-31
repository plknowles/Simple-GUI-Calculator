import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class CalculatorDisplay extends JTextField {
    ArrayList<Double> numbersAsDoubles = new ArrayList<>();
    ArrayList<String> numbersAsStrings = new ArrayList<>();
    ArrayList<Character> operators = new ArrayList<>();
    boolean bracketErrorOccurred = false;

    CalculatorDisplay() {
        this.setHorizontalAlignment(JLabel.RIGHT);
        this.setFont(new Font("digital-7", Font.PLAIN, 80));
        this.setBackground(Color.darkGray);
        this.setForeground(Color.white);
        this.setOpaque(true);
        this.setEditable(false);
        this.setCaretColor(getBackground());
        this.setText("");
        this.setBorder(null);
        setKeyListener();
    }

    public void addText(String text) {
        this.setText(this.getText().concat(text));
    }

    public void Error(String errorType) {
        if (errorType.equals("syntax")) {
            JOptionPane.showMessageDialog(null, "Syntax Error", "Syntax Error", JOptionPane.ERROR_MESSAGE);
        }
        if (errorType.equals("bracket")) {
            JOptionPane.showMessageDialog(null, "Mismatched Brackets Error", "Mismatched Brackets Error", JOptionPane.ERROR_MESSAGE);
        }
        if (errorType.equals("divide")) {
            JOptionPane.showMessageDialog(null, "Divide by Zero Error", "Divide by Zero Error", JOptionPane.ERROR_MESSAGE);
        }
        clearAllArrayLists();
    }

    public void clearAllArrayLists() {
        numbersAsDoubles.clear();
        numbersAsStrings.clear();
        operators.clear();
    }

    public void equals() {
        performBracketsCalculation();
        if(!bracketErrorOccurred) {
            splitOperatorsNumbers(this.getText());
            if(!numbersAsDoubles.isEmpty()) {
                this.setText(String.valueOf(numbersAsDoubles.get(0)));
            }
        }
        clearAllArrayLists();
    }

    public void performBracketsCalculation() {
        String display = this.getText();
        bracketErrorOccurred = false;
        while (display.contains("(") || display.contains(")")) {
            int openBracketIndex = display.lastIndexOf("(");
            int nextCloseBracketIndex = display.indexOf(")", openBracketIndex); //starts the index for indexOf() from the last "(", where "(" i = 0

            if (nextCloseBracketIndex == -1 || openBracketIndex == -1) { //handles missing brackets
                Error("bracket");
                bracketErrorOccurred = true;
                return;
            }

            String expressionWithinBrackets = display.substring(openBracketIndex + 1, nextCloseBracketIndex); //gets the String expression within the brackets
            splitOperatorsNumbers(expressionWithinBrackets); //sends the expression to be calculated
            double result = (numbersAsDoubles.get(0)); //gets the result

            display = display.substring(0, openBracketIndex) + result + display.substring(nextCloseBracketIndex + 1); //replaces brackets with result
            clearAllArrayLists();
        }
        this.setText(display);
    }


    public void splitOperatorsNumbers(String input) {
        if ((input.length() > 0))  { //won't run if there's nothing in the display
           char previousChar = ' '; //gives previousChar a value for the first iteration

           for (int i = 0; i < input.length(); i++) {
               char currentChar = input.charAt(i);
               int previousIndex = numbersAsStrings.size() - 1;
               if (numbersAsStrings.isEmpty() && ((currentChar == '-') || Character.isDigit(currentChar))) {
                   numbersAsStrings.add(String.valueOf(currentChar));
               } else {
                   if (Character.isDigit(currentChar) || currentChar == '.') { //if currentChar is a number or decimal point
                       if (!Character.isDigit(previousChar) && (previousChar != '.')) { //if previousChar is an operator
                           if (numbersAsStrings.get(previousIndex).equals("-")) { //if the previous value in numbersArray is - operator
                               numbersAsStrings.set(previousIndex, numbersAsStrings.get(previousIndex) + (input.charAt(i))); //the currentChar will be concatenated to the operator to create a negative number
                           } else {
                               numbersAsStrings.add(String.valueOf(currentChar)); //else adds the currentChar as a new value in the array
                           }
                       } else {
                           numbersAsStrings.set(previousIndex, numbersAsStrings.get(previousIndex) + (input.charAt(i))); //else if the previousChar is not an operator, will concatenate currentChar to the last value in numberArray
                       }
                   } else //else the currentChar is an operator
                       if (currentChar == '-' && !Character.isDigit(previousChar) && previousChar != '.') { //if the current character is - operator, and previous character is not a digit
                           if (numbersAsStrings.get(previousIndex).equals("-")) { //if the previous character is also - (--)
                               operators.add(currentChar); //adds current - to operators array to handle --
                           } else
                               numbersAsStrings.add(String.valueOf(currentChar)); //if current char is - and previous char is an operator that's not -, adds - to numbers array to create a negative number
                       } else {
                           operators.add(currentChar); //adds the operator to operatorArray
                       }
               }
               previousChar = currentChar; //makes currentChar previousChar for the next iteration
           }
       }
        System.out.println("numbers: " + numbersAsStrings);
        System.out.println("operators: " + operators);
        convertStringsToDoubles(numbersAsStrings);
        if (!this.getText().isBlank()) { //only runs calculation logic if display is not blank
            performCalculation();
        }
    }

    public void convertStringsToDoubles(ArrayList<String> numbersArray) {
        for (String str : numbersArray) { //converts <String>numbersArray to doubleArray
            try {
                double value = Double.parseDouble(str);
                numbersAsDoubles.add(value);
            } catch (NumberFormatException e) { //throws syntax error if non-numbers are detected in numbersArray
                Error("syntax");
                return;
            }
        }
    }

    public void performCalculation() {
        try {
            for (int i = 0; i < operators.size(); i++) {
                if (operators.get(i) == '/') {
                    double divisor = numbersAsDoubles.get(i + 1);
                        if (divisor == 0) {
                            Error("divide");
                            return;
                        }
                        double temp = applyOperator('/', numbersAsDoubles.get(i), numbersAsDoubles.get(i + 1));
                        numbersAsDoubles.set(i, temp);
                        numbersAsDoubles.remove(i + 1);
                        operators.remove(i);
                        i--; //Decrement i to account for the removed operator
                }
            }
            for (int i = 0; i < operators.size(); i++) {
                if (operators.get(i) == '*') {
                    double temp = applyOperator('*', numbersAsDoubles.get(i), numbersAsDoubles.get(i + 1));
                    numbersAsDoubles.set(i, temp);
                    numbersAsDoubles.remove(i + 1);
                    operators.remove(i);
                    i--;
                }
            }
            for (int i = 0; i < operators.size(); i++) {
                if (operators.get(i) == '+') {
                    double temp = applyOperator('+', numbersAsDoubles.get(i), numbersAsDoubles.get(i + 1));
                    numbersAsDoubles.set(i, temp);
                    numbersAsDoubles.remove(i + 1);
                    operators.remove(i);
                    i--;
                }
            }
            for (int i = 0; i < operators.size(); i++) {
                if (operators.get(i) == '-') {
                    double temp = applyOperator('-', numbersAsDoubles.get(i), numbersAsDoubles.get(i + 1));
                    numbersAsDoubles.set(i, temp);
                    numbersAsDoubles.remove(i + 1);
                    operators.remove(i);
                    i--;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Error("syntax");
        }
    }

    public double applyOperator(char operator, double num1, double num2) {
        switch (operator) {
            case '/' -> {
                return num1 / num2;
            }
            case '*' -> {
                return num1 * num2;
            }
            case '+' -> {
                return num1 + num2;
            }
            case '-' -> {
                return num1 - num2;
            }
            default -> {
                Error("syntax");
                throw new IllegalArgumentException("Invalid Operator: " + operator);
            }
        }
    }

    public void deleteText() {
        int textLength = this.getText().length();
        if (textLength > 0) {
            this.setText(this.getText().substring(0, textLength - 1));
        }
    }

    public void setKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case '1' -> CalculatorDisplay.this.addText("1");
                    case '2' -> CalculatorDisplay.this.addText("2");
                    case '3' -> CalculatorDisplay.this.addText("3");
                    case '4' -> CalculatorDisplay.this.addText("4");
                    case '5' -> CalculatorDisplay.this.addText("5");
                    case '6' -> CalculatorDisplay.this.addText("6");
                    case '7' -> CalculatorDisplay.this.addText("7");
                    case '8' -> CalculatorDisplay.this.addText("8");
                    case '9' -> CalculatorDisplay.this.addText("9");
                    case '0' -> CalculatorDisplay.this.addText("0");
                    case '(' -> CalculatorDisplay.this.addText("(");
                    case ')' -> CalculatorDisplay.this.addText(")");
                    case '.' -> CalculatorDisplay.this.addText(".");
                    case '+' -> CalculatorDisplay.this.addText("+");
                    case '-' -> CalculatorDisplay.this.addText("-");
                    case '/', '÷' -> CalculatorDisplay.this.addText("/");
                    case '*', '×' -> CalculatorDisplay.this.addText("*");
                    case KeyEvent.VK_DELETE, KeyEvent.VK_BACK_SPACE -> CalculatorDisplay.this.deleteText();
                    case '=', KeyEvent.VK_ENTER -> CalculatorDisplay.this.equals();
                    case 'C', 'c' -> {
                        CalculatorDisplay.this.clearAllArrayLists();
                        CalculatorDisplay.this.setText("");
                    }
                }
            }
        });
    }
}