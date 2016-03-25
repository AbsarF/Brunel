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

import org.brunel.app.brunel.Settings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Simple expandable item
 */
public class ExpandablePanel extends JPanel {

    private final JComponent main;
    private final Settings settings;
    private final JButton button;
    private final String settingsKey;
    private final GradientStrip buttonBox;

    public ExpandablePanel(String title, JComponent content, Settings settings, boolean horizontal) {
        super(new BorderLayout());
        this.main = content;
        this.settings = settings;
        this.button = new JButton();
        this.settingsKey = title + "-expandable-status";

        button.setToolTipText(title);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean visible = !main.isVisible();
                setStatus(visible);
            }
        });

        buttonBox = new GradientStrip(horizontal);
        if (horizontal) {
            buttonBox.add(Box.createHorizontalGlue());
            buttonBox.add(button);

            Box outer = Box.createVerticalBox();
            outer.setBorder(new EmptyBorder(4, 4, 4, 4));
            outer.add(buttonBox);

            add(outer, BorderLayout.NORTH);
            add(main, BorderLayout.CENTER);
        } else {
            buttonBox.add(Box.createVerticalGlue());
            buttonBox.add(button);

            Box outer = Box.createHorizontalBox();
            outer.setBorder(new EmptyBorder(4, 4, 4, 4));
            outer.add(buttonBox);

            add(outer, BorderLayout.WEST);

        }
        add(main, BorderLayout.CENTER);
        setOpaque(false);
        setStatus("true".equalsIgnoreCase(settings.getString(settingsKey)));
    }

    public void setStatus(boolean open) {
        button.setText(open ? "\u25bc" : "\u25ba");
        button.setBorder(new EmptyBorder(2, 2, 2, 2));
        main.setVisible(open);
        buttonBox.setOpaque(open);

        settings.putString(settingsKey, Boolean.toString(open));
    }

}
