package com.onpositive.mapper.actions;

import org.eclipse.jface.action.IAction;

import com.onpositive.mapper.editors.MapEditor;

public class HighlightCurrentLayerAction extends AbstractActiveMapEditorAction {

	@Override
	public void run(IAction action) {
		editor.setHighlightCurrentLayer(action.isChecked());
	}

	@Override
	protected void setActiveState(IAction action, MapEditor editor) {
		action.setChecked(editor.isHighlightCurrentLayer());
	}

}
