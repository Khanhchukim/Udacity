package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class NoteService {

    private final NoteMapper noteMapper;
    public NoteService(
        NoteMapper noteMapper
    ) {
        this.noteMapper = noteMapper;
    }

    public List<Note> selectAllNotes(int userId) {
        return noteMapper.selectAllNotes(userId);
    }

    public Note selectNote(int noteId ,int userId) {
        return noteMapper.selectNote(noteId, userId);
    }

    public Integer deleteNote(int noteId ,int userId) {
        return noteMapper.deleteNote(noteId, userId);
    }

    public Integer createNote(Note note) {
        return noteMapper.insertNote(note);
    }

    public Integer updateNote(Note note) {

        return noteMapper.updateNote(note);
    }

    public boolean isNoteContentInvalid(Note note, HttpSession session) {
        if (note.getNoteTitle().isBlank()) {
            showToastMessage("Note", "Note title is missing", "error", session);
            return true;
        }
        if (note.getNoteDescription().isBlank()) {
            showToastMessage("Note", "Note description is missing", "error", session);
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
