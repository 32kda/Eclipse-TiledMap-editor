package com.onpositive.mapper.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import tiled.core.Map;
import tiled.core.MapChangeListener;
import tiled.core.MapChangedEvent;
import tiled.core.MapLayer;
import tiled.core.ObjectGroup;
import tiled.core.TileLayer;
import tiled.core.TileSet;
import tiled.mapeditor.resources.Resources;

import com.onpositive.mapper.MapperPlugin;
import com.onpositive.mapper.actions.LayerViewAction;
import com.onpositive.mapper.actions.AddLayerAction;
import com.onpositive.mapper.actions.AddObjectGroupAction;
import com.onpositive.mapper.actions.CloneLayerAction;
import com.onpositive.mapper.actions.DeleteLayerAction;
import com.onpositive.mapper.actions.MergeLayerDownAction;
import com.onpositive.mapper.actions.MoveLayerDownAction;
import com.onpositive.mapper.actions.MoveLayerUpAction;
import com.onpositive.mapper.editors.MapEditor;

public class LayerViewPage extends Page {
	
//	private static final String[] columnNames = {
//        Resources.getString("dialog.main.locked.column"),
//        Resources.getString("dialog.main.show.column"),
//        Resources.getString("dialog.main.layername.column")
//	};

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	protected static Image tileLayerImg = AbstractUIPlugin.imageDescriptorFromPlugin(MapperPlugin.PLUGIN_ID,"icons/tile-grid.png").createImage();
	protected static Image objectLayerImg = AbstractUIPlugin.imageDescriptorFromPlugin(MapperPlugin.PLUGIN_ID,"icons/objects.png").createImage();
	
	class ViewContentProvider implements IStructuredContentProvider, ICheckStateProvider, ICheckStateListener, ICellModifier {
		protected Map map;
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput instanceof Map) {
				this.map = (Map) newInput;
			}
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			Vector<MapLayer> layerVector = map.getLayerVector();
			ArrayList<MapLayer> layerList = new ArrayList<MapLayer>(layerVector);
			Collections.reverse(layerList);
			return layerList.toArray();
		}
		@Override
		public boolean isChecked(Object element) {
			if (element instanceof MapLayer)
				return ((MapLayer) element).isVisible();
			return false;
		}
		@Override
		public boolean isGrayed(Object element) {
			return false;
		}
		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			((MapLayer)event.getElement()).setVisible(event.getChecked());
		}
		@Override
		public boolean canModify(Object element, String property) {
			return true;
		}
		@Override
		public Object getValue(Object element, String property) {
			if (element instanceof MapLayer)
				return ((MapLayer) element).getName();
			return null;
		}
		@Override
		public void modify(Object element, String property, Object value) {
			if (element instanceof TableItem && ((TableItem) element).getData() instanceof MapLayer) {
				((MapLayer) ((TableItem) element).getData()).setName((String) value);
			}
			updateViewer();
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			if (obj instanceof MapLayer) {
				if (index == 0)
					return ((MapLayer) obj).getName();
			}
			return "";
		}
		public Image getColumnImage(Object obj, int index) {
			if (obj instanceof TileLayer)
				return tileLayerImg;
			if (obj instanceof ObjectGroup)
				return objectLayerImg;
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	
	protected CheckboxTableViewer viewer;
	protected Action action1;
	protected Action action2;
	protected Action doubleClickAction;

	protected Map map;
	protected MapEditor mapEditor;
	protected LayerViewAction moveUpAction;
	protected LayerViewAction moveDownAction;
	protected LayerViewAction mergeDownAction;
	protected LayerViewAction addLayerAction;
	protected LayerViewAction addObjectGroupAction;
	protected LayerViewAction deleteLayerAction;
	protected LayerViewAction cloneLayerAction;
	protected ControlListener controlListener;
	private Composite parent;
	
	public LayerViewPage(MapEditor mapEditor) {
		super();
		this.mapEditor = mapEditor;
		map = mapEditor.getMap();
		mapEditor.addPartPropertyListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(MapEditor.CURRENT_LAYER_PROP))
					handleLayerChanged(Integer.parseInt((String) event.getNewValue()),Integer.parseInt((String) event.getNewValue()));
			}
		});
		map.addMapChangeListener(new MapChangeListener() {
			
			@Override
			public void tilesetsSwapped(MapChangedEvent e, int index0, int index1) {
			}
			
			@Override
			public void tilesetRemoved(MapChangedEvent e, int index) {
			}
			
			@Override
			public void tilesetAdded(MapChangedEvent e, TileSet tileset) {
			}
			
			@Override
			public void mapChanged(MapChangedEvent e) {
				if (e.type == MapChangedEvent.LAYER_CHANGE)
					updateViewer();
			}
		});
	}

	protected void handleLayerChanged(int newIndex, int oldIndex) {
		MapLayer layer = map.getLayer(newIndex);
		if (!viewer.getSelection().isEmpty()) {
			Object element = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
			if (!element.equals(layer))
				viewer.setSelection(new StructuredSelection(map.getLayer(newIndex)));
		}
	}

	@Override
	public void createControl(final Composite parent) {
		this.parent = parent;
		Table table = new Table(parent, SWT.CHECK | SWT.FULL_SELECTION | SWT.BORDER);
		parent.setBounds(parent.getClientArea());
		table.setLinesVisible(true);
		viewer = new CheckboxTableViewer(table);
		TableViewerColumn mainColumn = new TableViewerColumn(viewer,SWT.LEFT,0);
		mainColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				MapLayer p = (MapLayer) element;
				return p.getName();
			}
			
			
		});
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(100, 75, true));
		table.setLayout(layout);
		final TableColumn column = mainColumn.getColumn();
		column.setText("title");
		column.setWidth(500);
		column.setResizable(true);
		column.setMoveable(true);
		controlListener = new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				int newWidth = parent.getBounds().width - 5;
				if (newWidth > 0 && !column.isDisposed())
					column.setWidth(newWidth);
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
		parent.addControlListener(controlListener);
		ViewContentProvider provider = new ViewContentProvider();
		viewer.addCheckStateListener(provider);
		viewer.setContentProvider(provider);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setCheckStateProvider(provider);
		viewer.setCellModifier(provider);
		updateViewer();
		viewer.setSelection(new StructuredSelection(mapEditor.getCurrentLayer()),true);
