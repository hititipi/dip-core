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
package ru.dip.core.utilities;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import ru.dip.core.annotation.CallChangeDipChildren;
import ru.dip.core.annotation.ChangeDipChildren;
import ru.dip.core.annotation.NoChangeDipChildren;
import ru.dip.core.exception.SaveTableDIPException;
import ru.dip.core.model.DipElementType;
import ru.dip.core.model.DipUnit;
import ru.dip.core.model.interfaces.IDipDocumentElement;
import ru.dip.core.model.interfaces.IDipElement;
import ru.dip.core.model.interfaces.IDipParent;
import ru.dip.core.storage.DdeStorage;
import ru.dip.core.storage.IDdeID;
import ru.dip.core.table.TableWriter;

public class DipTableUtilities {
		
	//======================
	// new folder
	
	public static boolean canNewFolderAfter(DipUnit unit){
		IDdeID nextElement = getNextElement(unit);
		return  (nextElement == null || DipElementType.isFolderType(nextElement));
	}
	
	public static IDipParent addNewFolderBefore(IDipDocumentElement element, IFolder folder) {
		IDipParent parent = element.parent();
		List<IDdeID> children = parent.getDDEChildren();
		int index = children.indexOf(element.getDdeId());
		return parent.createNewFolder(folder, index);
	}
	
	public static IDipParent addNewFolderAfter(IDipDocumentElement element, IFolder folder) {
		IDipParent parent = element.parent();
		List<IDdeID> children = parent.getDDEChildren();
		int index = children.indexOf(element.getDdeId()) + 1;
		return parent.createNewFolder(folder, index);
	}
	
	public static IDipParent addNewFolderStart(IDipParent parent, IFolder folder) {
		return parent.createNewFolder(folder);
	}
	
	public static IDipParent addNewFolderEnd(IDipParent parent, IFolder folder) {
		int newLastIndex = parent.getDDEChildren().size();
		return parent.createNewFolder(folder, newLastIndex);
	}
	
	public static IDipParent addNewFolderByIndex(IDipParent parent, IFolder folder, int index) {
		return parent.createNewFolder(folder, index);
	}
	
	//=========================
	// include folder
	
	public static IDipParent addIncludeFolderBefore(IDipDocumentElement element, IFolder folder, String name,
			String description, boolean readOnly) {
		IDipParent parent = element.parent();
		List<IDdeID> children = parent.getDDEChildren();
		int index = children.indexOf(element.getDdeId());
		return parent.includeFolder(folder, index, name, description, readOnly);
	}
	
	public static IDipParent addIncludeFolderAfter(IDipDocumentElement element, IFolder folder, String name, 
			String description, boolean readOnly) {
		IDipParent parent = element.parent();
		List<IDdeID> children = parent.getDDEChildren();
		int index = children.indexOf(element.getDdeId()) + 1;
		return parent.includeFolder(folder, index, name, description, readOnly);
	}
	
	public static IDipParent addIncludeFolderStart(IDipParent parent, IFolder folder, String name,
			String description, boolean readOnly) {
		return parent.includeFolder(folder, name,  description, readOnly);
	}
	
	public static IDipParent addIncludeFolderEnd(IDipParent parent, IFolder folder, String name,
			String description, boolean readOnly) {
		int newLastIndex = parent.getDDEChildren().size();
		return parent.includeFolder(folder, newLastIndex, name, description, readOnly);
	}

	//=========================
	// new file
	
	public static boolean canNewFileBefore(IDipParent parent){
		IDdeID previousElement = getPreviousElement(parent);
		return (previousElement == null || DipElementType.isUnit(previousElement));
	}
	
	public static IDipElement addNewFileBefore(IDipDocumentElement element, IFile file) {
		IDipParent parent = element.parent();
		List<IDdeID> children = parent.getDDEChildren();
		int dipIndex = children.indexOf(element.getDdeId());
		return parent.createNewUnit(file, dipIndex);
	}

	public static IDipElement addNewFileAfter(IDipDocumentElement element, IFile file) {
		IDipParent parent = element.parent();
		List<IDdeID> children = parent.getDDEChildren();
		int dipIndex = children.indexOf(element.getDdeId());
		return parent.createNewUnit(file, dipIndex + 1);
	}
	
