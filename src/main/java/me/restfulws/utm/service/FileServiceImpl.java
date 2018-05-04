package me.restfulws.utm.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {
	private static final Logger logger = LogManager.getLogger();

	@Override
	public Path getFile(String fileName) {
		Path file = Paths.get(fileName);
		return file;
	}
	
	public List<Path> walkDir(Path path, List<Path> paths) {
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for(Path p: stream) {
				if (Files.isDirectory(p)) {
					walkDir(p, paths);
				}
				paths.add(p);
			}
		} catch (IOException | DirectoryIteratorException ex) {
			logger.error("{}: {}", ex.getClass(), ex.getMessage());
		}
		return paths;
	}

	@Override
	public boolean uploadFile(MultipartFile file, String name, String path) {
		try {
        	Path filePath = Paths.get(path);
        	if (Files.notExists(filePath)) {
        		logger.warn("Target path does not exist. Creating {}", path);
        		Files.createDirectory(filePath);
        	}
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath.toString() + File.separator + name)));
            FileCopyUtils.copy(file.getInputStream(), stream);
            stream.close();
            logger.info("Successfully uploaded {} ", filePath.toString() + File.separator + name);
            return true;
        }
        catch (Exception ex) {
        	logger.error("{}: {}", ex.getClass(), ex.getMessage());
        }
		return false;
	}
	
	@Override
	public String delete(String path) {
    	Path filePath = Paths.get(path);
		
    	try {
        	if (Files.notExists(filePath)) {
        		logger.warn("Target path does not exist. Creating {}", path);
        	} else {
        		Files.delete(filePath);
                logger.info("Successfully deleted {} ", filePath.toString());
        	}
        }
        catch (Exception ex) {
        	logger.error("{}: {}", ex.getClass(), ex.getMessage());
        }

		return filePath.getFileName().toString();
	}
}