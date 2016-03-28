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

import org.brunel.data.io.CSV;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A chart definition
 */
public class ItemSource extends Item {

    private static final String TABLE_NAME = "SOURCES";
    private static final String TABLE_DEFN = "command varchar, parameters varchar";
    private static final String IMAGE_NAME = "data16.png";

    private String location;

    public ItemSource() {
        super(TABLE_NAME, TABLE_DEFN, IMAGE_NAME);
    }

    private ItemSource(File file) {
        this();
        try {
            location = file.getCanonicalPath();
            label = CSV.readable(file.getName());
            id = location;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createFromStore(ResultSet rs) throws SQLException {
        readCommonFields(rs);
        location = rs.getString(3);
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
        return new Representation(this);
    }
}
