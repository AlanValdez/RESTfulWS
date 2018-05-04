package me.restfulws.utm.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class File implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name, path, fullPath, sizeBytes;
	private Link _link;

	public File() {}
	
	public File(String name, String path, String fullPath, String sizeBytes, Link _link) {
		this.name = name;
		this.path = path;
		this.fullPath = fullPath;
		this.sizeBytes = sizeBytes;
		this._link = _link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public String getSizeBytes() {
		return sizeBytes;
	}

	public void setSizeBytes(String sizeBytes) {
		this.sizeBytes = sizeBytes;
	}

	public Link get_link() {
		return _link;
	}

	public void set_link(Link _link) {
		this._link = _link;
	}
}