package com.onpositive.mapper.actions;

import org.eclipse.jface.action.IAction;

import com.onpositive.mapper.editors.MapEditor;

public class SnapToGridAction extends AbstractActiveMapEditorAction {

	@Override
	public void run(IAction action) {
		editor.setSnapToGrid(action.isChecked());
	}

	@Override
	protected void setActiveState(IAction action, MapEditor editor) {
		action.setChecked(editor.isSnapToGrid());
	}

}
