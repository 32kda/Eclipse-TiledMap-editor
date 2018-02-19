package com.onpositive.mapper.handlers;

import org.eclipse.core.commands.ExecutionEvent;

import com.onpositive.mapper.actions.AddTilesetAction;
import com.onpositive.mapper.editors.MapEditor;

public class AddTilesetHandler extends MapEditorActionHandler {

	@Override
	public void executeForEditor(ExecutionEvent event, MapEditor activeEditor) {
		AddTilesetAction tilesetAction = new AddTilesetAction();
		tilesetAction.runForMap(tilesetAction,activeEditor.getMap());
	}

}
