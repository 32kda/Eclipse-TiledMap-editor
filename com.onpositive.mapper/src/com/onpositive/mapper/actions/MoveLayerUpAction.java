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

import tiled.core.Map;

/**
 * Swaps the currently selected layer with the layer above.
 *
 * @version $Id$
 */
public class MoveLayerUpAction extends AbstractLayerAction
{
//    public MoveLayerUpAction(ISelectionProvider provider, MapEditor editor) {
//        super(provider,
//              editor,
//              Resources.getString("action.layer.moveup.name"),
//              Resources.getString("action.layer.moveup.tooltip"), Resources.getImageDescriptor("gnome-up.png"));
//
////        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift PAGE_UP"));
//    }

    protected void doPerformAction() {
        Map map = editor.getMap();
        int layerIndex = editor.getCurrentLayerIndex();
        int totalLayers = map.getTotalLayers();

        if (layerIndex < totalLayers - 1) {
            map.swapLayerUp(layerIndex);
            editor.setCurrentLayer(layerIndex + 1);
        }
    }
    
    @Override
    public boolean calcEnabled() {
    	return super.calcEnabled() && editor.getCurrentLayerIndex() < editor.getMap().getTotalLayers() - 1;
    }
}
