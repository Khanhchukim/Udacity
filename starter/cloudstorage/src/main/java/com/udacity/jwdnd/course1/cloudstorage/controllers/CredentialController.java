package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class CredentialController
{
    private final CredentialService credentialService;
    private final EncryptionService encryptionService;
    private final Map<String, String> commonModelObject;
    private static final String IS_SHOW_MODAL = "isShowModal";

    @Value("${ENCRYPTION_KEY}")
    private String encryptionKey;

    public CredentialController(CredentialService credentialService, EncryptionService encryptionService) {
        this.credentialService = credentialService;
        this.encryptionService = encryptionService;
        this.commonModelObject = new HashMap<>();
        commonModelObject.put("credential", "active show");
        commonModelObject.put("credentialTabPanel", "show active");
    }

    private void resetCommonModelObject() {
        commonModelObject.put(IS_SHOW_MODAL, "false");
    }

    @RequestMapping(value = "/credentials", method = RequestMethod.GET)
    public String getCredentials(Model model, HttpSession session) {
        resetCommonModelObject();
        if (Objects.isNull(session.getAttribute("userId"))) {
            return "redirect:/login";
        }
        int userId = (int)session.getAttribute("userId");
        List<Credential> credentialList = credentialService.getAllCredentials(userId)
                .stream().map(credential -> new Credential(
                credential.getCredentialId(),
                credential.getUrl(),
                credential.getUserName(),
                credential.getKey(),
                credential.getPassword(),
                credential.getUserId()
        )).collect(Collectors.toList());
        commonModelObject.put(IS_SHOW_MODAL, (String) session.getAttribute(IS_SHOW_MODAL));
        model.addAttribute("credentialList", credentialList);
        model.addAttribute("encryptionService", encryptionService);
        model.addAttribute("encryptionKey", encryptionKey);
        model.addAllAttributes(commonModelObject);
        session.removeAttribute(IS_SHOW_MODAL);
        return "home";
    }

    @RequestMapping(value = "/credentials/create-credential", method = RequestMethod.POST)
    public String postCredential(@ModelAttribute Credential credential,Model model ,HttpSession session) {
        int isCreateCredentialSuccess = -1;
        int isUpdateCredentialSuccess = -1;
        resetCommonModelObject();
        int userId = (int)session.getAttribute("userId");
        credential.setUserId(userId);
        if (!credentialService.isCredentialContentInvalid(credential, session)) {
            if (!Objects.isNull(credential.getCredentialId())) {
                isUpdateCredentialSuccess = credentialService.updateCredential(credential);
                if (isUpdateCredentialSuccess > -1) {
                    CredentialService.showToastMessage("UpdateCredential", "Credential is successfully updated", "success", session);
                } else {
                    CredentialService.showToastMessage("UpdateCredential", "Credential is not found", "error", session);
                }
            } else {
                isCreateCredentialSuccess = credentialService.createCredential(credential);
                if (isCreateCredentialSuccess > -1) {
                    CredentialService.showToastMessage("CreateCredential", "Credential is successfully created", "success", session);
                } else {
                    CredentialService.showToastMessage("CreateCredential", "Credential can not be created. There's an error", "error", session);
                }
            }
        }
        session.setAttribute(IS_SHOW_MODAL, "true");
        return "redirect:/credentials";
    }

    @RequestMapping(value = "/credentials/delete-credential", method = RequestMethod.GET)
    private String deleteCredential(@RequestParam(required = true) Integer credentialId, Model model, HttpSession session) {
        resetCommonModelObject();
        int userId = (int)session.getAttribute("userId");
        int isDeleteCredentialSuccess = credentialService.deleteCredential(credentialId, userId);
        if (isDeleteCredentialSuccess > -1) {
            CredentialService.showToastMessage("DeleteCredential", "Credential is successfully deleted", "success", session);
        } else {
            CredentialService.showToastMessage("DeleteCredential", "Credential is not found", "error", session);
        }
        session.setAttribute(IS_SHOW_MODAL, "true");
        return "redirect:/credentials";
    }
}
