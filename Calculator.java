import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculator {

    private JFrame frame;
    private JTextField textField;
    private StringBuilder currentInput;

    // Constructor to initialize the components
    public Calculator() {
        frame = new JFrame("Calculator");
        frame.setLayout(new BorderLayout());
        frame.setSize(400, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        currentInput = new StringBuilder();

        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.BOLD, 30));
        frame.add(textField, BorderLayout.NORTH);

        // Creating the panel for buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4));

        // Button labels and actions
        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+"
        };

        for (String label : buttons) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.PLAIN, 24));
            button.addActionListener(new ButtonClickListener());
            panel.add(button);
        }

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // ActionListener for handling button clicks
    private class ButtonClickListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("=")) {
                try {
                    // Calculate result
                    String result = String.valueOf(eval(currentInput.toString()));
                    textField.setText(result);
                    currentInput.setLength(0);  // Reset input after evaluation
                    currentInput.append(result);
                } catch (Exception ex) {
                    textField.setText("Error");
                }
            } else if (command.equals("C")) {
                currentInput.setLength(0);
                textField.setText("");
            } else {
                currentInput.append(command);
                textField.setText(currentInput.toString());
            }
        }

        private double eval(String expression) {
            // A simple method to evaluate basic expressions like "2+3*5"
            return new Object() {
                int pos = -1, c;

                void nextChar() {
                    c = (++pos < expression.length()) ? expression.charAt(pos) : -1;
                }

                boolean eat(int charToEat) {
                    while (c == ' ') nextChar();
                    if (c == charToEat) {
                        nextChar();
                        return true;
                    }
                    return false;
                }

                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) c);
                    return x;
                }

                double parseExpression() {
                    double x = parseTerm();
                    for (; ; ) {
                        if (eat('+')) x += parseTerm();
                        else if (eat('-')) x -= parseTerm();
                        else return x;
                    }
                }

                double parseTerm() {
                    double x = parseFactor();
                    for (; ; ) {
                        if (eat('*')) x *= parseFactor();
                        else if (eat('/')) x /= parseFactor();
                        else return x;
                    }
                }

                double parseFactor() {
                    if (eat('+')) return parseFactor();
                    if (eat('-')) return -parseFactor();

                    double x;
                    int startPos = this.pos;
                    if (eat('(')) {
                        x = parseExpression();
                        eat(')');
                    } else if ((c >= '0' && c <= '9') || c == '.') {
                        while ((c >= '0' && c <= '9') || c == '.') nextChar();
                        x = Double.parseDouble(expression.substring(startPos, this.pos));
                    } else {
                        throw new RuntimeException("Unexpected: " + (char) c);
                    }

                    return x;
                }
            }.parse();
        }
    }

    // Main method to launch the application
    public static void main(String[] args) {
        new Calculator();
    }
}
