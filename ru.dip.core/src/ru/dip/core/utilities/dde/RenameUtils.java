package ru.dip.core.utilities.dde;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.undo.MoveResourcesOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

import ru.dip.core.DipCorePlugin;
import ru.dip.core.exception.DeleteDIPException;
import ru.dip.core.exception.RenameDIPException;
import ru.dip.core.exception.TmpCopyException;
import ru.dip.core.link.LinkInteractor;
import ru.dip.core.model.DipDescription;
import ru.dip.core.model.DipElement;
import ru.dip.core.model.DipProject;
import ru.dip.core.model.DipSchemaElement;
import ru.dip.core.model.IncludeFolder;
import ru.dip.core.model.interfaces.IDipComment;
import ru.dip.core.model.interfaces.IDipDocumentElement;
import ru.dip.core.model.interfaces.IDipElement;
import ru.dip.core.model.interfaces.IDipParent;
import ru.dip.core.model.interfaces.IDipUnit;
import ru.dip.core.model.interfaces.IParent;
import ru.dip.core.model.interfaces.ITextComment;
import ru.dip.core.storage.DdeStorage;
import ru.dip.core.storage.IDdeID;
import ru.dip.core.unit.TablePresentation;
import ru.dip.core.unit.UnitExtension;
import ru.dip.core.unit.UnitPresentationCache;
import ru.dip.core.utilities.DebugPrintUtils;
import ru.dip.core.utilities.DipUtilities;
import ru.dip.core.utilities.ReservedUtilities;
import ru.dip.core.utilities.ResourcesUtilities;
import ru.dip.core.utilities.WorkbenchUtitlities;

public class RenameUtils {
	
	private static class RenameUnitContext {

		@SuppressWarnings("unused")
		private final IDdeID fOldDdeID;
		private final String fOldRelativeID;
		private final String fIncludeID;
		private final IFile fOldFile;
		private final DipDescription fDescription;
		private final String fDescriptionContent;
		private final IDipComment fDipComment;
		private final String fCommentContent;
		private final List<ITextComment> fTextComments;
		@SuppressWarnings("unused")
		private final List<UnitExtension> fExtensions;
		private final TablePresentation fTablePresentation;
		
		private RenameUnitContext(IDipUnit unit) {
			fOldDdeID = unit.getDdeId();
			fOldRelativeID =  DipUtilities.relativeProjectID(unit);		
			fIncludeID = unit.isIncluded() ? DipUtilities.relativeIncludeElement(unit) : null;
			fOldFile = unit.resource();
					
			fDescription = unit.dipDescription();
			fDescriptionContent = fDescription != null ? fDescription.getDescriptionContent() : null;
		
			// comment
			fDipComment = unit.comment();
			fCommentContent = fDipComment != null ? fDipComment.getCommentContent() : null;
			fTextComments =  fDipComment != null ? fDipComment.getTextComments() : null;

			fExtensions = unit.getAllUnitExtensions();
			fTablePresentation = UnitPresentationCache.getPresentation(fOldFile);			
		}
	}
	
	public static void renameUnit(IDipUnit unit, String newName, boolean reserve, Shell shell) throws RenameDIPException{
		RenameUnitContext context = new RenameUnitContext(unit);
		
		// если включено резервирование, создать file_name.rsvd
		if (reserve){
			ReservedUtilities.createReserveUnit(shell, unit);
		}
		
		// переименовать IFILE
		IPath newPath = context.fOldFile.getFullPath().removeLastSegments(1).append(newName);
		IStatus renamedStatus = doRenameOperation(context.fOldFile, newPath, shell);
		if (!renamedStatus.isOK()) {
			throw new RenameDIPException(unit, renamedStatus.getMessage());
		}
		
		// обновить ссылку на файл, обновить DdeID в родительском элементе
		IFile newFile = context.fOldFile.getParent().getFile(new Path(newName));
		unit.setResource(newFile);
		
		// обновить ОПИСАНИЕ и КОММЕНТАРИЙ
		updateReqDescription(unit, context.fDescription, context.fDescriptionContent);
		updateReqComment(unit, context.fDipComment, context.fCommentContent, context.fTextComments);
		
		// обновить TablePresentation (заменить ключ в UnitPresentationCache)
		if (context.fTablePresentation != null) {
			context.fTablePresentation.updateUnit(unit.getDdeId());
			UnitPresentationCache.putPresentation(newFile, context.fTablePresentation);
			UnitPresentationCache.remove(context.fOldFile);
		}
		
		// обновить ссылки
		updateLinks(unit, context.fOldRelativeID, context.fIncludeID, false);
				
		// обновить ProjectExplorer
		ResourcesUtilities.updateDipElement(unit);
	}
	
