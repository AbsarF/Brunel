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

package org.brunel.util;

import org.brunel.build.d3.D3Builder;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.brunel.util.PageOutput.Section.bodyEnd;
import static org.brunel.util.PageOutput.Section.bodyStart;
import static org.brunel.util.PageOutput.Section.brunel;
import static org.brunel.util.PageOutput.Section.controls;
import static org.brunel.util.PageOutput.Section.documentStart;
import static org.brunel.util.PageOutput.Section.headEnd;
import static org.brunel.util.PageOutput.Section.headStart;
import static org.brunel.util.PageOutput.Section.imports;
import static org.brunel.util.PageOutput.Section.scriptEnd;
import static org.brunel.util.PageOutput.Section.scriptStart;
import static org.brunel.util.PageOutput.Section.styleOverrides;
import static org.brunel.util.PageOutput.Section.stylesheets;
import static org.brunel.util.PageOutput.Section.svgItem;
import static org.brunel.util.PageOutput.Section.titles;

/**
 * Builds a HTML page for brunel
 */
public class PageOutput {

    /**
     * The various parts we define in the HTML
     */
    public enum Section {
        documentStart,
        headStart, imports, stylesheets, styleOverrides, headEnd,
        bodyStart, titles, svgItem, controls, footer, bodyEnd,
        scriptStart, brunel, scriptEnd,
        documentEnd
    }

    private final D3Builder builder;        // Source of information
    private final PrintWriter out;          // destination to write to
    private final List<String> titleList;   // Titles to add to the chart
    private final List<String> footerList;  // Footnotes to add to the chart
    private String pageTitle;               // title for the page

    /**
     * Define the page output creation
     *
     * @param builder the fully built base builder
     * @param writer  the output writer to send to
     */
    public PageOutput(D3Builder builder, Writer writer) {
        this.out = new PrintWriter(writer);
        this.builder = builder;
        this.titleList = new ArrayList<>();
        this.footerList = new ArrayList<>();
        this.pageTitle = "Brunel Output";
    }

    public PageOutput pageTitle(String name) {
        this.pageTitle = name;
        return this;
    }

    public PageOutput addTitles(String... names) {
        Collections.addAll(titleList, names);
        return this;
    }

    public PageOutput addFooters(String... names) {
        Collections.addAll(footerList, names);
        return this;
    }

    public PageOutput write() {

        // Assemble the pieces we need
        String css = builder.getStyleOverrides();

        writeSection(documentStart, "<!DOCTYPE html>", "<html lang=\"en\">");
        writeSection(headStart, "<head>",
                "\t<meta charset=\"UTF-8\">",
                "\t<meta http-Equiv=\"Cache-Control\" Content=\"no-cache\">",
                "\t<meta http-Equiv=\"Pragma\" Content=\"no-cache\">",
                "\t<meta http-Equiv=\"Expires\" Content=\"0\">",
                "\t<title>" + escapeHTML(pageTitle) + "</title>"
        );
        writeSection(imports, builder.makeImports());
        writeSection(stylesheets, builder.makeStyleSheets());

        if (!css.isEmpty()) {
            String[] lines = css.split("\n");
            List<String> complete = new ArrayList<>();
            complete.add("\t<style>\t\t/* Generated by Brunel style commands */");
            for (String line : lines) {
                if (line != null && !line.isEmpty())
                    complete.add("\t\t" + line);
            }
            complete.add("\t</style>");
            writeSection(styleOverrides, complete);
        }

        writeSection(headEnd, "</head>");
        writeSection(bodyStart, "<body style=\"margin:0\">");

        if (!titleList.isEmpty()) {
            String[] formattedTitles = new String[titleList.size()];
            for (int i = 0; i < formattedTitles.length; i++)
                formattedTitles[i] = format(titleList.get(i), i == 0 ? "h2" : "p");
            writeSection(titles, formattedTitles);
        }

        writeSection(svgItem, "<svg id=\"visualization\" width=\""
                + builder.visWidth + "\" height=\""
                + builder.visHeight + "\"></svg>");

        if (builder.getControls().isNeeded()) {
            writeSection(controls, "<div id=\"controls\" class=\"brunel\"></div>");
        }

        if (!footerList.isEmpty()) {
            String[] formattedTitles = new String[footerList.size()];
            for (int i = 0; i < formattedTitles.length; i++)
                formattedTitles[i] = format(footerList.get(i), "p");
            writeSection(titles, formattedTitles);
        }

        writeSection(bodyEnd, "</body>");

        writeSection(scriptStart, "<script>");
        writeSection(brunel, builder.getVisualization());
        writeSection(scriptEnd, "</script>");

        return this;
    }

    private String format(String s, String tag) {
        s = s.trim();
        if (s.startsWith("<") && s.endsWith(">")) return s;             // pre-formatted
        return "<" + tag + ">" + escapeHTML(s) + "</" + tag + ">";      // needs formatting

    }

    /**
     * A simple writer that can be overridden for custom content
     *
     * @param section the type that is being written (ignored in the default implementation)
     * @param lines   lines to write out
     */
    protected void writeSection(Section section, String... lines) {
        for (String content : lines)
            out.println(content);
    }

    /**
     * A simple writer that can be overridden for custom content
     *
     * @param section the type that is being written (ignored in the default implementation)
     * @param lines   lines to write out
     */
    private final void writeSection(Section section, List<String> lines) {
        writeSection(section, lines.toArray(new String[lines.size()]));
    }

    public static String escapeHTML(String s) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
                b.append("&#");
                b.append((int) c);
                b.append(';');
            } else {
                b.append(c);
            }
        }
        return b.toString();
    }

}