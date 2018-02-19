package com.onpositive.mapper.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.onpositive.mapper.views.LayerView;
import com.onpositive.mapper.views.TilesetView;

public class MapperPerspective implements IPerspectiveFactory {
	
	public static final String ID = "com.onpositive.mapper.perspective";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String FOLDER1_ID = "LeftFolder";
		IFolderLayout leftFolder =  layout.createFolder(FOLDER1_ID, IPageLayout.LEFT, (float)0.25, IPageLayout.ID_EDITOR_AREA);
		leftFolder.addView(LayerView.ID);
		leftFolder.addView(IPageLayout.ID_PROJECT_EXPLORER);
		//leftFolder.addView(AccessoriesGalleryPaletteView.ID);
		
		String FOLDER2_ID = "BottomFolder";
		IFolderLayout bottomFolder =  layout.createFolder(FOLDER2_ID, IPageLayout.BOTTOM, (float)0.5, FOLDER1_ID);		
		bottomFolder.addView(TilesetView.ID);
	}

}
