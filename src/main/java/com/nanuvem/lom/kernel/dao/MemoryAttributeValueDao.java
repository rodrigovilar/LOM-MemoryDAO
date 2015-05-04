package com.nanuvem.lom.kernel.dao;

import com.nanuvem.lom.api.AttributeValue;
import com.nanuvem.lom.api.dao.AttributeValueDao;

public class MemoryAttributeValueDao implements AttributeValueDao {

	private Long id = 1L;
	private MemoryDatabase memoryDatabase;

	public MemoryAttributeValueDao(MemoryDatabase memoryDatabase) {
		this.memoryDatabase = memoryDatabase;
	}

	public AttributeValue create(AttributeValue value) {
		value.setId(id++);
		value.setVersion(0);
		
		memoryDatabase.addAttributeValue(value);
		
		return value;
	}

	public AttributeValue update(AttributeValue value) {
		return memoryDatabase.updateAttributeValue(value);
	}
}
