package com.onpositive.mapper.handlers;

import org.eclipse.core.commands.ExecutionEvent;

import com.onpositive.mapper.editors.MapEditor;

public class SelectNextLayerHandler extends MapEditorActionHandler {

	@Override
	public void executeForEditor(ExecutionEvent event, MapEditor activeEditor) {
		activeEditor.selectNextLayer();		
	}

}
