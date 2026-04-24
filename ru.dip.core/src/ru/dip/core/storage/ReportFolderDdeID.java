package ru.dip.core.storage;

import java.util.Objects;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;

import ru.dip.core.model.DipElementType;

public class ReportFolderDdeID implements IDdeID {

	private IPath fPath;
	private String fName;
	
	public ReportFolderDdeID(IContainer container) {
		fPath = container.getLocation();
		fName = container.getProjectRelativePath().toOSString();
	}
	
	@Override
	public String getName() {
		return fName;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(fName, fPath);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportFolderDdeID other = (ReportFolderDdeID) obj;
		return Objects.equals(fName, other.fName) && Objects.equals(fPath, other.fPath);
	}

	@Override
	public boolean isDocumentElement() {
		return false;
	}
	
	@Override
	public DipElementType getType() {
		return DipElementType.REPORT_FOLDER;
	}

	@Override
	public String getAdditionalName() {
		return null;
	}

	@Override
	public IDdeID getParent() {
		return null;
	}

	@Override
	public void setParent(IDdeID newParent) {}
}
