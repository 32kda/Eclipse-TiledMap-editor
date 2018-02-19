package com.onpositive.mapper.editors;

import org.eclipse.core.commands.operations.AbstractOperation;

public interface ILocalUndoSupport {

	public abstract void addEdit(AbstractOperation operation);

}