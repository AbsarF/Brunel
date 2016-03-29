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

import org.brunel.workspace.item.Item;
import org.brunel.workspace.item.Stored;
import org.brunel.workspace.util.UI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Shows items stored in the DB
 */
public class ItemsPanel extends JPanel {

    private Stored<Item> displayedItems;
    private final JList<Item> contents;

    private final Action add, remove;

    public ItemsPanel(final Stored<Item>[] categories) {
        super(new GridBagLayout());
        this.add = makeAddAction();
        this.remove = makeRemoveAction();

        GridBagConstraints cons = new GridBagConstraints();
        setOpaque(false);
        cons.gridx = 0;
        cons.gridy = GridBagConstraints.RELATIVE;
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.weighty = 0.0;

        // The chooser for the type of items to view
        add(makeTypeComboBox(categories), cons);
        displayedItems = categories[0];

        cons.weighty = 1.0;
        cons.fill = GridBagConstraints.BOTH;
        contents = new JList<>(displayedItems);
        contents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contents.setCellRenderer(displayedItems);
        contents.setBackground(UI.BACKGROUND);
        contents.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                Item value = contents.getSelectedValue();
                remove.setEnabled(value != null);
                displayedItems.select(value);
            }
        });

        JScrollPane scroller = new JScrollPane(contents,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2), new LineBorder(UI.CONTROLS)));
        add(scroller, cons);

        cons.weighty = 0.0;
        cons.fill = GridBagConstraints.HORIZONTAL;

        JPanel buttons = new JPanel();
        buttons.setBackground(UI.BACKGROUND);
        buttons.add(new JButton(add));
        buttons.add(new JButton(remove));
        add(buttons, cons);

        fireValidation();
    }

    private Action makeRemoveAction() {
        AbstractAction action = new AbstractAction("-") {
            public void actionPerformed(ActionEvent e) {
                Item value = contents.getSelectedValue();
                if (UI.areYouSure(ItemsPanel.this, "PERMANENTLY delete the item '" + value + "'")) {
                    displayedItems.remove(value);
                    fireValidation();
                }
            }
        };
        action.setEnabled(false);
        return action;
    }

    private Action makeAddAction() {
        return new AbstractAction("+") {
            public void actionPerformed(ActionEvent e) {
                Item item = displayedItems.defineByUserInput();         // Make a new item using user input
                if (item == null) return;                               // User decided against making the item
                if (displayedItems.add(item)) {
                    fireValidation();
                } else {
                    UI.warn(ItemsPanel.this,
                            "Could not add the items as it already existed -- edit the existing item instead");
                }
            }
        };
    }

    private void fireValidation() {
        revalidate();
        repaint();
    }

    private JComboBox<Stored<Item>> makeTypeComboBox(Stored<Item>[] categories) {
        // Chooser for which to show
        final JComboBox<Stored<Item>> itemsChooser = new JComboBox<>(categories);
        itemsChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayedItems = (Stored<Item>) itemsChooser.getSelectedItem();
                contents.setModel(displayedItems);
                fireValidation();       // Ensure we see the changes
            }
        });
        return itemsChooser;
    }

}

