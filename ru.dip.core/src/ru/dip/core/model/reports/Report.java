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
package ru.dip.core.model.reports;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import ru.dip.core.model.DipElement;
import ru.dip.core.model.DipElementType;
import ru.dip.core.model.interfaces.IParent;
import ru.dip.core.storage.DdeStorage;

public class Report extends DipElement {

	public static final String REPORT_EXTENSION = "report";
	public static final String REPORT_EXTENSION_WITH_DOT = "." + REPORT_EXTENSION;

	public static Report instance(IResource resource, IParent parent) {
		Report report = new Report(resource, parent);		
		DdeStorage.instance.put(report.getDdeId(), report);
		return report;
	}
	
	private Report(IResource resource, IParent parent) {
		super(resource, parent);
	}

	@Override
	public DipElementType type() {
		return DipElementType.REPORT;
	}
	
	@Override
	public IFile resource() {
		return (IFile) super.resource();
	}
}
