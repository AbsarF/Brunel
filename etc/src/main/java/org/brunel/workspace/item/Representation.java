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

import org.brunel.workspace.util.GradientStrip;
import org.brunel.workspace.util.UI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Standard item representation
 */
public class Representation extends JPanel {

    private static final Color backgroundSelected;
    private static final EmptyBorder PADDING_BORDER = new EmptyBorder(2, 2, 2, 2);

    public final Item item;
    public final JComponent content;

    public void setSelected(boolean state) {
        if (content != null) content.setVisible(state);
        title.setColorB(state ? backgroundSelected : UI.CONTROLS);
    }

    static {
        UIDefaults defaults = javax.swing.UIManager.getDefaults();
        backgroundSelected = defaults.getColor("List.selectionBackground");
    }

    private final GradientStrip title;

    public Representation(final Item item, JComponent content) {
        super(new BorderLayout(0, 0));
        this.item = item;
        this.content = content;

        title = new GradientStrip(true);

        title.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1)
                    item.activity.fireActivate(item, Representation.this);
                else
                    item.activity.fireSelect(item, Representation.this);
            }
        });

        title.add(new JLabel(UI.readIcon(item.definition.imageName)));

        title.add(Box.createHorizontalGlue());
        JLabel label = new JLabel(item.label);
        label.setBorder(PADDING_BORDER);
        title.add(label);
        add(title, BorderLayout.NORTH);

        if (content != null) {
            content.setVisible(false);
            add(content, BorderLayout.CENTER);
        }

        setBorder(Color.gray);
    }

    private void setBorder(Color color) {
        setBorder(new CompoundBorder(PADDING_BORDER, new LineBorder(color, 1)));
    }

    public String toString() {
        return "Representation{" + item + "}";
    }
}
