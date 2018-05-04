package me.restfulws.utm.form;

import org.springframework.web.multipart.MultipartFile;

public class FileForm {
	private MultipartFile file;
	private String name, dir;
	
	public MultipartFile getFile() {
		return file;
	}
	
	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDir() {
		return dir;
	}
	
	public void setDir(String dir) {
		this.dir = dir;
	}
}