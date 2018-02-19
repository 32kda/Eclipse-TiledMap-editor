package com.onpositive.mapper.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import com.onpositive.mapper.editors.MapEditor;


public abstract class AbstractActiveMapEditorAction implements IEditorActionDelegate{

	protected MapEditor editor;
	protected IAction action;
	
	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof MapEditor) {
			editor = (MapEditor) targetEditor;
			this.action = action;
		} else
			editor = null;
		action.setEnabled(editor != null);
		if (editor != null) {
			setActiveState(action,editor);
		} else {
			action.setChecked(false);
		}
	}
	
	protected abstract void setActiveState(IAction action, MapEditor editor);

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		//Do nothing
	}
	
    public boolean calcEnabled() {
    	return editor != null;
    }
}
