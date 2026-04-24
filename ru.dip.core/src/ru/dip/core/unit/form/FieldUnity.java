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
package ru.dip.core.unit.form;

import java.util.List;
import java.util.stream.Collectors;

import ru.dip.core.model.DipElementType;
import ru.dip.core.model.interfaces.IDipUnit;
import ru.dip.core.schema.FormShowProperties;
import ru.dip.core.storage.DdeID;
import ru.dip.core.storage.DdeStorage;
import ru.dip.core.storage.IDdeID;
import ru.dip.core.unit.md.MarkdownSettings;

public class FieldUnity extends AbstractFormField {

	public static  FieldUnity instance(IDipUnit unit, List<FormField> fields) {
		FieldUnity unity = new FieldUnity(unit, fields);
		FieldUnity storageInstance = DdeStorage.instance.get(unity.getDdeId());
		if (storageInstance != null) {
			return storageInstance;
		}
		DdeStorage.instance.put(unity.getDdeId(), unity);
		return unity;
	}
		
	private IDdeID fDde;
		
	private FieldUnity(IDipUnit unit, List<FormField> fields) {
		super(unit);
		fFormFields = fields;
		fDde = DdeID.ofFormFieldUnity(this);
	}
	
	// Вынести потом в статический метод
	// добавить метод Compute DdeID
	@Override
	public void updateDdeID() {
		fDde = DdeID.ofFormFieldUnity(this);
		DdeStorage.instance.put(getDdeId(), this);
	}
	
	@Override
	public DipElementType type() {
		return DipElementType.FORM_FIELD;
	}
	
	@Override
	public boolean isVisible(IFormSettings formSetting) {
		for (FormField formField: fFormFields) {
			if (formField.isVisible(formSetting)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isFirstVisibleField(IFormSettings formSetting) {	
		for (FormField formField: fFormFields) {
			if (!formField.isVisible(formSetting)) {
				continue;
			}
			return formField.isFirstVisibleField(formSetting);
		}
		
		return false;
	}
	
	
	public String tablePresentation(IFormSettings formSettings, FormShowProperties formProperties, 
			MarkdownSettings mdSettings, int lineLength, boolean first) {
		clearTitlePoints();
		FormPresentationBuilder builder = new FormPresentationBuilder(this , getMdFormatPoints(), 
				formSettings, formProperties, mdSettings, lineLength);
		builder.build(first);
		return builder.toString();
	}
	
	// ========================
	// find
	
	@Override
	public String getContent() {
		return getFormFields().stream().map(FormField::getContent).collect(Collectors.joining(" "));
	}
	
	@Override
	public void updateFindedPoints(String content) {
		fFinderManager.updateFindedPoints(content);
	}
	
	@Override
	public void cleanFind() {
		fFinderManager.cleanFind();
	}

	@Override
	public IDdeID getDdeId() {
		return fDde;
	}
}
