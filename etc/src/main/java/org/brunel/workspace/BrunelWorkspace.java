/*
 * Copyright (c) 2016 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brunel.workspace;

import org.brunel.app.brunel.Settings;
import org.brunel.build.util.BuilderOptions;
import org.brunel.workspace.activity.Activity;
import org.brunel.workspace.component.BrunelDisplayPanel;
import org.brunel.workspace.component.ItemsPanel;
import org.brunel.workspace.component.BuilderPanel;
import org.brunel.workspace.component.WorkspaceFrame;
import org.brunel.workspace.db.Store;
import org.brunel.workspace.item.Item;
import org.brunel.workspace.item.ItemChart;
import org.brunel.workspace.item.Stored;
import org.brunel.workspace.util.Initialization;
import org.brunel.workspace.util.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Collections;

public class BrunelWorkspace {

    private static final Logger logger = LoggerFactory.getLogger(BrunelWorkspace.class);
    private final WorkspaceFrame frame;

    public static void main(String[] args) {
        Initialization.initializeLogging();
        Initialization.initializeUI("Brunel Workspace");
        Initialization.initializeJXBrowser();

        logger.debug("Building the Workspace");
        BuilderOptions options = BuilderOptions.make(args);
        final BrunelWorkspace workspace = new BrunelWorkspace(options);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                workspace.start();
            }
        });

    }

    private void start() {
        frame.setVisible(true);
    }

    private BrunelWorkspace(BuilderOptions options) {

        Settings settings = new Settings(BrunelWorkspace.class);
        Activity activity = new Activity();

        Store store = new Store();
        Stored<Item>[] itemSets = Stored.makeStores(store, activity);
        for (Stored<Item> a : itemSets) {
            Initialization.initializeTable(a, store);
            String tableName = ItemChart.DEFINITION.tableName;
            if (a.getTableName().equals(tableName) && store.getTableSize(tableName) == 0) {
                logger.info("Creating initial charts");
                Collections.addAll(a, ItemChart.makeInitialCharts(activity));
            }
        }

        BrunelDisplayPanel brunel = new BrunelDisplayPanel(options, activity);
        ItemsPanel itemsPanel = new ItemsPanel(itemSets, activity, settings);
        BuilderPanel statusPanel = new BuilderPanel(activity);

        this.frame = new WorkspaceFrame(settings, brunel, itemsPanel, statusPanel);
        Dimension size = UI.getScreenSize();
        int inset = (int) (50 * Math.round(size.getHeight() * 0.1 / 50));
        settings.persistWindowLocation(frame, "frame-location",
                inset, inset, size.width - 2 * inset, size.height - 2 * inset);
    }

}
