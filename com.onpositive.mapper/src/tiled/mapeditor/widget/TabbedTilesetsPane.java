/*
 *  Tiled Map Editor, (c) 2004-2008
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 *
 *  This class is based on TilesetChooserTabbedPane from Stendhal Map Editor
 *  by Matthias Totz <mtotz@users.sourceforge.net>
 */

package tiled.mapeditor.widget;

import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import tiled.core.Map;
import tiled.core.MapChangeListener;
import tiled.core.MapChangedEvent;
import tiled.core.TileSet;
import tiled.core.TilesetChangeListener;
import tiled.core.TilesetChangedEvent;
import tiled.mapeditor.brush.CustomBrush;
import tiled.mapeditor.util.TileRegionSelectionEvent;
import tiled.mapeditor.util.TileSelectionEvent;
import tiled.mapeditor.util.TileSelectionListener;

import com.onpositive.mapper.editors.MapEditor;

/**
 * Shows one tab for each Tileset.
 *
 * @version $Id$
 */
public class TabbedTilesetsPane extends Composite implements TileSelectionListener
{
    /**
     * Map of tile sets to tile palette panels
     */
    private final HashMap<TileSet, TilePalettePanel> tilePanels =
            new HashMap<TileSet, TilePalettePanel>();
    private final MyChangeListener listener = new MyChangeListener();
    private final MapEditor mapEditor;
    private Map map;
	private TabFolder tabFolder;       

    /**
     * Constructor.
     *
     * @param mapEditor reference to the MapEditor instance, used to change
     *                  the current tile and brush
     */
    public TabbedTilesetsPane(MapEditor mapEditor, Composite parent) {
    	super(parent,SWT.NONE);
    	setLayout(new FillLayout());
    	tabFolder = new TabFolder(this,SWT.NONE);
        this.mapEditor = mapEditor;
    }
    
    @Override
    public Point computeSize(int wHint, int hHint) {
    	// TODO Auto-generated method stub
    	return super.computeSize(wHint, hHint);
    }
    
    /**
     * Sets the tiles panes to the the ones from this map.
     * @param map the map of which to display the tilesets
     */
    public void setMap(Map map) {
        if (this.map != null) {
            this.map.removeMapChangeListener(listener);
        }

        if (map == null) {
            removeAll();
        } else {
            recreateTabs(map.getTilesets());
            map.addMapChangeListener(listener);
        }

        this.map = map;
    }

    private void removeAll() {
    	Control[] tabList = tabFolder.getTabList();
		for (Control control : tabList) {
			control.dispose();
		}
		
	}

	/**
     * Creates the panels for the tilesets.
     * @param tilesets the list of tilesets to create panels for
     */
    private void recreateTabs(List<TileSet> tilesets) {
        // Stop listening to the tile palette panels and their tilesets
        for (TilePalettePanel panel : tilePanels.values()) {
            panel.removeTileSelectionListener(this);
            panel.getTileset().removeTilesetChangeListener(listener);
        }
        tilePanels.clear();

        // Remove all tabs
        removeAll();

        if (tilesets != null) {
            // Add a new tab for each tileset of the map
            for (TileSet tileset : tilesets) {
                if (tileset != null) {
                    addTabForTileset(tileset);
                }
            }
        }
    }

    /**
     * Adds a tab with a {@link TilePalettePanel} for the given tileset.
     *
     * @param tileset the given tileset
     */
    private void addTabForTileset(TileSet tileset) {
        tileset.addTilesetChangeListener(listener);
        ScrolledComposite paletteScrollPane = new ScrolledComposite(tabFolder, SWT.BORDER
                | SWT.H_SCROLL | SWT.V_SCROLL);
        TilePalettePanel tilePanel = new TilePalettePanel(paletteScrollPane);
        tilePanel.setTileset(tileset);
        tilePanel.addTileSelectionListener(this);
        paletteScrollPane.setContent(tilePanel);
        addTab(tileset.getName(), paletteScrollPane);
        tilePanels.put(tileset, tilePanel);
    }

    private void addTab(String name, ScrolledComposite paletteScrollPane) {
    	TabItem item = new TabItem (tabFolder, SWT.NULL);
    	item.setText (name);
		item.setControl(paletteScrollPane);
	}

	/**
     * Informs the editor of the new tile.
     */
    public void tileSelected(TileSelectionEvent e) {
        mapEditor.setCurrentTile(e.getTile());
    }

    /**
     * Creates a stamp brush from the region contents and sets this as the
     * current brush.
     */
    public void tileRegionSelected(TileRegionSelectionEvent e) {
        mapEditor.setBrush(new CustomBrush(e.getTileRegion()));
    }

    private class MyChangeListener implements MapChangeListener, TilesetChangeListener
    {
        public void mapChanged(MapChangedEvent e) {
        }

        public void tilesetAdded(MapChangedEvent e, TileSet tileset) {
            addTabForTileset(tileset);
        }

        public void tilesetRemoved(MapChangedEvent e, int index) {
            TabItem tab = tabFolder.getItem(index);
            Control control = ((Composite) tab.getControl()).getChildren()[0];
            TilePalettePanel panel = (TilePalettePanel) control;
            TileSet set = panel.getTileset();
            panel.removeTileSelectionListener(TabbedTilesetsPane.this);
            set.removeTilesetChangeListener(listener);
            tilePanels.remove(set);
            removeTabAt(index);
        }

        public void tilesetsSwapped(MapChangedEvent e, int index0, int index1) {
        	System.err.println("Tileset swapping not supported yet");
//            int sIndex = getSelectionIndex();
//
//            String title0 = getItem(index0).getText();
//            String title1 = getItem(index1).getText();

//            Component comp0 = getComponentAt(index0); //TODO
//            Component comp1 = getComponentAt(index1);
//
//            removeTabAt(index1);
//            removeTabAt(index0);
//
//            insertTab(title1, null, comp1, null, index0);
//            insertTab(title0, null, comp0, null, index1);
//
//            if (sIndex == index0) {
//                sIndex = index1;
//            } else if (sIndex == index1) {
//                sIndex = index0;
//            }
//
//            setSelectedIndex(sIndex);
        }

        public void tilesetChanged(TilesetChangedEvent event) {
        }

        public void nameChanged(TilesetChangedEvent event, String oldName, String newName) {
            TileSet set = event.getTileset();
            int index = map.getTilesets().indexOf(set);

            tabFolder.getItem(index).setText(newName);
        }

        public void sourceChanged(TilesetChangedEvent event, String oldSource, String newSource) {
        }
    }
    
  	public void removeTabAt(int index) {
		TabItem item = tabFolder.getItem(index);
		item.getControl().dispose();
		item.dispose();
	}
}
