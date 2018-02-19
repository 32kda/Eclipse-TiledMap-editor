package com.onpositive.mapper.dialogs;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;

import com.onpositive.mapper.editors.MapEditor;
import com.onpositive.mapper.ui.UIUtil;

import tiled.core.TileSet;
import tiled.mapeditor.resources.Resources;
import tiled.mapeditor.util.cutter.BasicTileCutter;

public class NewTilesetDialog extends Dialog {
	
	private static final String IMPORT_ERROR_MSG = Resources.getString("dialog.newtileset.import.error.message");

	private ModifyListener modifyListener = new ModifyListener() {
		
		@Override
		public void modifyText(ModifyEvent e) {
			validate();
		}
	};
	protected Text imageText;
	protected Text nameText;
	protected String name = "";
	protected String imagePath = "";
	protected Spinner marginSpinner;
	protected Spinner spacingSpinner;
	protected int margin;
	protected int spacing;

	private int tileWidth;

	private int tileHeight;

	public NewTilesetDialog(IShellProvider parentShell) {
		super(parentShell);
		init();
	}

	public NewTilesetDialog(Shell parentShell) {
		super(parentShell);
		init();
	}
	
	protected void init() {
		setBlockOnOpen(true);
	}
	
	protected boolean isValid() {
		if (!nameText.isDisposed())
			name = nameText.getText().trim();
		if (!imageText.isDisposed())
			imagePath = imageText.getText().trim();
		return name != null && !name.isEmpty() && !imagePath.isEmpty();
	}
	
	protected void validate() {
		getButton(IDialogConstants.OK_ID).setEnabled(isValid());
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("New Tileset");
		getShell().setMinimumSize(400,100);
		Composite composite	= new Composite(parent,SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		composite.setLayout(new GridLayout(1,false));
		Group tilesetGroup = new Group(composite,SWT.NONE);
		tilesetGroup.setText("Tileset");
		tilesetGroup.setLayout(new GridLayout(3,false));
		tilesetGroup.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		
		Label title = new Label(tilesetGroup,SWT.NONE);
		title.setText("Name:");
		nameText = new Text(tilesetGroup,SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false,2,1));
		nameText.addModifyListener(modifyListener );
		
		title = new Label(tilesetGroup,SWT.NONE);
		title.setText("Image:");
		imageText = new Text(tilesetGroup,SWT.SINGLE | SWT.BORDER);
		imageText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
		final Button selectButton = new Button(tilesetGroup,SWT.PUSH);
		selectButton.setText("Browse...");
		selectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
		        FileDialog dialog = new FileDialog(selectButton.getShell(), SWT.OPEN);
		        dialog.setText("Open");
		        dialog.setFilterExtensions(new String[] { "*.bmp;*.png" });
		        dialog.setFilterNames(new String[] { 
		        		"images (*.bmp;*.png)", 
		        		"All files (*.*)" 
		        });
		        String filterPath = getFilterPath();
		        if (!filterPath.isEmpty())
		        	dialog.setFilterPath(filterPath);
		        dialog.open();
		        String path = dialog.getFilterPath();
		        String fileName = dialog.getFileName();
		        if (fileName != null && !fileName.isEmpty()) {
		        	imageText.setText(path + File.separator + fileName);
		        	if (nameText.getText().trim().isEmpty()) {
		        		int extIdx = fileName.lastIndexOf('.');
		        		if (extIdx > 0)
		        			nameText.setText(fileName.substring(0,extIdx));
		        		else
		        			nameText.setText(fileName);
		        	}
		        	validate();
		        }
			}
		});
		Group tilesGroup = new Group(composite,SWT.NONE);
		tilesGroup.setText("Tiles");
		tilesGroup.setLayout(new GridLayout(2,false));
		tilesGroup.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		title = new Label(tilesGroup,SWT.NONE);
		title.setText("Margin:");
		marginSpinner = new Spinner(tilesGroup,SWT.BORDER);
		marginSpinner.setMinimum(0);
		marginSpinner.setSelection(0);
		title = new Label(tilesGroup,SWT.NONE);
		title.setText("Spacing:");
		spacingSpinner = new Spinner(tilesGroup,SWT.BORDER);
		spacingSpinner.setMinimum(0);
		spacingSpinner.setSelection(0);

		return composite;
		
	}
	
	protected String getFilterPath() {
		IEditorPart activeEditor = UIUtil.getActiveEditor();
		if (activeEditor instanceof MapEditor) {
			return ((MapEditor) activeEditor).getBasePath();
		}
		return null;
	}

	@Override
	protected void okPressed() {
		margin = marginSpinner.getSelection();
		spacing = spacingSpinner.getSelection();
		name = nameText.getText().trim();
		imagePath = imageText.getText().trim();
		super.okPressed();
	}
	
	@Override
	protected Control createButtonBar(Composite parent) {
		Control created = super.createButtonBar(parent);
		validate();
		return created;
	}
	
	@Override
	protected void cancelPressed() {
		super.cancelPressed();
		name = null;
		imagePath = null;
	}
	
	public TileSet getResult() {
		if (isValid()) {
			TileSet tileSet = new TileSet();
			tileSet.setName(name);
			tileSet.setTilesetImageFilename(imagePath);
			tileSet.setTileMargin(margin);
			tileSet.setTileSpacing(spacing);
			return tileSet;
		}
		return null;
	}
	
	public TileSet getCuttedResult() {
		if (isValid()) {
			TileSet newTileset = new TileSet();
	        newTileset.setName(name);

	            final String file = imagePath;
	            final int width = tileWidth;
	            final int height = tileHeight;

	            try {
//	                if (transCheck.isSelected()) {
//	                    Color color = colorButton.getColor();
//	                    newTileset.setTransparentColor(color);
//	                }

	                newTileset.importTileBitmap(file,
	                        new BasicTileCutter(width, height, spacing, margin));
	                return newTileset;
	            }
	            catch (IOException e) {
	            	MessageDialog.openError(getParentShell(),"Error importing tileset",IMPORT_ERROR_MSG);
	            }

		}
		return null;
	}

	public void setInitialTileSize(int tileWidth, int tileHeight) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}
}
