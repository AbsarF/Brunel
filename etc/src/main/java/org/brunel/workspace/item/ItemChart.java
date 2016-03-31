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

import org.brunel.data.Data;
import org.brunel.workspace.activity.Activity;
import org.brunel.workspace.util.UI;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A chart definition
 */
public class ItemChart extends Item {


    private static final String[] base = new String[]{
            "bar | Bar Chart | bar x($1) y(#count) tooltip(#all) sort(#count) | categorical:20",
            "stacked | Stacked Bar | bar x($1) color($2) y(#count) tooltip(#all) sort(#count) | categorical:20, categorical:5",
            "barGrp | Group Bar | bar x($1) y($2) color($3) sort($3, $2) mean($2) | categorical:300, numeric, categorical:5",
            "scatter | Scatterplot | point x($1) y($2) | any, any",
            "scatterGroup | Group Scatter | point x($1) y($2) color($3) tooltip(#all) + line x($1) y($2) color($3) fit($2) | numeric, numeric, categorical:3",
            "line | Line Chart | line x($1) y($2) mean($2) | any, any",
            "bubble | Bubble Chart | point x($1) y($2) size($3) | any, any, positive",
            "wordle | Wordle | text x($1) cloud color($1) size(#count) | categorical:200",
            "wordle2 | Sized Wordle | text x($1) cloud color($1) size($2) | categorical:200, positive",
            "heatmap | Heat Map | x($1) y($2) sort(#count) color(#count) label(#count) | categorical:15, categorical:15",
            "treemap | TreeMap | bar x($1) treemap color($1[-2]) size(#count) label($1[-1]) | multiCategorical",
            "choropleth | Map | map x($1) label($1) color($2) tooltip(#all) | categorical, any",
            "lineGroup | Line Chart | line x($1) y($2) color($3) mean($2) | any, any, categorical"
    };
    private static final String TOOLTIP = "Select this [[Chart]] to activate it in the [[Builder Panel]]\n" +
            "It will be used as the template to build a visualization.";

    public static Item[] makeInitialCharts(Activity activity) {
        Item[] charts = new Item[base.length];
        for (int i = 0; i < base.length; i++) {
            String[] parts = base[i].split(" *\\| *");
            String[] parameterDefinitions = parts[3].split(" *\\, *");
            charts[i] = new ItemChart(activity, parts[0], parts[1], parts[2], parameterDefinitions);
        }
        return charts;
    }

    public static final ItemDefinition DEFINITION = new ItemDefinition(
            "CHARTS", "command varchar, parameters varchar", "chart16.png"
    );

    public String command;
    public String[] parameters;

    public ItemChart(Activity activity) {
        super(DEFINITION, activity);
    }

    private ItemChart(Activity activity, String id, String label, String command, String[] parameters) {
        super(DEFINITION, activity);
        this.id = id;
        this.label = label;
        this.command = command;
        this.parameters = parameters;
    }

    public boolean isMulti() {
        return parameters[parameters.length-1].startsWith("multi");
    }

    public ItemChart retrieve(ResultSet rs) throws SQLException {
        ItemChart item = new ItemChart(activity);
        item.readCommonFields(rs);
        item.command = rs.getString(3);
        item.parameters = rs.getString(4).split(" \\| ");
        return item;
    }

    public Object[] toStorableObjects() {
        return makeStorableObjects(command, Data.join(parameters, " | "));
    }

    public ItemChart defineByUserInput(JComponent owner) {
        return null;
    }

    protected Representation makeRepresentation() {
        return new Representation(this, makeParametersPanel(), TOOLTIP);
    }

    private JComponent makeParametersPanel() {
        Box box = Box.createVerticalBox();
        for (String s : parameters) {
            Box item = Box.createHorizontalBox();
            int p = s.indexOf(':');
            String main = s, extra = null;
            if (p > 0) {
                main = s.substring(0, p).trim();
                extra = s.substring(p + 1).trim();
            }
            item.add(Box.createHorizontalStrut(4));
            item.add(UI.makeSmall(new JLabel("- " + main), 2, 2));
            item.add(Box.createHorizontalGlue());
            if (extra != null) {
                JLabel label = new JLabel("(" + extra + ")");
                label.setForeground(Color.lightGray);
                item.add(UI.makeSmall(label, 2, 2));
            }
            box.add(item);
        }
        return box;
    }
}
