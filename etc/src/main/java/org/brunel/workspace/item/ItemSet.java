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

import java.util.List;

/**
 * A set of storeable items to be used
 */
public abstract class ItemSet<T extends Item> {
    public static ItemSet makeSources(Store store) {
        return new SourcesSet(store);
    }

    public static ItemSet makeCharts(Store store) {
        return new ChartSet(store);
    }

    protected final Store store;
    private final String title;

    private final List<T> items;

    protected ItemSet(Store store, String title) {
        this.store = store;
        this.title = title;
        this.items = readAll();
    }

    protected abstract List<T> readAll();

    public String toString() {
        return title;
    }
}
