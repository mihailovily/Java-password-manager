import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

public class PasswordManagerApp extends JFrame {
    private final JTextField loginField;
    private final JPasswordField passwordField;
    private final JComboBox<String> encryptionComboBox;
    private JTextArea resultTextArea;
    private SecretKey secretKey;

    public PasswordManagerApp() {
        setTitle("Менеджер паролей");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        loginField = new JTextField();
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Войти");
        encryptionComboBox = new JComboBox<>(new String[] { "Base64", "MD5", "Фейстель", "B64 с солью" });

        JPanel topPanel = new JPanel(new GridLayout(2, 2));
        topPanel.add(new JLabel("Логин:"));
        topPanel.add(loginField);
        topPanel.add(new JLabel("Пароль:"));
        topPanel.add(passwordField);

        JPanel middlePanel = new JPanel();
        middlePanel.add(new JLabel("Метод шифрования:"));
        middlePanel.add(encryptionComboBox);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(loginButton);

        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = loginField.getText();
                String password = new String(passwordField.getPassword());
                String encryptionMethod = (String) encryptionComboBox.getSelectedItem();

                if (login.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter both login and password.");
                    return;
                }

                String result = "";
                try {
                    switch (encryptionMethod) {
                        case "Base64":
                            result = encryptBase64(password);
                            break;
                        case "MD5":
                            result = encryptMD5(password);
                            break;
                        case "Фейстель":
                            result = encryptFeistel(password);
                            break;
                        case "B64 с солью":
                            result = encryptWithSalt(password);
                            break;
                    }
                    PasswordGeneratorMenu dialog = new PasswordGeneratorMenu(login, result);
                    dialog.pack();
                    dialog.setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Ошибка");
                }
            }
        });
    }

    private String encryptBase64(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    private String encryptMD5(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(Integer.toHexString(0xFF & b));
        }
        return hexString.toString();
    }

    private String encryptFeistel(String password) {
        return feistelCipher(password.getBytes());
    }

    private String encryptWithSalt(String password) {
        String salt = generateSalt();
        String saltedPassword = password + salt;
        return Base64.getEncoder().encodeToString(saltedPassword.getBytes());
    }

    private String feistelCipher(byte[] input) {
        int n = input.length;
        int half = n / 2;
        byte[] left = new byte[half];
        byte[] right = new byte[half];
        System.arraycopy(input, 0, left, 0, half);
        System.arraycopy(input, half, right, 0, half);

        byte[] temp = new byte[half];
        for (int i = 0; i < 10; i++) {
            temp = feistelRound(left, right);
            left = temp;
            temp = left;
        }
        byte[] result = new byte[n];
        System.arraycopy(left, 0, result, 0, half);
        System.arraycopy(right, 0, result, half, half);

        return Base64.getEncoder().encodeToString(result);
    }

    private byte[] feistelRound(byte[] left, byte[] right) {
        byte[] result = new byte[left.length];
        for (int i = 0; i < left.length; i++) {
            result[i] = (byte) (left[i] ^ right[i]);
        }
        return result;
    }

    private String generateSalt() {
        Random rand = new Random();
        byte[] salt = new byte[16];
        rand.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }


    public byte[] sha256Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
            PasswordManagerApp app = new PasswordManagerApp();
            app.setVisible(true);
    }
}
