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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import ru.dip.core.model.DipElementType;
import ru.dip.core.model.DipProject;
import ru.dip.core.model.interfaces.IDipParent;
import ru.dip.core.model.interfaces.IParent;
import ru.dip.core.storage.DdeStorage;
import ru.dip.core.storage.IDdeID;
import ru.dip.core.storage.ReportFolderDdeID;

public class ReportContainer implements IReportContainer {
	
	public static final String REPORT_FOLDER_NAME = "Reports";
	
	private IDdeID fDipParent;
	private List<IDdeID> fReports = new ArrayList<>();
	
	private IDdeID fDdeID;
	
	public ReportContainer(IDipParent parent) {
		fDipParent = parent.getDdeId();
		fDdeID = new ReportFolderDdeID(parent.resource());
	}
	
	@Override
	public DipElementType type() {
		return DipElementType.REPORT_FOLDER;
	}
	
	@Override
	public String getRelativePath() {
		return  getDipParent().resource().getProjectRelativePath().toOSString();		
	}
	
	@Override
	public String name() {
		return getRelativePath();
	}

	@Override
	public IDipParent getDipParent() {
		return DdeStorage.instance.get(fDipParent);
	}
	
	@Override
	public IParent parent() {
		return getDipParent();
	}
	
	@Override
	public IDdeID parentDdeId() {
		return fDipParent;
	}
	
	@Override
	public IContainer resource() {		
		return getDipParent().resource();
	}

	@Override
	public DipProject dipProject() {
		return getDipParent().dipProject();
	}
	
	//=========================
	// children
	
	protected void computeChildren() {
		fReports.clear();
		try {
			for (IResource resource: getDipParent().resource().members()){
				if (resource instanceof IFile){
					String extension = ((IFile) resource).getFileExtension();
					if (Report.REPORT_EXTENSION.equals(extension)){
						IDdeID report = Report.instance(resource, this).getDdeId();
						fReports.add(report);
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<IDdeID> getChildren() {
		return fReports;
	}

	@Override
	public Report loadReport(IFile file) {
		Report report = Report.instance(file, this);
		fReports.add(report.getDdeId());
		return report;
	}
	
	
	@Override
	public void addReportChild(IFile resource) {
		IDdeID report = Report.instance(resource, this).getDdeId(); 
		fReports.add(report);
	}

	@Override
	public boolean hasChildren() {
		return !fReports.isEmpty();
	}

	@Override
	public IDdeID getChild(String name) {
		for (IDdeID report: fReports) {
			if (name.equals(report.getName())) {
				return report;
			}
		}
		return null;
	}

	@Override
	public void removeChild(IDdeID child) {
		fReports.remove(child);
	}
	
	@Override
	public List<IDdeID> getReports() {
		return fReports;
	}
	
	//===========================
	// can edit (include)
	
	@Override
	public boolean canDelete() {
		return !getDipParent().isReadOnly();
	}

	@Override
	public boolean canRename() {
		return getDipParent().canDelete();
	}

	@Override
	public boolean hasParent(IParent parent) {
		return true;
	}

	@Override
	public boolean isReadOnly() {
		return getDipParent().isReadOnly();
	}

	@Override
	public boolean isIncluded() {
		return getDipParent().isIncluded();
	}

	//================
	// not used

	@Override
	public void refresh() {}

	@Override
	public void setResource(IResource resource) {}

	@Override
	public String id() {
		return null;
	}

	@Override
	public void updateWithProject() {}

	@Override
	public void setParent(IParent parent) {}

	@Override
	public void setReadOnly(boolean value) {}

	@Override
	public void setIncluded(boolean value) {}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public void dispose() {		
	}

	@Override
	public IDdeID getDdeId() {
		return fDdeID;
	}
}
