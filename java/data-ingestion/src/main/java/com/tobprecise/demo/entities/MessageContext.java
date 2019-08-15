package com.tobprecise.demo.entities;

import java.io.Serializable;
import java.util.UUID;

public class MessageContext implements Serializable {
	private UUID contextId;
	private int itemId;
	
	public UUID getContextId() {
		return contextId;
	}
	public void setContextId(UUID contextId) {
		this.contextId = contextId;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contextId == null) ? 0 : contextId.hashCode());
		result = prime * result + itemId;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageContext other = (MessageContext) obj;
		if (contextId == null) {
			if (other.contextId != null)
				return false;
		} else if (!contextId.equals(other.contextId))
			return false;
		if (itemId != other.itemId)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "MessageContext [contextId=" + contextId + ", itemId=" + itemId + "]";
	}
	
	
}
