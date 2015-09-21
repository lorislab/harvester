/*
 * Copyright 2015 Andrej_Petras.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lorislab.harvester.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;

import org.dbunit.operation.DatabaseOperation;

/**
 * The DB-Unit executor utility.
 *
 * @author Andrej_Petras
 */
public final class DbUnitExecutorUtil {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(DbUnitExecutorUtil.class.getName());

    /**
     * The type factory.
     */
    private static final Map<String, IDataTypeFactory> TYPE_FACTORY = new HashMap<>();

    /**
     * The type factory static block.
     */
    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            TYPE_FACTORY.put("Oracle", new Oracle10DataTypeFactory());
            LOGGER.log(Level.INFO, "Oracle driver install.");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.INFO, "Oracle driver not found.");
        }

        TYPE_FACTORY.put("H2", new H2DataTypeFactory());
        LOGGER.log(Level.INFO, "H2 driver install.");
    }

    /**
     * The operation.
     */
    private static final Map<String, DatabaseOperation> OPERATION = new HashMap<>();

    /**
     * Static block for the operation.
     */
    static {
        OPERATION.put("insert", DatabaseOperation.INSERT);
        OPERATION.put("refresh", DatabaseOperation.REFRESH);
        OPERATION.put("cleanInsert", DatabaseOperation.CLEAN_INSERT);
        OPERATION.put("delete", DatabaseOperation.DELETE);
        OPERATION.put("deleteAll", DatabaseOperation.DELETE_ALL);
        OPERATION.put("runcateTable", DatabaseOperation.TRUNCATE_TABLE);
        OPERATION.put("update", DatabaseOperation.UPDATE);
    }

    /**
     * The default constructor.
     */
    private DbUnitExecutorUtil() {
        // empty constructor.
    }

    /**
     * Executes the operation in the connection
     *
     * @param connection the connection.
     * @param operation the operation.
     * @param data the data.
     * @throws Exception if the method fails.
     */
    public static void execute(Connection connection, String operation, byte[] data) throws Exception {
        IDatabaseConnection idbConnection = getIDatabaseConnection(connection);

        try (InputStream in = new ByteArrayInputStream(data)) {

            IDataSet dataSet = new XlsDataSet(in);

            DatabaseOperation dbOperation = OPERATION.get(operation);
            if (dbOperation != null) {
                dbOperation.execute(idbConnection, dataSet);
            } else {
                LOGGER.log(Level.WARNING, "The operation: {0} is not registred", operation);
            }
        }
    }

    /**
     * Executes the operation in the connection
     *
     * @param connection the connection.
     * @param operation the operation.
     * @param fileName the file name.
     */
    public static void execute(Connection connection, String operation, String fileName) throws Exception {
        Path path = Paths.get(fileName);
        if (Files.isReadable(path)) {
            execute(connection, operation, path);
        } else {
            LOGGER.log(Level.SEVERE, "The {0} is not regular file.", fileName);
        }
    }

    /**
     * Executes the operation in the connection
     *
     * @param connection the connection.
     * @param operation the operation.
     * @param path the file path.
     */
    public static void execute(Connection connection, String operation, Path path) throws Exception {
        LOGGER.log(Level.INFO, "Execute file: {0}", path.toString());

        IDatabaseConnection idbConnection = getIDatabaseConnection(connection);
//        try (InputStream in = Files.newInputStream(path)) {
            IDataSet dataSet = new CsvDataSet(path.toFile());

            DatabaseOperation dbOperation = OPERATION.get(operation);
            if (dbOperation != null) {
                dbOperation.execute(idbConnection, dataSet);
            } else {
                throw new Exception("The operation: " + operation + " is not registred");
            }
//        }
    }

    /**
     * Gets ID database connection.
     *
     * @param connection the database connection.
     * @return the ID database connection.
     * @throws Exception if the method fails.
     */
    private static IDatabaseConnection getIDatabaseConnection(Connection connection) throws Exception {
        IDatabaseConnection result = new DatabaseConnection(connection);

        IDataTypeFactory idf = null;

        DatabaseMetaData metadata = connection.getMetaData();
        String dbName = metadata.getDatabaseProductName();

        Iterator<String> iter = TYPE_FACTORY.keySet().iterator();
        while (idf == null && iter.hasNext()) {
            String key = iter.next();
            if (dbName.contains(key)) {
                idf = TYPE_FACTORY.get(key);
            }
        }

        if (idf != null) {
            LOGGER.log(Level.FINE, "Use the data type factory: {0}", idf.getClass().getName());
            DatabaseConfig config = result.getConfig();
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, idf);
        } else {
            LOGGER.log(Level.FINE, "Use the default data type factory.");
        }
        return result;
    }

}
