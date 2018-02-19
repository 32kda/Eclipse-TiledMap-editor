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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

import tiled.core.Map;
import tiled.core.TileLayer;
import tiled.util.TileMergeHelper;

/**
 * Merges all layers of the map. Optionally it will create a new tileset with
 * merged tiles.
 *
 * @version $Id$
 */
public class MergeAllLayersAction extends AbstractLayerAction
{
//    public MergeAllLayersAction(ISelectionProvider provider, MapEditor editor) {
//        super(provider,
//              editor,
//              Resources.getString("action.layer.mergeall.name"), Resources.getString("action.layer.mergeall.tooltip"));
//    }

    protected void doPerformAction() {
        Map map = editor.getMap();

        boolean result = MessageDialog.open(MessageDialog.QUESTION_WITH_CANCEL,editor.getEditorSite().getShell(),"Merge Tiles?",
        		"Do you wish to merge tile images, and create a new tile set?",SWT.NONE);
//        int ret = JOptionPane.showConfirmDialog(null,
//                "Do you wish to merge tile images, and create a new tile set?",
//                "Merge Tiles?", JOptionPane.YES_NO_CANCEL_OPTION);

        if (result) {
            TileMergeHelper tmh = new TileMergeHelper(map);
            int len = map.getTotalLayers();
            //TODO: Add a dialog option: "Yes, visible only"
            TileLayer newLayer = tmh.merge(0, len, true);
            map.removeAllLayers();
            map.addLayer(newLayer);
            newLayer.setName("Merged Layer");
            map.addTileset(tmh.getSet());
            editor.setCurrentLayer(0);
        }
        else /*if (ret == JOptionPane.NO_OPTION)*/ {
            while (map.getTotalLayers() > 1) {
                map.mergeLayerDown(editor.getCurrentLayerIndex());
            }
            editor.setCurrentLayer(0);
        }
        
    }
    
    @Override
    public boolean calcEnabled() {
    	return editor.getMap().getTotalLayers() > 1;
    }
}
