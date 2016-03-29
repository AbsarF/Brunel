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

package org.brunel.workspace.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Processes any form of activity
 */
public class Activity {
    private static final Logger logger = LoggerFactory.getLogger(Activity.class);

    private final List<ActivityListener> listeners = new ArrayList<>();

    public void addListener(ActivityListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ActivityListener listener) {
        listeners.remove(listener);
    }

    public void fireSelect(Object target, Object source) {
        fire(new ActivityEvent(ActivityEvent.Type.select, target, source));
    }

    public void fireActivate(Object target, Object source) {
        fire(new ActivityEvent(ActivityEvent.Type.activate, target, source));
    }

    private void fire(ActivityEvent event) {
        logger.debug("Firing " + event);
        for (ActivityListener listener : listeners) {
            logger.trace("Sending event to listener "+ listener);
            listener.handleActivity(event);
        }
    }
}
