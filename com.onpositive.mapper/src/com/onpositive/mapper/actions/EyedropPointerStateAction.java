package com.onpositive.mapper.actions;

import com.onpositive.mapper.editors.MapEditor;

public class EyedropPointerStateAction extends PointerStateAction {

	@Override
	protected int getPointerState() {
		return MapEditor.PS_EYED;
	}
	
	@Override
	protected boolean shouldBeEnabled() {
		return !mapEditor.isObjectLayerActive();
	}

}
