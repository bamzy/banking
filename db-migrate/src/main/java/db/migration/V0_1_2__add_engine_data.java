package db.migration;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.io.DataInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by Bamdad on 8/8/2015.
 */
public class V0_1_2__add_engine_data implements JdbcMigration {

    @Override
    public void migrate(Connection connection) throws Exception {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("SANJAQ1_0_6.AGN");
        DataInputStream dataInputStream = new DataInputStream(resourceAsStream);
        byte[] bytes = new byte[123464];
        dataInputStream.readFully(bytes);
        PreparedStatement statement =

                connection.prepareStatement("INSERT INTO tms.engine (id, name, description, version, data,checksum, dataLength) VALUES ('1','Default', 'نسخه پیش فرض', '5',?,'dfajkh234lkdfasdfkjhvadr--f9da0-AAAA','123464')");
        statement.setBytes(1, bytes);
        try {
            statement.execute();
        } catch (Exception ex) {
            ex.getMessage();
        } finally {
            statement.close();
        }

    }
}
