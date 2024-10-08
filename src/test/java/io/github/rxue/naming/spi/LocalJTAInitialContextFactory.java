package io.github.rxue.naming.spi;

import org.mariadb.jdbc.MariaDbDataSource;
import io.github.rxue.ConfigurationUtil;

import javax.naming.*;
import javax.naming.spi.InitialContextFactory;
import java.sql.SQLException;
import java.util.*;

public class LocalJTAInitialContextFactory implements InitialContextFactory {
    @Override
    public Context getInitialContext(Hashtable<?, ?> hashtable) {
        Context context = new MemoryContext();
        try {
            MariaDbDataSource ds = new MariaDbDataSource(ConfigurationUtil.getJdbcURL());
            context.bind("mariaDBDataSource", ds);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context;
    }
}
