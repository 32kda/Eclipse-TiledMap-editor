package com.onpositive.mapper.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.PlatformUI;

import tiled.core.Map;

import com.onpositive.mapper.dialogs.NewTilesetDialog;

public class AddTilesetAction extends TilesetAction {
	
	public AddTilesetAction() {
	}
	
	@Override
	public void runForMap(IAction action, Map map) {
		NewTilesetDialog dialog = new NewTilesetDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		dialog.setInitialTileSize(map.getTileWidth(),map.getTileHeight());
		int open = dialog.open();
		if (open == Dialog.OK) {
			map.addTileset(dialog.getCuttedResult());
		}
		
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
