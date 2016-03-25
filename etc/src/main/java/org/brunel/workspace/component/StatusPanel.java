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

import javax.swing.*;
import java.awt.*;

/**
 * Created by graham on 3/24/16.
 */
public class StatusPanel extends JPanel {

    public StatusPanel() {
        super(new GridBagLayout());
        setOpaque(false);
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridy = 0;
        cons.gridx = GridBagConstraints.RELATIVE;
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.weighty = 0.0;
        add(new JLabel("items"), cons);
        add(new JLabel("foo"), cons);
        add(new JLabel("bar"), cons);
        add(new JButton("OK"), cons);
        add(Box.createHorizontalStrut(100), cons);
        cons.weighty = 1.0;
        add(Box.createVerticalGlue(), cons);
    }
}
