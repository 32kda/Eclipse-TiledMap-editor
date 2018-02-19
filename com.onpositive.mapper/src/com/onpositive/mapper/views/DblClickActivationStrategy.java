package com.onpositive.mapper.views;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.MouseEvent;

public class DblClickActivationStrategy extends ColumnViewerEditorActivationStrategy {

	public DblClickActivationStrategy(ColumnViewer viewer) {
		super(viewer);
	}
	
	@Override
	protected boolean isEditorActivationEvent(
			ColumnViewerEditorActivationEvent event) {
		boolean singleSelect = ((IStructuredSelection)getViewer().getSelection()).size() == 1;
		boolean isLeftMouseSelect = event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION && ((MouseEvent)event.sourceEvent).button == 1;

		return singleSelect && (isLeftMouseSelect
				|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC
				|| event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL);
	}
	
}