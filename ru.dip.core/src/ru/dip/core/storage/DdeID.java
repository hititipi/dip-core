package ru.dip.core.storage;

import java.util.Map;
import java.util.Objects;

import ru.dip.core.model.DipElement;
import ru.dip.core.model.DipElementType;
import ru.dip.core.model.glossary.GlossaryField;
import ru.dip.core.model.glossary.GlossaryFolder;
import ru.dip.core.model.interfaces.IDipElement;
import ru.dip.core.model.vars.VarContainer;
import ru.dip.core.unit.UnitExtension;
import ru.dip.core.unit.form.FieldUnity;
import ru.dip.core.unit.form.FormField;

public class DdeID implements IDdeID {

	public static IDdeID ofFormField(FormField formField) {	
		String formName =  formField.getField() != null ?  formField.getField().getName() : null;
		IDdeID id =   new DdeID(formName, formField.getDipUnit().getDdeId(), formField.type(), formName);
		return DdeStorage.instance.getOrPut(id);
	}
	
	public static IDdeID ofFormFieldUnity(FieldUnity fieldUnity) {
		IDdeID id =   new DdeID(null, fieldUnity.getDipUnit().getDdeId(), fieldUnity.type(), fieldUnity.name());
		return DdeStorage.instance.getOrPut(id);
	}

	public static IDdeID updateID(IDdeID oldId, DipElement element) {
		if (oldId == null) {
			throw new RuntimeException();
		}
		DdeID old = (DdeID) oldId;
		
		// необходимо поменять записть в HashMap, иначе будет неправильно искать
		Map<IDdeID, IDdeID> map = DdeStorage.instance.getIdHashMap();	
		map.remove(oldId);
			
		old.fFileName = element.name();
		old.fParent = element.parentDdeId();
		old.fType = element.type();
		old.fAdditionalName = null;
		
		map.put(old, old);	
		return old;
	}
	
	public static IDdeID of(IDipElement element) {				
		IDdeID id =   new DdeID(element.name(), element.parentDdeId(), element.type(), null);				
		return DdeStorage.instance.getOrPut(id);

	}
	
	public static IDdeID ofUnitExtension(UnitExtension element, IDdeID unitID) {				
		IDdeID id = new DdeID("UnitExtension", unitID, element.type(), null);
		return DdeStorage.instance.getOrPut(id);
	}
	
	public static IDdeID ofGlossaryField(GlossaryField glossaryField) {
		IDdeID id = new DdeID(
				glossaryField.name(),
				glossaryField.parent().getDdeId(),
				glossaryField.type(),
				glossaryField.name());
		return DdeStorage.instance.getOrPut(id);
	}
	
	public static IDdeID ofGlossaryFolder(GlossaryFolder glossaryFolder) {
		IDdeID id =  new DdeID(glossaryFolder.getGlossaryFile().getName(), glossaryFolder.parentDdeId(), glossaryFolder.type(), null);
		return DdeStorage.instance.getOrPut(id);

	}
	
	public static IDdeID ofVarContainer(VarContainer varContainer) {
		IDdeID id =  new DdeID(varContainer.resource().getName(), varContainer.parentDdeId(),  varContainer.type(), null);
		return DdeStorage.instance.getOrPut(id);
	}
	
	
	private String fFileName;
	private IDdeID fParent;
	private  DipElementType fType;
	private  String fAdditionalName;
	
	private DdeID(String fileName, IDdeID parent, DipElementType type, String additional) {
		fType = type;
		fParent = parent;
		fFileName = fileName;
		fAdditionalName = additional;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(fAdditionalName, fFileName, fParent, fType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DdeID other = (DdeID) obj;
		return Objects.equals(fAdditionalName, other.fAdditionalName) && Objects.equals(fFileName, other.fFileName)
				&& Objects.equals(fParent, other.fParent) && fType == other.fType;
	}
	
	@Override
	public String getName() {
		return fFileName;
	}
		
	@Override
	public String getAdditionalName() {
		return fAdditionalName;
	}
	
	@Override
	public DipElementType getType() {
		return fType;
	}
	
	@Override
	public boolean isDocumentElement() {
		return fType.isDocumentPart();
	}
	
	@Override
	public IDdeID getParent() {
		return fParent;
	}

	@Override
	public void setParent(IDdeID newParent) {
		fParent = newParent;	
	}
	
	@Override
	public String toString() {
		return "DDE: " + fFileName + "  "  + fType + " " + fParent;
	}
}
