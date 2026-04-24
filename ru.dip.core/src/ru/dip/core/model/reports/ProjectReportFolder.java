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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import ru.dip.core.model.DipContainer;
import ru.dip.core.model.DipElementType;
import ru.dip.core.model.interfaces.IDipParent;
import ru.dip.core.model.interfaces.IParent;
import ru.dip.core.storage.DdeStorage;
import ru.dip.core.storage.IDdeID;

/**
 * Папка в корне проекта (для обратной совместимости) Включает в себя
 * виртуальные контейнеры из других вложенных в проект папок
 */
public class ProjectReportFolder extends DipContainer implements IMainReportContainer {

	public static final String REPORT_FOLDER_NAME = "Reports";

	public static ProjectReportFolder instance(IFolder container, IDipParent parent) {
		ProjectReportFolder reportFolder = new ProjectReportFolder(container, parent);		
		DdeStorage.instance.put(reportFolder.getDdeId(), reportFolder);		
		return reportFolder;

	}

	private List<IDdeID> fReports;
	private List<IDdeID> fReportContainers = new ArrayList<>();

	private ProjectReportFolder(IContainer container, IParent parent) {
		super(container, parent);
	}

	@Override
	public String getRelativePath() {
		return resource().getProjectRelativePath().toOSString();
	}

	@Override
	public DipElementType type() {
		return DipElementType.REPORT_FOLDER;
	}

	@Override
	public IFolder resource() {
		return (IFolder) super.resource();
	}

	// =============================
	// children

	@Override
	public void computeChildren() {
		fReports = new ArrayList<>();
		fChildren = new ArrayList<>();
		try {
			for (IResource resource : resource().members()) {
				if (resource instanceof IFile) {
					String extension = ((IFile) resource).getFileExtension();
					if (Report.REPORT_EXTENSION.equals(extension)) {
						Report report = Report.instance(resource, this);
						fReports.add(report.getDdeId());
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IDdeID getChild(String name) {
		for (IDdeID report : getReports()) {
			if (name.equals(report.getName())) {
				return report;
			}
		}
		return null;
	}

	@Override
	public List<IDdeID> getChildren() {
		if (fReports == null) {
			computeChildren();
		}
		List<IDdeID> result = new ArrayList<>();
		result.addAll(fReports);
		if (fReportContainers != null) {
			result.addAll(fReportContainers);
		}
		return result;
	}

	@Override
	public void addContainer(IReportContainer reportContainer) {
		fReportContainers.add(reportContainer.getDdeId());
	}

	@Override
	public void removeContainer(IReportContainer reportContainer) {
		fReportContainers.remove(reportContainer.getDdeId());
	}

	@Override
	public Report loadReport(IFile file) {
		Report report = Report.instance(file, this);
		fReports.add(report.getDdeId());
		return report;
	}

	@Override
	public IDipParent getDipParent() {
		return (IDipParent) parent();
	}

	@Override
	public List<IDdeID> getReports() {
		if (fReports == null) {
			computeChildren();
		}
		return fReports;
	}

	@Override
	public void removeChild(IDdeID child) {
		fReports.remove(child);
	}

	@Override
	public void addReportChild(IFile resource) {
	}

}
