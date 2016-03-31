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

package org.brunel.workspace.component;

import org.brunel.app.brunel.Settings;
import org.brunel.workspace.util.ExpandablePanel;
import org.brunel.workspace.util.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * The Frame for the workspace
 */
public class WorkspaceFrame extends JFrame {

    private final BrunelDisplayPanel brunel;

    public WorkspaceFrame(Settings settings, BrunelDisplayPanel brunel, ItemsPanel itemsPanel, BuilderPanel statusPanel) {
        this.brunel = brunel;
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UI.BACKGROUND);

        JPanel inner = new JPanel(new BorderLayout());
        inner.setBackground(UI.BACKGROUND);

        inner.add(new ExpandablePanel("Status", statusPanel, settings, false), BorderLayout.SOUTH);
        inner.add(brunel, BorderLayout.CENTER);

        main.add(new ExpandablePanel("Panels", itemsPanel, settings, true), BorderLayout.EAST);
        main.add(inner, BorderLayout.CENTER);

        setContentPane(main);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);
        menubar.add(makeUtilityMenu());
    }

    private JMenu makeUtilityMenu() {
        JMenu menu = new JMenu("Do");

        AbstractAction a = new AbstractAction("Show Web Page") {
            public void actionPerformed(ActionEvent e) {
                String[] pages = UI.ask(brunel, "Go to URL",
                        new String[]{"Internet Address", null, "Enter a fully-qualified URL (with http://)"});
                if (pages != null) brunel.getBrowser().loadURL(pages[0]);
            }
        };
        menu.add(a);

        return menu;
    }
}
