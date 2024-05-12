package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {
    private final Map<String, String> commonModelObject;
    private final FileService fileService;
    private static final String IS_SHOW_MODAL = "isShowModal";
    public FileUploadController(FileService fileService) {
        this.fileService = fileService;
        this.commonModelObject = new HashMap<>();
        commonModelObject.put("file", "active show");
        commonModelObject.put("fileTabPanel", "show active");
    }

    private void resetCommonModelObject() {
        commonModelObject.put(IS_SHOW_MODAL, "false");
    }

    @RequestMapping(value = "/files", method = RequestMethod.GET)
    public String getHomePage(Model model, HttpSession session) {
        resetCommonModelObject();
        if (Objects.isNull(session.getAttribute("userId"))) {
            return "redirect:/login";
        }
        int userId = (int)session.getAttribute("userId");
        List<File> fileList = fileService.selectAllFile(userId)
                .stream()
                .map(file -> new File(
                        file.getFileId(),
                        file.getFileName(),
                        file.getContentType(),
                        file.getFileSize(),
                        file.getUserId(),
                        file.getFileData()
                )).collect(Collectors.toList());
        model.addAttribute("fileList", fileList);
        commonModelObject.put(IS_SHOW_MODAL, (String) session.getAttribute(IS_SHOW_MODAL));
        model.addAllAttributes(commonModelObject);
        session.removeAttribute(IS_SHOW_MODAL);
        session.removeAttribute("isSignUpSuccess");
        return "home";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logoutAction(HttpSession session) throws IOException {
        session.setAttribute("isLogout", true);
        session.removeAttribute("userId");
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(value = "/files/delete-file", method = RequestMethod.GET)
    private String deleteFile(@RequestParam(required = true) String fileName, Model model, HttpSession session) {
        resetCommonModelObject();
        int userId = (int)session.getAttribute("userId");
        int isDeleteSuccessful =  fileService.deleteFile(fileName, userId);
        if (isDeleteSuccessful > -1) {
            FileService.showToastMessage("FileDelete", "File is successfully deleted", "success", session);
        } else {
            FileService.showToastMessage("FileDelete", "File is not found", "error", session);
        }
        session.setAttribute(IS_SHOW_MODAL, "true");
        return "redirect:/files";
    }

    @RequestMapping(value = "/files/upload-file", method = RequestMethod.POST)
    private String uploadFile(@RequestParam("fileUpload") MultipartFile fileUpload, Model model, HttpSession session) throws IOException, SQLException {
        resetCommonModelObject();
        int userId = (int)session.getAttribute("userId");
        if (fileUpload.isEmpty()) {
            FileService.showToastMessage("FileUpload", "File is not selected", "error", session);
            session.setAttribute(IS_SHOW_MODAL, "true");
            return "redirect:/files";
        }
        int isCreateFileSuccessful = fileService.createFile(fileUpload, userId);
        if (isCreateFileSuccessful > -1) {
            FileService.showToastMessage("FileUpload", "FileUpload is successfully uploaded", "success", session);
        } else {
            FileService.showToastMessage("FileUpload", "FileName is duplicate", "error", session);
        }
        session.setAttribute(IS_SHOW_MODAL, "true");
        return "redirect:/files";
    }
}
