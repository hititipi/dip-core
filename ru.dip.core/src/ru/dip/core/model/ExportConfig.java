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

public class ExportConfig extends DipElement {
	
	public static final String EXTENSION = "dipexp";

	public static ExportConfig instance(IResource resource, IParent parent) {
		ExportConfig exportConfig = new ExportConfig(resource, parent);
		ExportConfig storageInstance = DdeStorage.instance.get(exportConfig.getDdeId());
		if (storageInstance != null) {
			return storageInstance;
		}
		
		DdeStorage.instance.put(exportConfig.getDdeId(), exportConfig);
		return exportConfig;
	}
	
	private ExportConfig(IResource resource, IParent parent) {
		super(resource, parent);
	}

	@Override
	public DipElementType type() {		
		return DipElementType.EXPORT_CONFIG;
	}

}
