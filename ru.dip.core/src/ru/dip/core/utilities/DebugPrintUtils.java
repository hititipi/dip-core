package ru.dip.core.utilities;

import ru.dip.core.model.IncludeFolder;
import ru.dip.core.model.interfaces.IDipElement;
import ru.dip.core.model.interfaces.IParent;
import ru.dip.core.storage.IDdeID;

public class DebugPrintUtils {
	
	public static void printIncludeFolders(IParent parent) {
		System.out.println("=== INCLUDE FOLDERS");
		for (IDdeID childID: parent.getChildren()) {
			IDipElement dipElement = childID.getElement();
			if (dipElement instanceof IncludeFolder) {
				IncludeFolder includeFolder = (IncludeFolder) dipElement;
				if (includeFolder.resource() == null) {
					System.out.println("IncludeFolder: NULL RESOURCE: " + includeFolder.name() );
				}
				
				System.out.println("IncludeFolder: " + includeFolder.name() + " Container: " + includeFolder.resource() + "  location:  " + includeFolder.resource().getLocation() 
						+ " parent:  " + includeFolder.parent().resource().getLocation()
						+ "  " + includeFolder.getLinkRelativePath());
			}			
		}
		System.out.println();
	}
	

}
