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
import org.brunel.workspace.activity.Activity;
import org.brunel.workspace.activity.ActivityEvent;
import org.brunel.workspace.activity.ActivityListener;
import org.brunel.workspace.item.Item;
import org.brunel.workspace.item.Stored;
import org.brunel.workspace.util.UI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Shows items stored in the DB
 */
public class ItemsPanel extends JPanel implements ActivityListener {

    private static final String ITEMS_PANEL_CATEGORY = "items-panel-category";
    private final Stored<Item>[] categories;
    private final Settings settings;
    private final Item[] lastSelected;
    private final JComboBox<Stored<Item>> comboBox;
    private int currentCategory;
    private final JPanel contents;
    private Item selected;

    private final Action add, remove;

    public ItemsPanel(final Stored<Item>[] categories, Activity activity, Settings settings) {
        super(new GridBagLayout());
        this.settings = settings;
        this.categories = categories;
        this.lastSelected = new Item[categories.length];
        this.comboBox = makeTypeComboBox(categories);
        this.add = makeAddAction();
        this.remove = makeRemoveAction();

        GridBagConstraints cons = new GridBagConstraints();
        setOpaque(false);
        cons.gridx = 0;
        cons.gridy = GridBagConstraints.RELATIVE;
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.weighty = 0.0;

        // The chooser for the type of items to view
        add(comboBox, cons);

        cons.weighty = 1.0;
        cons.fill = GridBagConstraints.BOTH;
        contents = new JPanel(new GridBagLayout());

        contents.setBackground(UI.BACKGROUND);

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

        int initial = settings.getInteger(ITEMS_PANEL_CATEGORY, 0);
        if (initial >= categories.length) initial = 0;
        setContents(initial, null);

        activity.addListener(this);
    }

    public void handleActivity(ActivityEvent event) {
        if (categories[currentCategory].contains(event.target)) {
            selectItem((Item) event.target);
        }
    }

    private void selectItem(Item item) {
        if (selected != null) selected.setSelected(false);
        if (item == null) {
            selected = null;
        } else if (selected == item) {
            item.setSelected(false);
            selected = null;
        } else {
            item.setSelected(true);
            selected = item;
        }

        lastSelected[currentCategory] = selected;
        remove.setEnabled(selected != null);
    }

    private Action makeRemoveAction() {
        AbstractAction action = new AbstractAction("-") {
            public void actionPerformed(ActionEvent e) {
                if (UI.areYouSure(ItemsPanel.this, "PERMANENTLY delete the item '" + selected + "'")) {
                    categories[currentCategory].remove(selected);
                    setContents(currentCategory, null);
                    selectItem(null);
                }
            }
        };
        action.setEnabled(false);
        return action;
    }

    private Action makeAddAction() {
        return new AbstractAction("+") {
            public void actionPerformed(ActionEvent e) {
                Item item = categories[currentCategory].defineByUserInput();    // Make a new item using user input
                if (item == null) return;                                       // User decided against making the item
                if (categories[currentCategory].add(item)) {
                    setContents(currentCategory, item);
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
                setContents(itemsChooser.getSelectedIndex(), null);
            }
        });
        return itemsChooser;
    }

    private void setContents(int index, Item selectItem) {
        settings.putString(ITEMS_PANEL_CATEGORY, Integer.toString(index));

        if (selectItem == null)
            selectItem = lastSelected[index];
        else
            lastSelected[index] = selectItem;

        currentCategory = index;
        if (comboBox.getSelectedIndex() != index) {
            comboBox.setSelectedIndex(index);
        }

        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = 0;
        cons.gridy = GridBagConstraints.RELATIVE;
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.weightx = 1.0;
        cons.weighty = 0.0;
        contents.removeAll();
        Stored<Item> items = categories[index];
        for (Item item : items)
            contents.add(item.getRepresentation(), cons);
        cons.weighty = 1.0;
        contents.add(Box.createVerticalGlue(), cons);

        selectItem(selectItem);
        fireValidation();
    }

    public String toString() {
        return "ItemsPanel";
    }
}

