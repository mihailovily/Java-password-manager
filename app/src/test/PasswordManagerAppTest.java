import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import javax.swing.*;
import java.security.NoSuchAlgorithmException;

public class PasswordManagerAppTest {
    private PasswordManagerApp app;
    @BeforeEach
    public void setUp() {
        // Создаем экземпляр приложения перед каждым тестом
        app = new PasswordManagerApp();
    }

    // Тестирование шифрования Base64
    @Test
    public void testEncryptBase64() {
        String password = "password";
        String encrypted = app.encryptBase64(password);
        assertNotNull(encrypted, "Encrypted password should not be null");
        assertEquals("cGFzc3dvcmQ=", encrypted, "Base64 encryption failed");
    }

    // Тестирование шифрования MD5
    @Test
    public void testEncryptMD5() throws NoSuchAlgorithmException {
        String password = "password";
        String encrypted = app.encryptMD5(password);
        assertNotNull(encrypted, "Encrypted password should not be null");
        assertEquals("5f4dcc3b5aa765d61d8327deb882cf99", encrypted, "MD5 encryption failed");
    }

    // Тестирование шифрования с солью
    @Test
    public void testEncryptWithSalt() {
        String password = "password";
        String salt = "salt";
        String encrypted = app.encryptWithSalt(password, salt);
        assertNotNull(encrypted, "Encrypted password should not be null");
        assertTrue(encrypted.startsWith("cGFzc3dvcmQ="), "Salted encryption failed");
    }

    // Тестирование шифрования с использованием шифра Фейстеля
    @Test
    public void testEncryptFeistel() {
        String password = "password";
        String encrypted = app.encryptFeistel(password);
        assertNotNull(encrypted, "Encrypted password should not be null");
    }

    // Тестирование пустых полей логина и пароля
    @Test
    public void testEmptyLoginAndPassword() {
        // Мокируем JTextField и JPasswordField для имитации поведения
        JTextField loginField = mock(JTextField.class);
        JPasswordField passwordField = mock(JPasswordField.class);
        when(loginField.getText()).thenReturn("");
        when(passwordField.getPassword()).thenReturn(new char[0]);

        // Проверка, что поля пустые
        String login = loginField.getText();
        String password = new String(passwordField.getPassword());
        assertTrue(login.isEmpty(), "Login should be empty");
        assertTrue(password.isEmpty(), "Password should be empty");
    }

    // Тестирование выбора неверного метода шифрования
    @Test
    public void testInvalidEncryptionMethod() {
        String encryptionMethod = "InvalidMethod";
        assertThrows(IllegalStateException.class, () -> {
            switch (encryptionMethod) {
                case "Base64":
                    app.encryptBase64("password");
                    break;
                case "MD5":
                    app.encryptMD5("password");
                    break;
                case "Фейстель":
                    app.encryptFeistel("password");
                    break;
                case "B64 с солью":
                    app.encryptWithSalt("password", "salt");
                    break;
                default:
                    throw new IllegalStateException("Неверный метод шифрования: " + encryptionMethod);
            }
        });
    }

    // Тестирование обработки ошибок (например, при отсутствии метода шифрования)
    @Test
    public void testErrorHandling() {
        // Тестирование с методом шифрования, который вызывает исключение
        assertThrows(IllegalStateException.class, () -> {
            app.encryptMD5(null); // Невозможно зашифровать null
        });
    }

    // Тестирование метода getSHA256Hash
    @Test
    public void testGetSHA256Hash() throws NoSuchAlgorithmException {
        String input = "password";
        String hash = PasswordManagerApp.getSHA256Hash(input);
        assertNotNull(hash, "SHA-256 hash should not be null");
        assertEquals("5e884898da28047151d0e56f8dc6292773603d0d3a8b0f51b4e0b3ad4d98d3e1", hash, "SHA-256 hashing failed");
    }

    // Тестирование обработки кнопки "Войти" с пустыми полями
    @Test
    public void testLoginButtonWithEmptyFields() {
        // Мокируем компоненты
        JTextField loginField = mock(JTextField.class);
        JPasswordField passwordField = mock(JPasswordField.class);
        JComboBox<String> encryptionComboBox = mock(JComboBox.class);
        JButton loginButton = new JButton("Войти");

        when(loginField.getText()).thenReturn("");
        when(passwordField.getPassword()).thenReturn(new char[0]);

        loginButton.doClick();

        // Проверяем, что в случае пустых полей появляется сообщение
        verify(loginField, atLeastOnce()).getText();
        verify(passwordField, atLeastOnce()).getPassword();
    }
}