//		columnLayout.setColumnData( table.getColumn(0), new ColumnWeightData( 100 ) );
		viewer.setColumnProperties(new String[]{"name"}); //Only name supported yet
		viewer.setCellEditors(new CellEditor[]{new TextCellEditor(table)});
		TableViewerEditor.create(viewer,new DblClickActivationStrategy(viewer),TreeViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection.isEmpty())
					return;
				if (selection instanceof IStructuredSelection) {
					Object firstElement = ((IStructuredSelection) selection).getFirstElement();
					Vector<MapLayer> layerVector = map.getLayerVector();
//					int index = layerVector.size() - layerVector.indexOf(firstElement) - 1;
					int index = layerVector.indexOf(firstElement);
					mapEditor.setCurrentLayer(index);
				}
					
			}
		});

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "com.onpositive.mapper.viewer");
		makeActions();
		hookContextMenu();
//		hookDoubleClickAction();
		contributeToActionBars();
		getSite().setSelectionProvider(viewer);
		parent.layout(true,true);
	}

	@Override
	public Control getControl() {
		return viewer.getTable();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				LayerViewPage.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu("#PopupMenu",menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
//		manager.add(action1);
//		manager.add(new Separator());
//		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(addLayerAction);
		manager.add(addObjectGroupAction);
		manager.add(new Separator());
		manager.add(moveUpAction);
		manager.add(moveDownAction);
		manager.add(mergeDownAction);
		manager.add(deleteLayerAction);
		manager.add(cloneLayerAction);
		// Other plug-ins can contribute their actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(addLayerAction);
		manager.add(addObjectGroupAction);
		manager.add(new Separator());
		manager.add(moveUpAction);
		manager.add(moveDownAction);
		manager.add(mergeDownAction);
		manager.add(deleteLayerAction);
		manager.add(cloneLayerAction);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
		
		moveUpAction = new LayerViewAction(new MoveLayerUpAction(),viewer, mapEditor,Resources.getString("action.layer.moveup.name"), Resources.getString("action.layer.moveup.tooltip"),Resources.getImageDescriptorFromPlugin("gnome-up.png"));
		moveDownAction = new LayerViewAction(new MoveLayerDownAction(),viewer, mapEditor,Resources.getString("action.layer.movedown.name"), Resources.getString("action.layer.movedown.tooltip"),Resources.getImageDescriptorFromPlugin("gnome-down.png"));
		mergeDownAction = new LayerViewAction(new MergeLayerDownAction(),viewer, mapEditor,Resources.getString("action.layer.mergedown.name"), Resources.getString("action.layer.mergedown.tooltip"),Resources.getImageDescriptorFromPlugin("stock-merge-down-16.png"));
		addLayerAction = new LayerViewAction(new AddLayerAction(),viewer, mapEditor,Resources.getString("action.layer.add.name"), Resources.getString("action.layer.add.tooltip"),Resources.getImageDescriptorFromPlugin("gnome-new.png"));
		addObjectGroupAction = new LayerViewAction(new AddObjectGroupAction(),viewer, mapEditor,Resources.getString("action.objectgroup.add.name"), Resources.getString("action.objectgroup.add.tooltip"),Resources.getImageDescriptorFromPlugin("new-object-layer.png"));
		deleteLayerAction = new LayerViewAction(new DeleteLayerAction(),viewer, mapEditor,Resources.getString("action.layer.delete.name"), Resources.getString("action.layer.delete.tooltip"), Resources.getImageDescriptorFromPlugin("gnome-delete.png"));
		cloneLayerAction = new LayerViewAction(new CloneLayerAction(),viewer, mapEditor,Resources.getString("action.layer.duplicate.name"), Resources.getString("action.layer.duplicate.tooltip"), Resources.getImageDescriptorFromPlugin("gimp-duplicate-16.png"));
		
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Layer View",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	protected void updateViewer() {
		viewer.setInput(map);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		parent.removeControlListener(controlListener);
	}

	public MapEditor getMapEditor() {
		return mapEditor;
	}
}
