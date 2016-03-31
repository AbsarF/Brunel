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

import org.brunel.build.util.DataCache;
import org.brunel.data.Dataset;
import org.brunel.workspace.activity.Activity;
import org.brunel.workspace.activity.ActivityEvent;
import org.brunel.workspace.activity.ActivityListener;
import org.brunel.workspace.util.UI;

import javax.swing.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A visualization ready to be displayed
 */
public class ItemVis extends Item {

    private static final String TOOLTIP = "This is a complete [[Visualization]].\n" +
            "Double-click it to show it in the main view";

    private static final ItemDefinition DEFINITION = new ItemDefinition(
            "VISUALIZATION", "dataset varchar, command varchar", "chart16.png"
    );

    public String dataDef;
    public String brunelCommand;

    private ItemVis lastPublished;

    public ItemVis(Activity activity) {
        super(DEFINITION, activity);
    }

    public ItemVis(String dataDef, String brunelCommand, Activity activity) {
        super(DEFINITION, activity);
        this.dataDef = dataDef;
        this.brunelCommand = brunelCommand;
        if (dataDef == null) throw new NullPointerException("Data must be defined");
        if (brunelCommand == null) throw new NullPointerException("Brunel must be defined");
    }

    public Dataset getDataset() throws IOException {
        return DataCache.get(dataDef);
    }

    public ItemVis retrieve(ResultSet rs) throws SQLException {
        ItemVis item = new ItemVis(activity);
        item.readCommonFields(rs);
        item.dataDef = rs.getString(3);
        item.brunelCommand = rs.getString(4);
        return item;
    }

    public Object[] toStorableObjects() {
        return makeStorableObjects(dataDef, brunelCommand);
    }

    public ItemVis defineByUserInput(JComponent owner) {
        if (lastPublished == null) return null;
        String[] definitions = UI.ask(owner, "Define this visualization",
                new String[]{"Brunel Command", lastPublished.brunelCommand},
                new String[]{"Data", lastPublished.dataDef},
                new String[]{"Name", null, "Unique name for the visualization"},
                new String[]{"Label", null, "Label for the visualization"}
        );
        if (definitions == null) return null;
        ItemVis vis = new ItemVis(lastPublished.dataDef, lastPublished.brunelCommand, activity);
        vis.id = definitions[0];
        vis.label = definitions[1];
        return vis;
    }

    public void setIsRepresentative() {
        activity.addListener(new ActivityListener() {
            public void handleActivity(ActivityEvent event) {
                if (event.type == ActivityEvent.Type.activate && event.target instanceof ItemVis)
                    lastPublished = (ItemVis) event.target;
            }
        });
    }

    protected Representation makeRepresentation() {
        return new Representation(this, null, TOOLTIP);
    }
}
