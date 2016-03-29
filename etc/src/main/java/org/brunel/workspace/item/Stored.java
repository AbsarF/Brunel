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

import org.brunel.workspace.data.ItemSource;
import org.brunel.workspace.db.Store;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A set of storeable items to be used
 */
public class Stored<T extends Item> extends AbstractListModel<T> implements ListCellRenderer<Item> {

    private final Store store;
    private final String title;
    private final T representative;
    private T selected;

    private final List<T> items;

    private Stored(Store store, String title, T representative) {
        this.store = store;
        this.title = title;
        this.representative = representative;
        this.items = new ArrayList<>();
    }

    public boolean add(T item) {
        try {
            store.addToTable(item.definition.tableName, item);
        } catch (Exception e) {
            if (isSQLConstraint(e.getCause())) return false;
            throw e;
        }
        int n = items.size();
        items.add(item);
        fireContentsChanged(this, n, n);
        return true;
    }

    public void remove(T item) {
        store.removeFromTable(item.definition.tableName, item);
        items.remove(item);
        fireContentsChanged(this, 0, items.size());
    }

    public void select(T value) {
        if (selected != null) {
            selected.getRepresentation().showContent(false);
            int p = items.indexOf(selected);
            fireContentsChanged(this, p, p);
        }
        if (value != null) {
            value.getRepresentation().showContent(true);
            int p = items.indexOf(value);
            fireContentsChanged(this, p, p);
        }
        selected = value;
    }

    private boolean isSQLConstraint(Throwable e) {
        if (e instanceof SQLException) {
            SQLException s = (SQLException) e;
            return s.getMessage().toLowerCase().contains("constraint violation");
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public T defineByUserInput() {
        return (T) representative.defineByUserInput();

    }

    public Component getListCellRendererComponent(JList<? extends Item> list, Item value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        Representation rep = value.getRepresentation();
        rep.setState(isSelected, cellHasFocus);
        return rep;
    }

    public String getTableDefinition() {
        return representative.definition.tableDefinition;
    }

    public String getTableName() {
        return representative.definition.tableName;
    }

    public Item[] getDefaultItems() {
        return representative.definition.defaultItems;
    }

    @SuppressWarnings("unchecked")
    private void initialize() {
        items.clear();
        List list = store.retrieve(representative.definition.tableName, representative);
        items.addAll(list);
    }

    @SuppressWarnings("unchecked")
    public static Stored<Item>[] makeStores(Store store) {
        Stored[] items = {
                new Stored<>(store, "Sources", new ItemSource()),
                new Stored<>(store, "Charts", new ItemChart())
        };
        for (Stored<Item> s : items) s.initialize();
        return items;
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
