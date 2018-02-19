package com.onpositive.mapper.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import com.onpositive.mapper.editors.MapEditor;

public abstract class PointerStateAction extends Action implements IEditorActionDelegate, IPropertyChangeListener{
	
	protected MapEditor mapEditor;
	protected IAction action;

	public PointerStateAction() {
		super("Set pointer state",SWT.RADIO);
	}

	@Override
	public void run() {
		mapEditor.setCurrentPointerState(getPointerState());
	}
	
	protected abstract int getPointerState();
	
	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof MapEditor) {
			if (mapEditor != null)
				mapEditor.removePartPropertyListener(this);
			this.action = action;
			this.mapEditor = (MapEditor) targetEditor;
			mapEditor.addPartPropertyListener(this);
			setCheckedState();
		}
	}

	protected void setCheckedState() {
		int currentPointerState = ((MapEditor) mapEditor).getCurrentPointerState();
		boolean isChecked = currentPointerState == getPointerState();
		setChecked(isChecked);
		action.setChecked(isChecked);
		setEnabledState();
	}

	protected void setEnabledState() {
		boolean enabled = shouldBeEnabled();
		setEnabled(enabled);
		action.setEnabled(enabled);
	}

	protected boolean shouldBeEnabled() {
		return true;
	}

	@Override
	public void run(IAction action) {
		mapEditor.setCurrentPointerState(getPointerState());
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// Do nothing
		
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(MapEditor.POINTER_STATE_PROP))
			setCheckedState();
		else if (event.getProperty().equals(MapEditor.CURRENT_LAYER_PROP))
			setEnabledState();
	}

}
