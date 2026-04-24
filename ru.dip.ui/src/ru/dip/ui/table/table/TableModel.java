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
package ru.dip.ui.table.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.graphics.Point;

import ru.dip.core.model.DipDescription;
import ru.dip.core.model.DipElementType;
import ru.dip.core.model.DipProject;
import ru.dip.core.model.DipTableContainer;
import ru.dip.core.model.interfaces.IParent;
import ru.dip.core.model.reports.IReportContainer;
import ru.dip.core.model.vars.IVarContainer;
import ru.dip.core.storage.DdeStorage;
import ru.dip.core.storage.IDdeID;
import ru.dip.core.utilities.GITUtilities;
import ru.dip.ui.table.ktable.model.ITableInputModel;
import ru.dip.core.model.interfaces.IDipComment;
import ru.dip.core.model.interfaces.IDipElement;
import ru.dip.core.model.interfaces.IDipParent;
import ru.dip.core.model.interfaces.IDipDocumentElement;

/**
 *  Модель для отображения в Document
 *  Включает список родительских элементов + сам контейнер
 */
public class TableModel implements IDipParent, ITableInputModel {
	
	private final IDdeID fDipContainer;
	private List<IDdeID> fParents;	
	private String fHash;
	
	public TableModel(DipTableContainer dipTableContainer) {
		if (dipTableContainer.getDdeId() == null) {
			throw new RuntimeException();
		}
		fHash = GITUtilities.getCurrentProjectHash(dipTableContainer.dipProject());		
		fDipContainer = dipTableContainer.getDdeId();
		setParents(dipTableContainer);
	}
	
	//===========================
	// update
	
	public void updateModel() {
		// NOP
	}
	
	/**
	 * Дополнительное обновление элемента, в моделях наследниках
	 */
	public void additionalUpdate(IDipDocumentElement endElement) {}
	
	public void additionalUpdate(Set<IResource> resources) {}
	
	//=================================
	
	public DipTableContainer getContainer(){		
		return DdeStorage.instance.get(fDipContainer);
	}
	
	public String getHash() {
		return fHash;
	}
	
	
	private void setParents(IDipParent parent){
		fParents = new ArrayList<>();
		if (parent == null) {
			throw new RuntimeException();
		}
		
		fParents.add(parent.getDdeId());
		while (parent.type() != DipElementType.RPOJECT){
			parent = parent.parent();
			fParents.add(0, parent.getDdeId());			
		}
	}
	
	public List<IDipParent> getParentsList(){
		return DdeStorage.instance.getObjList(fParents);
	}
	
	/**
	 * Для Section (является ли объект одним из заголовоков-родителей)
	 */
	public boolean isParentHeader(IDipElement element){
		return fParents.contains(element.getDdeId());
	}
	
	public boolean isTable(IDipElement element){
		return getContainer().equals(element);
	}
	
	public boolean isChild(IDipElement element){		
		return element.hasParent(getContainer());
	}
	
	//============================
	// IDipParent
	

	@Override
	public String name() {
		return getContainer().name();
	}

	@Override
	public DipElementType type() {
		return getContainer().type();
	}

	@Override
	public IContainer resource() {
		return getContainer().resource();
	}
	
	@Override
	public String id() {
		return getContainer().id();
	}

	@Override
	public IDipParent parent() {
		return getContainer().parent();
	}
	
	@Override
	public IDdeID parentDdeId() {
		// TODO Auto-generated method stub
		return  getContainer().parentDdeId();
	}

	@Override
	public boolean isRoot() {		
		return getContainer().isRoot();
	}
	
	@Override
	public void setParent(IParent parent) {
		
	}

	@Override
	public boolean hasParent(IParent parent) {
		return false;
	}

	@Override
	public String description() {
		return getContainer().description();
	}

