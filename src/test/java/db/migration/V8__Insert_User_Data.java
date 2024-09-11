package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import java.sql.PreparedStatement;

public class V8__Insert_User_Data extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {

        String secretKey = System.getenv("PASS_CRYPT_SECRET");

        if (secretKey == null) {
            throw new IllegalStateException("PASS_CRYPT_SECRET environment variable is not set.");
        }

        PasswordEncoder passwordEncoder = new Pbkdf2PasswordEncoder(
                secretKey, 8, 185000,
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256
        );

        String encryptedPassword = passwordEncoder.encode("TestAdmin123");

        try (PreparedStatement statement = context.getConnection().prepareStatement(
                "INSERT INTO users (user_name, full_name, password, account_non_expired, account_non_locked," +
                        " credentials_non_expired, enabled) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, "TestAdmin");
            statement.setString(2, "TestAdminFullName");
            statement.setString(3, encryptedPassword);
            statement.setBoolean(4, true);
            statement.setBoolean(5, true);
            statement.setBoolean(6, true);
            statement.setBoolean(7, true);
            statement.execute();
        }
    }
}