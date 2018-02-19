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
 * Swaps the currently selected layer with the layer below.
 *
 * @version $Id$
 */
public class MoveLayerDownAction extends AbstractLayerAction
{
//    public MoveLayerDownAction(ISelectionProvider provider, MapEditor editor) {
//        super(provider,
//              editor,
//              Resources.getString("action.layer.movedown.name"),
//              Resources.getString("action.layer.movedown.tooltip"), Resources.getImageDescriptor("gnome-down.png"));
//
////        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift PAGE_DOWN"));
//    }

    protected void doPerformAction() {
        Map map = editor.getMap();
        int layerIndex = editor.getCurrentLayerIndex();

        if (layerIndex > 0) {
            map.swapLayerDown(layerIndex);
            editor.setCurrentLayer(layerIndex - 1);
        }
    }
    
    @Override
    public boolean calcEnabled() {
    	return super.calcEnabled() && editor.getCurrentLayerIndex() > 0;
    }
}
