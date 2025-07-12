import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.security.SecureRandom;

public class PasswordGeneratorApp extends JFrame implements ActionListener {

    private JTextField lengthField;
    private JCheckBox lowercaseCheckbox, uppercaseCheckbox, numbersCheckbox, symbolsCheckbox, avoidAmbiguousCheckbox;
    private JCheckBox autoCopyCheckbox, showPasswordCheckbox;
    private JTextArea resultArea;
    private JLabel strengthLabel;
    private JButton generateButton, copyButton, clearButton;

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[{]};:'\"\\|,<.>/?";
    private static final String AMBIGUOUS = "O0Il1";

    public PasswordGeneratorApp() {
        setTitle("Advanced Password Generator");
        setSize(550, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(15, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("Enter password length (4 - 128):"));
        lengthField = new JTextField();
        panel.add(lengthField);

        lowercaseCheckbox = new JCheckBox("Include Lowercase (a-z)");
        uppercaseCheckbox = new JCheckBox("Include Uppercase (A-Z)");
        numbersCheckbox = new JCheckBox("Include Numbers (0-9)");
        symbolsCheckbox = new JCheckBox("Include Special Characters (!@#$)");
        avoidAmbiguousCheckbox = new JCheckBox("Avoid Ambiguous Characters (e.g. 0, O, I, l)");

        autoCopyCheckbox = new JCheckBox("Auto-Copy to Clipboard");
        showPasswordCheckbox = new JCheckBox("Show Password");

        panel.add(lowercaseCheckbox);
        panel.add(uppercaseCheckbox);
        panel.add(numbersCheckbox);
        panel.add(symbolsCheckbox);
        panel.add(avoidAmbiguousCheckbox);

        panel.add(autoCopyCheckbox);
        panel.add(showPasswordCheckbox);

        generateButton = new JButton("Generate Password");
        generateButton.addActionListener(this);
        panel.add(generateButton);

        panel.add(new JLabel("Generated Password:"));
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(resultArea);
        panel.add(scroll);

        showPasswordCheckbox.addActionListener(e -> togglePasswordVisibility());

        strengthLabel = new JLabel("Password Strength: ");
        panel.add(strengthLabel);

        copyButton = new JButton("Copy to Clipboard");
        copyButton.addActionListener(e -> copyToClipboard());
        panel.add(copyButton);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearFields());
        panel.add(clearButton);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            int length = Integer.parseInt(lengthField.getText().trim());

            if (length < 4 || length > 128) {
                showError("Please enter a length between 4 and 128.");
                return;
            }

            String characterPool = buildCharacterPool();

            if (characterPool.isEmpty()) {
                showError("Please select at least one character set.");
                return;
            }

            String password = generatePassword(characterPool, length);
            resultArea.setText(password);
            strengthLabel.setText("Password Strength: " + evaluateStrength(length));

            if (autoCopyCheckbox.isSelected()) {
                copyToClipboard();
            }

        } catch (NumberFormatException ex) {
            showError("Invalid input! Please enter a numeric value.");
        }
    }

    private String buildCharacterPool() {
        StringBuilder pool = new StringBuilder();

        if (lowercaseCheckbox.isSelected()) pool.append(LOWERCASE);
        if (uppercaseCheckbox.isSelected()) pool.append(UPPERCASE);
        if (numbersCheckbox.isSelected()) pool.append(NUMBERS);
        if (symbolsCheckbox.isSelected()) pool.append(SYMBOLS);

        // Remove ambiguous characters if needed
        if (avoidAmbiguousCheckbox.isSelected()) {
            for (char c : AMBIGUOUS.toCharArray()) {
                int index;
                while ((index = pool.indexOf(String.valueOf(c))) != -1) {
                    pool.deleteCharAt(index);
                }
            }
        }

        return pool.toString();
    }

    private String generatePassword(String characterPool, int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characterPool.length());
            password.append(characterPool.charAt(index));
        }

        return password.toString();
    }

    private void togglePasswordVisibility() {
        resultArea.setEchoChar(showPasswordCheckbox.isSelected() ? (char) 0 : '\u2022');
    }

    private String evaluateStrength(int length) {
        int score = 0;
        if (lowercaseCheckbox.isSelected()) score++;
        if (uppercaseCheckbox.isSelected()) score++;
        if (numbersCheckbox.isSelected()) score++;
        if (symbolsCheckbox.isSelected()) score++;

        if (length >= 12 && score >= 3) return "Strong";
        if (length >= 8 && score >= 2) return "Medium";
        return "Weak";
    }

    private void copyToClipboard() {
        String password = resultArea.getText();
        if (!password.isEmpty()) {
            StringSelection selection = new StringSelection(password);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            JOptionPane.showMessageDialog(this, "Password copied to clipboard!");
        }
    }

    private void clearFields() {
        lengthField.setText("");
        resultArea.setText("");
        strengthLabel.setText("Password Strength:");
        lowercaseCheckbox.setSelected(false);
        uppercaseCheckbox.setSelected(false);
        numbersCheckbox.setSelected(false);
        symbolsCheckbox.setSelected(false);
        avoidAmbiguousCheckbox.setSelected(false);
        autoCopyCheckbox.setSelected(false);
        showPasswordCheckbox.setSelected(false);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PasswordGeneratorApp().setVisible(true));
    }
}
