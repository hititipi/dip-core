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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import ru.dip.core.model.glossary.GlossaryFolder;
import ru.dip.core.model.interfaces.IDipElement;
import ru.dip.core.model.reports.ProjectReportFolder;
import ru.dip.core.model.reports.Report;
import ru.dip.core.model.vars.VarContainer;
import ru.dip.core.storage.IDdeID;
import ru.dip.core.unit.ChangeLogPresentation;
import ru.dip.core.unit.GlossaryPresentation;
import ru.dip.core.unit.TocRefPresentation;
import ru.dip.core.unit.UnitType;
import ru.dip.core.utilities.DipUtilities;

public enum DipElementType {

	RPOJECT, 
	SCHEMA_FOLDER, 
	SCHEMA, 
	SCHEMA_LIST, 
	FOLDER, UNIT, 
	RESERVED_FOLDER, 
	RESERVED_UNIT,
	RESERVED_MARKER,
	TABLE,	
	COMMENT,
	DESCRIPTION,
	FOLDER_COMMENT,
	REPORT_FOLDER,
	REPORT, 
	GLOSSARY_FOLDER,
	GLOSSARY_FIELD,
	GLOSS_REF,
	EXPORT_CONFIG,
	TOC_REF,
	SERV_FOLDER,
	CHANGE_LOG,
	BROKEN_FOLDER,
	INCLUDE_FOLDER,
	VARIABLES_CONTAINER,
	VARIABLE,
	
	UNIT_PRESENTATION,
	UNIT_DESCRIPTION,
	FORM_FIELD,
	UNITY_FORM_FIELD,
	UNDEFINE;
	
	public static DipElementType getType(IResource resource){
		if  (resource instanceof IFolder){
			return getType((IFolder) resource);
		} else {
			return getType((IFile) resource);
		}
	}
	
	public static DipElementType getType(IFolder folder){
		String name = folder.getName();
		if (folder.isLinked()) {
			return DipElementType.INCLUDE_FOLDER;
		} else if (DipSchemaFolder.SCHEMA_FOLDER_NAME.equals(name)){
			return DipElementType.SCHEMA_FOLDER;
		} else if (DipUtilities.isServedFolder(name)) {
			return DipElementType.SERV_FOLDER;
		} else if (isReservedFolder(folder)){
			return DipElementType.RESERVED_FOLDER;
		} else if (ProjectReportFolder.REPORT_FOLDER_NAME.equals(name)){
			if (folder.getParent() instanceof IProject || folder.getParent().isLinked()) {
				return DipElementType.REPORT_FOLDER;
			} else {
				return DipElementType.FOLDER;
			}
		} else if (DipUtilities.isNotDnfo(folder)) {
			return DipElementType.BROKEN_FOLDER;
		} else {
			return DipElementType.FOLDER;
		}
	}
	
	public static boolean isReservedFolder(IFolder folder) {
		IFile file = folder.getFile(".rsvd");
		return file.exists();
	}
	
	public static DipElementType getType(IFile file){
		String fileName = file.getName();
		if (DipReservedMarker.RESERVED_MARKER_NAME.equals(fileName)){
			return DipElementType.RESERVED_MARKER;
		}		
		if (DipFolderComment.FILE_NAME.equals(fileName)){
			return DipElementType.FOLDER_COMMENT;
		}		
		String extension = file.getFileExtension();
		if (DipReservedUnit.EXTENSION.equals(extension)){
			return DipElementType.RESERVED_UNIT;
		}
		if (DnfoTable.TABLE_FILE_NAME.equals(fileName)){
			return DipElementType.TABLE;
		}
		if (GlossaryFolder.GLOS_FILE.equals(fileName)){
			return DipElementType.GLOSSARY_FOLDER;
		}					
		if (GlossaryPresentation.FILE_NAME.equals(fileName)){
			return DipElementType.GLOSS_REF;
		}
		if (VarContainer.VAR_FILE.equals(fileName)) {
			return DipElementType.VARIABLES_CONTAINER;
		}
		if (TocRefPresentation.FILE_NAME.equals(fileName)) {
			return DipElementType.TOC_REF;
		}		
		if (UnitType.isChangeLog(fileName)) {
			return DipElementType.CHANGE_LOG;
		}
		if (Report.REPORT_EXTENSION.equals(extension)){
			return DipElementType.REPORT;
		}
		if (fileName.startsWith(".")){
			if (fileName.equals(GlossaryPresentation.FILE_NAME + ".r")
				|| fileName.equals(ChangeLogPresentation.CHANGE_LOG_NAME + ".r")
				|| fileName.equals(ChangeLogPresentation.CHANGE_LOG_REF_NAME + ".r")
				|| fileName.equals(TocRefPresentation.FILE_NAME + ".r")) {
				return DipElementType.COMMENT;
			}
			if (fileName.equals(GlossaryPresentation.FILE_NAME + ".d")
					|| fileName.equals(ChangeLogPresentation.CHANGE_LOG_NAME + ".d")
					|| fileName.equals(ChangeLogPresentation.CHANGE_LOG_REF_NAME + ".d")
					|| fileName.equals(TocRefPresentation.FILE_NAME + ".d")) {
					return DipElementType.DESCRIPTION;
				}
			return DipElementType.UNDEFINE;
		}
		if ("dip".equals(extension)) {
			return DipElementType.UNDEFINE;
		}		
		if (DipComment.EXTENSION.equals(extension)){
			return DipElementType.COMMENT;
		}		
		if (DipDescription.EXTENSION.equals(extension)){
			return DipElementType.DESCRIPTION;
		}	
		if (ExportConfig.EXTENSION.equals(extension)){
			return DipElementType.EXPORT_CONFIG;
		}		
		return DipElementType.UNIT;
	}
	
	
	public static boolean isFolderType(IDipElement element) {
		return isFolderType(element.type());
	}
	
	public static boolean isFolderType(DipElementType elementType) {
		return elementType == DipElementType.FOLDER || elementType == DipElementType.INCLUDE_FOLDER;
	}
	
	public static boolean isFolderType(IDdeID nextElement) {
		return isFolderType(nextElement.getType());
	}
	
	public static  boolean isUnit(IDdeID elementId) {
		return isUnit(elementId.getType());
	}
	
	public static  boolean isUnit(DipElementType elementType) {
		return  elementType == DipElementType.UNIT;
	}
	
	public boolean isDocumentPart() {
		// надо уточнить не упустил  ли еще что-то
		
		return this == RPOJECT 
				|| this == FOLDER
				|| this == UNIT
				|| this == UNIT_PRESENTATION
				|| this == UNIT_DESCRIPTION
				|| this == FORM_FIELD
				|| this == UNITY_FORM_FIELD
				;
				
				
	}




	
}
