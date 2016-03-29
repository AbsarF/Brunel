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

package org.brunel.workspace.data;

import org.brunel.data.Dataset;
import org.brunel.data.Field;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.IOException;

/**
 * Created by graham on 3/3/16.
 */
public class FieldTransferHandler extends TransferHandler {

    public static final DataFlavor FIELD_FLAVOR = new DataFlavor(Field.class, "field");
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(Dataset.class, "data");

    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        super.exportAsDrag(comp, e, action);
    }

    public boolean importData(JComponent c, Transferable t) {
        try {
            if (canImport(c, t.getTransferDataFlavors())) {
                Field f = (Field) t.getTransferData(FIELD_FLAVOR);
                ((FieldDroppable) c).dropField(f);
                return true;
            }
        } catch (Exception e) {
            // Will not happen
        }

        return false;
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        if (c instanceof FieldDroppable)
            for (DataFlavor flavor : flavors)
                if (FIELD_FLAVOR.equals(flavor)) return true;
        return false;
    }

    public int getSourceActions(JComponent c) {
        return c.getParent() instanceof FieldComponent ? COPY : NONE;
    }

    protected Transferable createTransferable(JComponent c) {
        FieldComponent fieldComponent = (FieldComponent) c.getParent();
        Field f = fieldComponent.field;
        Dataset data = fieldComponent.dataset;
        return new FieldTransferable(f, data);
    }

    private static class FieldTransferable implements Transferable {
        private final Field field;
        private final Dataset data;

        public FieldTransferable(Field f, Dataset data) {
            field = f;
            this.data = data;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{FIELD_FLAVOR, DATA_FLAVOR};
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor == FIELD_FLAVOR || flavor == DATA_FLAVOR;
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor == FIELD_FLAVOR) return field;
            if (flavor == DATA_FLAVOR) return data;
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
