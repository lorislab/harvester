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
package org.lorislab.harvester.web.rs.service;

import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.lorislab.harvester.util.DataSourceUtil;
import org.lorislab.harvester.util.HarvesterProperties;
import org.lorislab.harvester.util.DbUnitExecutorUtil;

/**
 * The directory data import service.
 * 
 * @author Andrej_Petras
 */
@Stateless(name = "DirectoryDataImportService")
@Local(DirectoryDataImportService.class)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class DirectoryDataImportServiceBean implements DirectoryDataImportService {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(DirectoryDataImportService.class.getName());

    /**
     * The EXCEL file matcher.
     */
    private static final PathMatcher MATCHER_EXCEL = FileSystems.getDefault().getPathMatcher("glob:**.xls");

    /**
     * {@inheritDoc }
     */
    @Override
    public void executeOperation(String operation) throws Exception {
        String dir = HarvesterProperties.getRootDirectory();
        
        Path root = Paths.get(dir);
        if (Files.exists(root)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(root, Files::isDirectory)) {
                for (Path entry : stream) {

                    String dirName = entry.getFileName().toString();
                    try {
                        LOGGER.log(Level.INFO, "Start import directory: {0}", dirName);

                        Properties properties = HarvesterProperties.getHarvesterProperties(entry);
                        
                        String jndi = properties.getProperty("jndi");
                        LOGGER.log(Level.INFO, "JNDI: {0}", jndi);
                        Connection connection = DataSourceUtil.getConnection(jndi);

                        if (Files.isDirectory(entry)) {

                            try (DirectoryStream<Path> tmp = Files.newDirectoryStream(entry, (Path file) -> {
                                return Files.isRegularFile(file) && MATCHER_EXCEL.matches(file);
                            })) {
                                tmp.forEach((file) -> DbUnitExecutorUtil.execute(connection, operation, file));
                            }

                        } else {
                            LOGGER.log(Level.WARNING, "The data is not directory {0}", entry.toString());
                        }

                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Error execute absolute for the JNDI: {0} error: {1}", new Object[]{dirName, ex.getMessage()});
                        LOGGER.log(Level.FINEST, "Error: " + ex.getMessage(), ex);
                    }
                }
            }
        }
    }

}
