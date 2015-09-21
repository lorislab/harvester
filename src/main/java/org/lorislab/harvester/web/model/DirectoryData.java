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
package org.lorislab.harvester.web.model;

import java.util.Set;

/**
 *
 * @author Andrej_Petras
 */
public class DirectoryData {
    
    private String name;
    
    private boolean exists;
    
    private Set<DatasourceDirectoryData> datasources;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<DatasourceDirectoryData> getDatasources() {
        return datasources;
    }

    public void setDatasources(Set<DatasourceDirectoryData> datasources) {
        this.datasources = datasources;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }
    
}
