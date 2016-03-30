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

import org.brunel.data.Field;
import org.brunel.workspace.data.FieldDroppable;
import org.brunel.workspace.data.FieldTransferHandler;
import org.brunel.workspace.util.UI;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * A place to drag a field to
 */
public class FieldSlot extends UI.HelpLabel implements FieldDroppable {

    private static final String TOOLTIP = "Drag a [[Field]] into this slot to build the visualization";

    private final int index;
    private final BuilderPanel listener;

    private final String definition;

    private static final Border BORDER = new CompoundBorder(
            new MatteBorder(0, 4, 0, 4, UI.BACKGROUND),
            new CompoundBorder(new LineBorder(Color.gray, 1), UI.BORDER_PADDING)
    );

    public FieldSlot(String definition, int index, BuilderPanel listener) {
        super(definition, TOOLTIP);
        this.definition = definition;
        this.index = index;
        this.listener = listener;
        setOpaque(true);
        setField(null);
        setTransferHandler(new FieldTransferHandler());
        setBorder(BORDER);
    }

    public void dropField(Field field) {
        setField(field);
        listener.setFieldParameter(index, field);
    }

    public void setField(Field field) {
        if (field != null) {
            setText(field.label);
            setForeground(Color.black);
            setBackground(UI.HIGHLIGHT);
        } else {
            setText(definition);
            setForeground(Color.lightGray);
            setBackground(UI.BACKGROUND);
        }
    }

}
