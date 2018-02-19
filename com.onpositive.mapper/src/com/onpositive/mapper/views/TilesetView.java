package com.onpositive.mapper.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.part.PageSite;

import com.onpositive.mapper.editors.MapEditor;

@SuppressWarnings("restriction")
public class TilesetView extends PageBookView {
	
	public static final String ID = "com.onpositive.mapper.views.TilesetView";

	@Override
	protected IPage createDefaultPage(PageBook book) {
		Page page = new Page() {
			
			private Composite composite;

			@Override
			public void setFocus() {
				// Do nothing
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
	protected PageRec doCreatePage(IWorkbenchPart part) {
		if (!(part instanceof MapEditor))
			return null;
		TilesetPage tilesetPage = new TilesetPage((MapEditor) part);
		initPage(tilesetPage);
		tilesetPage.createControl(getPageBook());
		return new PageRec(part,tilesetPage);
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		// Do nothing
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
		if (page != null) {
			return page.getActiveEditor();
		}
		return null;
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		if (part instanceof MapEditor)
			return true;
		return false;
	}
	
	/**
	 * Initializes the given page with a page site.
	 * <p>
	 * Subclasses should call this method after the page is created but before
	 * creating its controls.
	 * </p>
	 * <p>
	 * Subclasses may override
	 * </p>
	 * 
	 * @param page
	 *            The page to initialize
	 */
	protected void initPage(IPageBookViewPage page) {
		try {
			page.init(new PageSite(getViewSite()));
		} catch (PartInitException e) {
			WorkbenchPlugin.log(getClass(), "initPage", e); //$NON-NLS-1$
		}
	}

}
