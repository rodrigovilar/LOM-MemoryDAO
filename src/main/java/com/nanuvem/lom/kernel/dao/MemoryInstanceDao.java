package com.nanuvem.lom.kernel.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;

import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.Instance;
import com.nanuvem.lom.api.dao.InstanceDao;

public class MemoryInstanceDao implements InstanceDao {

	private Long id = 1L;
	private MemoryDatabase memoryDatabase;

	public MemoryInstanceDao(MemoryDatabase memoryDatabase) {
		this.memoryDatabase = memoryDatabase;
	}

	public Instance create(Instance instance) {
		instance.setId(id++);
		instance.setVersion(0);

		memoryDatabase.addInstance(instance);
		
		return instance;
	}

	public List<Instance> listAllInstances(String fullEntityName) {
		Entity entity = memoryDatabase.findEntityByFullName(fullEntityName);
		
		List<Instance> cloneInstances = new ArrayList<Instance>();
		for (Instance it : memoryDatabase.getInstances(entity.getId())) {
			cloneInstances.add((Instance) SerializationUtils.clone(it));
		}
		return cloneInstances;
	}

	public Instance findInstanceById(Long id) {
		Collection<Entity> entities = memoryDatabase.getEntities();

		for (Entity entity : entities) {
			for (Instance instanceEach : memoryDatabase.getInstances(entity.getId())) {
				if (instanceEach.getId().equals(id)) {
					return instanceEach;
				}
			}
		}
		return null;
	}

    public List<Instance> findInstancesByEntityId(Long entityId) {
        return memoryDatabase.getInstances(entityId);
    }

	public Instance update(Instance instance) {
		instance.setVersion((instance.getVersion() + 1));
		memoryDatabase.updateInstance(instance);
		return instance;		
	}

	public void delete(Long id) {
		// TODO Auto-generated method stub
		
	}

}