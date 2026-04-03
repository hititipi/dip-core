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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import ru.dip.core.model.DipProject;
import ru.dip.core.model.interfaces.IDipEditor;
import ru.dip.core.model.interfaces.IDipParent;
import ru.dip.core.utilities.WorkbenchUtitlities;

public class UnitPresentationCache {
	
private static class PresentationId {
		
		private IFile fFile;
		private long fLocalTimeStamp;
		
		public PresentationId(IFile file) {
			fFile = file;
			fLocalTimeStamp = file.getLocalTimeStamp();
		}
		
		public IFile getFile() {
			return fFile;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((fFile == null) ? 0 : fFile.hashCode());
			result = prime * result + (int) (fLocalTimeStamp ^ (fLocalTimeStamp >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PresentationId other = (PresentationId) obj;
			if (fFile == null) {
				if (other.fFile != null)
					return false;
			} else if (!fFile.equals(other.fFile))
				return false;
			if (fLocalTimeStamp != other.fLocalTimeStamp)
				return false;
			return true;
		}
	}

	
	private static Map<PresentationId, TablePresentation> fPresentationById = new HashMap<>();
	
	public static TablePresentation getPresentation(IFile file) {
		return fPresentationById.get(new PresentationId(file));
	}
	
	public static void putPresentation(IFile file, TablePresentation presentation) {
		fPresentationById.put(new PresentationId(file), presentation);
	}
	
	public static void applyIfExists(IFile file, Consumer<TablePresentation> consumer) {
		TablePresentation tablePresentation = fPresentationById.get(new PresentationId(file));
		if (tablePresentation != null) {
			consumer.accept(tablePresentation);
		}
	}
	
	public static void clearHash() {
		Set<IProject> projects = WorkbenchUtitlities.getOpenedDocumentEditors()
				.stream()
				.map(IDipEditor::model)
				.map(IDipParent::dipProject)
				.map(DipProject::resource)
				.collect(Collectors.toSet());
		
		Set<PresentationId> filesToRemove = 
				fPresentationById.entrySet()
				.stream()
				.filter(e -> !projects.contains(e.getKey().getFile().getProject()))
				.peek(e ->  disposePresentation(e.getValue()))
				.map(e -> e.getKey())
				.collect(Collectors.toSet());
		
		fPresentationById.keySet().removeAll(filesToRemove);
	}
	
	private static void disposePresentation(TablePresentation presentation) {
		if (presentation != null) {
			presentation.dispose();
		}
	}
}
