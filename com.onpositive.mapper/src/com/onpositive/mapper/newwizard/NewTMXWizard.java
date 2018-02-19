package com.onpositive.mapper.newwizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class NewTMXWizard extends Wizard implements INewWizard {
	
	protected NewTMXFilePage newFilePage;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		newFilePage = new NewTMXFilePage(selection);	
		setWindowTitle("New TMX map");
	}
	
	@Override
	public void addPages() {
		addPage(newFilePage);
	}
		
	@Override
	public boolean performFinish() {
		final IFile file = newFilePage.createNewFile();
		  //Now invoke the finish method.
        IRunnableWithProgress op =
            new IRunnableWithProgress() {
            public void run(
                    IProgressMonitor monitor)
                    throws InvocationTargetException {
                try {
                    doFinish(file,
                        monitor);
                } finally {
                    monitor.done();
                }
            }
        };
        try {
            getContainer().run(true, false, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException =
                e.getTargetException();
            MessageDialog.openError(
                getShell(),
                "Error",
                realException.getMessage());
            return false;
        }
        return true;
	}

	protected void doFinish(final IFile file, IProgressMonitor monitor) {
        monitor.setTaskName(
                "Opening file for editing...");
            getShell().getDisplay().asyncExec(
                new Runnable() {
                public void run() {
                    IWorkbenchPage page =
                        PlatformUI.getWorkbench().
                            getActiveWorkbenchWindow().
                            getActivePage();
                    try {
                        IDE.openEditor(
                            page,
                            file,
                            true);
                    } catch (PartInitException e) {
                    }
                }
            });
            monitor.worked(1);
	}

	
}