	public static IDipElement addNewFileStart(IDipParent parent, IFile file) {
		return parent.createNewUnit(file);
	}
	
	public static IDipElement addNewFileEnd(IDipParent parent, IFile file){
		int lastUnitIndex = getLastUnitIndex(parent) + 1;
		return parent.createNewUnit(file, lastUnitIndex);
	}
	
	public static IDipElement addNewFileByIndex(IDipParent parent, IFile file, int index) {
		return parent.createNewUnit(file, index);
	}

	//===============================================
	//   UP - DOWN
	
	@NoChangeDipChildren
	public static boolean canUp(IDipDocumentElement element){		
		if (element instanceof DipUnit){
			return canUp((DipUnit) element);
		} else if (element instanceof IDipParent){
			return canUp((IDipParent) element);
		}		
		return false;
	}
	
	@NoChangeDipChildren
	private static boolean canUp(DipUnit unit){
		IDdeID previous = getPreviousElement(unit);
		return previous != null && DipElementType.isUnit(previous);	
	}
	
	@NoChangeDipChildren
	private static boolean canUp(IDipParent parent){
		IDdeID previous = getPreviousElement(parent);
		return previous != null && DipElementType.isFolderType(previous);
	}
	
	/**
	 * Можно ли двигать вверх: одного типа, один родитель, друг за другом, верхний можно двигать вверх
	 */
	@NoChangeDipChildren
	public static boolean canUp(TreeSet<IDipDocumentElement> dipDocumentElements) {
		IDipDocumentElement first = dipDocumentElements.first();
		IDipDocumentElement last = dipDocumentElements.last();

		if (first.getClass() != last.getClass()) {
			return false;
		}
		IDipParent parent = first.parent();
		int currentIndex = first.getIndex() - 1;
		
		for (IDipDocumentElement dipDocumentElement : dipDocumentElements) {
			if (!dipDocumentElement.parent().equals(parent)) {
				return false;
			}
			int index = dipDocumentElement.getIndex();
			if (index != currentIndex + 1) {
				return false;
			}
			currentIndex = index;			
		}												
		return canUp(first);
	}
	
	// перенести в DipUtilities
	@CallChangeDipChildren
	public static void up(IDipDocumentElement element){
		List<IDdeID> children = element.parent().getDDEChildren();
		int newIndex = getIndex(element) - 1; 
		moveElement(element.getDdeId(), newIndex, children);
		saveModel(element.parent());
	}
	
	@CallChangeDipChildren
	public static void up(TreeSet<IDipDocumentElement> dipDocumentElements) {
		IDipDocumentElement first = dipDocumentElements.first();	
		IDdeID movedElement = getPreviousElement(first);
		IDipDocumentElement last = dipDocumentElements.last();
		if (first == null || movedElement == null || last == null) {
			return;
		}
		int newIndex = last.getIndex();		
		List<IDdeID> children = first.parent().getDDEChildren();
		moveElement(movedElement, newIndex, children);
		saveModel(first.parent());
	}
		
	@NoChangeDipChildren
	public static boolean canDown(IDipDocumentElement element){		
		if (element instanceof DipUnit){
			return canDown((DipUnit) element);
		} else if (element instanceof IDipParent){
			return canDown((IDipParent) element);
		}		
		return false;
	}
	
	@NoChangeDipChildren
	private static boolean canDown(DipUnit unit){
		IDdeID next = getNextElement(unit);
		return next != null && DipElementType.isUnit(next);	
	}
	
	@NoChangeDipChildren
	private static boolean canDown(IDipParent parent){
		IDdeID next = getNextElement(parent);
		return next != null && DipElementType.isFolderType(next);
	}
	
	/**
	 * Можно ли двигать вниз: одного типа, один родитель, друг за другом, нижний можно двигать вниз
	 */
	@NoChangeDipChildren
	public static boolean canDown(TreeSet<IDipDocumentElement> dipDocumentElements) {
		IDipDocumentElement first = dipDocumentElements.first();		
		IDipDocumentElement last = dipDocumentElements.last();
		if (first.getClass() != last.getClass()) {
			return false;
		}
		IDipParent parent = first.parent();
		int currentIndex = first.getIndex() - 1;
		
		for (IDipDocumentElement dipDocumentElement : dipDocumentElements) {
			if (!dipDocumentElement.parent().equals(parent)) {
				return false;
			}
			int index = dipDocumentElement.getIndex();
			if (index != currentIndex + 1) {
				return false;
			}
			currentIndex = index;			
		}												
		return canDown(last);
	}
	
