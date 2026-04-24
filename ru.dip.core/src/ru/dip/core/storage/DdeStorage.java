package ru.dip.core.storage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;

import ru.dip.core.model.DipProject;
import ru.dip.core.model.interfaces.IDipDocumentElement;
import ru.dip.core.model.interfaces.IDipElement;
import ru.dip.core.model.interfaces.IDipUnit;
import ru.dip.core.storage.IDdeID.InludeParent;

public class DdeStorage {
	
	public static DdeStorage instance = new DdeStorage();
	
	private DdeStorage() {}
	
	private Map<IDdeID, IDdeID> fIdentityIDMap = new IdentityHashMap<>();
	private Map<IDdeID, IDdeID> fHashIdMap = new HashMap<IDdeID, IDdeID>();
	
	private Map<IDdeID, IDipElement> fStorage = new IdentityHashMap<>();	
	private Map<DipProject, Set<IDdeID>> fStorageByDipProject =new HashMap<>();
	
	public Map<IDdeID, IDdeID> getIdHashMap(){
		return fHashIdMap;
	}
	
	public IDdeID getOrPut(IDdeID newInstance) {
		IDdeID instance = fHashIdMap.get(newInstance);	
		if (instance != null) {
			return instance; 
		} else {
			fIdentityIDMap.put(newInstance, newInstance);
			fHashIdMap.put(newInstance, newInstance);
			return newInstance;
		}
	}

	public <T extends IDipElement> Optional<T> getOptional(IDdeID id){
		T element = this.<T>get(id); 
		return Optional.ofNullable(element);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends IDipElement> T get(IDdeID id) {
		return (T)fStorage.get(id);				
	}
	
	public void put(IDdeID id, IDipElement element) {
		fStorage.put(id, element);
		if (element.dipProject() != null) {
			fStorageByDipProject.computeIfAbsent(element.dipProject(), k -> new HashSet<IDdeID>()).add(id);
		}
	}

	public List<IDipElement> getList(Collection<IDdeID> ids) {
		return ids.stream().map(fStorage::get).collect(Collectors.toList());
	}
	
	@SuppressWarnings("unchecked")
	public <T extends IDipElement> List<T> getObjList(Collection<IDdeID> ids) {
		return ids.stream()
				.map(id -> (T)fStorage.get(id))
				.collect(Collectors.toList());
	}
	
	public List<IDipDocumentElement> getDocumentElementList(Collection<IDdeID> ids) {
		return ids.stream()
				.map(this::<IDipDocumentElement>get)
				.collect(Collectors.toList());
	}
	
	public List<IDipUnit> getUnitList(List<IDdeID> ids) {
		return ids.stream().map(fStorage::get)
				.map(IDipUnit.class::cast)
				.collect(Collectors.toList());
	}

	/**
	 * Удаляет все дочерние элементы контейнера, сам контейнер не удаляется
	 * Можно вызывать для файла (удаляется в т.ч. UnitExtension)
	 */
	public void clearContainer(IDdeID container) {
		fStorage.keySet().removeIf(dde -> IDdeID.containsInParent(dde, container, InludeParent.EXCLUDE));	
		fIdentityIDMap.keySet().removeIf(dde -> IDdeID.containsInParent(dde, container, InludeParent.EXCLUDE));
		fHashIdMap.keySet().removeIf(dde -> IDdeID.containsInParent(dde, container, InludeParent.EXCLUDE));
	}
	
	/**
	 * Удаляет все дочерние элементы контейнера и сам контейнер
	 * Можно вызывать для файла (удаляется в т.ч. UnitExtension)
	 */
	public void deleteContainer(IDdeID container) {
		fStorage.keySet().removeIf(dde -> IDdeID.containsInParent(dde, container, InludeParent.INCLUDE));	
		fIdentityIDMap.keySet().removeIf(dde -> IDdeID.containsInParent(dde, container, InludeParent.INCLUDE));
		fHashIdMap.keySet().removeIf(dde -> IDdeID.containsInParent(dde, container, InludeParent.INCLUDE));
	}
	
	public void deleteProject(DipProject dipProject) {
		clearContainer(dipProject.getDdeId());
		fStorageByDipProject.remove(dipProject);
		fStorage.remove(dipProject.getDdeId());
		fIdentityIDMap.remove(dipProject.getDdeId());
		fHashIdMap.remove(dipProject.getDdeId());
	}

	public DipProject getOrCreate(IProject project){
		DipProject dipProject = fStorageByDipProject
				.keySet()
				.stream()
				.filter(dp -> Objects.equals(dp.resource(), project))
				.findFirst()
				.orElse(null);
		if (dipProject == null) {
			dipProject = DipProject.instance(project);			
		}
		return dipProject;
	}

	public Collection<DipProject> getProjects() {
		return fStorageByDipProject.keySet();
	}

	public void clear() {
		fStorageByDipProject.clear();
		fStorage.clear();
		fIdentityIDMap.clear();
		fHashIdMap.clear();
	}
	
	public void printDEBUG() {
		System.out.println("DDE STORAGE");
		System.out.println(fStorage.keySet());
	}
}
