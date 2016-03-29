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
import org.brunel.data.Field;
import org.brunel.model.VisItem;
import org.brunel.util.WebDisplay;
import org.brunel.workspace.activity.Activity;
import org.brunel.workspace.activity.ActivityEvent;
import org.brunel.workspace.activity.ActivityListener;
import org.brunel.workspace.item.ItemChart;
import org.brunel.workspace.util.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Uses JxBrowser to show Brunel
 */
public class BrunelDisplayPanel extends BrowserView implements ActivityListener {

    private static final Logger logger = LoggerFactory.getLogger(BrunelDisplayPanel.class);

    private static final String INITIAL_HTML =
            "<html><body><div style='font-family:Helvetica;font-weight:bold;width:100%;height:100%'>" +
                    "<span style='color:red;position:absolute;top:20%;left:10vw;font-size:30vh'>Brunel</span>" +
                    "<span style='opacity:0.3;position:absolute;top:39.5%;left:22vw;font-size:16vh;font-style:italic'>" +
                    "Workspace&nbsp;&nbsp;</span>" +
                    "</div></body></html>";
    private final BuilderOptions buildOptions;

    private final List<Field> fields = new ArrayList<>();
    private ItemChart chart;
    private Dataset data;

    public BrunelDisplayPanel(BuilderOptions buildOptions, Activity activity) {
        this.buildOptions = buildOptions;
        setBackground(UI.BACKGROUND);
        getBrowser().loadHTML(INITIAL_HTML);
        activity.addListener(this);
    }

    public void handleActivity(ActivityEvent event) {
        boolean change = false;
        if (event.getField() != null) {
            fields.add(0, event.getField());
            change = true;
        }
        if (event.getChart() != null) {
            chart = event.getChart();
            change = true;
        }
        if (event.getData() != null) {
            if (data != event.getData()) fields.clear();
            data = event.getData();
            change = true;
        }
        if (change) showIfValid();
    }

    private void showIfValid() {
        if (data == null) return;
        if (chart == null) return;
        if (chart.parameters.length > fields.size()) return;

        String text = makeText();

        logger.debug("Brunel Command: " + text);

        Action action;
        try {
            action = Action.parse(text);
        } catch (Exception e) {
            logger.error("Brunel Command to parse: " + text, e);
            return;
        }

        VisItem vis;
        try {
            vis = action.apply(data);
        } catch (Exception e) {
            logger.error("Brunel Command to build: " + text, e);
            return;
        }

        showSimple(vis, action);
    }

    private String makeText() {
        String text = chart.command;
        String[] parameters = chart.parameters;
        Map<Field, Integer> toBin = new LinkedHashMap<>();

        for (int i = 0; i < parameters.length; i++) {
            String p = parameters[i].toLowerCase();
            Field f = fields.get(i);

            if (p.startsWith("multi")) {
                // Multiple fields
                if (i != parameters.length - 1)
                    throw new IllegalStateException("Multi field must be last parameter");

                // Bin each if needed
                if (p.startsWith("multicat")) {
                    for (int j = i; j < fields.size(); j++) {
                        int bins = needsBinning(p, fields.get(j));
                        if (bins > 0) toBin.put(fields.get(j), bins);
                    }
                }

                // Modify the text
                text = this.modifyStringForMulti(text, fields, i);

            } else {

                // Handle binning (including converting to categorical when needed
                int bins = needsBinning(p, f);
                if (bins > 0) toBin.put(f, bins);

                // Modify the text
                text = text.replaceAll("\\$" + (i + 1), f.name);
            }

        }

        // Add in the binning
        for (Field f : toBin.keySet())
            text += " bin(" + f.name + ":" + toBin.get(f) + ")";

        return text;

    }

    public void showSimple(VisItem item, Action action) {
        D3Builder builder = D3Builder.make(buildOptions);
        int width = getWidth() - 20;
        int height = getHeight() - 20;
        builder.build(item, width, height);
        String html = WebDisplay.writeHtml(builder, width, height, Collections.<String>emptyList(), null);
        getBrowser().loadHTML(html);
    }

    public String toString() {
        return "BrunelDisplayPanel";
    }

    private String modifyStringForMulti(String text, List<Field> fields, int start) {
        int n = fields.size() - start;
        String all = "";

        for (int i = 0; i < n; ++i) {
            text = text.replaceAll("\\$" + (start + 1) + "\\[" + i + "\\]", fields.get(start + i).name);
            text = text.replaceAll("\\$" + (start + 1) + "\\[-" + i + "\\]", fields.get(fields.size() - 1 - i).name);
            if (i > 0) {
                all = all + ", ";
            }

            all = all + fields.get(start + i).name;
        }

        return text.replaceAll("\\$" + (start + 1), all);
    }

    private int needsBinning(String p, Field f) {
        String[] parts = p.split(":");
        String main = parts[0].trim();
        int divs = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : -1;
        if (main.startsWith("cat") || main.startsWith("multicat")) {
            // We need categorical fields
            if (f.preferCategorical()) {
                // If we have a given number we want, but have way too many, bin to reduce
                if (divs > 0 && f.categories().length > 2 * divs) return divs;
                else return -1;
            } else {
                // If numeric, we need to bin (9 bins as a default)
                return divs > 0 ? divs : 9;
            }
        }
        return -1;
    }
}
