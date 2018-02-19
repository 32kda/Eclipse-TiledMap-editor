package com.onpositive.mapper.actions;

import com.onpositive.mapper.editors.MapEditor;

public class DrawPointerStateAction extends PointerStateAction {

	@Override
	protected int getPointerState() {
		return MapEditor.PS_PAINT;
	}
	
	@Override
	protected boolean shouldBeEnabled() {
		return !mapEditor.isObjectLayerActive();
	}

}
