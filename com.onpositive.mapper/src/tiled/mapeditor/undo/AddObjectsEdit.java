/*
 *  Tiled Map Editor, (c) 2008
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

import tiled.core.MapObject;
import tiled.core.ObjectGroup;
import tiled.mapeditor.resources.Resources;

/**
 * Adds an object to an object group.
 *
 * @version $Id$
 */
public class AddObjectsEdit extends AbstractOperation
{
    private final ObjectGroup objectGroup;
    private final MapObject[] mapObjects;

    public AddObjectsEdit(ObjectGroup objectGroup, MapObject[] mapObjects) {
    	super(Resources.getString("action.object.add.multiple.name"));
        this.objectGroup = objectGroup;
        this.mapObjects = mapObjects;
    }
    
    public AddObjectsEdit(ObjectGroup objectGroup, MapObject mapObject) {
    	super(Resources.getString("action.object.add.name"));
        this.objectGroup = objectGroup;
        this.mapObjects = new MapObject[]{mapObject};
    }

    @Override
    public boolean canExecute() {
    	return false;
    }

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		for (MapObject object : mapObjects) {
			objectGroup.addObject(object);
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		for (MapObject object : mapObjects) {
			objectGroup.addObject(object);
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		for (MapObject object : mapObjects) {
			objectGroup.removeObject(object);
		}
		return Status.OK_STATUS;
	}
}
