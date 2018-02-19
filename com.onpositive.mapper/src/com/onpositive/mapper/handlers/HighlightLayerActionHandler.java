package com.onpositive.mapper.handlers;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.State;

import com.onpositive.mapper.editors.MapEditor;

public class HighlightLayerActionHandler extends MapEditorActionHandler {

	@Override
	public void executeForEditor(ExecutionEvent event, MapEditor activeEditor) {
		Command command = event.getCommand();
		State state = command.getState("STYLE");
		state.setValue(!(Boolean) state.getValue());
		activeEditor.setHighlightCurrentLayer((Boolean) state.getValue());
	}

}