	private static IStatus doRenameOperation(IFile oldFile, IPath newPath, Shell shell) {
		MoveResourcesOperation mp = new MoveResourcesOperation(oldFile, newPath, "Rename resource");
		try {
			return mp.execute(null, WorkspaceUndoUtil.getUIInfoAdapter(shell));
		} catch (ExecutionException e) {
			e.printStackTrace();
			return Status.error(e.getMessage(), e);
		}
	}
	
	/**
	 *  Обновляет описание после переименования
	 */
	private static void updateReqDescription(IDipUnit unit, DipDescription description, String content) {
		if (description != null){
			description.delete();
		}
		unit.removeDescription();
		if (content != null && !content.isEmpty()) {
			unit.updateDescription(content);
		}
	}
	
	/**
	 * Обновляет комментарий после переименования
	 */
	private static void updateReqComment(IDipUnit unit, IDipComment comment, String content, List<ITextComment> textComments) {
		// удалить старый коммент
		if (comment != null){
			comment.delete();
		}
		unit.deleteDipComment();
		// создаем новый
		if (content != null && !content.isEmpty()) {
			unit.updateDipComment(content);
		}
		if (textComments != null && !textComments.isEmpty()) {
			unit.updateTextAnnotations(textComments);
		}
	}
	
	private static void updateLinks(IDipDocumentElement unit, String lastID, String includeID, boolean withChildrenLinks) {
		String newRelativeID = DipUtilities.relativeProjectID(unit);
		LinkInteractor.instance().updateLinks(lastID, newRelativeID, unit.dipProject(), withChildrenLinks);									
		if (unit.isIncluded()) {
			String newIncludeID =  DipUtilities.relativeIncludeElement(unit);								
			IncludeFolder folder =  DipUtilities.findIncludeFolder(unit);
			LinkInteractor.instance().updateLinks(includeID, newIncludeID, folder, withChildrenLinks);									
		}
	}
	
	//===========================
	// rename Folder
	
	private static class RenameFolderContext {
		
		private IDipParent fDipFolder;
		private final String fLastID;
		private final String fIncludeID;
		private final IFolder fFolder;

		private final String fNewName;
		private final IPath fNewPath;
		private final boolean fReserve;
		
		private RenameFolderContext(IDipParent renameDipParent, String newName, boolean reserve) {
			fDipFolder = renameDipParent;
			fLastID = DipUtilities.relativeProjectID(renameDipParent);
			fIncludeID = renameDipParent.isIncluded() ? DipUtilities.relativeIncludeElement(renameDipParent) : null;
			fFolder =  (IFolder) renameDipParent.resource();
			fNewName = newName;
			fNewPath = fFolder.getFullPath().removeLastSegments(1).append(newName);
			fReserve = reserve;
		}		
	}
	
	
	public static void renameFolder(IDipParent dipParent, String newName, boolean reserve, Shell shell) throws RenameDIPException{
		DebugPrintUtils.printIncludeFolders(dipParent);
		RenameFolderContext context = new RenameFolderContext(dipParent, newName, reserve);
		IFolder newFolder = doRenameFolder(context, shell);	
		DebugPrintUtils.printIncludeFolders(dipParent);
		afterFolderRename(dipParent, newFolder);		
		updateLinks(context.fDipFolder, context.fLastID, context.fIncludeID, true);	
		ResourcesUtilities.updateDipElement(dipParent.parent());
		WorkbenchUtitlities.updateProjectExplorer();
	}
		
