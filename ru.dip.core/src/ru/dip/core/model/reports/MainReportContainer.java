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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ru.dip.core.model.interfaces.IDipParent;
import ru.dip.core.storage.DdeStorage;
import ru.dip.core.storage.IDdeID;

public class MainReportContainer extends ReportContainer implements IMainReportContainer {

	public static MainReportContainer instance(IDipParent parent) {
		MainReportContainer container = new MainReportContainer(parent);
		DdeStorage.instance.put(container.getDdeId(), container);		
		return container;
	}
	
	
	/**
	 * Главный ReportContainer в проекте без физической папки Reports 
	 * (В старых версиях используется ProjectReportFolder)
	 */
	private MainReportContainer(IDipParent parent) {
		super(parent);
	}

	private List<IReportContainer> fReportContainers = new ArrayList<>();

	@Override
	public List<IDdeID> getChildren() {
		if (getReports() == null) {
			super.computeChildren();
		}

		List<IDdeID> result = new ArrayList<>();
		result.addAll(getReports());
		if (fReportContainers != null) {
			result.addAll(fReportContainers.stream().map(IReportContainer::getDdeId).collect(Collectors.toList()));
		}
		return result;
	}
	
	@Override
	public boolean hasChildren() {
		return super.hasChildren() || !fReportContainers.isEmpty();
	}
	

	@Override
	public void addContainer(IReportContainer reportContainer) {
		fReportContainers.add(reportContainer);
	}

	@Override
	public void removeContainer(IReportContainer originalReportContainer) {
		fReportContainers.remove(originalReportContainer);
	}

}
