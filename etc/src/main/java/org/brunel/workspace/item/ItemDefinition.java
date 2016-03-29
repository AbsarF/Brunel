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

/**
 * Created by graham on 3/28/16.
 */
public class ItemDefinition {

    public final String tableName;
    public final String tableDefinition;
    public final Item[] defaultItems;
    public final String imageName;


    public ItemDefinition(String tableName, String tableDefinition, String imageName, Item[] defaultItems) {
        this.tableName = tableName;
        this.tableDefinition = "id varchar primary key, label varchar, " + tableDefinition;
        this.defaultItems = defaultItems;
        this.imageName = imageName;
    }
}
