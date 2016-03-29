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

import javax.swing.*;
import java.awt.*;

/**
 * UI utilities
 */
public class UI {
    public static final Color BACKGROUND = Color.white;
    public static final Color CONTROLS = new Color(200, 200, 222);

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
}
