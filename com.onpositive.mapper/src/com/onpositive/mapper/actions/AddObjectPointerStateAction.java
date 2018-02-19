package com.onpositive.mapper.actions;

import com.onpositive.mapper.editors.MapEditor;

public class AddObjectPointerStateAction extends PointerStateAction {

	@Override
	protected int getPointerState() {
		return MapEditor.PS_ADDOBJ;
	}
	
	@Override
	protected boolean shouldBeEnabled() {
		return mapEditor.isObjectLayerActive();
	}

}
