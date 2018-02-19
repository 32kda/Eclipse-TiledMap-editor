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
import tiled.core.MapLayer;

/**
 * Adds a layer to the current map and selects it.
 *
 * @version $Id$
 */
public class AddLayerAction extends AbstractLayerAction
{

    protected void doPerformAction() {
        Map currentMap = editor.getMap();
        MapLayer newLayer = currentMap.addLayer();
        editor.setCurrentLayer(currentMap.getTotalLayers() - 1);
        if (viewer != null)
        	viewer.editElement(newLayer,0);
    }
    
    @Override
	public boolean calcEnabled() {
		return true;
	}
    
    @Override
    public boolean isEnabled() {
    	return true;
    }

}
