package com.onpositive.mapper.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class UIUtil {
	
	public static Color SELECTION_FRAME_COLOR = new Color(Display.getDefault(),new RGB(0x31,0x6a,0xc5));
	public static Color SELECTION_FILL_COLOR = new Color(Display.getDefault(),new RGB(0x76,0x7d,0xA0));
	
	public static IEditorPart getActiveEditor() {
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null)
			return null;
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		if (activePage == null)
			return null;
		return activePage.getActiveEditor();
	}
}
