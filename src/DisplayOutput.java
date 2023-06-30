import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DisplayOutput extends JTextField {
    DisplayOutput() {
        this.setHorizontalAlignment(JLabel.RIGHT);
        this.setFont(new Font("digital-7", Font.PLAIN,60));
        this.setBackground(Color.gray);
        this.setForeground(Color.BLACK);
        this.setOpaque(true);
        this.setEditable(false);
        this.setCaretColor(getBackground());
    }

    public void addText(String text) {
        this.setText(this.getText().concat(text));
    }

    public void clearAll() {
        this.setText("");
    }

    public void equals() {
        this.setText(String.valueOf(displayExtractor()));

    }

    public double displayExtractor() {
        String display = this.getText();
        ArrayList<String> numbersArray = new ArrayList<>();
        ArrayList<Character> operatorsArray = new ArrayList<>();
        ArrayList<Double> doubleArray = new ArrayList<>();

        if (display.length() > 0) { //won't run if there's nothing in the display
            char previousChar = ' '; //gives previousChar a value for the first iteration

            for(int i = 0; i < display.length(); i++) {
                char currentChar = display.charAt(i);
                int lastIndex = numbersArray.size() - 1;
                if (numbersArray.isEmpty()) {
                    numbersArray.add(String.valueOf(currentChar));
                } else {
                    if (Character.isDigit(currentChar) || currentChar == '.') { //if currentChar is a number or decimal point
                        if (!Character.isDigit(previousChar) && (previousChar != '.')) { //if previousChar is an operator
                            if (numbersArray.get(lastIndex).equals("-") || numbersArray.get(lastIndex).equals("+")) { //if the previous value in numbersArray is a + or - operator
                                numbersArray.set(lastIndex, numbersArray.get(lastIndex) + (display.charAt(i))); //the currentChar will be concatenated to the operator to create a negative/positive number
                            } else {
                                numbersArray.add(String.valueOf(currentChar));//else adds the currentChar as a new value in the array
                            }
                        } else {
                            numbersArray.set(lastIndex, numbersArray.get(lastIndex) + (display.charAt(i))); //else if the previousChar is not an operator, will concatenate currentChar to the last value in numberArray
                        }
                    } else //else the currentChar is an operator
                        if (!Character.isDigit(previousChar) && (previousChar != '.')){ //if the previous character is an operator
                            numbersArray.add(String.valueOf(currentChar)); //if there's two operators in a row, this adds the second operator to the numbersArray
                        } else {
                            operatorsArray.add(currentChar); //adds the operator to operatorArray
                        }
                }
                previousChar = currentChar; //makes currentChar previousChar for the next iteration
            }
            System.out.println("Numbers: " + numbersArray);
            System.out.println("Operators: " + operatorsArray);
        }

        for (String str : numbersArray) { //converts <String>numbersArray to doubleArray
            double value = Double.parseDouble(str);
            doubleArray.add(value);
        }
        System.out.println("Double numbers: " + doubleArray);
        double result = performCalculation(doubleArray, operatorsArray);
        System.out.println("Result: " + result);
    return result;
    }

    public double performCalculation(ArrayList<Double>doubleArray, ArrayList<Character>operatorsArray){
        for(int i = 0; i < operatorsArray.size(); i++){
            if (operatorsArray.get(i) =='/'){
                double temp = divide(doubleArray.get(i),doubleArray.get(i+1));
                doubleArray.set(i, temp);
                doubleArray.remove(i+1);
                operatorsArray.remove(i);
            }
        }
        for(int i = 0; i < operatorsArray.size(); i++){
            if (operatorsArray.get(i) =='*'){
                double temp = multiply(doubleArray.get(i),doubleArray.get(i+1));
                doubleArray.set(i, temp);
                doubleArray.remove(i+1);
                operatorsArray.remove(i);
            }
        }
        for(int i = 0; i < operatorsArray.size(); i++){
            if (operatorsArray.get(i) =='+'){
                double temp = add(doubleArray.get(i),doubleArray.get(i+1));
                doubleArray.set(i, temp);
                doubleArray.remove(i+1);
                operatorsArray.remove(i);
            }
        }
        for(int i = 0; i < operatorsArray.size(); i++){
            if (operatorsArray.get(i) =='-'){
                double temp = subtract(doubleArray.get(i),doubleArray.get(i+1));
                doubleArray.set(i, temp);
                doubleArray.remove(i+1);
                operatorsArray.remove(i);
            }
        }
        return (doubleArray.get(0));
    }

    public double divide(double num1, double num2){
        return num1/num2;
    }

    public double multiply(double num1, double num2){
        return num1*num2;
    }

    public double add(double num1, double num2){
        return num1+num2;
    }

    public double subtract(double num1, double num2){
        return num1-num2;
    }

    public void deleteText() {
        int textLength = this.getText().length();
        if (textLength > 0) {
            this.setText(this.getText().substring(0, textLength - 1));
        }
    }
}