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

import org.brunel.data.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A chart definition
 */
public class ItemChart extends Item {

    private String command;
    private String[] parameters;

    public void createFromStore(ResultSet rs) throws SQLException {
        readCommonFields(rs);
        command = rs.getString(3);
        parameters = rs.getString(4).split(" \\| ");
    }

    public Object[] toStorableObjects() {
        return makeStorableObjects(command, Data.join(parameters, " | "));
    }

    public boolean defineByUserInput() {
        return false;
    }

    public String getIconResourcePath() {
        return "chart16.png";
    }

    protected Representation makeRepresentation() {
        return new Representation(this);
    }
}
