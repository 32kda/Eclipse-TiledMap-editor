package com.onpositive.mapper.actions;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.onpositive.mapper.editors.MapEditor;

public class CopyAllAction extends AbstractMapEditorAction implements IPropertyChangeListener {
	
	public CopyAllAction(MapEditor mapEditor) {
		super(mapEditor);
	}
	
	@Override
	public void run() {
		mapEditor.copyAll();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(MapEditor.MARQEE_SELECTION_PROP))
			setEnabled(mapEditor.hasSelection());
	}

}
