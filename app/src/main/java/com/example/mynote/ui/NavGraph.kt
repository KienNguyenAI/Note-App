package com.example.mynote.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mynote.FirestoreRepository
import com.example.mynote.Note
import com.example.mynote.ui.auth.LoginScreen


@Composable
fun AppNavGraph(navController: NavHostController) {
    val firestoreRepo = FirestoreRepository()
    val notes = remember { mutableStateListOf<Note>() }

    LaunchedEffect(Unit) {
        firestoreRepo.getNotesFromFirestore("123") { retrievedNotes ->
            notes.clear()
            notes.addAll(retrievedNotes)
        }
    }

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen (
                navController = navController,
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                notes = notes
            )
        }
        composable("note/{noteId}/{title}/{description}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""

            NoteScreen(
                navController = navController,
                noteId = noteId,
                initialTitle = title,
                initialDescription = description,

                onAddNote = { newTitle, newDescription ->

                    val newNote = Note(
                        noteId = "",
                        title = newTitle,
                        description = newDescription
                    )
                    firestoreRepo.addNote("123", newNote)
                    notes.add(newNote)
                },
                onDeleteNote = { noteIdToDelete ->
                    firestoreRepo.deleteNoteInFirestore("123", noteIdToDelete)
                    notes.removeIf { it.noteId == noteIdToDelete }
                },
                onUpdateNote = { noteId, title, description ->
                    firestoreRepo.updateNote("123", noteId, title, description)
                    notes.replaceAll {
                        if (it.noteId == noteId) it.copy(title = title, description = description) else it
                    }
                }
            )
        }
    }
}