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

import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import org.brunel.action.Action;
import org.brunel.build.d3.D3Builder;
import org.brunel.build.util.BuilderOptions;
import org.brunel.data.Dataset;
import org.brunel.model.VisItem;
import org.brunel.util.WebDisplay;
import org.brunel.workspace.activity.Activity;
import org.brunel.workspace.activity.ActivityEvent;
import org.brunel.workspace.activity.ActivityListener;
import org.brunel.workspace.item.ItemVis;
import org.brunel.workspace.util.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.Collections;

/**
 * Uses JxBrowser to show Brunel
 */
public class BrunelDisplayPanel extends BrowserView implements ActivityListener {

    private static final Logger logger = LoggerFactory.getLogger(BrunelDisplayPanel.class);

    private static final String INITIAL_HTML =
            "<html><body><div style='font-family:Helvetica;font-weight:bold;width:100%;height:100%'>" +
                    "<span style='color:red;position:absolute;top:20%;left:10vw;font-size:25vh'>Brunel</span>" +
                    "<span style='opacity:0.3;position:absolute;top:39.5%;left:22vw;font-size:14vh;font-style:italic'>" +
                    "Workspace&nbsp;&nbsp;</span>" +
                    "</div></body></html>";

    private final D3Builder builder;
    private VisItem item;

    public BrunelDisplayPanel(BuilderOptions buildOptions, Activity activity) {
        setBackground(UI.BACKGROUND);
        getBrowser().loadHTML(INITIAL_HTML);
        activity.addListener(this);
        builder = D3Builder.make(buildOptions);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if (item == null)  getBrowser().loadHTML(INITIAL_HTML);
                else showSimple(item);
            }
        });
    }

    public void handleActivity(ActivityEvent event) {
        if (event.type == ActivityEvent.Type.activate && event.target instanceof ItemVis) {
            ItemVis v = (ItemVis) event.target;
            try {
                Dataset data = v.getDataset();
                Action action = Action.parse(v.brunelCommand);
                showSimple(action.apply(data));
            } catch (IOException e) {
                logger.error("Could not read data: " + v.dataDef, e);
            } catch (Exception e) {
                logger.error("Could not apply brunel: " + v.brunelCommand, e);
            }
        }
    }

    private void showSimple(VisItem item) {
        this.item = item;
        int width = getWidth() - 20;
        int height = getHeight() - 20;
        builder.build(item, width, height);
        String html = WebDisplay.writeHtml(builder, width, height, Collections.<String>emptyList(), null);
        getBrowser().loadHTML(html);
    }

    public String toString() {
        return "BrunelDisplayPanel";
    }

}
