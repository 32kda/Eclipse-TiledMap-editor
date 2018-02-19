package com.onpositive.mapper.newwizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tiled.core.TileSet;
import tiled.util.Util;

import com.onpositive.mapper.dialogs.NewTilesetDialog;
import com.onpositive.mapper.newwizard.ListDialogField.ColumnsDescription;

public class NewTMXFilePage extends WizardNewFileCreationPage {
	
	public static final class TilesetLabelProvider extends LabelProvider implements ITableLabelProvider{

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof TileSet) {
				if (columnIndex == 0)
					return ((TileSet) element).getName();
				if (columnIndex == 1)
					return ((TileSet) element).getTilebmpFile();
			}
			return null;
		}
	}
	
	public final class TilesetAdapter implements IListAdapter {

		@Override
		public void customButtonPressed(ListDialogField field, int index) {
			if (index == 0)
				addTileset();
		}

		@Override
		public void selectionChanged(ListDialogField field) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void doubleClicked(ListDialogField field) {
			// TODO Auto-generated method stub
			
		}
		
	}

	public static final String TITLE = "New TMX map file";
	static final String[] OPTIONS = new String[]{"16","32","64","128","256"};
	protected Combo mapWidthCombo;
	protected Combo mapHeightCombo;
	protected Combo tileWidthCombo;
	protected Combo tileHeightCombo;
	protected VerifyListener verifyListener = new VerifyListener() {
		
		@Override
		public void verifyText(VerifyEvent e) {
			e.text = e.text.trim();
			for (int i = 0; i < e.text.length(); i++) {
				if (!Character.isDigit(e.text.charAt(i))) {
					e.doit = false;
					return;
				}
			}
		}
	};
	
	private ModifyListener modifyListener = new ModifyListener() {
		
		@Override
		public void modifyText(ModifyEvent e) {
			setPageComplete(validatePage());
		}
	};
	private IListAdapter adapter = new TilesetAdapter();
	private ListDialogField tilesetsField;

	public NewTMXFilePage(IStructuredSelection selection) {
		super(TITLE, selection);
		setTitle(TITLE);
		setDescription("Please specify file name and map parameters");
		setFileExtension("tmx");
	}
	
	public void addTileset() {
		NewTilesetDialog dialog = new NewTilesetDialog(getShell());
		String widthTxt = tileWidthCombo.getText();
		String heightTxt = tileHeightCombo.getText();
		if (!widthTxt.isEmpty() && !heightTxt.isEmpty()) {
		dialog.setInitialTileSize(Integer.parseInt(widthTxt),Integer.parseInt(heightTxt));
		}
		dialog.open();
		TileSet result = dialog.getResult();
		if (result != null)
			tilesetsField.addElement(result);
	}

	@Override
	protected void createLinkTarget() {
		// Do nothing
	}
	
	@Override
	protected void createAdvancedControls(Composite parent) {
		Composite mapParamsComposite = new Composite (parent,SWT.NONE);
		mapParamsComposite.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		GridLayout layout = new GridLayout(4,false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		mapParamsComposite.setLayout(layout);
		Label titleLabel = new Label(mapParamsComposite,SWT.NONE);
		titleLabel.setText("Map width");
		Composite editComposite = new Composite(mapParamsComposite,SWT.NONE);
		mapWidthCombo = new Combo(editComposite,SWT.DROP_DOWN);
		GridData layoutData = new GridData(SWT.FILL,SWT.CENTER,false,false);
		mapWidthCombo.setLayoutData(layoutData);
		
		GridLayout layout2 = new GridLayout(3,false);
		layout2.marginWidth = 0;
		layout2.marginHeight = 0;
		editComposite.setLayout(layout2);
		titleLabel = new Label(editComposite,SWT.NONE);
		titleLabel.setText("Map height");
		mapHeightCombo = new Combo(editComposite,SWT.DROP_DOWN);
		layoutData = new GridData(SWT.FILL,SWT.CENTER,false,false);
		mapHeightCombo.setLayoutData(layoutData);
		titleLabel = new Label(mapParamsComposite,SWT.NONE);
		titleLabel.setText("Tile width");
		tileWidthCombo = new Combo(editComposite,SWT.DROP_DOWN);
		layoutData = new GridData(SWT.FILL,SWT.CENTER,false,false);
		tileWidthCombo.setLayoutData(layoutData);
		titleLabel = new Label(editComposite,SWT.NONE);
		titleLabel.setText("Tile height");
		tileHeightCombo = new Combo(editComposite,SWT.DROP_DOWN);
		layoutData = new GridData(SWT.FILL,SWT.CENTER,false,false);
		tileHeightCombo.setLayoutData(layoutData);
		editComposite.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false,3,2));
		tilesetsField = new ListDialogField(adapter,new String[]{"Add","Remove"},new TilesetLabelProvider());
		tilesetsField.setRemoveButtonIndex(1);
		tilesetsField.setTableColumns(new ColumnsDescription(new String[]{"Name","Path"},true));
		tilesetsField.setLabelText("Tilesets");
		tilesetsField.doFillIntoGrid(mapParamsComposite,4);
		initializeValues();
	}

	protected void initializeValues() {
		mapWidthCombo.setText("100");
		mapHeightCombo.setText("100");
		tileWidthCombo.setItems(OPTIONS);
		tileHeightCombo.setItems(OPTIONS);
		tileWidthCombo.select(1); //32x32 by default
		tileHeightCombo.select(1);
		
		mapWidthCombo.addVerifyListener(verifyListener);
		mapHeightCombo.addVerifyListener(verifyListener);
		tileWidthCombo.addVerifyListener(verifyListener);
		tileHeightCombo.addVerifyListener(verifyListener);
		
		mapWidthCombo.addModifyListener(modifyListener);
		mapHeightCombo.addModifyListener(modifyListener);
		tileWidthCombo.addModifyListener(modifyListener);
		tileHeightCombo.addModifyListener(modifyListener);
	}
	
	@Override
	protected boolean validatePage() {
		boolean result = super.validatePage();
		if (!result) 
			return false;
		if (tileWidthCombo.getText().isEmpty() || tileHeightCombo.getText().isEmpty() || mapWidthCombo.getText().isEmpty() || mapHeightCombo.getText().isEmpty()) {
			setErrorMessage("Please set correct map parameters! Positive integers required.");
			return false;
		}
		return result;
	}
	
	@Override
	protected IStatus validateLinkedResource() {
		return Status.OK_STATUS;
	}
	
	@Override
	protected InputStream getInitialContents() {
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder domBuilder;
			domBuilder = domFactory.newDocumentBuilder();
			Document newDoc = domBuilder.newDocument();
			Element rootElement = newDoc.createElement("map");
			rootElement.setAttribute("version","1.0");
			rootElement.setAttribute("orientation","orthogonal"); //TODO different orientations
			rootElement.setAttribute("width",mapWidthCombo.getText());
			rootElement.setAttribute("height",mapHeightCombo.getText());
			rootElement.setAttribute("tilewidth",tileWidthCombo.getText());
			rootElement.setAttribute("tileheight",tileHeightCombo.getText());
			Element defaultLayer = newDoc.createElement("layer");
			appendTilesets(newDoc,rootElement);
			defaultLayer.setAttribute("name","Tile Layer 1");
			defaultLayer.setAttribute("width",mapWidthCombo.getText());
			defaultLayer.setAttribute("height",mapHeightCombo.getText());
			rootElement.appendChild(defaultLayer);
			newDoc.appendChild(rootElement);
			TransformerFactory tranFactory = TransformerFactory.newInstance();
		    Transformer aTransformer = tranFactory.newTransformer();
		    Source src = new DOMSource(newDoc);
		    ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
			Result dest = new StreamResult(resultStream);
		    aTransformer.transform(src,dest);
		    resultStream.close();
		    return new ByteArrayInputStream(resultStream.toByteArray()); 
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		return super.getInitialContents();
	}

	protected void appendTilesets(Document newDoc, Element rootElement) {
		List<?> elements = tilesetsField.getElements();
		int gid = 1;
		for (Object object : elements) {
			TileSet tileSet = (TileSet) object;
			Element tileSetElement = newDoc.createElement("tileset");
			tileSetElement.setAttribute("name",tileSet.getName());
			tileSetElement.setAttribute("firstgid","" + gid);
			tileSetElement.setAttribute("tilewidth",tileWidthCombo.getText());
			tileSetElement.setAttribute("tileheight",tileHeightCombo.getText());
			Element imageElement = newDoc.createElement("image");
			IWorkspace workspace = ResourcesPlugin.getWorkspace();  
			  
			IPath resourcePath = workspace.getRoot().getLocation().append(getContainerFullPath().append(
					getFileName()));
			String relativePath = Util.getRelativePath(resourcePath.toOSString(),tileSet.getTilebmpFile());
			imageElement.setAttribute("source",relativePath);
			tileSetElement.appendChild(imageElement);
			rootElement.appendChild(tileSetElement);
			gid++;
		}
	}

}