	@CallChangeDipChildren
	public static void down(IDipDocumentElement element){
		List<IDdeID> children = element.parent().getDDEChildren();
		int newIndex = getIndex(element) + 1;
		moveElement(element.getDdeId(), newIndex, children);
		saveModel(element.parent());
	}
	
	@CallChangeDipChildren
	public static void down(TreeSet<IDipDocumentElement> dipDocumentElements) {
		IDipDocumentElement first = dipDocumentElements.first();	
		IDipDocumentElement last = dipDocumentElements.last();
		IDdeID movedElement = getNextElement(last);
		if (first == null || movedElement == null || last == null) {
			return;
		}
		int newIndex = first.getIndex();			
		List<IDdeID> children = first.parent().getDDEChildren();
		moveElement(movedElement, newIndex, children);
		saveModel(first.parent());
	}
	
	@ChangeDipChildren	
	private static void moveElement(IDdeID ddeId, int newIndex, List<IDdeID> parentChildren) {
		parentChildren.remove(ddeId);
		parentChildren.add(newIndex, ddeId);	
	}
	
	//=====================================
	// into folder
	
	public static boolean canIntoFolder(TreeSet<IDipDocumentElement> dipDocElements) {
		IDipParent parent = dipDocElements.first().parent();
		if (parent == null) {
			return false;
		}		
		for (IDipDocumentElement dipDocElement: dipDocElements) {
			if (!parent.equals(dipDocElement.parent())) {
				return false;
			}
		}				
		return true;
	}
	
	//========================
	// previous/next element
	
	public static  IDdeID getNextElement(IDipDocumentElement element){
		// можно переделать чтобы возвращал DdeID
		List<IDdeID> children = element.parent().getDDEChildren();
		
		//int index = children.indexOf(element);
		int index = getIndex(element);	
		if (index == children.size() - 1){
			return null;
		}
		return children.get(index + 1);
	}
	
	// переделать  чтобы возввращал IDdeID
	public static IDdeID getPreviousElement(IDipDocumentElement element){
		if (element == null) {
			return null;
		}
		IDipParent parent = element.parent();
		if (parent == null) {
			return null;
		}		
		
		List<IDdeID> children = parent.getDDEChildren();
		int index = getIndex(element);	
		if (index <= 0){
			return null;
		}
		return children.get(index - 1);
	}
	
	public static int getFirstParentIndex(IDipParent parent){
		List<IDdeID> children = parent.getDDEChildren();
		for (int i = 0; i < children.size(); i++){
			IDdeID element = children.get(i);			
			if (DipElementType.isFolderType(element.getType())) {
				return i;
			}
		}
		return children.size();
	}
	
	public static IDipDocumentElement getLastUnitElement(IDipParent parent){
		int index = getLastUnitIndex(parent);
		IDdeID ddeId = parent.getDDEChildren().get(index);
		return DdeStorage.instance.get(ddeId);
	}
	
	public static int getLastUnitIndex(IDipParent parent) {
		List<IDdeID> children = parent.getDDEChildren();		
		int result = -1;
		for (int i = 0; i < children.size(); i++){
			IDdeID element = children.get(i);
			if (DipElementType.isFolderType(element.getType())){
				return result;
			} else {
				result++;
			}
		}
		return result;
	}
	
	
	// можно удалить т.к. есть метод getIndex в IDipDocumentElement
	public static int getIndex(IDipDocumentElement element) {
		List<IDdeID> children = element.parent().getDDEChildren();		
		return children.indexOf(element.getDdeId());
	}
	
	public static IDipParent getLastParent(IDipParent parent) {
		List<IDdeID> children = parent.getDDEChildren();		
		if (children.isEmpty()) {
			return null;
		}
		IDdeID last = children.get(children.size() - 1);
		if (DipElementType.isFolderType(last.getType())) {
			return (IDipParent) DdeStorage.instance.get(last);
		}
		return null;
	}
	
