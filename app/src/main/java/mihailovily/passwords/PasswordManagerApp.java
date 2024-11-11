package mihailovily.passwords;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordManagerApp extends JFrame {
    private final JTextField loginField;
    private final JPasswordField passwordField;
    private final JComboBox<String> encryptionComboBox;

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

        loginButton.addActionListener(e -> {
            String login = loginField.getText();
            String password = new String(passwordField.getPassword());
            String encryptionMethod = (String) encryptionComboBox.getSelectedItem();

            if (login.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both login and password.");
                return;
            }

            try {
                switch (encryptionMethod) {
                    case "Base64":
                        login = encryptBase64(login);
                        password = encryptBase64(password);
                        break;
                    case "MD5":
                        login = encryptMD5(login);
                        password = encryptMD5(password);
                        break;
                    case "Фейстель":
                        login = encryptFeistel(login);
                        password = encryptFeistel(password);
                        break;
                    case "B64 с солью":
                        String salted_login = getSHA256Hash(login);
                        password = encryptWithSalt(password, salted_login);
                        break;
                    case null:
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + encryptionMethod);
                }
                PasswordGeneratorMenu dialog = new PasswordGeneratorMenu(login, password);
                dialog.pack();
                dialog.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Ошибка");
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

    private String encryptWithSalt(String password, String salt) {
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

        byte[] temp;
        for (int i = 0; i < 10; i++) {
            temp = feistelRound(left, right);
            left = temp;
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

    public static String getSHA256Hash(String input) throws NoSuchAlgorithmException {
        // Получаем объект MessageDigest для алгоритма SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // Преобразуем строку в массив байтов и вычисляем хэш
        byte[] hashBytes = digest.digest(input.getBytes());

        // Преобразуем байты хэша в строку в шестнадцатеричном формате
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }

    public static void main(String[] args) {
            PasswordManagerApp app = new PasswordManagerApp();
            app.setVisible(true);
    }
}
