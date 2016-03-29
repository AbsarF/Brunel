
/*
 * Copyright (c) 2015 IBM Corporation and others.
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

package org.brunel.app.brunel;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.prefs.Preferences;

 public class Settings {

    public final String name;

     private final Preferences preferences;

    public Settings(String name) {
        preferences = Preferences.userNodeForPackage(Settings.class);
        this.name = name;
        String home = System.getProperty("user.home");
        File cognitiveTop = new File(home, ".org.brunel");
        File localTop = new File(cognitiveTop, name);
        File store = new File(localTop, "store");
        store.mkdirs();
    }

     public Settings(Class<?> clazz) {
         this(clazz.getCanonicalName());
     }

     public int getInteger(String key, int defaultValue) {
         String v = getString(key);
         return v == null ? defaultValue : Integer.parseInt(v);
     }

     public String getString(String key) {
        return preferences.get(name + "-" + key, null);
    }

    public void persistWindowLocation(final Frame frame, String keyValue,
                                      int defaultX, int defaultY, int defaultWidth, int defaultHeight) {
        final String key = name + "-" + keyValue;
        int x = getInt(key + "-x", defaultX);
        int y = getInt(key + "-y", defaultY);
        int width = getInt(key + "-width", defaultWidth);
        int height = getInt(key + "-height", defaultHeight);
        GraphicsDevice[] screenDevices = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getScreenDevices();
        for (GraphicsDevice screenDevice : screenDevices) {
            Rectangle b = screenDevice.getDefaultConfiguration().getBounds();
            Rectangle maxBounds = new Rectangle(b.x + 20, b.y + 20,
                    b.width - 40, b.height - 40);
            if (maxBounds.intersects(new Rectangle(x, y, width, height))) frame.setLocation(x, y);
        }
        if (width > 0 && height > 0) {
            Dimension d = new Dimension(width, height);
            frame.setPreferredSize(d);
            frame.setSize(d);
        }
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (frame.isVisible()) saveWindowState(key, frame);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                if (frame.isVisible()) saveWindowState(key, frame);
            }
        });
    }

    private int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    private void saveWindowState(String key, Frame frame) {
        if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == 0) {
            preferences.putInt(key + "-x", frame.getLocation().x);
            preferences.putInt(key + "-y", frame.getLocation().y);
            preferences.putInt(key + "-width", frame.getSize().width);
            preferences.putInt(key + "-height", frame.getSize().height);
        }
    }


    public void putString(String key, String value) {
        preferences.put(name + "-" + key, value);
    }


}
