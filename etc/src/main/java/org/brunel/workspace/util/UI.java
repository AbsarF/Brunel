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

package org.brunel.workspace.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

/**
 * UI utilities
 */
public class UI {
    private static final Logger logger = LoggerFactory.getLogger(UI.class);

    public static final Color BACKGROUND = Color.white;
    public static final Color CONTROLS = new Color(200, 200, 222);
    public static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 11);
    public static final Color HIGHLIGHT = new Color(235, 235, 255);
    public static final Border BORDER_PADDING = new EmptyBorder(2, 2, 2, 2);

    public static Dimension getScreenSize() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        return new Dimension(width, height);
    }

    public static void warn(JComponent component, String text) {
        Component c = SwingUtilities.getWindowAncestor(component);
        JOptionPane.showMessageDialog(c, text, null, JOptionPane.WARNING_MESSAGE);
    }

    public static boolean areYouSure(JComponent component, String text) {
        Component c = SwingUtilities.getWindowAncestor(component);
        int result = JOptionPane.showConfirmDialog(c, "Are you sure you want to " + text + "?",
                null, JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    public static ImageIcon readIcon(String name) {
        try {
            return new ImageIcon(ImageIO.read(UI.class.getResource("/icons/" + name)));
        } catch (IOException e) {
            logger.error("Unable to read Icon: " + name);
            return null;
        }
    }
}
