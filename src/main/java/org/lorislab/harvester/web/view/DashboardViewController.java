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
package org.lorislab.harvester.web.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import org.lorislab.harvester.web.events.OnCloseEvent;
import org.lorislab.harvester.web.events.OnOpenEvent;
import org.lorislab.harvester.web.events.OnUpdateEvent;
import org.lorislab.harvester.web.events.UpdateViewEvent;
import org.lorislab.harvester.web.model.DirectoryData;
import org.lorislab.harvester.web.model.ImportLogItem;
import org.lorislab.harvester.web.service.DirectoryDataImportExecutorService;

/**
 *
 * @author Andrej_Petras
 */
@Named(value = "dashboard")
@ApplicationScoped
public class DashboardViewController implements Serializable {

    private static final long serialVersionUID = -4169427096137734767L;

    private static final Logger LOGGER = Logger.getLogger(DashboardViewController.class.getName());
        
    @Inject
    @UpdateViewEvent
    private Event<String> events;
    
    @Inject
    private DirectoryDataImportExecutorService service;

    private DirectoryData directory;

    private boolean running;
    
    private List<ImportLogItem> logs;

    public List<ImportLogItem> getLogs() {
        return logs;
    }

    public boolean isRunning() {
        return running;
    }
            
    @PostConstruct
    public void reloadDirectory() {
        try {
            directory = null;
            directory = service.getDirectoryData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public DirectoryData getDirectory() {
        return directory;
    }
    
    public synchronized void startImportData() {
        if (!running) {
            LOGGER.info("###################### START ################### ");
            running = true;
            logs = new ArrayList<>();
            service.executeOperation("insert");
            LOGGER.info("###################### START ################### ");            
        }
    }
    
    public void onUpdateEvent(@Observes @OnUpdateEvent ImportLogItem log) {
        logs.add(log);
        events.fire("logs");
    } 
    
    public void onOpenEvent(@Observes @OnOpenEvent String message) {
        LOGGER.info("Start");
        events.fire("start");
    } 
    
    public void onCloseEvent(@Observes @OnCloseEvent String message) {
        LOGGER.info("Close");
        running = false;
        events.fire("close");
    }    
}
