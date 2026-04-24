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

import org.eclipse.core.resources.IResource;

import ru.dip.core.model.interfaces.IParent;
import ru.dip.core.storage.DdeStorage;

public class ChangeLog extends DipUnit {

	public static ChangeLog instance(IResource resource, IParent parent) {
		ChangeLog changeLog = new ChangeLog(resource, parent);
		ChangeLog storageInstance = DdeStorage.instance.get(changeLog.getDdeId());
		if (storageInstance != null) {
			return storageInstance;
		}
		
		DdeStorage.instance.put(changeLog.getDdeId(), changeLog);
		changeLog.init();
		return changeLog;
	}
		
	private ChangeLog(IResource resource, IParent parent) {
		super(resource, parent);
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;		
	}
}
