package ru.dip.core.storage;

import ru.dip.core.model.DipElementType;
import ru.dip.core.model.interfaces.IDipElement;

public interface IDdeID {
	
	public static enum InludeParent {
		INCLUDE, EXCLUDE;
	}
	
	public static boolean isDipParent(Object obj) {
		return obj instanceof IDdeID && DipElementType.isFolderType((IDdeID) obj);
	}
	
	public static boolean containsInParent(IDdeID id, IDdeID parentId, InludeParent include) {
		IDdeID curentParent = include == InludeParent.INCLUDE ? id : id.getParent();
		while (curentParent != null) {
			if (curentParent.equals(parentId)) {
				return true;
			}
			curentParent = curentParent.getParent();
		}
		return false;
	}
	
	default <T extends IDipElement> T getElement() {
		return  DdeStorage.instance.<T>get(this);
	}

	String getName();
	
	IDdeID getParent();
	
	void setParent(IDdeID newParent);
	
	boolean isDocumentElement();
	
	DipElementType getType();
	
	String getAdditionalName();
	
}
