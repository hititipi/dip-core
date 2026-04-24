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
package ru.dip.core.model.vars;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import ru.dip.core.model.DipProject;
import ru.dip.core.model.DipTableContainer;
import ru.dip.core.model.interfaces.IDipDocumentElement;
import ru.dip.core.model.interfaces.IDipElement;
import ru.dip.core.model.interfaces.IDipParent;
import ru.dip.core.model.interfaces.IVariablesSupport;
import ru.dip.core.storage.DdeStorage;
import ru.dip.core.storage.IDdeID;

public class ProjectVarContainer extends VarContainer {

	public static ProjectVarContainer instance(IFile varFile, DipProject project) {
		ProjectVarContainer projectVarContainer = new ProjectVarContainer(varFile, project);	
		DdeStorage.instance.put(projectVarContainer.getDdeId(), projectVarContainer);		
		return projectVarContainer;
	}
	
	private List<IDdeID> fFolders = new ArrayList<>();
	private Map<IResource, IVarContainer> fContainerByFolder = new HashMap<>();
	
	private ProjectVarContainer(IFile varFile, DipProject project) {
		super(varFile, project);
	}
	
	public void addContainer(VarContainer varContainer) {
		fFolders.add(varContainer.getDdeId());
		fContainerByFolder.put(varContainer.getDipParent().resource(), varContainer);
	}
	
	public void removeContainer(VarContainer varContainer) {		
		fFolders.remove(varContainer.getDdeId());
		fContainerByFolder.remove(varContainer.getDipParent().resource());
	}
	
	/**
	 * Список переменных, которые могут использоваться в элементе
	 */
	public Collection<Variable> getVariablesForUnit(IDipDocumentElement dde){
		Map<String, Variable> result = new HashMap<>();
		IDipParent parent = dde.parent();
		while (parent != null) {
			IVarContainer container = fContainerByFolder.get(parent.resource());			
			if (container != null) {
				container.getVariables().forEach(f -> result.put(f.name(), f));
			}
			parent = parent.parent();
		}		
		getVariables().forEach(f -> result.put(f.name(), f));	
		return result.values();
	}

	public List<IDipElement> getChildrenObjs() {
		List<IDipElement> result = new ArrayList<>();
		result.addAll(getVariables());						
		if (fFolders != null) {
			
			result.addAll(DdeStorage.instance.getList(fFolders));
		}		
		return result;
	}
	
	public List<IDdeID> getChildren(){
		throw new UnsupportedOperationException();
	}
	
	
	/**
	 * Возвращает все переменные в т.ч. во вложенных папках
	 */
	public List<Variable> getAllVariables(){
		List<Variable> result = new ArrayList<Variable>();
		result.addAll(getVariables());
		for (IVarContainer container: DdeStorage.instance.getList(fFolders).stream().map(IVarContainer.class::cast)
				.collect(Collectors.toList())) {		
			result.addAll(container.getVariables());
		}
		return result;
	}
	
	//=========================
	// undefined vars
	
	public Set<String> getUndefinedVars(){
		Set<String> vars = getAllUsedVarNames();
		Set<String> allVarNames = getAllVariables()
				.stream().map(Variable::name).collect(Collectors.toSet());		
		return vars.stream().filter(v -> !allVarNames.contains(v)).collect(Collectors.toSet());
	}
		
	private Set<String> getAllUsedVarNames(){
		Set<String> vars = new HashSet<String>();
		getAllVarNames(dipProject(), vars);
		return vars;
	}
	
	private void getAllVarNames(DipTableContainer parent, Set<String> terms) {
		for (IDipDocumentElement dde: parent.getDipChildren()) {
			if (dde instanceof DipTableContainer) {
				getAllVarNames((DipTableContainer) dde, terms);
			} else if (dde instanceof IVariablesSupport) {
				((IVariablesSupport) dde).findVars(terms);	
			}
		}
	}

}
