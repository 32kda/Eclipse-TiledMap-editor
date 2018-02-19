package com.onpositive.mapper.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.*;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;

import com.onpositive.mapper.editors.MapEditor;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class LayerView extends PageBookView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.onpositive.mapper.views.LayerView";
	


	/**
	 * The constructor.
	 */
	public LayerView() {
	}

	@Override
	protected IPage createDefaultPage(PageBook book) {
		Page page = new Page() {
			
			private Composite composite;

			@Override
			public void setFocus() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Control getControl() {
				return composite;
			}
			
			@Override
			public void createControl(Composite parent) {
				composite = new Composite(parent,SWT.NONE);				
			}
		};
		initPage(page);
		page.createControl(getPageBook());
		return page;
	}
	
	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		if (part instanceof MapEditor)
			return true;
		return false;
	}

	protected IEditorPart getActiveEditor() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			return null;
		IWorkbenchPage activePage = window.getActivePage();
		if (activePage == null)
			return null;
		IEditorPart activeEditor = activePage.getActiveEditor();
		return activeEditor;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		if (!(part instanceof MapEditor))
			return null;
		LayerViewPage layerPage = new LayerViewPage(((MapEditor) part));
		initPage(layerPage);
		layerPage.createControl(getPageBook());
		return new PageRec(part,layerPage);
	}
	
	@Override
	protected void showPageRec(PageRec pageRec) {
		super.showPageRec(pageRec);
		if (pageRec.page instanceof LayerViewPage) {
			MapEditor mapEditor = ((LayerViewPage) pageRec.page).getMapEditor();
			UndoActionHandler undoAction = new UndoActionHandler(getSite(), mapEditor.getUndoContext());
			undoAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_UNDO);
			RedoActionHandler redoAction = new RedoActionHandler(getSite(), mapEditor.getUndoContext());
			redoAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_REDO);
			IActionBars actionBars = ((IViewSite) getSite()).getActionBars();
		    actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);
		    actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction);
		    // Update action bars.
		    getViewSite().getActionBars().updateActionBars();
		}
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		// TODO Auto-generated method stub
		
	}

	 /* (non-Javadoc)
     * Method declared on PageBookView.
     */
    protected IWorkbenchPart getBootstrapPart() {
        IWorkbenchPage page = getSite().getPage();
        if (page != null) {
			return page.getActiveEditor();
		}

        return null;
    }
	
	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		partActivated(part);
	}

}