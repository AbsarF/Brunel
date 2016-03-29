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

package org.brunel.workspace.data;

import org.brunel.data.Dataset;
import org.brunel.data.Field;
import org.brunel.workspace.activity.Activity;
import org.brunel.workspace.util.UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Represents a field (and can be dragged)
 */
public class FieldComponent extends JPanel {

    public static final FieldTransferHandler FIELD_TRANSFER_HANDLER = new FieldTransferHandler();

    private static final Icon dateIcon = UI.readIcon("calendar16.png");
    private static final Icon numericIcon = UI.readIcon("ruler16.png");
    private static final Icon categoricalIcon = UI.readIcon("pie16.png");

    public final Field field;
    public final Dataset dataset;

    public FieldComponent(final Field field, final Dataset dataset, final Activity activity, MouseMotionListener dragger) {
        super(new BorderLayout());
        this.field = field;
        this.dataset = dataset;
        field.set("source", dataset.strProperty("source"));

        Icon icon = field.isDate() ? dateIcon : (field.preferCategorical() ? categoricalIcon : numericIcon);
        JLabel label = new JLabel(restrictSize(field.label), icon, JLabel.LEADING);
        label.setFont(UI.SMALL_FONT);
        add(label, BorderLayout.CENTER);

        label.addMouseMotionListener(dragger);
        label.setTransferHandler(FIELD_TRANSFER_HANDLER);

        setBorder(new EmptyBorder(2,2,2,2));
        setBackground(Color.white);

        MouseAdapter activityPropagation = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                activity.fireSelect(dataset, FieldComponent.this);
                if (e.getClickCount() > 1)
                    activity.fireActivate(field, FieldComponent.this);
                else
                    activity.fireSelect(field, FieldComponent.this);
            }
        };
        label.addMouseListener(activityPropagation);
    }

    private String restrictSize(String label) {
        return label.length() > 20 ? label.substring(0,19) + "\u2026" : label;
    }

    public String toString() {
        return "FieldComponent{" + field.name + "}";
    }
}
