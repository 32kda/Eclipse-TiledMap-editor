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
 * Merges the current layer with the one below and selects the merged layer.
 *
 * @version $Id$
 */
public class MergeLayerDownAction extends AbstractLayerAction
{
//    public MergeLayerDownAction(ISelectionProvider provider, MapEditor editor) {
//        super(provider,
//              editor,
//              Resources.getString("action.layer.mergedown.name"),
//        	  Resources.getString("action.layer.mergedown.tooltip"), Resources.getImageDescriptor("stock-merge-down-16.png"));
//
////        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift control M"));
//    }

    protected void doPerformAction() {
        Map map = editor.getMap();
        int layerIndex = editor.getCurrentLayerIndex();

        if (layerIndex > 0) {
            map.mergeLayerDown(layerIndex);
            editor.setCurrentLayer(layerIndex - 1);
        }
    }
    
    @Override
    public boolean calcEnabled() {
    	return super.calcEnabled() && editor.getCurrentLayerIndex() > 0;
    }
}
