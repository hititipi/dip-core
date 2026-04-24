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
package ru.dip.core.unit;

import org.eclipse.core.resources.IResource;

import ru.dip.core.model.DipDescription;
import ru.dip.core.model.DipElementType;
import ru.dip.core.model.DipProject;
import ru.dip.core.model.interfaces.IFindable;
import ru.dip.core.model.interfaces.IGlossarySupport;
import ru.dip.core.model.interfaces.IParent;
import ru.dip.core.model.interfaces.IDipComment;
import ru.dip.core.model.interfaces.IDipParent;
import ru.dip.core.model.interfaces.IDipUnit;
import ru.dip.core.model.interfaces.IDipDocumentElement;
import ru.dip.core.model.interfaces.IUnitExtension;
import ru.dip.core.model.interfaces.IVariablesSupport;
import ru.dip.core.storage.DdeStorage;
import ru.dip.core.storage.IDdeID;

public abstract class UnitExtension implements IUnitExtension, IDipDocumentElement, IFindable, IGlossarySupport, IVariablesSupport {
	
	private IDdeID fUnit;

	public UnitExtension(IDipUnit unit) {
		fUnit = unit.getDdeId();
	}
	
	public IDipUnit getDipUnit(){
		return DdeStorage.instance.get(fUnit);
	}
	
	@Override
	public IDipDocumentElement strong() {
		return getDipUnit();
	}
	
	@Override
	public boolean isReadOnly() {
		return getDipUnit().isReadOnly();
	}
	
	@Override
	public void setReadOnly(boolean value) {
		getDipUnit().setReadOnly(value);
	}
	
	@Override
	public boolean isIncluded() {
		return getDipUnit().isIncluded();
	}
	
	@Override
	public void setIncluded(boolean value) {
		getDipUnit().setIncluded(value);
	}
	
	@Override
	public boolean canDelete() {
		return getDipUnit().canDelete();
	}
	
	@Override
	public boolean canRename() {
		return getDipUnit().canRename();
	}
	
	//===============
	// IDipDocElement
	
	@Override
	public String name() {
		return getDipUnit().name();
	}

	@Override
	public DipElementType type() {
		return DipElementType.UNDEFINE;
	}

	@Override
	public IResource resource() {
		return getDipUnit().resource();
	}

	@Override
	public IDipParent parent() {
		return getDipUnit().parent();
	}
	
	@Override
	public IDdeID parentDdeId() {
		return getDipUnit().parentDdeId();
	}
	
	@Override
	public void setParent(IParent parent) {
		
	}

	@Override
	public boolean hasParent(IParent parent) {
		return getDipUnit().hasParent(parent);
	}

	@Override
	public String description() {
		return getDipUnit().description();
	}

	@Override
	public void setDescription(String description) {
		getDipUnit().setDescription(description);
	}
	
	@Override
	public DipDescription dipDescription() {
		return getDipUnit().dipDescription();
	}
	
	@Override
	public void setDipDescription(DipDescription description) {
		getDipUnit().setDipDescription(description);
	}
	
	@Override
	public void removeDescription() {
		getDipUnit().removeDescription();	
	}
	
	@Override
	public void updateDescription(String newDescriptionContent) {
		getDipUnit().updateDescription(newDescriptionContent);
	}
	
	@Override
	public IDipComment comment() {
		return getDipUnit().comment();
	}
	
	@Override
	public String getCommentContent() {
		return getDipUnit().getCommentContent();
	}
	
	@Override
	public void setDipComment(IDipComment comment) {
		getDipUnit().setDipComment(comment);
	}
	
	@Override
	public void updateDipComment(String newCommentContent) {
		getDipUnit().updateDipComment(newCommentContent);
	}

	@Override
	public void deleteDipComment() {
		getDipUnit().deleteDipComment();
	}
	

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public String id() {
		return getDipUnit().id();
	}

	@Override
	public DipProject dipProject() {
		return getDipUnit().dipProject();
	}
	
	@Override
	public void setResource(IResource resource) {
		getDipUnit().setResource(resource);
	}
	
	@Override
	public void updateWithProject() {
		getDipUnit().updateWithProject();
	}

	@Override
	public boolean isDisabled() {
		return getDipUnit().isDisabled();
	}
	@Override
	public void setDisabled(boolean value) {
		getDipUnit().setDisabled(value);
	}
	
	@Override
	public boolean isDisabledInDocument() {
		return getDipUnit().isDisabledInDocument();
	}
	
	@Override
	public void dispose() {
		
	}

	public IDdeID getDipUnitId() {
		return fUnit;
	}

	public void setUnitID(IDdeID ddeId) {
		fUnit = ddeId;
	}
}
