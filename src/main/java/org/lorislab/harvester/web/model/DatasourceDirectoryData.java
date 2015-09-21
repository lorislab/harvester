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
public class DatasourceDirectoryData {
    
    private String name;
    
    private Set<String> files;
    
    private String jndi;
    
    private String error;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getFiles() {
        return files;
    }

    public void setFiles(Set<String> files) {
        this.files = files;
    }

    public String getJndi() {
        return jndi;
    }

    public void setJndi(String jndi) {
        this.jndi = jndi;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    
}
