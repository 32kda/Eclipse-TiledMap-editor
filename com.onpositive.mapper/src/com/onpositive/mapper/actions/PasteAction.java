package com.onpositive.mapper.actions;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.onpositive.mapper.editors.MapEditor;

public class PasteAction extends AbstractMapEditorAction implements IPropertyChangeListener{
	
	public PasteAction(MapEditor mapEditor) {
		super(mapEditor);
	}

	@Override
	public void run() {
		mapEditor.paste();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(MapEditor.CLIPBOARD_CONTENT_PROP))
			setEnabled(mapEditor.hasClipboardData());
	}
	
}
