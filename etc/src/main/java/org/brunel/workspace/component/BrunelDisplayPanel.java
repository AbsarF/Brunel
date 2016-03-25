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
import org.brunel.model.VisItem;
import org.brunel.util.WebDisplay;
import org.brunel.workspace.util.UI;

import java.util.Collections;

/**
 * Uses JxBrowser to show Brunel
 */
public class BrunelDisplayPanel extends BrowserView {

    private static final String INITIAL_HTML =
            "<html><body><div style='font-family:Helvetica;font-weight:bold;width:100%;height:100%'>" +
                    "<span style='color:red;position:absolute;top:20%;left:10vw;font-size:30vh'>Brunel</span>" +
                    "<span style='opacity:0.3;position:absolute;top:39.5%;left:22vw;font-size:16vh;font-style:italic'>" +
                    "Workspace&nbsp;&nbsp;</span>" +
                    "</div></body></html>";
    private final BuilderOptions buildOptions;

    public BrunelDisplayPanel(BuilderOptions buildOptions) {
        this.buildOptions = buildOptions;
        setBackground(UI.BACKGROUND);
        getBrowser().loadHTML(INITIAL_HTML);
    }

    public void showSimple(VisItem item, Action action) {
        D3Builder builder = D3Builder.make(buildOptions);
        int width = getWidth() - 20;
        int height = getHeight() - 20;
        builder.build(item, width, height);
        String html = WebDisplay.writeHtml(builder, width, height, Collections.<String>emptyList(), null);
        getBrowser().loadHTML(html);
    }
}
