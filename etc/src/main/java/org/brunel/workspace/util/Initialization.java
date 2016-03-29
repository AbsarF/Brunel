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

package org.brunel.workspace.util;

import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.LoggerProvider;
import org.brunel.workspace.db.Store;
import org.brunel.workspace.item.Item;
import org.brunel.workspace.item.Stored;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.swing.*;
import java.util.logging.Handler;

/**
 * Helpful stuff for UIs
 */
public class Initialization {

    private static final Logger logger = LoggerFactory.getLogger(Initialization.class);

    public static void initializeJXBrowser() {
        // Allow local file access
        logger.debug("Disabling chromium local security");
        BrowserPreferences.setChromiumSwitches("--disable-web-security", "--allow-file-access-from-files");

        // Bridge the old java.util.logging calls to SLF4J
        logger.debug("Re-directing JxBrowser logging to SLF4J");
        SLF4JBridgeHandler handler = new SLF4JBridgeHandler();
        setHandler(LoggerProvider.getBrowserLogger(), handler);
        setHandler(LoggerProvider.getIPCLogger(), handler);
        setHandler(LoggerProvider.getChromiumProcessLogger(), handler);
    }

    public static void initializeUI(String name) {
        String lcOSName = System.getProperty("os.name").toLowerCase();
        boolean IS_MAC = lcOSName.startsWith("mac os x");
        if (IS_MAC) logger.info("Detected Mac OS X");

        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", name);

        logger.debug("Setting system Look and feel");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // I guess we won't have anything nice
        }

    }

    public static void initializeLogging() {
        logger.debug("Bridging java.util.logging calls to SLF4J");
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private static void setHandler(java.util.logging.Logger logger, Handler handler) {
        Handler[] handlers = logger.getHandlers();
        for (Handler h : handlers) logger.removeHandler(h);
        logger.addHandler(handler);
    }

    public static void initializeTable(Stored<Item> a, Store store) {
        String tableName = a.getTableName();
        String definition = a.getTableDefinition();
        logger.debug("Ensuring table exists: " + tableName);
        store.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" + definition + ");");

    }
}
