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

import org.brunel.data.Dataset;
import org.brunel.data.Field;
import org.brunel.workspace.activity.Activity;
import org.brunel.workspace.activity.ActivityEvent;
import org.brunel.workspace.activity.ActivityListener;
import org.brunel.workspace.item.ItemChart;
import org.brunel.workspace.item.ItemVis;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by graham on 3/24/16.
 */
public class BuilderPanel extends JPanel implements ActivityListener {

    private final java.util.List<Field> fields = new ArrayList<>();
    private final Activity activity;
    private ItemChart chart;
    private Dataset data;
    private ItemVis vis;

    public BuilderPanel(Activity activity) {
        super(new GridBagLayout());
        this.activity = activity;
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = 0;
        cons.gridy = GridBagConstraints.RELATIVE;
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.weightx = 1.0;
        cons.weighty = 0.0;

//        add(makeDefinitionPanel(activity), cons);
//        add(makeFieldSlotsPanel(activity), cons);
//        add(makeBrunelEditorPanel(activity), cons);

        cons.weighty = 1.0;
        add(Box.createVerticalGlue(), cons);
        activity.addListener(this);

    }

    public void handleActivity(ActivityEvent event) {
        boolean change = false;
        if (event.getField() != null) {
            fields.add(0, event.getField());
            change = true;
        }
        if (event.getChart() != null) {
            chart = event.getChart();
            change = true;
        }
        if (event.getData() != null) {
            if (data != event.getData()) fields.clear();
            data = event.getData();
            change = true;
        }
        if (change) propagateIfValid();
    }

    private void propagateIfValid() {
        if (data == null || chart == null || chart.parameters.length > fields.size()) return;
        String dataDefinition = data.strProperty("source");
        vis = new ItemVis(dataDefinition, makeText(), activity);
        activity.fireActivate(vis, this);
    }

    private String makeText() {
        String text = chart.command;
        String[] parameters = chart.parameters;
        Map<Field, Integer> toBin = new LinkedHashMap<>();

        for (int i = 0; i < parameters.length; i++) {
            String p = parameters[i].toLowerCase();
            Field f = fields.get(i);

            if (p.startsWith("multi")) {
                // Multiple fields
                if (i != parameters.length - 1)
                    throw new IllegalStateException("Multi field must be last parameter");

                // Bin each if needed
                if (p.startsWith("multicat")) {
                    for (int j = i; j < fields.size(); j++) {
                        int bins = needsBinning(p, fields.get(j));
                        if (bins > 0) toBin.put(fields.get(j), bins);
                    }
                }

                // Modify the text
                text = this.modifyStringForMulti(text, fields, i);

            } else {

                // Handle binning (including converting to categorical when needed
                int bins = needsBinning(p, f);
                if (bins > 0) toBin.put(f, bins);

                // Modify the text
                text = text.replaceAll("\\$" + (i + 1), f.name);
            }

        }

        // Add in the binning
        for (Field f : toBin.keySet())
            text += " bin(" + f.name + ":" + toBin.get(f) + ")";

        return text;

    }

    public String toString() {
        return "BuilderPanel";
    }

    private String modifyStringForMulti(String text, List<Field> fields, int start) {
        int n = fields.size() - start;
        String all = "";

        for (int i = 0; i < n; ++i) {
            text = text.replaceAll("\\$" + (start + 1) + "\\[" + i + "\\]", fields.get(start + i).name);
            text = text.replaceAll("\\$" + (start + 1) + "\\[-" + i + "\\]", fields.get(fields.size() - 1 - i).name);
            if (i > 0) {
                all = all + ", ";
            }

            all = all + fields.get(start + i).name;
        }

        return text.replaceAll("\\$" + (start + 1), all);
    }

    private int needsBinning(String p, Field f) {
        String[] parts = p.split(":");
        String main = parts[0].trim();
        int divs = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : -1;
        if (main.startsWith("cat") || main.startsWith("multicat")) {
            // We need categorical fields
            if (f.preferCategorical()) {
                // If we have a given number we want, but have way too many, bin to reduce
                if (divs > 0 && f.categories().length > 2 * divs) return divs;
                else return -1;
            } else {
                // If numeric, we need to bin (9 bins as a default)
                return divs > 0 ? divs : 9;
            }
        }
        return -1;
    }

}
