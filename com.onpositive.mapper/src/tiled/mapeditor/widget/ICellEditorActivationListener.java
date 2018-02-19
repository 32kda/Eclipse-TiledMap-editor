package tiled.mapeditor.widget;

import org.eclipse.jface.viewers.CellEditor;

/**
 * A listener which is notified when a cell editor is 
 * activated/deactivated
 */
public interface ICellEditorActivationListener {
    /**
     * Notifies that the cell editor has been activated
     *
     * @param cellEditor the cell editor which has been activated
     */
    public void cellEditorActivated(CellEditor cellEditor);

    /**
     * Notifies that the cell editor has been deactivated
     *
     * @param cellEditor the cell editor which has been deactivated
     */
    public void cellEditorDeactivated(CellEditor cellEditor);
}
