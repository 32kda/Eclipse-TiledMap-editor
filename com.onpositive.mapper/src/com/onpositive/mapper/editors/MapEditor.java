package com.onpositive.mapper.editors;

import java.awt.geom.Area;
import java.io.ByteArrayInputStream;
import java.util.ListIterator;
import java.util.Stack;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.part.EditorPart;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.MapObject;
import tiled.core.ObjectGroup;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.io.xml.XMLMapTransformer;
import tiled.mapeditor.brush.AbstractBrush;
import tiled.mapeditor.brush.CustomBrush;
import tiled.mapeditor.resources.Resources;
import tiled.mapeditor.selection.SelectionLayer;
import tiled.util.Converter;
import tiled.util.TiledConfiguration;
import tiled.view.MapView;

public class MapEditor extends EditorPart {
	
	protected class MapMouseListener implements MouseListener, MouseMoveListener, MouseTrackListener {

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseDown(MouseEvent e) {
			mousePressed(e);
		}

		@Override
		public void mouseUp(MouseEvent e) {
			mouseReleased(e);
		}

		@Override
		public void mouseMove(MouseEvent e) {
			mouseMoved(e);
		}

		@Override
		public void mouseEnter(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExit(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseHover(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
    // Constants and the like
    private static final int PS_POINT     = 0;
    private static final int PS_PAINT     = 1;
    private static final int PS_ERASE     = 2;
    private static final int PS_POUR      = 3;
    private static final int PS_EYED      = 4;
    private static final int PS_MARQUEE   = 5;
    private static final int PS_MOVE      = 6;
    private static final int PS_ADDOBJ    = 7;
    private static final int PS_REMOVEOBJ = 8;
    private static final int PS_MOVEOBJ   = 9;
    
    private static final Preferences prefs = TiledConfiguration.root();

	private Map currentMap;
	private Tile currentTile;
	private AbstractBrush currentBrush;

	private Point mousePressLocation, mouseInitialPressLocation;
	private Point mouseLastPixelLocation;
	private Point mouseInitialScreenLocation;
	private Point moveDist;
	private int mouseButton;
	private boolean bMouseIsDown, bMouseIsDragging;
	
    private int currentPointerState;
    private MapObject currentObject = null;
    private int currentLayer = -1;
    private SelectionLayer cursorHighlight;
    
    private SelectionLayer marqueeSelection;

	private MapView mapView;
	private ScrolledComposite mapScrollView;
	private IStatusLineManager statusLineManager;
	private MapMouseListener mouseListener = new MapMouseListener();

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		try {
			FileDocumentProvider documentProvider = new FileDocumentProvider();
			documentProvider.connect(input);
			IDocument document = documentProvider.getDocument(input);
			if (input instanceof IPathEditorInput) {
				IPath path = ((IPathEditorInput) input).getPath();
				System.out.println(path.toOSString());
				currentMap = new XMLMapTransformer().readMap(new ByteArrayInputStream(
						document.get().getBytes()), path.toFile());
			} else
				currentMap = new XMLMapTransformer().readMap(new ByteArrayInputStream(
						document.get().getBytes()));
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setPartName(input.getName());
		IActionBars actionBars = site.getActionBars();
		if (actionBars != null) {
			statusLineManager = actionBars.getStatusLineManager();
		}
		firePropertyChange(IEditorPart.PROP_TITLE);
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		mapScrollView = new ScrolledComposite(composite,
				SWT.H_SCROLL | SWT.V_SCROLL);
		mapView = MapView.createViewforMap(mapScrollView, currentMap);
		mapView.addMouseListener(mouseListener);
		mapView.addMouseMoveListener(mouseListener);
		mapView.addMouseTrackListener(mouseListener);
		mapScrollView.setContent(mapView);
		// Set the minimum size
		// scrolledComposite.setMinSize(400, 400);

		// Expand both horizontally and vertically
		// scrolledComposite.setExpandHorizontal(true);
		// scrolledComposite.setExpandVertical(true);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public Map getMap() {
		return currentMap;
	}

	public void setCurrentTile(Tile tile) {
		this.currentTile = tile;
	}

	public void setBrush(AbstractBrush currentBrush) {
		this.currentBrush = currentBrush;
	}

	// private ColorManager colorManager;
	//
	// public MapEditor() {
	// super();
	// colorManager = new ColorManager();
	// setSourceViewerConfiguration(new XMLConfiguration(colorManager));
	// setDocumentProvider(new XMLDocumentProvider());
	// }
	// public void dispose() {
	// colorManager.dispose();
	// super.dispose();
	// }

	public void mousePressed(MouseEvent e) {
		Point tile = mapView.screenToTileCoords(e.x, e.y);
		mouseButton = e.button;
		bMouseIsDown = true;
		bMouseIsDragging = false;
		mousePressLocation = mapView.screenToTileCoords(e.x, e.y);
		mouseInitialPressLocation = mousePressLocation;

		MapLayer layer = getCurrentLayer();

		if (mouseButton == 2
				|| (mouseButton == 1 && (e.stateMask & SWT.ALT) != 0)) {
			// Remember screen location for scrolling with middle mouse button
			mouseInitialScreenLocation = new Point(e.x, e.y);
		} else if (mouseButton == 1) {
			switch (currentPointerState) {
			case PS_PAINT:
				if (layer instanceof TileLayer) {
					currentBrush.startPaint(currentMap, tile.x, tile.y,
							mouseButton, currentLayer);
				}
			case PS_ERASE:
			case PS_POUR:
//				paintEdit = new MapLayerEdit(layer, createLayerCopy(layer),
//						null);
				break;
			default:
			}
		}

		if (currentPointerState == PS_MARQUEE) {
			boolean contains = false;
			if (marqueeSelection != null
					&& marqueeSelection.getSelectedArea().contains(tile.x,
							tile.y)) {
				contains = true;
			}
			if (marqueeSelection == null && !contains) {
				marqueeSelection = new SelectionLayer(currentMap.getWidth(),
						currentMap.getHeight());
				currentMap.addLayerSpecial(marqueeSelection);
			} else if (marqueeSelection != null
					&& (e.stateMask & SWT.BUTTON1) > 0) {
				currentMap.removeLayerSpecial(marqueeSelection);
				if (contains) {
					marqueeSelection = null;
				} else {
					marqueeSelection = new SelectionLayer(
							currentMap.getWidth(), currentMap.getHeight());
					currentMap.addLayerSpecial(marqueeSelection);
				}
			}
		} else if (currentPointerState == PS_MOVE) {
			// Initialize move distance to (0, 0)
			moveDist = new Point(0, 0);
		}

		doMouse(e);
	}
	
    public void mouseReleased(MouseEvent event) {
        final MapLayer layer = getCurrentLayer();
        final Point limp = mouseInitialPressLocation;

        if (currentPointerState == PS_MARQUEE) {
            // Uncommented to allow single tile selections
            /*
            Point tile = mapView.screenToTileCoords(event.x, event.y);
            if (tile.y - limp.y == 0 && tile.x - limp.x == 0) {
                if (marqueeSelection != null) {
                    currentMap.removeLayerSpecial(marqueeSelection);
                    marqueeSelection = null;
                }
            }
            */

            // There should be a proper notification mechanism for this...
            //if (marqueeSelection != null) {
            //    tileInstancePropertiesDialog.setSelection(marqueeSelection);
            //}
        } else if (currentPointerState == PS_MOVE) {
            if (layer != null && (moveDist.x != 0 || moveDist.x != 0)) {
//                undoSupport.postEdit(new MoveLayerEdit(layer, moveDist));
            }
        } else if (currentPointerState == PS_PAINT) {
            if (layer instanceof TileLayer) {
                currentBrush.endPaint();
            }
        } else if (currentPointerState == PS_MOVEOBJ) {
            if (layer instanceof ObjectGroup && currentObject != null &&
                    (moveDist.x != 0 || moveDist.x != 0)) {
//                undoSupport.postEdit(
//                        new MoveObjectEdit(currentObject, moveDist));
            }
        }


        if (/*bMouseIsDragging && */currentPointerState == PS_PAINT ||
                currentPointerState == PS_ADDOBJ)
        {
            Point tile = mapView.screenToTileCoords(
                    event.x, event.y);
            int minx = Math.min(limp.x, tile.x);
            int miny = Math.min(limp.y, tile.y);

            Rectangle bounds = new Rectangle(
                    minx, miny,
                    (Math.max(limp.x, tile.x) - minx) + 1,
                    (Math.max(limp.y, tile.y) - miny) + 1);

            // STAMP
            if (mouseButton == 3 &&
                    layer instanceof TileLayer) {
                // Right mouse button dragged: create and set custom brush
                TileLayer brushLayer = new TileLayer(bounds);
                brushLayer.copyFrom(getCurrentLayer());
                brushLayer.setOffset(tile.x - (int) bounds.width / 2,
                                     tile.y - (int) bounds.height / 2);

                // Do a quick check to make sure the selection is not empty
                if (brushLayer.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            Resources.getString("dialog.selection.empty"),
                            Resources.getString("dialog.selection.empty"),
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    setBrush(new CustomBrush(brushLayer));
                    cursorHighlight.setOffset(
                            tile.x - (int) bounds.width / 2,
                            tile.y - (int) bounds.height / 2);
                }
            }
            else if (mouseButton == 1 &&
                    layer instanceof ObjectGroup) {
                // TODO: Fix this to use pixels in the first place
                // (with optional snap to grid)
                int w = currentMap.getTileWidth();
                int h = currentMap.getTileHeight();
                MapObject object = new MapObject(
                        bounds.x * w,
                        bounds.y * h,
                        bounds.width * w,
                        bounds.height * h);
                /*Point pos = mapView.screenToPixelCoords(
                        event.x, event.y);*/
                ObjectGroup group = (ObjectGroup) layer;
//                undoSupport.postEdit(new AddObjectEdit(group, object));
                group.addObject(object);
                mapView.redraw();
            }

            //get rid of any visible marquee
            if (marqueeSelection != null) {
                currentMap.removeLayerSpecial(marqueeSelection);
                marqueeSelection = null;
            }
        }

//        if (paintEdit != null) {
//            if (layer != null) {
//                try {
//                    MapLayer endLayer = paintEdit.getStart().createDiff(layer);
//                    paintEdit.end(endLayer);
//                    undoSupport.postEdit(paintEdit);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            paintEdit = null;
//        }

        currentObject = null;

        mouseButton = SWT.DEFAULT;
        bMouseIsDown = false;
        bMouseIsDragging = false;
    }
	
    /**
     * Returns the currently selected layer.
     *
     * @return the currently selected layer
     */
    public MapLayer getCurrentLayer() {
        return currentMap.getLayer(currentLayer);
    }
    
    private void doMouse(MouseEvent event) {
        if (currentMap == null || currentLayer < 0) {
            return;
        }

        Point tile = mapView.screenToTileCoords(event.x, event.y);
        MapLayer layer = getCurrentLayer();

        if (layer == null) {
            return;
        } else if (mouseButton == 3) {
            if (layer instanceof TileLayer) {
                if (!bMouseIsDragging) {
                    // Click event is sent before the drag event
                    // so this one always happens
                    Tile newTile = ((TileLayer)layer).getTileAt(tile.x, tile.y);
                    setCurrentTile(newTile);
                } else if (currentPointerState == PS_PAINT) {
                    // In case we are dragging to create a custom brush, let
                    // the user know where we are creating it from
                    if (marqueeSelection == null) {
                        marqueeSelection = new SelectionLayer(
                                currentMap.getWidth(), currentMap.getHeight());
                        currentMap.addLayerSpecial(marqueeSelection);
                    }

                    Point limp = mouseInitialPressLocation;
                    Rectangle oldArea =
                        marqueeSelection.getSelectedAreaBounds();
                    int minx = Math.min(limp.x, tile.x);
                    int miny = Math.min(limp.y, tile.y);

                    Rectangle selRect = new Rectangle(
                            minx, miny,
                            (Math.max(limp.x, tile.x) - minx)+1,
                            (Math.max(limp.y, tile.y) - miny)+1);

                    marqueeSelection.selectRegion(Converter.SWTRectToAWT(selRect));
                    if (oldArea != null) {
                        oldArea.add(marqueeSelection.getSelectedAreaBounds());
                        mapView.repaintRegion(oldArea);
                    }
                }
            } else if (layer instanceof ObjectGroup && !bMouseIsDragging) {
                // Get the object on this location and display the relative options dialog
                ObjectGroup group = (ObjectGroup) layer;
                Point pos = mapView.screenToPixelCoords(
                        event.x, event.y);
                MapObject obj = group.getObjectNear(pos.x, pos.y, mapView.getZoom());
                if (obj != null) {
//                    ObjectDialog od = new ObjectDialog(appFrame, obj, undoSupport);
//                    od.getProps();
                }
            }
        } else if (mouseButton == 2 ||
                (mouseButton == 1 &&
                 (event.stateMask & SWT.ALT ) != 0)) {
            // Scroll with middle mouse button
            int dx = event.x - mouseInitialScreenLocation.x;
            int dy = event.y - mouseInitialScreenLocation.y;
            Point currentPosition = mapScrollView.getOrigin();
//            JViewport mapViewPort = mapScrollPane.getViewport();
//            Point currentPosition = mapViewPort.getViewPosition();
            mouseInitialScreenLocation = new Point(
                    event.x - dx,
                    event.y - dy);

            Point newPosition = new Point(
                    currentPosition.x - dx,
                    currentPosition.y - dy);

            // Take into account map boundaries in order to prevent
            // scrolling past them
            Point viewSize = mapView.getSize();
            Point viewportSize = mapScrollView.getSize();
            int maxX = viewSize.x - viewportSize.x;
            int maxY = viewSize.y - viewportSize.y;
            newPosition.x = Math.min(maxX, Math.max(0, newPosition.x));
            newPosition.y = Math.min(maxY, Math.max(0, newPosition.y));

//            mapViewPort.setViewPosition(newPosition);
        } else if (mouseButton == 1) {
            switch (currentPointerState) {
                case PS_PAINT:
//                    paintEdit.setPresentationName(TOOL_PAINT); //TODO undo
                    if (layer instanceof TileLayer) {
                        try {
                            mapView.repaintRegion(
                                    currentBrush.doPaint(tile.x, tile.y));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case PS_ERASE:
//                    paintEdit.setPresentationName(TOOL_ERASE);
                    if (layer instanceof TileLayer) {
                        ((TileLayer) layer).setTileAt(tile.x, tile.y, null);
                        mapView.repaintRegion(new Rectangle(
                                tile.x, tile.y, 1, 1));
                    }
                    break;
                case PS_POUR:
//                    paintEdit = null;
                    if (layer instanceof TileLayer) {
                        TileLayer tileLayer = (TileLayer) layer;
                        Tile oldTile = tileLayer.getTileAt(tile.x, tile.y);
                        pour(tileLayer, tile.x, tile.y, currentTile, oldTile);
                        mapView.redraw();
                    }
                    break;
                case PS_EYED:
                    if (layer instanceof TileLayer) {
                        TileLayer tileLayer = (TileLayer) layer;
                        Tile newTile = tileLayer.getTileAt(tile.x, tile.y);
                        setCurrentTile(newTile);
                    }
                    break;
                case PS_MOVE: {
                    Point translation = new Point(
                            tile.x - mousePressLocation.x,
                            tile.y - mousePressLocation.y);

                    layer.translate(translation.x, translation.y);
                    moveDist.x += translation.x;
                    moveDist.y += translation.y;
                    mapView.redraw();
                    break;
                }
                case PS_MARQUEE:
                    if (!(layer instanceof TileLayer)) {
                        break;
                    }
                    if (marqueeSelection != null) {
                        Point limp = mouseInitialPressLocation;
                        Rectangle oldArea =
                            marqueeSelection.getSelectedAreaBounds();
                        int minx = Math.min(limp.x, tile.x);
                        int miny = Math.min(limp.y, tile.y);

                        Rectangle selRect = new Rectangle(
                                minx, miny,
                                (Math.max(limp.x, tile.x) - minx)+1,
                                (Math.max(limp.y, tile.y) - miny)+1);

                        if ((event.stateMask & SWT.SHIFT) > 0) {
                            marqueeSelection.add(new Area(Converter.SWTRectToAWT(selRect)));
                        } else if ((event.stateMask & SWT.CONTROL) > 0) {
                            marqueeSelection.subtract(new Area(Converter.SWTRectToAWT(selRect)));
                        } else {
                            marqueeSelection.selectRegion(Converter.SWTRectToAWT(selRect));
                        }
                        if (oldArea != null) {
                            oldArea.add(
                                    marqueeSelection.getSelectedAreaBounds());
                            mapView.repaintRegion(oldArea);
                        }
                    }
                    break;
                case PS_ADDOBJ:
                    if (layer instanceof  ObjectGroup) {
                        if (marqueeSelection == null) {
                            marqueeSelection = new SelectionLayer(
                                    currentMap.getWidth(),
                                    currentMap.getHeight());
                            currentMap.addLayerSpecial(marqueeSelection);
                        }

                        Point limp = mouseInitialPressLocation;
                        Rectangle oldArea =
                            marqueeSelection.getSelectedAreaBounds();
                        int minx = Math.min(limp.x, tile.x);
                        int miny = Math.min(limp.y, tile.y);

                        Rectangle selRect = new Rectangle(
                                minx, miny,
                                (Math.max(limp.x, tile.x) - minx) + 1,
                                (Math.max(limp.y, tile.y) - miny) + 1);

                        marqueeSelection.selectRegion(Converter.SWTRectToAWT(selRect));
                        if (oldArea != null) {
                            oldArea.add(marqueeSelection.getSelectedAreaBounds());
                            mapView.repaintRegion(oldArea);
                        }
                    }
                    break;
                case PS_REMOVEOBJ:
                    if (layer instanceof ObjectGroup) {
                        ObjectGroup group = (ObjectGroup) layer;
                        Point pos = mapView.screenToPixelCoords(
                                event.x, event.y);
                        MapObject obj = group.getObjectNear(pos.x, pos.y, mapView.getZoom());
                        if (obj != null) {
//                            undoSupport.postEdit(new RemoveObjectEdit(group, obj));
                            group.removeObject(obj);
                            // TODO: repaint only affected area
                            mapView.redraw();
                        }
                    }
                    break;
                case PS_MOVEOBJ:
                    if (layer instanceof ObjectGroup) {
                        Point pos = mapView.screenToPixelCoords(
                                event.x, event.y);
                        if (currentObject == null) {
                            ObjectGroup group = (ObjectGroup) layer;
                            currentObject = group.getObjectNear(pos.x, pos.y, mapView.getZoom());
                            if (currentObject == null) { // No object to move
                                break;
                            }
                            mouseLastPixelLocation = pos;
                            moveDist = new Point(0, 0);
                            break;
                        }
                        Point translation = new Point(
                                pos.x - mouseLastPixelLocation.x,
                                pos.y - mouseLastPixelLocation.y);
                        currentObject.translate(translation.x, translation.y);
                        moveDist.x += translation.x; 
                        moveDist.y += translation.y;
                        mouseLastPixelLocation = pos;
                        mapView.redraw();
                    }
                    break;
            }
        }
    }
    
    private void pour(TileLayer layer, int x, int y,
            Tile newTile, Tile oldTile) {
        if (newTile == oldTile || !layer.canEdit()) return;

        Rectangle area;
        TileLayer before = (TileLayer) createLayerCopy(layer);
        TileLayer after;

        // Check that the copy was succesfully created
        if (before == null) {
            return;
        }

        if (marqueeSelection == null) {
            area = new Rectangle(x, y,0,0);
            Stack<Point> stack = new Stack<Point>();

            stack.push(new Point(x, y));
            while (!stack.empty()) {
                // Remove the next tile from the stack
                Point p = stack.pop();

                // If the tile it meets the requirements, set it and push its
                // neighbouring tiles on the stack.
                if (layer.contains(p.x, p.y) &&
                        layer.getTileAt(p.x, p.y) == oldTile)
                {
                    layer.setTileAt(p.x, p.y, newTile);
                    Converter.add(area,p.x,p.y);

                    stack.push(new Point(p.x, p.y - 1));
                    stack.push(new Point(p.x, p.y + 1));
                    stack.push(new Point(p.x + 1, p.y));
                    stack.push(new Point(p.x - 1, p.y));
                }
            }
        } else {
            if (marqueeSelection.getSelectedArea().contains(x, y)) {
                area = marqueeSelection.getSelectedAreaBounds();
                for (int i = area.y; i < area.height + area.y; i++) {
                    for (int j = area.x; j < area.width + area.x; j++) {
                        if (marqueeSelection.getSelectedArea().contains(j, i)){
                            layer.setTileAt(j, i, newTile);
                        }
                    }
                }
            } else {
                return;
            }
        }

        Rectangle bounds = new Rectangle(
                area.x, area.y, area.width + 1, area.height + 1);
        after = new TileLayer(bounds);
        after.copyFrom(layer);

//        MapLayerEdit mle = new MapLayerEdit(layer, before, after); //TODO undo
//        mle.setPresentationName(TOOL_FILL);
//        undoSupport.postEdit(mle);
    }
    
    public void mouseMoved(MouseEvent e) {
        // Update state of mouse buttons
        bMouseIsDown = e.button > 0;
        if (bMouseIsDown) {
            doMouse(e);
        }

        Point tile = mapView.screenToTileCoords(e.x, e.y);
        updateTileCoordsLabel(tile);
        updateCursorHighlight(tile);
    }

    public void mouseDragged(MouseEvent e) {
        bMouseIsDragging = true;

        doMouse(e);

        mousePressLocation = mapView.screenToTileCoords(e.x, e.y);
        Point tile = mapView.screenToTileCoords(e.x, e.y);

        updateTileCoordsLabel(tile);
        updateCursorHighlight(tile);
    }
    
    private static MapLayer createLayerCopy(MapLayer layer) {
        try {
            return (MapLayer) layer.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void updateTileCoordsLabel(Point tile) {
        if (currentMap.inBounds(tile.x, tile.y)) {
        	statusLineManager.setMessage(String.valueOf(tile.x) + ", " + tile.y);
        } else {
        	statusLineManager.setMessage("");
        }
    }

    private void updateCursorHighlight(Point tile) {
        if (prefs.getBoolean("cursorhighlight", true)) {
            Rectangle redraw = cursorHighlight.getBounds();
            Rectangle brushRedraw = currentBrush.getBounds();

            brushRedraw.x = tile.x - brushRedraw.width / 2;
            brushRedraw.y = tile.y - brushRedraw.height / 2;

            if (!redraw.equals(brushRedraw)) {
                if (currentBrush instanceof CustomBrush) {
                    CustomBrush customBrush = (CustomBrush) currentBrush;
                    ListIterator<MapLayer> layers = customBrush.getLayers();
                    while (layers.hasNext()) {
                        MapLayer layer = layers.next();
                        layer.setOffset(brushRedraw.x, brushRedraw.y);
                    }
                }
                mapView.repaintRegion(redraw);
                cursorHighlight.setOffset(brushRedraw.x, brushRedraw.y);
                //cursorHighlight.selectRegion(currentBrush.getShape());
                mapView.repaintRegion(brushRedraw);
            }
        }
    }

}
