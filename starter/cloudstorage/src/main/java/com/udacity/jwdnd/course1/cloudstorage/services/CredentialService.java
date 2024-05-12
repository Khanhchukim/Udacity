package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class CredentialService {

    private final CredentialMapper credentialMapper;
    public CredentialService(
            CredentialMapper credentialMapper
    ) {
        this.credentialMapper = credentialMapper;
    }

    public List<Credential> getAllCredentials(int userId) {
        return credentialMapper.selectAllCredentials(userId);
    }

    public Credential getCredential(String url, int userId) {
        return credentialMapper.selectCredentail(url, userId);
    }

    public int createCredential(Credential credential) {
        return credentialMapper.insertCredential(credential);
    }

    public int deleteCredential(Integer credentialId, int userId) {
        return credentialMapper.deleteCredential(credentialId, userId);
    }

    public int updateCredential(Credential credential) {
        return credentialMapper.updateCredential(credential);
    }

    public boolean isCredentialContentInvalid(Credential credential, HttpSession session) {
        if (credential.getUrl().isBlank()) {
            showToastMessage("Credential", "Url is missing", "error", session);
            return true;
        }
        if (credential.getUserName().isBlank()) {
            showToastMessage("Credential", "UserName is missing", "error", session);
            return true;
        }
        if (credential.getPassword().isBlank()) {
            showToastMessage("Credential", "Password is missing", "error", session);
            return true;
        }
        return false;
    }

    public static void showToastMessage(String title, String content, String type, HttpSession session) {
        session.setAttribute("messageContent", content);
        session.setAttribute("messageTitle", title);
        session.setAttribute("messageType", type);
    }
}
