package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Service
public class FileService {
    private final FileMapper fileMapper;

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public int createFile(MultipartFile file, Integer userId) throws IOException, SQLException {
        byte[] bytes = file.getInputStream().readAllBytes();
        Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
        File newUploadedFile = new File(
                null,
                file.getOriginalFilename(),
                file.getContentType(),
                Long.valueOf(file.getSize()).toString(),
                userId,
                Base64.encodeBase64String(bytes)
        );
        if (isDuplicateFileName(newUploadedFile)) {
            return -1;
        }
        return fileMapper.insertFile(newUploadedFile);
    }


    public File selectFile(String fileName, int userId) {
        return fileMapper.selectFile(fileName, userId);
    }

    public List<File> selectAllFile(Integer userId) {
        return fileMapper.selectAllFile(userId);
    }

    public int deleteFile(String fileName, Integer userId) {
        return fileMapper.deleteFile(fileName, userId);
    }

    public boolean isDuplicateFileName(File file) {
        return !Objects.isNull(selectFile(file.getFileName(), file.getUserId()));
    }

    public static void showToastMessage(String title, String content, String type, HttpSession session) {
        session.setAttribute("messageContent", content);
        session.setAttribute("messageTitle", title);
        session.setAttribute("messageType", type);
    }
}
