package com.tobprecise.demo.entities;

import java.io.Serializable;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class MessageContext implements Serializable {
	@Getter @Setter private UUID contextId;
	@Getter @Setter private int itemId;
}
