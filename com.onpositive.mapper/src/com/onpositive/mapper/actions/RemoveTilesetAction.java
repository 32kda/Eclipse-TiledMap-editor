package com.onpositive.mapper.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import tiled.core.LayerLockedException;
import tiled.core.Map;
import tiled.core.TileSet;

public class RemoveTilesetAction extends TilesetAction {

	private Map map;
	private TileSet selectedTileSet;

	@Override
	protected void runForMap(IAction action, Map map) {
		try {
			map.removeTileset(selectedTileSet);
		} catch (LayerLockedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection.isEmpty() || map == null)
			setEnabled(false);
		else {
			if (selection instanceof IStructuredSelection) {
				selectedTileSet = (TileSet) ((IStructuredSelection) selection).getFirstElement();
			}
			setEnabled(true);
		}
	}

}
