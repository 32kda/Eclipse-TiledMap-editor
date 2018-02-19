package com.onpositive.mapper.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;

import com.onpositive.mapper.editors.MapEditor;
import com.onpositive.mapper.ui.UIUtil;

public abstract class MapEditorActionHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart activeEditor = UIUtil.getActiveEditor();
		if (activeEditor instanceof MapEditor) {
			executeForEditor(event, (MapEditor)activeEditor);
		}
		return null;
	}

	public abstract void executeForEditor(ExecutionEvent event, MapEditor activeEditor);

}