	//=============================
	// numbering name
	
	public static String getNextNumber(IDipParent parent, IDipDocumentElement previous){		
		if (parent.isFileNumeration()){			
			String stepStr = parent.getFileStep();
			if (previous == null){
				return stepStr;
			}
		return getNextNumber(stepStr, previous);
		}		
		return null;
	}
	
	public static String getStartNumberInFolder(IDipParent parent) {
		if (parent.isFileNumeration()) {
			return parent.getFileStep();
		}		
		return null;		
	}
	
	public static String getEndNumberInFolder(IDipParent parent) {
		if (parent.isFileNumeration()) {
			IDipDocumentElement last = getLastUnitElement(parent);
			return getNextNumber(parent.getFileStep(), last);			
		}	
		return null;
	}
	
	public static String getNextNumber(String stepStr, IDipDocumentElement previous){
		if (previous == null){
			return stepStr;
		}
		String previousName = previous.name();
		if (previousName.length() < stepStr.length()){
			IDdeID newPrevious = getPreviousElement(previous);
			return getNextNumber(stepStr, DdeStorage.instance.get(newPrevious));
		}
		String previousNumberString = previousName.substring(0, stepStr.length());
		try {
			int previousNumber = Integer.parseInt(previousNumberString);
			int stepNumber = Integer.parseInt(stepStr);
			if (previousNumber % stepNumber == 0){
				int number = previousNumber + stepNumber;
				return appendStepPrefix(stepStr, number);
			} 		
		} catch (NumberFormatException ignore){
			// NOP
		}
		IDdeID newPrevious = getPreviousElement(previous);
		return getNextNumber(stepStr, DdeStorage.instance.get(newPrevious));		
	}
	
	public static String getFolderNextNumber(IDipParent parent, IDipDocumentElement previous){		
		if (parent.isFolderNumeration()){			
			String stepStr = parent.getFolderStep();
			if (previous == null  || !(previous instanceof IDipParent)){
				return stepStr;
			}
			return getFolderNextNumber(stepStr, previous);	
		}		
		return null;
	}
	
	public static String getFolderNextNumber(String stepStr, IDipDocumentElement previous){
		if (previous == null || !(previous instanceof IDipParent)){
			return stepStr;
		}
		String previousName = previous.name();
		if (previousName.length() < stepStr.length()){
			IDdeID newPrevious = getPreviousElement(previous);
			return getNextNumber(stepStr, newPrevious.getElement());
		}
		String previousNumberString = previousName.substring(0, stepStr.length());
		try {
			int previousNumber = Integer.parseInt(previousNumberString);
			int stepNumber = Integer.parseInt(stepStr);
			if (previousNumber % stepNumber == 0){
				int number = previousNumber + stepNumber;
				return appendStepPrefix(stepStr, number);
			} 		
		} catch (NumberFormatException ignore){
			// NOP
		}
		IDdeID newPrevious = getPreviousElement(previous);
		return getNextNumber(stepStr, newPrevious.getElement());		
	}
	
	public static String getStartFolderNumber(IDipParent parent) {
		if (parent.isFolderNumeration()) {
			return parent.getFolderStep();
		}		
		return null;		
	}
	
	public static String getEndFolderNumber(IDipParent parent) {
		if (parent.isFolderNumeration()) {			
			List<IDipDocumentElement> children = parent.getDdeElements();
			if (children.isEmpty()) {
				return null;
			}			
			IDipDocumentElement last = children.get(children.size() - 1);
			return getFolderNextNumber(parent, last);	
		}	
		return null;
	}
	
	public static String appendStepPrefix(String step, int number) {
		String numberString = String.valueOf(number);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < step.length() - numberString.length(); i++) {
			builder.insert(0, '0');
		}
		builder.append(numberString);
		return builder.toString();
	}
	
	//================================
	// save table
	
	public static void saveModel(IDipParent parent) {
		try {
			TableWriter.saveModel(parent);
		} catch (ParserConfigurationException | IOException e) {
			new SaveTableDIPException(parent, "Ошибка сохранения таблицы Document");
			WorkbenchUtitlities.openError("Save table error", "Ошибка сохранения таблицы");
			e.printStackTrace();
		}
	}
	
}
