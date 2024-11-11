package mihailovily.passwords;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class PasswordGeneratorMenu extends JDialog {
    private JPanel contentPanel = new JPanel();
    private JPanel topPanel = new JPanel();
    private JButton buttonGet = new JButton("Получить");;
    private JTextField siteField = new JTextField("", 20);
    private JTextField result = new JTextField("", 20);

    public PasswordGeneratorMenu(String login, String salt) {
        setContentPane(contentPanel);
        getRootPane().setDefaultButton(buttonGet);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buttonGet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    getPass(login, salt);
                } catch (NoSuchAlgorithmException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Сайт:"));
        topPanel.add(siteField);
        JPanel middlePanel = new JPanel();
        middlePanel.add(new JLabel("Пароль:"));
        middlePanel.add(result);
        result.setEditable(false);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(buttonGet);

        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }


    private void getPass(String login, String salt) throws NoSuchAlgorithmException {
        String clientpassword = login + siteField.getText() + salt;
        System.out.println(getSHA256Hash(clientpassword));
        clientpassword = encryptMD5(clientpassword);
        result.setText(clientpassword);

        System.out.println("Пароль: " + salt);

    }

    public static void main(String[] args) {
        PasswordGeneratorMenu dialog = new PasswordGeneratorMenu(args[0], args[1]);
        dialog.setSize(300, 300);
        dialog.setVisible(true);
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

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
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
}
