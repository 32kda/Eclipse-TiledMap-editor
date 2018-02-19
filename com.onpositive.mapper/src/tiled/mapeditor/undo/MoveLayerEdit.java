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


import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Point;

import tiled.core.MapLayer;
import tiled.mapeditor.resources.Resources;

/**
 * @version $Id$
 */
public class MoveLayerEdit extends AbstractOperation
{
    private final MapLayer layer;
    private final Point moveDist;

    public MoveLayerEdit(MapLayer layer, Point moveDist) {
    	super(Resources.getString("action.layer.move.name"));
        this.layer = layer;
        this.moveDist = moveDist;
    }

    public String getPresentationName() {
        return Resources.getString("action.layer.move.name");
    }

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean canExecute() {
		return false;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		layer.translate(moveDist.x, moveDist.y);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		layer.translate(-moveDist.x, -moveDist.y);
		return Status.OK_STATUS;
	}
}
