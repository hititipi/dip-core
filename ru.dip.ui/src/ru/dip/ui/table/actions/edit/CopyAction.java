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
package ru.dip.ui.table.actions.edit;

import ru.dip.core.model.interfaces.IDipDocumentElement;
import ru.dip.ui.Messages;
import ru.dip.ui.table.actions.DocumentAction;
import ru.dip.ui.table.ktable.KTableComposite;

public class CopyAction extends DocumentAction {

	public CopyAction(KTableComposite tableComposite) {
		super(tableComposite);
		setText(Messages.CopyAction_ActionName);
	}
	
	@Override
	public void run() {
		fTableComposite.getPasteInteractor().doCopyToBuffer();
	}
	
	@Override
	public void enableOneSelection(IDipDocumentElement selectedDipDocElement) {
		setEnabled(true);
	}
	
	@Override
	public void enableSeveralSelection() {
		setEnabled(true);
	}

}
