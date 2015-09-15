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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The harvester properties.
 *
 * @author Andrej_Petras
 */
public final class HarvesterProperties {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(HarvesterProperties.class.getName());

    /**
     * The root directory property name.
     */
    private static final String DIRECTORY_PROPERTY_NAME = "org.lorislab.harvester.directory";

    /**
     * The root directory property default value.
     */
    private static final String DIRECTORY_PROPERTY_DEFAULT = "harvester";

    /**
     * The harvester properties.
     */
    private static final String PROPERTY_FILE_NAME = "harvester.properties";

    /**
     * The default constructor.
     */
    private HarvesterProperties() {
        // empty constructor
    }

    /**
     * Gets the root directory.
     *
     * @return the root directory.
     */
    public static String getRootDirectory() {
        return System.getProperty(DIRECTORY_PROPERTY_NAME, DIRECTORY_PROPERTY_DEFAULT);
    }

    /**
     * Gets the harvester properties.
     *
     * @param path the directory path.
     * @return the corresponding harvester properties.
     */
    public static Properties getHarvesterProperties(Path path) {
        Properties result = new Properties();
        try {
            Path file = path.resolve(PROPERTY_FILE_NAME);
            if (Files.exists(path)) {
                result.load(Files.newInputStream(file));
            } else {
                LOGGER.log(Level.WARNING, "Missing property file: {0}", file.toString());
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error reading the " + PROPERTY_FILE_NAME, ex);
        }
        return result;
    }
}
