package com.tobprecise.demo.entities;

public class FileMetadata {
	private String fileId;
	private String originalName;
	private String RelativePath;
	private Long Size;
	private String UploadedTime;
	
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
		return RelativePath;
	}
	public void setRelativePath(String relativePath) {
		RelativePath = relativePath;
	}
	public Long getSize() {
		return Size;
	}
	public void setSize(Long size) {
		Size = size;
	}
	public String getUploadedTime() {
		return UploadedTime;
	}
	public void setUploadedTime(String uploadedTime) {
		UploadedTime = uploadedTime;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((RelativePath == null) ? 0 : RelativePath.hashCode());
		result = prime * result + ((Size == null) ? 0 : Size.hashCode());
		result = prime * result + ((UploadedTime == null) ? 0 : UploadedTime.hashCode());
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
		if (RelativePath == null) {
			if (other.RelativePath != null)
				return false;
		} else if (!RelativePath.equals(other.RelativePath))
			return false;
		if (Size == null) {
			if (other.Size != null)
				return false;
		} else if (!Size.equals(other.Size))
			return false;
		if (UploadedTime == null) {
			if (other.UploadedTime != null)
				return false;
		} else if (!UploadedTime.equals(other.UploadedTime))
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
		return "FileMetadata [fileId=" + fileId + ", originalName=" + originalName + ", RelativePath=" + RelativePath
				+ ", Size=" + Size + ", UploadedTime=" + UploadedTime + "]";
	}
	
}
