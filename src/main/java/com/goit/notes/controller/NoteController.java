package com.goit.notes.controller;

import com.goit.notes.entity.Access;
import com.goit.notes.entity.Note;

import com.goit.notes.entity.NoteUser;
import com.goit.notes.service.NoteService;
import com.goit.notes.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;


import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/note")
@Slf4j
public class NoteController {

    private final NoteService noteService;
    private final UserService userService;
    private Note currentNote;

    @GetMapping("/listNotes")
    public ModelAndView listAllNotes(Model model, ModelAndView modelAndView) {

        NoteUser noteUser = getNoteUser();
        model.addAttribute("title", "List Notes");
        model.addAttribute("message", "Hello " + noteUser.getUserName().toUpperCase() + " this is your list notes");
        modelAndView.addObject("notes", noteUser.getNotes());
        modelAndView.setViewName("listNotes");
        return modelAndView;
    }

    @GetMapping("/createNote")
    public String create(Model model) {
        model.addAttribute("title", "Create Note");
        model.addAttribute("message", "Add new note");
        return "createNote";
    }

    @PostMapping("/createNote")
    public String createNote(@Valid Note note) {
        note.setNoteUser(getNoteUser());
        noteService.save(note);
        return "redirect:/note/listNotes";
    }

    @GetMapping("/editNote")
    public String edit(@RequestParam("id") Note note, Model model) {
        currentNote = noteService.getById(note.getId());
        model.addAttribute("message", "Edit note");
        model.addAttribute("note", currentNote);
        return "editNote";
    }

    @PostMapping("/editNote")
    public String editNote(@Valid Note note) {
        note.setId(currentNote.getId());
        note.setNoteUser(getNoteUser());
        noteService.save(note);
        return "redirect:/note/listNotes";
    }

    @GetMapping("/deleteNote")
    public String deleteNote(@RequestParam("id") Note note) {
        noteService.delete(note.getId());
        return "redirect:/note/listNotes";
    }

    @GetMapping("/share/{id}")
    public String shareNote(@RequestParam("id") Note note) {
        if (note.getAccess() != Access.PUBLIC) {
            return "Oops something wrong....";
        }
        return "redirect:/note/listNotes";
    }

    @ModelAttribute("note")
    public Note defaultNote() {
        return new Note();
    }

    private NoteUser getNoteUser() {
        Optional<NoteUser> authorizedUser = userService.findByName(SecurityContextHolder.getContext().getAuthentication().getName());
        return authorizedUser.orElseThrow(() -> new UsernameNotFoundException("User is not found"));
    }
}


