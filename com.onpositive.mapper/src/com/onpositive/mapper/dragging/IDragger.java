package com.onpositive.mapper.dragging;

import org.eclipse.swt.events.MouseEvent;


public interface IDragger {
	public void handleMove(MouseEvent e);
	public boolean canStartDrag(MouseEvent e);
	public void handleDragStart(MouseEvent e);
	public void handleDrag(MouseEvent e);
	public boolean canFinishDrag(MouseEvent e);
	public void handleDragFinish(MouseEvent e);
}
