package mihailovily.passwords;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.NoSuchAlgorithmException;


public class PasswordGeneratorMenu extends JDialog {
    private final JTextField siteField = new JTextField("", 20);
    private final JTextField result = new JTextField("", 20);

    public PasswordGeneratorMenu(String login, String salt) {
        JPanel contentPanel = new JPanel();
        setContentPane(contentPanel);
        JButton buttonGet = new JButton("Получить");
        getRootPane().setDefaultButton(buttonGet);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buttonGet.addActionListener(e -> {
            try {
                getPass(login, salt);
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
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
        String safe_password = login + siteField.getText() + salt;
        safe_password = PasswordManagerApp.getSHA256Hash(safe_password);
        result.setText(safe_password);

    }

    public static void main(String[] args) {
        PasswordGeneratorMenu dialog = new PasswordGeneratorMenu(args[0], args[1]);
        dialog.setSize(300, 300);
        dialog.setVisible(true);
    }


}
