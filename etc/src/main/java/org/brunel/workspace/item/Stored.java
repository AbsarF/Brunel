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

import org.brunel.workspace.activity.Activity;
import org.brunel.workspace.data.ItemSource;
import org.brunel.workspace.db.Store;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A set of storeable items to be used
 */
public class Stored<T extends Item> extends ArrayList<T>  {

    private final Store store;
    private final String title;
    private final T representative;
    private final Activity activity;
    private T selected;

    private Stored(Store store, String title, T representative) {
        this.store = store;
        this.title = title;
        this.representative = representative;
        this.activity = representative.activity;
    }

    public boolean add(T item) {
        try {
            store.addToTable(item.definition.tableName, item);
        } catch (Exception e) {
            if (isSQLConstraint(e.getCause())) return false;
            throw e;
        }
        return super.add(item);
    }

    public boolean remove(Object o) {
        T item = (T) o;
        store.removeFromTable(item.definition.tableName, item);
        return super.remove(item);
    }

    public void select(T value) {
        if (selected != null) selected.setSelected(false);
        if (value != null) value.setSelected(true);
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

    public String getTableDefinition() {
        return representative.definition.tableDefinition;
    }

    public String getTableName() {
        return representative.definition.tableName;
    }

    @SuppressWarnings("unchecked")
    private void initialize() {
        clear();
        List list = store.retrieve(representative.definition.tableName, representative);
        addAll(list);
    }

    @SuppressWarnings("unchecked")
    public static Stored<Item>[] makeStores(Store store, Activity activity) {
        Stored[] items = {
                new Stored<>(store, "Sources", new ItemSource(activity)),
                new Stored<>(store, "Charts", new ItemChart(activity))
        };
        for (Stored<Item> s : items) s.initialize();
        return items;
    }

    public String toString() {
        return title;
    }

}
