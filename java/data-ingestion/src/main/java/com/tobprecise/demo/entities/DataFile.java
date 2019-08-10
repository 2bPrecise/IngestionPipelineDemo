package com.tobprecise.demo.entities;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class DataFile {
	@Getter @Setter private UUID id;
	@Getter @Setter private String relativePath;
	@Getter @Setter private String fileType;
}
