/*
 *  Tiled Map Editor, (c) 2004-2006
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 */

package com.onpositive.mapper.actions;

import java.text.MessageFormat;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.mapeditor.resources.Resources;

/**
 * Clones the current layer, adds the clone to the map at the top of the layer
 * stack and then selects it.
 *
 * @version $Id$
 */
public class CloneLayerAction extends AbstractLayerAction
{
//    public CloneLayerAction(ISelectionProvider provider, MapEditor editor) {
//        super(provider,
//              editor,
//              Resources.getString("action.layer.duplicate.name"),
//              Resources.getString("action.layer.duplicate.tooltip"), Resources.getImageDescriptor("gimp-duplicate-16.png"));
//    }

    protected void doPerformAction() {
        MapLayer currentLayer = editor.getCurrentLayer();
        Map currentMap = editor.getMap();

        if (currentLayer != null) {
            try {
                MapLayer clone = (MapLayer) currentLayer.clone();
                String newName = Resources.getString(
                        "action.layer.duplicate.newlayer.name");
                clone.setName(MessageFormat.format(newName, new Object[]{clone.getName()}));
                currentMap.addLayer(clone);
                editor.setCurrentLayer(currentMap.getTotalLayers() - 1);
            } catch (CloneNotSupportedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
