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

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import ru.dip.core.model.interfaces.IDescriptionSupport;
import ru.dip.core.model.interfaces.IDipDocumentElement;
import ru.dip.core.model.interfaces.IDipParent;
import ru.dip.core.model.interfaces.IParent;
import ru.dip.core.storage.DdeStorage;
import ru.dip.core.storage.IDdeID;
import ru.dip.core.utilities.FileUtilities;
import ru.dip.core.utilities.ResourcesUtilities;

public class DipDescription extends DipElement implements IDescriptionSupport  {

	public static final String EXTENSION = "d";
	
	private IDdeID fDipElement;
	private String fDesciptionContent;
	
	private static DipDescription instance(IResource resource, IParent parent) {
		DipDescription dipDescription = new DipDescription(resource, parent);
		DipDescription storageInstance = DdeStorage.instance.get(dipDescription.getDdeId());
		if (storageInstance != null) {
			return storageInstance;
		}
		
		DdeStorage.instance.put(dipDescription.getDdeId(), dipDescription);
		return dipDescription;
	}
	
	private DipDescription(IResource resource, IParent parent) {
		super(resource, parent);
	}
	
	public static DipDescription createExistsDipDescription(IFile file, IParent parent){
		DipDescription description = DipDescription.instance(file, parent); 
		description.fDesciptionContent = description.getDescriptionText();
		return description;
	}
	
	public static DipDescription createNewDipDescription(IDipDocumentElement dipDocumentElement, String descriptionContent){
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		String name  = dipDocumentElement.name() + ".d";
		IDipParent parent = dipDocumentElement.parent();
		try {
			IFile file = ResourcesUtilities.createFile(parent.resource(), name, descriptionContent, shell);
			if (file.exists()){
				DipDescription description = DipDescription.instance(file, parent); 
				description.fDipElement = dipDocumentElement.getDdeId();
				description.fDesciptionContent = descriptionContent;				
				return description;
			}			
		} catch (CoreException e) {
			e.printStackTrace();
		}		
		return null;
	}
	
	public void setCorrespondingElement(){
		IDipDocumentElement dipElement = findCorrespondingElement();
		if (dipElement != null){
			fDipElement = dipElement.getDdeId();
			dipElement.setDipDescription(this);
		} else {
			fDipElement = null;
		}
	}
	
	public IDipDocumentElement findCorrespondingElement(){
		if (parent() instanceof IDipParent){
			IDipParent dipParent = (IDipParent) parent();
			String elementName = getElementName();
			for (IDdeID dipElement: dipParent.getChildren()){			
				if (dipElement.isDocumentElement() && elementName.equals(dipElement.getName())){
					return DdeStorage.instance.get(dipElement);
				}
			}			
		}
		return null;
	}
	
	private String getElementName(){
		String fullName = name();
		return fullName.substring(0, fullName.length() - 2);
	}
	
	private String getDescriptionText(){
		try {
			return FileUtilities.readFile(resource());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void updateDescriptionText(String newContent) {
		if (newContent.equals(fDesciptionContent)){
			return;
		}
		fDesciptionContent = newContent;
		try (PrintWriter writer = new PrintWriter(resource().getLocation().toOSString(), StandardCharsets.UTF_8)){
			writer.print(newContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void delete() {
		try {
			ResourcesUtilities.deleteResource(resource(), null);
			parent().removeChild(getDdeId());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public DipElementType type() {
		return DipElementType.DESCRIPTION;
	}
	
	@Override
	public IFile resource() {
		return (IFile) super.resource();
	}

	public IDipDocumentElement getDipDocElement(){
		return DdeStorage.instance.get(fDipElement);
	}
	
	public String getDescriptionContent(){
		return fDesciptionContent;
	}

}
