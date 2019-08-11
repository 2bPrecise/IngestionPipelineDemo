package com.tobprecise.demo.entities;

import lombok.Getter;
import lombok.Setter;

public class FileMetadata {
	@Getter @Setter private String fileId;
	@Getter @Setter private String originalName;
	@Getter @Setter private String RelativePath;
	@Getter @Setter private Long Size;
	@Getter @Setter private String UploadedTime;
}
