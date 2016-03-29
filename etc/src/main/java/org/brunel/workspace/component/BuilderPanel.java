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

import org.brunel.build.util.DataCache;
import org.brunel.data.Dataset;
import org.brunel.data.Field;
import org.brunel.workspace.activity.Activity;
import org.brunel.workspace.activity.ActivityEvent;
import org.brunel.workspace.activity.ActivityListener;
import org.brunel.workspace.item.ItemChart;
import org.brunel.workspace.item.ItemVis;
import org.brunel.workspace.util.UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builds Brunel
 */
public class BuilderPanel extends JPanel implements ActivityListener {

    private final Activity activity;
    private final JLabel definitionComponent;
    private final Box fieldsComponent;
    private ItemChart chart;
    private Field[] param;
    private Dataset data;

    public BuilderPanel(Activity activity) {
        super(new GridBagLayout());
        setBackground(UI.BACKGROUND);
        this.activity = activity;
        this.definitionComponent = makeDefinition();
        this.fieldsComponent = makeFieldsPanel();

        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = 0;
        cons.gridy = GridBagConstraints.RELATIVE;
        cons.fill = GridBagConstraints.BOTH;
        cons.weightx = 1;
        cons.weighty = 0;

        // The top panel gives overall definitions and slots to drag to
        Box top = Box.createHorizontalBox();
        top.setBorder(new MatteBorder(1,0,1,0, UI.CONTROLS));
        top.add(definitionComponent);
        top.add(fieldsComponent);
        top.add(Box.createVerticalStrut(32));
        top.add(Box.createHorizontalGlue());
        add(top, cons);

        // And the brunel definition
        cons.weighty = 1;
        add(makeBrunelPanel(), cons);

        activity.addListener(this);
    }

    public void setFieldParameter(int index, Field field) {
        param[index] = field;
        ((FieldSlot) fieldsComponent.getComponent(index)).setField((Field) field);
        if (field == null) return;
        try {
            this.data = DataCache.get(field.strProperty("source"));
            if (this.data == null) throw new NullPointerException("No source property found in field");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // remove fields from other data sets
        for (int i = 0; i < param.length; i++)
            if (param[i] != null && data.field(param[i].name) == null)
                param[i] = null;

        fireChanges();
    }

    private int getFirstEmptyParameter() {
        for (int i = 0; i < param.length; i++)
            if (param[i] == null) return i;
        return 0;
    }

    private JLabel makeDefinition() {
        JLabel label = new JLabel();
        label.setBorder(new EmptyBorder(2, 6, 2, 2));
        return label;
    }

    private Box makeFieldsPanel() {
        Box box = Box.createHorizontalBox();
        box.setBackground(UI.BACKGROUND);
        box.setBorder(new EmptyBorder(6,2,6,2));
        return box;
    }

    private Component makeBrunelPanel() {
        BrunelEditor editor = new BrunelEditor(activity);
        editor.setPreferredSize(new Dimension(10000, 60));
        return editor;
    }

    public void handleActivity(ActivityEvent event) {
        if (event.getChart() != null && event.getChart() != chart) {
            chart = event.getChart();
            buildForChart();
        } else if (event.type == ActivityEvent.Type.activate && event.getField() != null && param != null) {
            setFieldParameter(getFirstEmptyParameter(), event.getField());
            fireChanges();
        }
    }

    private void buildForChart() {
        definitionComponent.setText(chart.getLabel());
        definitionComponent.setToolTipText(chart.command);
        fieldsComponent.removeAll();
        param = new Field[chart.parameters.length];
        for (int i = 0; i < chart.parameters.length; i++)
            fieldsComponent.add(new FieldSlot(chart.parameters[i], i, this));
        fireChanges();
    }

    private void fireChanges() {
        validate();
        repaint();

        if (chart == null) return;

        // We can make an action, even with no fields
        String brunelCommand = makeText();
        activity.fireSelect(org.brunel.action.Action.parse(brunelCommand), this);

        // Can we make a complete vis item?
        if (data == null) return;
        for (Field s : param) if (s == null) return;

        ItemVis vis = new ItemVis(param[0].strProperty("source"), brunelCommand, activity);
        activity.fireActivate(vis, this);
    }

    private String makeText() {
        String text = chart.command;
        String[] parameters = chart.parameters;
        Map<Field, Integer> toBin = new LinkedHashMap<>();

        for (int i = 0; i < parameters.length; i++) {
            String p = parameters[i].toLowerCase();
            Field field = param[i];

            if (p.startsWith("multi")) {
                // Multiple fields
                if (i != parameters.length - 1)
                    throw new IllegalStateException("Multi field must be last parameter");

                // Bin each if needed
                if (p.startsWith("multicat")) {
                    for (int j = i; j < param.length; j++) {
                        int bins = needsBinning(p, param[j]);
                        if (bins > 0) toBin.put(param[j], bins);
                    }
                }

                // Modify the text
                text = this.modifyStringForMulti(text, param, i);

            } else {

                // Handle binning (including converting to categorical when needed
                int bins = needsBinning(p, field);
                if (bins > 0) toBin.put(field, bins);

                // Modify the text
                if (field != null) text = text.replaceAll("\\$" + (i + 1), field.name);
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

    private String modifyStringForMulti(String text, Field[] fields, int start) {
        int n = fields.length - start;
        String all = "";

        for (int i = 0; i < n; ++i) {
            text = text.replaceAll("\\$" + (start + 1) + "\\[" + i + "\\]", fields[start + i].name);
            text = text.replaceAll("\\$" + (start + 1) + "\\[-" + i + "\\]", fields[fields.length - 1 - i].name);
            if (i > 0) {
                all = all + ", ";
            }

            all = all + fields[start + i].name;
        }

        return text.replaceAll("\\$" + (start + 1), all);
    }

    private int needsBinning(String p, Field f) {
        if (f == null) return -1;
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
