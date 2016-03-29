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

import org.brunel.action.Action;
import org.brunel.action.Parser;
import org.brunel.app.brunel.Common;
import org.brunel.workspace.activity.Activity;
import org.brunel.workspace.activity.ActivityEvent;
import org.brunel.workspace.activity.ActivityListener;
import org.brunel.workspace.data.ItemSource;
import org.brunel.workspace.item.ItemVis;
import org.brunel.workspace.util.UI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Edit Brunel
 */
public class BrunelEditor extends JTextPane implements DocumentListener, Runnable, ActivityListener {

    private final Style defaultStyle;
    private final Parser parser = new Parser();

    private String dataName;

    public BrunelEditor(final Activity activity) {
        setBackground(UI.BACKGROUND);
        setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, UI.BACKGROUND));
        setForeground(Color.darkGray);
        setFont(new Font("Monospaced", Font.PLAIN, 14));

        DefaultStyledDocument sc = (DefaultStyledDocument) getDocument();

        defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);

        // Styles are: error, name, syntax, field, option, number, string

        Style errorStyle = sc.addStyle("?", defaultStyle);
        StyleConstants.setUnderline(errorStyle, true);
        StyleConstants.setForeground(errorStyle, Color.red);

        Style commandStyle = sc.addStyle("name", defaultStyle);
        StyleConstants.setForeground(commandStyle, Color.black);
        StyleConstants.setBold(commandStyle, true);

        Style syntaxStyle = sc.addStyle("syntax", defaultStyle);
        StyleConstants.setForeground(syntaxStyle, Color.gray);

        Style fieldStyle = sc.addStyle("field", defaultStyle);
        StyleConstants.setForeground(fieldStyle, Color.blue);

        Style specialFieldStyle = sc.addStyle("option", defaultStyle);
        StyleConstants.setForeground(specialFieldStyle, Common.GREEN2);
        StyleConstants.setItalic(specialFieldStyle, true);
        StyleConstants.setBold(specialFieldStyle, true);
        sc.addStyle("list", specialFieldStyle);

        Style numberStyle = sc.addStyle("number", defaultStyle);
        StyleConstants.setForeground(numberStyle, Common.GREEN2);
        StyleConstants.setBold(numberStyle, true);

        Style stringStyle = sc.addStyle("string", defaultStyle);
        StyleConstants.setForeground(stringStyle, Common.GREEN2);
        StyleConstants.setItalic(stringStyle, true);

        getDocument().addDocumentListener(this);

        getActionMap().put(getInputMap().get(KeyStroke.getKeyStroke("ENTER")), new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Action a = Action.parse(getText());
                activity.fireActivate(a, BrunelEditor.this);

                if (dataName != null) {
                    // Build this fully operational item
                    ItemVis target = new ItemVis(dataName, a.toString(), activity);
                    activity.fireActivate(target, BrunelEditor.this);
                }
            }
        });

        activity.addListener(this);
    }

    public void handleActivity(ActivityEvent event) {
        if (event.target instanceof Action) {
            Action action = (Action) event.target;
            setText(action.toString());
        }
        if (event.getField() != null) dataName = event.getField().strProperty("source");
        if (event.target instanceof ItemSource) dataName = ((ItemSource)event.target).location;
    }

    public void insertUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(this);
    }

    public void removeUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(this);
    }

    public void changedUpdate(DocumentEvent e) {
        // Do not care
    }

    public void run() {

        String text = getText();
        if (text.length() < 1) return;

        // Tokenize the text, then parse to assign actions and types
        //noinspection UnnecessaryFullyQualifiedName
        java.util.List<Parser.BrunelToken> tokens = parser.tokenize(text);
        try {
            setToolTipText(null);
            parser.makeActionFromTokens(tokens, text);
        } catch (Exception e) {
            setToolTipText(e.getMessage());
        }

        // Colorize
        DefaultStyledDocument d = (DefaultStyledDocument) getDocument();
        d.setCharacterAttributes(0, text.length(), defaultStyle, true);
        for (Parser.BrunelToken token : tokens) {
            d.setCharacterAttributes(token.start, token.end - token.start, getStyle(token.parsedType), false);
        }

    }

}
