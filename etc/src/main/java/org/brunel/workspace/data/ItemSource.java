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

package org.brunel.workspace.data;

import org.brunel.data.Field;
import org.brunel.data.io.CSV;
import org.brunel.workspace.item.Item;
import org.brunel.workspace.item.ItemDefinition;
import org.brunel.workspace.item.Representation;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A chart definition
 */
public class ItemSource extends Item {

    private static final ItemDefinition DEFINITION = new ItemDefinition(
            "SOURCES", "location varchar", "data16.png", new Item[0]
    );

    private String location;

    public ItemSource() {
        super(DEFINITION);
    }

    private ItemSource(File file) {
        this();
        try {
            location = file.getCanonicalPath();
            String name = file.getName();
            int p = name.lastIndexOf('.');
            if (p > 0) name = name.substring(0, p);
            label = CSV.readable(name);
            id = location;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemSource retrieve(ResultSet rs) throws SQLException {
        ItemSource result = new ItemSource();
        result.readCommonFields(rs);
        result.location = rs.getString(3);
        return result;
    }

    public Object[] toStorableObjects() {
        return makeStorableObjects(location);
    }

    public ItemSource defineByUserInput() {
        FileDialog f = new FileDialog((JFrame) null);
        f.setTitle("Choose a CSV File");
        f.setMode(FileDialog.LOAD);
        f.setMultipleMode(false);
        f.setVisible(true);
        File[] files = f.getFiles();
        if (files.length > 0) {
            return new ItemSource(files[0]);
        } else {
            return null;
        }
    }

    protected Representation makeRepresentation() {
        return new Representation(this, makeFieldsList());
    }

    private JComponent makeFieldsList() {
        Box box = Box.createVerticalBox();
        box.setBackground(Color.CYAN);
        box.setOpaque(true);
        try {
            String content = new String(Files.readAllBytes(Paths.get(location)), "utf-8");
            Field[] fields = CSV.read(content);
            for (Field f : fields) {
                box.add(new JLabel(f.label));
            }
            return box;
        } catch (Exception e) {
            return new JLabel("Error reading file");
        }
    }
}
