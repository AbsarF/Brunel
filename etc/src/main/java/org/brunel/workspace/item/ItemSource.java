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
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A chart definition
 */
public class ItemSource extends Item {

    private String location;

    public void createFromStore(ResultSet rs) throws SQLException {
        readCommonFields(rs);
        location = rs.getString(3);
    }

    public Object[] toStorableObjects() {
        return makeStorableObjects(location);
    }

    public boolean defineByUserInput() {
        FileDialog f = new FileDialog((JFrame) null);
        f.setTitle("Choose a CSV File");
        f.setMode(FileDialog.LOAD);
        f.setMultipleMode(false);
        f.setVisible(true);
        File[] files = f.getFiles();
        if (files.length > 0) {
            File file = files[0];
            id = "src-" + file.getName();
            label = CSV.readable(file.getName());
            location = file.getAbsolutePath();
            return true;
        } else {
            return false;
        }
    }

    public String getIconResourcePath() {
        return "data16.png";
    }

    protected Representation makeRepresentation() {
        return new Representation(this);
    }
}
