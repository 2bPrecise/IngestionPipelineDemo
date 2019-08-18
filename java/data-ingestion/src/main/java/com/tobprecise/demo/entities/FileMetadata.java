package com.tobprecise.demo.entities;

public class FileMetadata {
	private String fileId;
	private String originalName;
	private String relativePath;
	private Long size;
	private String uploadedTime;
	
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getOriginalName() {
		return originalName;
	}
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	public String getRelativePath() {
		return relativePath;
	}
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public String getUploadedTime() {
		return uploadedTime;
	}
	public void setUploadedTime(String uploadedTime) {
		this.uploadedTime = uploadedTime;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((relativePath == null) ? 0 : relativePath.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((uploadedTime == null) ? 0 : uploadedTime.hashCode());
		result = prime * result + ((fileId == null) ? 0 : fileId.hashCode());
		result = prime * result + ((originalName == null) ? 0 : originalName.hashCode());
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
		FileMetadata other = (FileMetadata) obj;
		if (relativePath == null) {
			if (other.relativePath != null)
				return false;
		} else if (!relativePath.equals(other.relativePath))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		if (uploadedTime == null) {
			if (other.uploadedTime != null)
				return false;
		} else if (!uploadedTime.equals(other.uploadedTime))
			return false;
		if (fileId == null) {
			if (other.fileId != null)
				return false;
		} else if (!fileId.equals(other.fileId))
			return false;
		if (originalName == null) {
			if (other.originalName != null)
				return false;
		} else if (!originalName.equals(other.originalName))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "FileMetadata [fileId=" + fileId + ", originalName=" + originalName + ", relativePath=" + relativePath
				+ ", size=" + size + ", uploadedTime=" + uploadedTime + "]";
	}
	
}