	private static IFolder doRenameFolder(RenameFolderContext context, Shell shell) throws RenameDIPException {
		try {
			if (context.fReserve) {
				// копируем
				context.fFolder.copy(context.fNewPath, true, null);
				IFolder newFolder = context.fFolder.getParent().getFolder(new Path(context.fNewName));

				// удаляем старую папку с резервированием
				IDipParent oldDipFolder = context.fDipFolder.parent().createNewFolder(context.fFolder);
				try {
					DipUtilities.deleteElement(oldDipFolder, true, shell, DipUtilities.NO_TMP);
				} catch (TmpCopyException e) {
					// NOP (ошибки не должно быть т.к. удаляется без копирования)
				}
				return newFolder;
			} else {
				IFolder newFolder = context.fFolder.getParent().getFolder(new Path(context.fNewName));
				context.fFolder.move(context.fNewPath, true, null);
				return newFolder;
			}
		} catch (CoreException | DeleteDIPException e) {
			e.printStackTrace();
			throw new RenameDIPException(context.fDipFolder, e.getMessage());
		}
	}
	
	private static void afterFolderRename(IParent renamedFolder, IContainer newContainer){				
		DebugPrintUtils.printIncludeFolders(renamedFolder);		
		renamedFolder.setResource(newContainer);
		DebugPrintUtils.printIncludeFolders(renamedFolder);
		updateFilesInFolder(renamedFolder);	
	}

	public static void updateFilesInFolder(IParent parent) {
		IContainer parentContainer = (IContainer) parent.resource();
		for (IDdeID child: parent.getChildren()) {			
			IDipElement element = child.getElement();
			if (element.isIncluded()) {
				//для этих элементов Resource не изменился
				element.setParent(parent);
				continue;
			}
			IResource resource = element.resource();
			if (resource instanceof IFile) {
				if (element instanceof IDipUnit) {
					IDipUnit dipUnit = (IDipUnit) element;					
					IFile oldFile = dipUnit.resource();
					IFile newFile = parentContainer.getFile(new Path(element.name()));				
					dipUnit.setResource(parentContainer.getFile(new Path(element.name())));				
					UnitPresentationCache.changeKey(oldFile, newFile);					
				} else {				
					element.setResource(parentContainer.getFile(new Path(element.name())));
				}
			} else if (resource instanceof IFolder) {
				element.setResource(parentContainer.getFolder(new Path(element.name())));
				updateFilesInFolder((IParent) element);
			}
		}
	}

	public static void renameProject(DipProject dipProject, String newName, Shell shell) throws RenameDIPException {
		DipCorePlugin.logInfo("Rename Project " + dipProject.name());
		IProject project = dipProject.getProject();
		IPath path = project.getFullPath().removeLastSegments(1).append(newName);
		try {
			project.move(path, true, null);
			ResourcesUtilities.updateProject(project);		
			IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(newName);			
			afterRenameProject(dipProject, newProject);
		} catch (CoreException e) {
			e.printStackTrace();
			throw new RenameDIPException(dipProject, e.getMessage());
		}		
	}
	
	private static void afterRenameProject(DipProject renamedFolder, IContainer newContainer){
		renamedFolder.setResource(newContainer);
		updateFilesInFolder(renamedFolder);
		DdeStorage.instance.getOrCreate(newContainer.getProject()).refresh();
	}

	
	public static void renameReport(DipElement report, String newName, Shell shell) throws RenameDIPException{
		IFile file = (IFile) report.resource();
		IPath newPath = file.getFullPath().removeLastSegments(1).append(newName);
		MoveResourcesOperation mp = new MoveResourcesOperation(file, newPath, "Move resource");
		try {
			IStatus status = mp.execute(null, WorkspaceUndoUtil.getUIInfoAdapter(shell));
			if (!status.isOK()){
				throw new RenameDIPException(report, status.getMessage());
			}
			IFile newFile = file.getParent().getFile(new Path(newName));
			report.setResource(newFile);			
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw new RenameDIPException(report, e.getMessage());
		}
		ResourcesUtilities.updateDipElement(report);
	}
	
	public static void renameSchema(DipSchemaElement schema, String newName, Shell shell) throws RenameDIPException{
		if (shell == null){
			shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		}	
		IFile file = schema.resource();
		IPath newPath = file.getFullPath().removeLastSegments(1).append(newName);
		MoveResourcesOperation mp = new MoveResourcesOperation(file, newPath, "Move resource");
		try {
			IStatus status = mp.execute(null, WorkspaceUndoUtil.getUIInfoAdapter(shell));
			if (!status.isOK()){
				throw new RenameDIPException(schema, status.getMessage());
			}
			IFile newFile = file.getParent().getFile(new Path(newName));
			schema.setResource(newFile);
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw new RenameDIPException(schema, e.getMessage());
		}
		ResourcesUtilities.updateDipElement(schema);
	}
	
}
