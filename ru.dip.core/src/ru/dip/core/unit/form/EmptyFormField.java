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

import ru.dip.core.model.interfaces.IDipUnit;
import ru.dip.core.storage.DdeStorage;

public class EmptyFormField extends FormField {

	public static EmptyFormField instance(IDipUnit unit) {
		EmptyFormField formField = new EmptyFormField(unit);
		EmptyFormField storageInstance = DdeStorage.instance.get(formField.getDdeId());
		if (storageInstance != null) {
			return storageInstance;
		}
		DdeStorage.instance.put(formField.getDdeId(), formField);
		return formField;
	}
	
	private EmptyFormField(IDipUnit unit) {
		super(unit, null);		
	}
	
	@Override
	public String getText() {
		return "EMPTY";
	}
	
}
