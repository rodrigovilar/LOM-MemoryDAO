package com.nanuvem.lom.kernel.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.nanuvem.lom.api.Attribute;
import com.nanuvem.lom.api.AttributeValue;
import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.Instance;

public class MemoryDatabase {

	private HashMap<Long, Entity> entitiesById = new HashMap<Long, Entity>();
	private HashMap<Long, List<Instance>> instancesByEntityId = new HashMap<Long, List<Instance>>();

	public void addEntity(Entity entity) {
		entitiesById.put(entity.getId(), entity);
	}

	public Collection<Entity> getEntities() {
		return entitiesById.values();
	}

	public void updateEntity(Entity entity) {
		Entity myEntity = findEntityById(entity.getId());

		myEntity.setName(entity.getName());
		myEntity.setNamespace(entity.getNamespace());
		myEntity.setVersion(entity.getVersion());
	}

	public void deleteEntity(Long id) {
		entitiesById.remove(id);
	}

	public Entity findEntityByFullName(String fullName) {
		Collection<Entity> values = entitiesById.values();
		for (Entity entity : values) {
			if (entity.getFullName().equalsIgnoreCase(fullName)) {
				return entity;
			}
		}

		return null;
	}

	public Entity findEntityById(Long id) {
		return entitiesById.get(id);
	}

	public void addAttribute(Attribute attribute) {
		Entity entity = findEntityById(attribute.getEntity().getId());
		shiftSequence(attribute, entity);
		attribute.setEntity(entity);
	}

	public Attribute updateAtribute(Attribute attribute) {
		Entity entity = findEntityById(attribute.getEntity().getId());

		Attribute attributeInEntity = null;
		boolean changeSequence = false;

		for (int i = 0; i < entity.getAttributes().size(); i++) {
			attributeInEntity = entity.getAttributes().get(i);
			if (attribute.getId().equals(attributeInEntity.getId())) {

				if (!attribute.getSequence().equals(
						attributeInEntity.getSequence())) {
					changeSequence = true;
				}

				attributeInEntity.setName(attribute.getName());
				attributeInEntity.setType(attribute.getType());
				attributeInEntity
						.setConfiguration(attribute.getConfiguration());
				attributeInEntity
						.setVersion(attributeInEntity.getVersion() + 1);
				break;
			}
		}

		if (changeSequence) {
			Attribute temp = null;
			for (Attribute at : entity.getAttributes()) {
				if (attribute.getId().equals(at.getId())) {
					temp = at;
					entity.getAttributes().remove(at);
					temp.setSequence(attribute.getSequence());

					this.shiftSequence(temp, entity);
					break;
				}
			}
		}

		return attributeInEntity;
	}

	private void shiftSequence(Attribute attribute, Entity entity) {
		int i = 0;
		for (; i < entity.getAttributes().size(); i++) {
			if (attribute.getSequence().equals(
					entity.getAttributes().get(i).getSequence())) {
				break;
			}
		}

		i++;
		entity.getAttributes().add(i - 1, attribute);

		for (; i < entity.getAttributes().size(); i++) {
			Attribute nextAttribute = null;
			try {
				nextAttribute = entity.getAttributes().get(i);
			} catch (IndexOutOfBoundsException e) {
				break;
			}

			if (nextAttribute.getSequence().equals(
					entity.getAttributes().get(i - 1).getSequence())) {
				nextAttribute.setSequence(nextAttribute.getSequence() + 1);
			}
		}
	}

	public void addInstance(Instance instance) {
		Entity entity = findEntityById(instance.getEntity().getId());
		instance.setEntity(entity);
		getInstances(entity.getId()).add(instance);
	}

	public void updateInstance(Instance instance) {
		Entity entity = findEntityById(instance.getEntity().getId());
		List<Instance> instances = getInstances(entity.getId());

		for (int i = 0; i < instances.size(); i++) {
			if (instances.get(i).getId().equals(instance.getId())) {
				instances.remove(i);
				instances.add(i, instance);
				break;
			}
		}
	}

	public List<Instance> getInstances(Long idEntity) {
		if (instancesByEntityId.get(idEntity) == null) {
			instancesByEntityId.put(idEntity, new ArrayList<Instance>());
		}

		return instancesByEntityId.get(idEntity);
	}

	public void addAttributeValue(AttributeValue value) {
		Instance instance = findInstanceById(value.getInstance().getId());
		value.setInstance(instance);
		instance.getValues().add(value);
	}

	private Instance findInstanceById(Long id) {
		for (Entity entity : getEntities()) {
			for (Instance instance : getInstances(entity.getId())) {
				if (instance.getId().equals(id)) {
					return instance;
				}
			}
		}
		return null;
	}

	public AttributeValue updateAttributeValue(AttributeValue value) {
		Instance instance = findInstanceById(value.getInstance().getId());

		for (AttributeValue attributeValue : instance.getValues()) {
			if (value.getId().equals(attributeValue.getId())) {
				attributeValue.setAttribute(value.getAttribute());
				attributeValue.setInstance(value.getInstance());
				attributeValue.setValue(value.getValue());
				attributeValue.setVersion(attributeValue.getVersion() + 1);

				return attributeValue;
			}
		}
		return null;
	}

}
