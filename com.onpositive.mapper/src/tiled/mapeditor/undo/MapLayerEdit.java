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

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import tiled.core.MapLayer;

/**
 * @version $Id$
 */
public class MapLayerEdit extends AbstractOperation {
	private final MapLayer editedLayer;
	private MapLayer layerUndo, layerRedo;
	private String name;
	private boolean inProgress;

	public MapLayerEdit(MapLayer layer) {
		super("edit");
		editedLayer = layer;
	}

	public MapLayerEdit(MapLayer layer, MapLayer before) {
		this(layer);
		start(before);
	}

	public MapLayerEdit(MapLayer layer, MapLayer before, MapLayer after) {
		this(layer, before);
		end(after);
	}

	public void start(MapLayer fml) {
		layerUndo = fml;
		inProgress = true;
	}

	public void end(MapLayer fml) {
		if (!inProgress) {
			new Exception("end called before start").printStackTrace();
		}
		if (fml != null) {
			layerRedo = fml;
			inProgress = false;
		}
	}

	public MapLayer getStart() {
		return layerUndo;
	}

	/* inherited methods */
	public void undo() throws CannotUndoException {
		if (editedLayer == null) {
			throw new CannotUndoException();
		}
		layerUndo.copyTo(editedLayer);
	}

	public boolean canUndo() {
		return layerUndo != null && editedLayer != null;
	}

	public void redo() throws CannotRedoException {
		if (editedLayer == null) {
			throw new CannotRedoException();
		}
		layerRedo.copyTo(editedLayer);
	}

	public boolean canRedo() {
		return layerRedo != null && editedLayer != null;
	}

	public void die() {
		layerUndo = null;
		layerRedo = null;
		inProgress = false;
	}

	public boolean addEdit(UndoableEdit anEdit) {
		if (inProgress && anEdit.getClass() == getClass()) {
			// TODO: absorb the edit
			// return true;
		}
		return false;
	}

	public void setPresentationName(String s) {
		name = s;
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
		if (editedLayer == null) {
			throw new CannotRedoException();
		}
		layerRedo.copyTo(editedLayer);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (editedLayer == null) {
			throw new CannotUndoException();
		}
		layerUndo.copyTo(editedLayer);
		return Status.OK_STATUS;
	}
}
