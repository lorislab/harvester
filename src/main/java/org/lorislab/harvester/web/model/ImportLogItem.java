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

import java.io.Serializable;

/**
 *
 * @author Andrej_Petras
 */
public class ImportLogItem implements Serializable {
    
    private static final long serialVersionUID = -3094173919003877283L;
    
    private String jndi;
    
    private String file;
    
    private String message;

    private String operation;
    
    /**
     * @return the jndi
     */
    public String getJndi() {
        return jndi;
    }

    /**
     * @param jndi the jndi to set
     */
    public void setJndi(String jndi) {
        this.jndi = jndi;
    }

    /**
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
        
}
