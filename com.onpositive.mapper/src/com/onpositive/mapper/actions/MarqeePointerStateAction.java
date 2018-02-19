package com.onpositive.mapper.actions;

import com.onpositive.mapper.editors.MapEditor;

public class MarqeePointerStateAction extends PointerStateAction {

	@Override
	protected int getPointerState() {
		return MapEditor.PS_MARQUEE;
	}
	

}
