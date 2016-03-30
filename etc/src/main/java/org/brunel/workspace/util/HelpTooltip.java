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
 * My tooltip
 */
public class HelpTooltip extends JToolTip {

    private static final HelpTooltip INSTANCE = new HelpTooltip();

    public static JToolTip make(JComponent target) {
        INSTANCE.setComponent(target);
        return INSTANCE;
    }

    private JLabel label = new JLabel();
    private JPanel inner;

    private HelpTooltip() {
        super();
        setBackground(new Color(255, 255, 120));
        label = UI.makeSmall(new JLabel(), 2, 2);
        label.setMaximumSize(new Dimension(200, 500));
        label.setOpaque(false);
        inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.setBorder(UI.BORDER_PADDING);
        inner.add(BorderLayout.CENTER, label);
        setLayout(new BorderLayout());
        add(inner);
    }

    public Dimension getPreferredSize() {
        return inner.getPreferredSize();
    }

    public void setTipText(String tipText) {
        if (tipText != null && !tipText.isEmpty())
            label.setText(makeHTML(tipText));
        else
            super.setTipText(tipText);
    }

    private String makeHTML(String text) {
        StringBuilder b = new StringBuilder();
        b.append("<html>");
        for (String s : text.split("\n")) {
            s = addLineBreaks(s, 36);
            b.append("<p style='padding:3px'>");
            s = s.replaceAll("\\[\\[", "<span style='color:red; font-weight:bold'>")
                    .replaceAll("\\{\\{", "<span style='color:gray; font-weight:italic'>")
                    .replaceAll("\\]\\]", "</span>")
                    .replaceAll("\\}\\}", "</span>");
            b.append(s);
            b.append("</p>");
        }
        return b.toString();
    }

    private String addLineBreaks(String s, int max) {
        if (s.length() < max) return s;
        StringBuilder b = new StringBuilder();
        int at = 0;
        while (at + max < s.length()) {
            int p = max;
            while (p > max / 2 && s.charAt(p + at) != ' ') p--;
            if (s.charAt(p + at) == ' ') {
                // Found a space
                b.append(s.substring(at, p+at));
                at += p + 1;
                b.append("<br>");
            } else {
                b.append(s.substring(at, at + max));
                at += max;
            }
        }
        if (at < s.length()) b.append(s.substring(at));
        return b.toString();
    }

}
