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

/**
 * Activity events
 */
public class ActivityEvent {

    public enum Type {
        select,         // User indicated this
        activate        // perform an action for it
    }

    public final Type type;
    public final Object source;
    public final Object target;

    public ActivityEvent(Type type, Object target, Object source) {
        this.type = type;
        this.source = source;
        this.target = target;
    }


    public String toString() {
        return "ActivityEvent{" + type + " " + target + " from " + source + "}";
    }
}
