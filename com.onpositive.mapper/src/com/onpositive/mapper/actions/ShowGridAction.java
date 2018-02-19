package com.onpositive.mapper.actions;

import org.eclipse.jface.action.IAction;

import com.onpositive.mapper.editors.MapEditor;

public class ShowGridAction extends AbstractActiveMapEditorAction {
	
	@Override
	public void run(IAction action) {
		editor.setShowGrid(action.isChecked());
	}

	@Override
	protected void setActiveState(IAction action, MapEditor editor) {
		action.setChecked(editor.isShowGrid());
	}

}
