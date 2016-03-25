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

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.IOException;

/**
 * Standard item representation
 */
public class Representation extends JPanel {

    private static final Color backgroundSelected;
    private static final EmptyBorder PADDING_BORDER = new EmptyBorder(2, 2, 2, 2);

    static {
        UIDefaults defaults = javax.swing.UIManager.getDefaults();
        backgroundSelected = defaults.getColor("List.selectionBackground");
    }

    private final GradientStrip title;

    public Representation(Item item) {
        super(new BorderLayout(0, 0));
        setBackground(UI.BACKGROUND);

        title = new GradientStrip(true);

        String iconPath = item.getIconResourcePath();
        try {
            ImageIcon icon = new ImageIcon(ImageIO.read(getClass().getResource("/icons/" + iconPath)));
            title.add(new JLabel(icon));
        } catch (IOException e) {
            e.printStackTrace();
        }

        title.add(Box.createHorizontalGlue());
        JLabel label = new JLabel(item.label);
        label.setBorder(PADDING_BORDER);
        title.add(label);
        add(title, BorderLayout.NORTH);
        setBorder(Color.gray);
    }

    public void setState(boolean selected, boolean focused) {
        title.setColorB(selected ? backgroundSelected : UI.CONTROLS);
        setBorder(focused ? UI.CONTROLS : UI.BACKGROUND);

    }

    private void setBorder(Color color) {
        CompoundBorder border = new CompoundBorder(PADDING_BORDER, new LineBorder(color, 1));
        setBorder(border);
    }
}
