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

package tiled.mapeditor.undo;

import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.onpositive.mapper.editors.MapEditor;

import tiled.core.Map;
import tiled.core.MapLayer;

/**
 * A change in the layer state. Used for adding, removing and rearranging
 * the layer stack of a map.
 */
public class MapLayerStateEdit extends AbstractOperation
{
    private final MapEditor mapEditor;
    private final Vector<MapLayer> layersBefore;
    private final Vector<MapLayer> layersAfter;
    private final String name;
	private int oldIdx = -1;
	private int newIdx = -1;

    public MapLayerStateEdit(MapEditor editor,
                             Vector<MapLayer> before,
                             Vector<MapLayer> after,
                             String name) {
    	super(name);
        mapEditor = editor;
        layersBefore = before;
        layersAfter = after;
        this.name = name;
    }
    
    public MapLayerStateEdit(MapEditor editor,
            Vector<MapLayer> before,
            Vector<MapLayer> after,
            String name, 
            int oldIdx,
            int newIdx) {
		super(name);
		mapEditor = editor;
		layersBefore = before;
		layersAfter = after;
		this.name = name;
		this.oldIdx = oldIdx;
		this.newIdx = newIdx;
	}

    public String getPresentationName() {
        return name;
    }
    
    @Override
    public boolean canExecute() {
    	return false;
    }

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		mapEditor.getMap().setLayerVector(layersAfter);
		if (newIdx > -1)
			mapEditor.setCurrentLayer(newIdx);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		mapEditor.getMap().setLayerVector(layersBefore);
		if (oldIdx > -1)
			mapEditor.setCurrentLayer(oldIdx);
		return Status.OK_STATUS;
	}
}
