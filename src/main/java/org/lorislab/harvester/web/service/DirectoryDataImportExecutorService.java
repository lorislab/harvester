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
package org.lorislab.harvester.web.service;

import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.lorislab.harvester.util.DataSourceUtil;
import org.lorislab.harvester.util.DbUnitExecutorUtil;
import org.lorislab.harvester.util.HarvesterProperties;
import org.lorislab.harvester.web.events.OnCloseEvent;
import org.lorislab.harvester.web.events.OnOpenEvent;
import org.lorislab.harvester.web.events.OnUpdateEvent;
import org.lorislab.harvester.web.model.DatasourceDirectoryData;
import org.lorislab.harvester.web.model.DirectoryData;
import org.lorislab.harvester.web.model.ImportLogItem;

/**
 *
 * @author Andrej_Petras
 */
@Stateless
public class DirectoryDataImportExecutorService {

    /**
     * The EXCEL file matcher.
     */
    private static final PathMatcher MATCHER_EXCEL = FileSystems.getDefault().getPathMatcher("glob:**.csv");

    private static final Logger LOGGER = Logger.getLogger(DirectoryDataImportExecutorService.class.getName());

    @Inject
    @OnUpdateEvent
    private Event<ImportLogItem> events;

    @Inject
    @OnOpenEvent
    private Event<String> openEvents;

    @Inject
    @OnCloseEvent
    private Event<String> closeEvents;

    @Asynchronous
    public void executeOperation(String operation) {
        try {
            LOGGER.info("Start");
            openEvents.fire("Start");

            String dir = HarvesterProperties.getRootDirectory();

            Path root = Paths.get(dir);
            if (Files.exists(root)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(root, Files::isDirectory)) {
                    for (Path entry : stream) {
                        ImportLogItem log = new ImportLogItem();                        
                        log.setFile(entry.toString());
                        log.setOperation(operation);
                        
                        String dirName = entry.getFileName().toString();
                        try {
                            LOGGER.log(Level.INFO, "Start import directory: {0}", dirName);

                            Properties properties = HarvesterProperties.getHarvesterProperties(entry);
                            String jndi = properties.getProperty("jndi");                            
                            log.setJndi(jndi);                            
                            
                            LOGGER.log(Level.INFO, "JNDI: {0}", jndi);
                            try (Connection connection = DataSourceUtil.getConnection(jndi)) {
                                if (Files.isDirectory(entry)) {
                                        DbUnitExecutorUtil.execute(connection, operation, entry);                                                                                                                        
                                } else {
                                    LOGGER.log(Level.WARNING, "The data is not directory {0}", entry.toString());
                                }
                            }

                        } catch (Exception ex) {
                            LOGGER.log(Level.SEVERE, "Error execute absolute for the JNDI: {0} error: {1}", new Object[]{dirName, ex.getMessage()});
                            LOGGER.log(Level.FINEST, "Error: " + ex.getMessage(), ex);
                            log.setMessage(ex.toString());                            
                        }
                        
                        events.fire(log);
                    }
                }
            } else {
                LOGGER.log(Level.WARNING, "The harvester root directory does not exists. Directory: {0}", root.toString());
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        closeEvents.fire("Close");
        LOGGER.info("Close");
    }

    public DirectoryData getDirectoryData() throws Exception {
        DirectoryData result = new DirectoryData();
        String dir = HarvesterProperties.getRootDirectory();

        Path root = Paths.get(dir);
        result.setName(root.toAbsolutePath().toString());

        boolean exists = Files.exists(root);
        result.setExists(exists);

        if (exists) {
            result.setDatasources(new HashSet<>());

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(root, Files::isDirectory)) {
                for (Path entry : stream) {

                    DatasourceDirectoryData item = new DatasourceDirectoryData();
                    item.setName(entry.getFileName().toString());
                    item.setFiles(new HashSet<>());

                    try {
                        Properties properties = HarvesterProperties.getHarvesterProperties(entry);
                        String jndi = properties.getProperty("jndi");
                        item.setJndi(jndi);

                        try (DirectoryStream<Path> tmp = Files.newDirectoryStream(entry, (Path file) -> {
                            return Files.isRegularFile(file) && MATCHER_EXCEL.matches(file);
                        })) {
                            tmp.forEach((file) -> item.getFiles().add(file.getFileName().toString()));
                        }

                        DataSource ds = DataSourceUtil.getDataSource(jndi);

                    } catch (Exception ex) {
                        item.setError(ex.toString());
                    }

                    result.getDatasources().add(item);
                }
            }
        }
        return result;
    }
}
