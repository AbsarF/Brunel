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

import org.brunel.workspace.item.ItemSet;
import org.brunel.workspace.util.UI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by graham on 3/24/16.
 */
public class ItemsPanel extends JPanel {

    public ItemsPanel(ItemSet... categories) {
        super(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        setOpaque(false);
        cons.gridx = 0;
        cons.gridy = GridBagConstraints.RELATIVE;
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.weighty = 0.0;

        // Chooser for which to show
        final JComboBox chooser = new JComboBox<>(categories);
        chooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ItemSet items = (ItemSet) chooser.getSelectedItem();
                showItems(items);
            }
        });

        add(chooser, cons);

        cons.weighty = 1.0;
        cons.fill = GridBagConstraints.BOTH;
        JPanel contents = new JPanel();
        contents.setBackground(UI.BACKGROUND);
        contents.setBorder(new CompoundBorder(new EmptyBorder(2,2,2,2),new LineBorder(UI.CONTROLS)));
        add(contents, cons);

        cons.weighty = 0.0;
        cons.fill = GridBagConstraints.HORIZONTAL;

        JPanel buttons = new JPanel();
        buttons.setBackground(UI.BACKGROUND);
        buttons.add(new JButton("+"));
        buttons.add(new JButton("-"));
        add(buttons, cons);
    }

    private void showItems(ItemSet items) {

    }
}