	@Override
	public void setDescription(String description) {
		getContainer().setDescription(description);
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public List<IDdeID> getChildren() {
		return getContainer().getChildren();
	}

	@Override
	public boolean hasChildren() {
		return getContainer().hasChildren();
	}

	@Override
	public IDdeID getChild(String name) {
		return getContainer().getChild(name);
	}

	
	@Override
	public IDipElement createUnit(IFile file) {
		return getContainer().createUnit(file);
	}

	@Override
	public IDipElement createFolder(IFolder folder) {
		return getContainer().createFolder(folder);
	}

	@Override
	public IDipElement createReservedFolder(IFolder folder) {
		return getContainer().createFolder(folder);
	}

	@Override
	public IDipElement createReservedUnit(IFile file) {
		return getContainer().createReservedUnit(file);
	}

	@Override
	public List<IDipDocumentElement> getDdeElements() {
		return getContainer().getDdeElements();
	}
	
	@Override
	public List<IDdeID> getDDEChildren() {
		return getContainer().getDDEChildren();
	}

	@Override
	public IDipDocumentElement[] getOneListChildren() {
		return getContainer().getOneListChildren();
	}
	
	@Override
	public IDipDocumentElement[] getDipChildren() {		
		return getContainer().getDipChildren();
	}

	@Override
	public DipProject dipProject() {
		return getContainer().dipProject();
	}

	@Override
	public IDipElement createNewUnit(IFile file) {
		return getContainer().createNewUnit(file);
	}

	@Override
	public IDipDocumentElement createNewUnit(IFile file, int reqIndex) {
		return getContainer().createNewUnit(file, reqIndex);
	}

	@Override
	public IDipParent createNewFolder(IFolder folder) {
		return getContainer().createNewFolder(folder);
	}

	@Override
	public IDipParent createNewFolder(IFolder folder, int dipIndex) {
		return getContainer().createNewFolder(folder, dipIndex);
	}

	@Override
	public void setResource(IResource resource) {
		getContainer().setResource(resource);
	}

	@Override
	public void addNewChild(IDipDocumentElement dipDocElement, int dipIndex) {
		getContainer().addNewChild(dipDocElement, dipIndex);		
	}

	@Override
	public String getLocalNumber() {
		return getContainer().getLocalNumber();
	}

	@Override
	public boolean isActiveNumeration() {
		return getContainer().isActiveNumeration();
	}

	@Override
	public void setActiveNumeration(boolean active) {
		getContainer().setActiveNumeration(active);		
	}

	@Override
	public String getParentNumber() {
		return getContainer().getParentNumber();
	}

	@Override
	public String number() {
		return getContainer().number();
	}

	@Override
	public void refresh() {
		getContainer().refresh();
	}

	@Override
	public IDipComment comment() {
		return getContainer().comment();
	}
	
	@Override
	public String getCommentContent() {
		return getContainer().getCommentContent();
	}

	@Override
	public void setDipComment(IDipComment comment) {
		getContainer().setDipComment(comment);
	}

	@Override
	public DipDescription dipDescription() {
		return getContainer().dipDescription();
	}

	@Override
	public void setDipDescription(DipDescription description) {
		getContainer().setDipDescription(description);
	}

	@Override
	public void removeDescription() {
		getContainer().removeDescription();
	}

	@Override
	public void updateDescription(String newDescriptionContent) {
		getContainer().updateDescription(newDescriptionContent);	
	}

	@Override
	public void updateDipComment(String newCommentContent) {
		getContainer().updateDipComment(newCommentContent);
	}

	@Override
	public void deleteDipComment() {
		getContainer().deleteDipComment();		
	}

	@Override
	public boolean isFileNumeration() {		
		return getContainer().isFileNumeration();
	}

	@Override
	public boolean isFolderNumeration() {
		return getContainer().isFolderNumeration();
	}

	@Override
	public void setFileStep(String step) {
		getContainer().setFileStep(step);		
	}

	@Override
	public String getFileStep() {
		return getContainer().getFileStep();
	}

	@Override
	public String getFolderStep() {
		return getContainer().getFolderStep();
	}

	@Override
	public void setFolderStep(String step) {
		getContainer().setFolderStep(step);
	}

	@Override
	public void updateWithProject() {
		getContainer().updateWithProject();
	}

	@Override
	public String getNumberDescrition(boolean showNumeration) {
		return getContainer().getNumberDescrition(showNumeration);
	}

	@Override
	public IDipParent includeFolder(IFolder folder, String name, String description, boolean readOnly) {
		return getContainer().includeFolder(folder, name, description, readOnly);
	}

	@Override
	public IDipParent includeFolder(IFolder folder, int dipIndex, String name, String description, boolean readOnly) {
		return getContainer().includeFolder(folder, dipIndex, name, description, readOnly);
	}

	@Override
	public boolean isReadOnly() {
		return getContainer().isReadOnly();
	}

	@Override
	public void setReadOnly(boolean value) {
		getContainer().setReadOnly(value);
	}

	@Override
	public boolean isIncluded() {
		return getContainer().isIncluded();
	}
	
	@Override
	public void setIncluded(boolean value) {
		getContainer().setIncluded(value);
	}

	@Override
	public boolean canDelete() {
		return getContainer().canDelete();
	}

	@Override
	public boolean canRename() {
		return getContainer().canRename();
	}

	@Override
	public String getPageBreak() {
		return getContainer().getPageBreak();
	}

	@Override
	public void setPageBreak(String value) {
		getContainer().setPageBreak(value);
	}

	@Override
	public boolean isDisabled() {
		return getContainer().isDisabled();
	}

	@Override
	public void setDisabled(boolean value) {
		getContainer().setDisabled(value);
	}

	@Override
	public boolean isDisabledInDocument() {
		return getContainer().isDisabledInDocument();
	}

	@Override
	public void sort() throws ParserConfigurationException, IOException {
		getContainer().sort();		
	}

	@Override
	public IDipElement createReservedMarker(IFile file) {
		return getContainer().createReservedMarker(file);
	}

	@Override
	public IDipDocumentElement strong() {
		return getContainer().strong();
	}

	@Override
	public boolean hasFindResult() {
		return getContainer().hasFindResult();
	}

	@Override
	public List<Point> getFindedPoints() {
		return getContainer().getFindedIdPoints();
	}

	@Override
	public void updateFindedPoints(String newContent) {
		getContainer().updateFindedPoints(newContent);
	}

	@Override
	public IVarContainer getVariablesContainer() {
		return getContainer().getVariablesContainer();
	}

	@Override
	public void deleteVarContainer() {
		getContainer().deleteVarContainer();
	}

	@Override
	public IReportContainer getReportContainer() {
		return getContainer().getReportContainer();
	}

	@Override
	public void dispose() {
		//fDipContainer = null;
		fParents.clear();
	}

	@Override
	public IDdeID getDdeId() {
		return fDipContainer;
	}

	@Override
	public void removeChild(IDdeID child) {
		getContainer().removeChild(child);
	}

}
