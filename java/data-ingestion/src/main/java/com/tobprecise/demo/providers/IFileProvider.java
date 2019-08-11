package com.tobprecise.demo.providers;

import java.io.InputStream;

import com.tobprecise.demo.entities.FileMetadata;

public interface IFileProvider {
	InputStream download(FileMetadata metadata) throws Exception;
}
