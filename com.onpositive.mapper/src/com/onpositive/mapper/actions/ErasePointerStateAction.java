package com.onpositive.mapper.actions;

import com.onpositive.mapper.editors.MapEditor;

public class ErasePointerStateAction extends PointerStateAction {

	public ErasePointerStateAction() {
		super();
	}

	@Override
	protected int getPointerState() {
		return MapEditor.PS_ERASE;
	}
	
	@Override
	protected boolean shouldBeEnabled() {
		return !mapEditor.isObjectLayerActive();
	}

}
