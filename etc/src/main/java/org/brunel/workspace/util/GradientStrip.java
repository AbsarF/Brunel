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
 * A strip of gradient colors
 */
public class GradientStrip extends Box {

    private Color a, b; // Gradient Colors
    private final boolean horizontal;

    public GradientStrip(Color a, Color b, boolean horizontal) {
        super(horizontal ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS);
        this.a = a;
        this.b = b;
        this.horizontal = horizontal;
        setOpaque(true);
    }

    public GradientStrip(boolean horizontal) {
        this(UI.BACKGROUND, UI.CONTROLS, horizontal);
    }

    public void setColorB(Color color) {
        this.b = color;
        repaint();
    }

    protected void paintComponent(Graphics graphics) {
        if (isOpaque()) {
            Graphics2D g = (Graphics2D) graphics.create();
            GradientPaint paint = horizontal ? new GradientPaint(0, 0, a, getWidth(), 0, b) :
                    new GradientPaint(0, 0, a, 0, getHeight(), b);
            g.setPaint(paint);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

}
