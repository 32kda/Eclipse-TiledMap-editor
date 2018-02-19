package com.onpositive.mapper.actions;

import com.onpositive.mapper.editors.MapEditor;

public class ResizeObjectPointerStateAction extends PointerStateAction {

	@Override
	protected int getPointerState() {
		return MapEditor.PS_RESIZEOBJ;
	}
	
	@Override
	protected boolean shouldBeEnabled() {
		return mapEditor.isObjectLayerActive();
	}

}
