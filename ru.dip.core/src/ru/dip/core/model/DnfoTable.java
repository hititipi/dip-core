/******************************************************************************* * 
 * Copyright (c) 2025 Denis Melnik.
 * Copyright (c) 2025 Ruslan Sabirov.
 * Copyright (c) 2025 Andrei Motorin.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package ru.dip.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import ru.dip.core.model.interfaces.IParent;
import ru.dip.core.storage.DdeStorage;

public class DnfoTable extends DipElement {

	public static final String TABLE_FILE_NAME = ".dnfo";
	public static final String TABLE_DOCUMENT_LABEL = "Document";
	public static final String TABLE_SECTION_LABEL = "Section";
	
	public static DnfoTable instance(IResource resource, IParent parent) {
		DnfoTable dnfoTable = new DnfoTable(resource, parent);
		DnfoTable storageInstance = DdeStorage.instance.get(dnfoTable.getDdeId());
		if (storageInstance != null) {
			return storageInstance;
		}
		
		DdeStorage.instance.put(dnfoTable.getDdeId(), dnfoTable);
		return dnfoTable;
	}
	
	private DnfoTable(IResource resource, IParent parent) {
		super(resource, parent);
	}

	@Override
	public DipElementType type() {
		return DipElementType.TABLE;
	}
	
	@Override
	public IFile resource() {
		return (IFile) super.resource();
	}
	
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}
}
