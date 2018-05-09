package entity;

import com.google.inject.AbstractModule;
import common.DBConnection;

import java.sql.Connection;

public class DIModule extends AbstractModule {

    private static final DIModule INSTANCE = new DIModule();

    private DIModule() {
    }

    public static DIModule getInstance() {
        return INSTANCE;
    }

    @Override
    protected void configure() {
        bind(Connection.class).toInstance(DBConnection.createConnection());
    }
}