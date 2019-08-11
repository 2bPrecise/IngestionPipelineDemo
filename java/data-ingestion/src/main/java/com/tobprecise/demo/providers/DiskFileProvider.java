package com.tobprecise.demo.providers;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.tobprecise.demo.entities.FileMetadata;

public class DiskFileProvider implements IFileProvider {

	private String _basePath;

	public DiskFileProvider(String basePath) {
		_basePath = basePath;
	}
	
	@Override
	public InputStream download(FileMetadata metadata) throws Exception {
		Path absolute = Paths.get(_basePath, metadata.getRelativePath());
		return new FileInputStream(absolute.toString());
	}

}
