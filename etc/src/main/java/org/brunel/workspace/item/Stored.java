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

package org.brunel.workspace.item;

import org.brunel.workspace.db.Store;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A set of storeable items to be used
 */
public class Stored<T extends Item> extends AbstractListModel<T> implements ListCellRenderer<Item> {

    private final Store store;
    private final String title;

    private final List<T> items;
    private final Class<T> itemClass;

    public Stored(Store store, String title, Class<T> itemClass) {
        this.store = store;
        this.title = title;
        this.itemClass = itemClass;
        this.items = new ArrayList<>();
    }

    public void add(T item) {
        int n = items.size();
        items.add(item);
        fireContentsChanged(this, n, n);
    }

    public Component getListCellRendererComponent(JList<? extends Item> list, Item value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        Representation rep = value.getRepresentation();
        rep.setState(isSelected, cellHasFocus);
        return rep;
    }

    private void initialize() {
        items.clear();
        addItemsFromStore();
    }

    public T makeNew() {
        try {
            // Build and then display a UI to define it
            T instance = itemClass.newInstance();
            if (instance.defineByUserInput())
                return instance;
            else
                return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Stored<Item>[] makeStores(Store store) {
        Stored[] items = {
                new Stored<>(store, "Sources", ItemSource.class),
                new Stored<>(store, "Charts", ItemChart.class)
        };
        for (Stored<Item> s : items) s.initialize();
        return items;
    }

    public void remove(int item) {
        items.remove(item);
        fireContentsChanged(this, item, items.size());
    }

    private final void addItemsFromStore() {
    }

    public String toString() {
        return title;
    }

    public int getSize() {
        return items.size();
    }

    public T getElementAt(int index) {
        return items.get(index);
    }

}
