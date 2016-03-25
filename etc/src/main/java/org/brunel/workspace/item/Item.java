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

import org.brunel.workspace.db.Storable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A storeable thing
 */
public abstract class Item implements Storable<Item> {

    public String id;
    public String label;

    protected Object[] makeStorableObjects(int additionalItemCount) {
        Object[] objects = new Object[additionalItemCount + 2];
        objects[0] = id;
        objects[1] = label;
        return objects;
    }

    protected void readCommonFields(ResultSet rs) throws SQLException {
        id = rs.getString(1);
        label = rs.getString(2);
    }

}
