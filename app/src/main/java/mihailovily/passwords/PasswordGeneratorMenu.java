package mihailovily.passwords;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


public class PasswordGeneratorMenu extends JDialog {
    private final JTextField siteField = new JTextField("", 20);
    private final JTextField result = new JTextField("", 20);

    public PasswordGeneratorMenu(String saltedLogin, String saltedPassword, boolean useRandomPassword) {
        // init GUI
        JPanel contentPanel = new JPanel();
        setContentPane(contentPanel);
        JButton buttonGet = new JButton("Получить");
        getRootPane().setDefaultButton(buttonGet);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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

        if (useRandomPassword) {
            System.out.println(generateRandomSalt(10));
        }

        // Оживляем кнопку
        buttonGet.addActionListener(e -> { // при нажатии кнопки генерируем пароль на основе домена и кредов пользователя
            try {
                if (useRandomPassword) {
                    result.setText(genPass(saltedLogin + generateRandomSalt(10), saltedPassword)); // при нажатии кнопки генерируем пароль на основе домена и кредов пользователя
                }
                else {
                    result.setText(genPass(saltedLogin, saltedPassword));
                }
                } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Прервать процесс при закрытии окна
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });


    }


    private String genPass(String login, String password) throws NoSuchAlgorithmException {
        return PasswordManagerApp.getSHA256Hash(login + siteField.getText() + password);
    }

    public String generateRandomSalt(int targetLength) {
        int leftLimit = 97; // от 'a'
        int rightLimit = 122; // до 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetLength);
        for (int i = 0; i < targetLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    public static void main(String[] args) {
        PasswordGeneratorMenu dialog = new PasswordGeneratorMenu(args[0], args[1], Boolean.parseBoolean(args[2]));
        dialog.setSize(300, 300);
        dialog.setVisible(true);
    }


}
