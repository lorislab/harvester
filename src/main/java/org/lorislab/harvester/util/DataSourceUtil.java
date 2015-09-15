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

import java.sql.Connection;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * The data source utility.
 *
 * @author Andrej_Petras
 */
public final class DataSourceUtil {

    /**
     * The default constructor.
     */
    private DataSourceUtil() {
        // empty constructor
    }

    /**
     * Gets the connection.
     *
     * @param jndi the JNDI.
     * @return the corresponding connection.
     * @throws Exception if the method fails.
     */
    public static Connection getConnection(String jndi) throws Exception {
        DataSource result = getDataSource(jndi);
        return result.getConnection();
    }

    /**
     * Gets the connection.
     *
     * @param jndi the JNDI.
     * @return the corresponding connection.
     * @throws Exception if the method fails.
     */
    public static DataSource getDataSource(String jndi) throws Exception {
        InitialContext initialContext = new InitialContext();
        return (DataSource) initialContext.lookup(jndi);
    }
}
