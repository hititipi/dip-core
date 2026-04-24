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

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import ru.dip.core.model.DipElementType;
import ru.dip.core.model.DipProject;
import ru.dip.core.model.interfaces.IDipElement;
import ru.dip.core.model.interfaces.IDipParent;
import ru.dip.core.model.interfaces.IParent;
import ru.dip.core.storage.DdeStorage;
import ru.dip.core.storage.IDdeID;

/**
 * Обвертка на ReportContainer Используется для отображения внутри папок
 */
public class FolderReportContainer implements IReportContainer, IDipElement {

	public static FolderReportContainer instance(ReportContainer container) {
		FolderReportContainer reportContainer = new FolderReportContainer(container);
		IDdeID id = reportContainer.getDdeId();
		DdeStorage.instance.put(id, reportContainer);
		return reportContainer;
	}
	
	
	private final ReportContainer fReportContainer;

	private FolderReportContainer(ReportContainer container) {
		fReportContainer = container;
	}

	public ReportContainer getOriginalReportContainer() {
		return fReportContainer;
	}

	@Override
	public Report loadReport(IFile file) {
		return fReportContainer.loadReport(file);
	}

	@Override
	public String getRelativePath() {
		return fReportContainer.getRelativePath();
	}

	@Override
	public List<IDdeID> getReports() {
		return fReportContainer.getReports();
	}
	
	@Override
	public IDdeID getChild(String name) {
		return fReportContainer.getChild(name);
	}

	@Override
	public IDipParent getDipParent() {
		return fReportContainer.getDipParent();
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return fReportContainer.getAdapter(adapter);
	}

	@Override
	public DipProject dipProject() {
		return fReportContainer.dipProject();
	}

	@Override
	public String name() {
		return ReportContainer.REPORT_FOLDER_NAME;
	}

	@Override
	public DipElementType type() {
		return fReportContainer.type();
	}

	@Override
	public IContainer resource() {
		return fReportContainer.resource();
	}

	@Override
	public void setResource(IResource resource) {
		fReportContainer.setResource(resource);
	}

	@Override
	public String id() {
		return fReportContainer.id();
	}

	@Override
	public void updateWithProject() {
		fReportContainer.updateWithProject();
	}

	@Override
	public boolean canDelete() {
		return fReportContainer.canDelete();
	}

	@Override
	public boolean canRename() {
		return fReportContainer.canRename();
	}

	@Override
	public IParent parent() {
		return fReportContainer;
	}
	
	@Override
	public IDdeID parentDdeId() {
		return fReportContainer.getDdeId();
	}

	@Override
	public void setParent(IParent parent) {
		fReportContainer.setParent(parent);
	}

	@Override
	public boolean hasParent(IParent parent) {
		return fReportContainer.hasParent(parent);
	}

	@Override
	public boolean isReadOnly() {
		return fReportContainer.isReadOnly();
	}

	@Override
	public void setReadOnly(boolean value) {
		fReportContainer.setReadOnly(value);
	}

	@Override
	public boolean isIncluded() {
		return fReportContainer.isIncluded();
	}

	@Override
	public void setIncluded(boolean value) {
		fReportContainer.setIncluded(value);
	}

	@Override
	public List<IDdeID> getChildren() {
		return fReportContainer.getChildren();
	}

	@Override
	public boolean hasChildren() {
		return fReportContainer.hasChildren();
	}

	@Override
	public void removeChild(IDdeID child) {
		fReportContainer.removeChild(child);
	}

	@Override
	public void refresh() {
		fReportContainer.refresh();
	}

	@Override
	public void addReportChild(IFile resource) {
		fReportContainer.addReportChild(resource);
	}

	@Override
	public void dispose() {		
	}

	@Override
	public IDdeID getDdeId() {
		return fReportContainer.getDdeId();
	}
}
