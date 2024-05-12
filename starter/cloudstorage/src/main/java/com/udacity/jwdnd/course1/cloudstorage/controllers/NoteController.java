package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
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

@Controller
public class NoteController {
    private final NoteService noteService;
    private final Map<String, String> commonModelObject;
    private static final String IS_SHOW_MODAL = "isShowModal";

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
        this.commonModelObject = new HashMap<>();
        commonModelObject.put("note", "active show");
        commonModelObject.put("noteTabPanel", "show active");
    }

    private void resetCommonModelObject() {
        commonModelObject.put(IS_SHOW_MODAL, "false");
    }

    @RequestMapping(value = "/notes", method = RequestMethod.GET)
    private String getNotes(Model model, HttpSession session) {
        resetCommonModelObject();
        if (Objects.isNull(session.getAttribute("userId"))) {
            return "redirect:/login";
        }
        int userId = (int)session.getAttribute("userId");
        List<Note> noteList = noteService.selectAllNotes(userId);
        commonModelObject.put(IS_SHOW_MODAL, (String) session.getAttribute(IS_SHOW_MODAL));
        model.addAttribute("noteList", noteList);
        model.addAllAttributes(commonModelObject);
        session.removeAttribute(IS_SHOW_MODAL);
        return "home";
    }


    @RequestMapping(value = "/notes/create-note", method = RequestMethod.POST)
    private String postNote(@ModelAttribute Note note, Model model, HttpSession session) {
        int isCreateNoteSuccess = -1;
        int isUpdateNoteSuccess = -1;
        resetCommonModelObject();
        int userId = (int)session.getAttribute("userId");
        note.setUserId(userId);
        if (!noteService.isNoteContentInvalid(note, session)) {
            if (!Objects.isNull(note.getNoteId())) {
                isUpdateNoteSuccess = noteService.updateNote(note);
                if (isUpdateNoteSuccess > -1) {
                    NoteService.showToastMessage("UpdateNote", "Note is successfully updated", "success", session);
                } else {
                    NoteService.showToastMessage("UpdateNote", "Note can not be updated. There's an error", "error", session);
                }
            } else {
                isCreateNoteSuccess = noteService.createNote(note);
                if (isCreateNoteSuccess > -1) {
                    NoteService.showToastMessage("CreateNote", "Note is successfully created", "success", session);
                } else {
                    NoteService.showToastMessage("CreateNote", "Note can not be created. There's an error", "error", session);
                }
            }
        }
        session.setAttribute(IS_SHOW_MODAL, "true");
        return "redirect:/notes";
    }

    @RequestMapping(value = "/notes/delete-note", method = RequestMethod.GET)
    private String deleteNote(@RequestParam(required = true) Integer noteId, Model model, HttpSession session) {
        resetCommonModelObject();
        int userId = (int)session.getAttribute("userId");
        int isDeleteNoteSuccess = noteService.deleteNote(noteId, userId);
        if (isDeleteNoteSuccess > -1) {
            NoteService.showToastMessage("DeleteNote", "Note is successfully deleted", "success", session);
        } else {
            NoteService.showToastMessage("DeleteNote", "Note is not found", "error", session);
        }
        session.setAttribute(IS_SHOW_MODAL, "true");
        return "redirect:/notes";
    }

}
