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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;

import ru.dip.core.model.glossary.GlossRef;
import ru.dip.core.model.interfaces.IDipUnit;
import ru.dip.core.model.interfaces.IParent;
import ru.dip.core.storage.IDdeID;
import ru.dip.core.utilities.ResourcesUtilities;
import ru.dip.core.utilities.WorkbenchUtitlities;

public abstract class DipContainer extends DipElement implements IParent {

	protected List<IDdeID> fChildren;
	
	public DipContainer(IContainer container, IParent parent) {
		super(container, parent);
	}

	@Override
	public List<IDdeID> getChildren() {
		if (fChildren == null){
			computeChildren();
		}		
		return fChildren;
	}
	
	public abstract void computeChildren();
	
	@Override
	public boolean hasChildren() {
		return getChildren().size() > 0;
	}
	
	public DipReservedFolder createReservedFolder(IFolder folder){
		DipReservedFolder reservedFolder = DipReservedFolder.instance(folder, this);
		fChildren.add(reservedFolder.getDdeId());
		return reservedFolder;
	}
	
	public DipFolder createFolder(IFolder folder){
		DipFolder dipFolder = DipFolder.instance(folder, this);
		fChildren.add(dipFolder.getDdeId());
		return dipFolder;
	}
	
	public IncludeFolder createIncludeFolder(IFolder folder) {		
		if (!chechLinkFolder(folder)) {
			return null;
		}	
		IncludeFolder incFolder = IncludeFolder.instance(folder, this);
		
		fChildren.add(incFolder.getDdeId());
		dipProject().addIncludeFolder(incFolder.getDdeId());
		return incFolder;
	}
	
	private boolean chechLinkFolder(IFolder folder) {
		if (folder.isLinked()) {
			Path linkTarget = Paths.get(folder.getLocationURI());
			if (!Files.exists(linkTarget)) {
				try {
					ResourcesUtilities.deleteResource(folder,  WorkbenchUtitlities.getShell());
				} catch (CoreException e) {
					e.printStackTrace();
				}				
				return false;
			}
		}
		return true;
	}

	public DipReservedMarker createReservedMarker(IFile file){
		DipReservedMarker marker = DipReservedMarker.instance(file, this);
		fChildren.add(marker.getDdeId());
		return marker;
	}
	
	public DipReservedUnit createReservedUnit(IFile file){
		DipReservedUnit reservedUnit = DipReservedUnit.instance(file, this);
		fChildren.add(reservedUnit.getDdeId());
		return reservedUnit;
	}
	
	public IDipUnit createUnit(IFile file){
		IDipUnit unit = DipUnit.instance(file, this);
		fChildren.add(unit.getDdeId());
		return unit;
	}
	
	public IDipUnit createGlossRef(IFile file){
		IDipUnit unit = GlossRef.instance(file, this);
		fChildren.add(unit.getDdeId());
		return unit;
	}
	
	public TocRef createTocRef(IFile file){
		TocRef unit = TocRef.instance(file, this);
		fChildren.add(unit.getDdeId());
		return unit;
	}
	
	public ChangeLog createChangeLog(IFile file){
		ChangeLog unit = ChangeLog.instance(file, this);
		fChildren.add(unit.getDdeId());
		return unit;
	}
	
	public DipComment createDipComment(IFile file){
		DipComment comment = DipComment.createExistsDipComment(file, this);
		fChildren.add(comment.getDdeId());
		return comment;
	}
	
	public DipFolderComment createFolderDipComment(IFile file){
		DipFolderComment comment = DipFolderComment.createExistsDipComment(file, this);
		fChildren.add(comment.getDdeId());
		return comment;
	}
		
	public DipDescription createDipDescription(IFile file){
		DipDescription description = DipDescription.createExistsDipDescription(file, this);
		fChildren.add(description.getDdeId());
		return description;
	}
	
	@Override
	public IDdeID getChild(String name) {
		if (fChildren == null){
			computeChildren();
		}
		for (IDdeID child: fChildren){					
			if (name.equals(child.getName())){
				return child;
			}
		}
		return null;
	}
	
	@Override
	public void removeChild(IDdeID child) {
		fChildren.remove(child);
	}
	
	@Override
	public void refresh() {		
		computeChildren();
	}
	
	@Override
	public IContainer resource() {
		return (IContainer) super.resource();
	}
	
}
