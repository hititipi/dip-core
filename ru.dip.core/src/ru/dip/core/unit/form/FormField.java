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

import ru.dip.core.form.model.Field;
import ru.dip.core.model.DipElementType;
import ru.dip.core.model.interfaces.IDipUnit;
import ru.dip.core.storage.DdeID;
import ru.dip.core.storage.DdeStorage;
import ru.dip.core.storage.IDdeID;
import ru.dip.core.unit.TextPresentation;

public class FormField extends AbstractFormField {

	public static FormField instance(IDipUnit unit, Field field) {
		FormField formField = new FormField(unit, field);
		
		// Если брать из хранилища, то нужно будет устновить новый field 
		/*FormField storageInstance = DdeStorage.instance.get(formField.getDdeId());
		if (storageInstance != null) {
			return storageInstance;
		}*/		
		DdeStorage.instance.put(formField.getDdeId(), formField);
		return formField;
	}
		
	private Field fField;
	private IDdeID fDde;

	protected FormField(IDipUnit unit, Field field) {
		super(unit);
		fField = field;
		if (fField != null) {
			fText = createText();
		}
		fFormFields = List.of(this);
		fDde = DdeID.ofFormField(this);		
	}
	
	@Override
	public void updateDdeID() {
		fDde = DdeID.ofFormField(this);	
		DdeStorage.instance.put(getDdeId(), this);
	}
	
	@Override
	public IDdeID getDdeId() {
		return fDde;
	}
	
	@Override
	public DipElementType type() {
		return DipElementType.FORM_FIELD;
	}

	private String createText() {
		StringBuilder builder = new StringBuilder();
		String title = fField.getTitle();
		builder.append(title);
		builder.append(": ");
		if (fField.getValue() != null) {
			builder.append(fField.getValue());
		}
		return builder.toString();
	}

	@Override
	public boolean isVisible(IFormSettings formSetting) {
		FormPresentation formPresentation = getFormPresentation();
		if (formPresentation.getFields().indexOf(fField) == 0
				&& !getFormPresentation().hasVisibleField(formSetting)) {
			return true;
		}
		if (fField == null) {
			return true;
		}
		
		return getFormPresentation().isVisible(formSetting, fField);
	}
	
	@Override
	public boolean isFirstVisibleField(IFormSettings formSetting) {
		FormPresentation formPresentation = getFormPresentation();
		int index = formPresentation.getFields().indexOf(fField);
		return index == formPresentation.firstVisibleField(formSetting);
	}

	// ========================
	// find
	
	@Override
	public String getContent() {
		String content = getField().getValue();
		return TextPresentation.prepareText(content, getDipUnit());
	}
	
	// ======================
	// getters & setters

	public Field getField() {
		return fField;
	}
	
	public void updateField(Field field) {
		fField = field;
	}

	@Override
	public String toString() {
		return "FormField: " + getDipUnit().name() + "  "  + getField().getName();
	}

}
