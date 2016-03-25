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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by graham on 3/25/16.
 */
public class ChartSet extends ItemSet<ChartItem> {
    public ChartSet(Store store) {
        super(store, "Visualizations");
    }

    protected List<ChartItem> readAll() {
        List<ChartItem> charts = new ArrayList<>();

        ChartItem tempA = new ChartItem();
        tempA.id = "bar";
        tempA.label = "Bar Chart";
        tempA.command = "bar x($1) y(#count) tooltip(#all) sort(#count)";
        tempA.parameters = new String[]{"categorical:20"};

        ChartItem tempB = new ChartItem();
        tempB.id = "scatter";
        tempB.label = "Scatterplot";
        tempB.command = "point x($1) y($2)";
        tempB.parameters = new String[]{"any", "any"};

        Collections.addAll(charts, tempA, tempB);

        return charts;
    }

}
