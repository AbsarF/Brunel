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
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

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

    public static JLabel makeLabelWithHelp(String text, String tooltip) {
        return new HelpLabel(text, tooltip);
    }

    public static JTextField makeTextFieldWithHelp(String text, String tooltip) {
        return new HelpTextField(text, tooltip);
    }

    public static <T extends JComponent> T addTitle(T item, String title) {
        TitledBorder b = new TitledBorder(new EmptyBorder(4, 4, 4, 4), title,
                TitledBorder.DEFAULT_POSITION, TitledBorder.DEFAULT_JUSTIFICATION,
                new Font("Arial", Font.PLAIN, 10), Color.lightGray);
        item.setBorder(b);
        return item;
    }

    public static <T extends JComponent> T makeSmall(T item) {
        item.setFont(UI.SMALL_FONT);
        item.setBackground(UI.BACKGROUND);
        item.setOpaque(true);
        return item;
    }

    public static <T extends JComponent> T makeSmall(T item, int hpad, int vpad) {
        item = makeSmall(item);
        item.setBorder(new EmptyBorder(vpad, hpad, vpad, hpad));
        return item;
    }

    public static String[] ask(JComponent component, String title, String[]... parts) {
        final ArrayList<JTextComponent> userResults = new ArrayList<>();
        Window c = SwingUtilities.getWindowAncestor(component);
        final JDialog dialog = new JDialog(c, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(UI.BACKGROUND);
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = 0;
        cons.gridy = GridBagConstraints.RELATIVE;
        cons.fill = GridBagConstraints.BOTH;
        cons.insets = new Insets(4, 4, 4, 4);

        for (String[] part : parts) {
            JComponent main;
            String help = part.length > 2 ? part[2] : null;
            if (part[1] == null) {
                // This is a user question
                JTextField field = UI.makeTextFieldWithHelp("", help);
                userResults.add(field);
                main = field;
            } else {
                main = UI.makeLabelWithHelp(part[1], help);
            }
            addTitle(main, part[0]).setBackground(UI.BACKGROUND);
            content.add(main, cons);
        }

        Box buttons = Box.createHorizontalBox();
        buttons.add(Box.createHorizontalGlue());
        JButton define = new JButton("Define");
        JButton cancel = new JButton("Cancel");
        buttons.add(define);
        buttons.add(Box.createHorizontalStrut(4));
        buttons.add(cancel);
        content.add(buttons, cons);

        dialog.setContentPane(content);

        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                userResults.clear();              // So it will be cancelled
            }
        });

        define.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (JTextComponent c : userResults)
                    if (c.getText().isEmpty()) return;
                dialog.setVisible(false);
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(c);
        dialog.setVisible(true);
        if (userResults.isEmpty()) return null;         // Cancelled

        String[] results = new String[userResults.size()];
        for (int i = 0; i < results.length; i++)
            results[i] = userResults.get(i).getText();
        return results;
    }

    public static class HelpLabel extends JLabel {
        public HelpLabel(String text, String tooltip) {
            super(text);
            setToolTipText(tooltip);
        }

        public JToolTip createToolTip() {
            return HelpTooltip.make(this);
        }

    }

    public static class HelpTextField extends JTextField {
        public HelpTextField(String text, String tooltip) {
            super(text);
            setToolTipText(tooltip);
        }

        public JToolTip createToolTip() {
            return HelpTooltip.make(this);
        }

    }

}
