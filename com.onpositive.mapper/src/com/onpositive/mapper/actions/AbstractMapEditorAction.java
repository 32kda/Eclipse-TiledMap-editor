package com.onpositive.mapper.actions;

import org.eclipse.jface.action.Action;

import com.onpositive.mapper.editors.MapEditor;

public abstract class AbstractMapEditorAction extends Action {

	protected MapEditor mapEditor;

	public AbstractMapEditorAction(MapEditor mapEditor) {
		this.mapEditor = mapEditor;
	}
	
}
