package com.example.mynote


import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {
    fun addNote(userId: String, note: Note) {
        val db = FirebaseFirestore.getInstance()


        val newNote = hashMapOf(
            "title" to note.title,
            "description" to note.description,
            "userId" to userId
        )

        db.collection("users")
            .document(userId)
            .collection("notes")
            .add(newNote)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Note added successfully with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding note", e)
            }
    }

    fun updateNote(userId: String, noteId: String, title: String, description: String) {
        val db = FirebaseFirestore.getInstance()


        val noteRef = db.collection("users").document(userId).collection("notes").document(noteId)

        noteRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val currentTitle = document.getString("title") ?: ""
                val currentDescription = document.getString("description") ?: ""


                if (currentTitle != title || currentDescription != description) {
                    val updatedNote = hashMapOf(
                        "title" to title,
                        "description" to description
                    )


                    noteRef.update(updatedNote as Map<String, Any>)
                        .addOnSuccessListener {
                            Log.d(TAG, "Note updated successfully with ID: $noteId")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error updating note", e)
                        }
                } else {

                    Log.d(TAG, "No changes detected, update not performed")
                }
            }
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Error fetching note data", exception)
        }
    }


    fun deleteNoteInFirestore(userId: String, noteId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(userId)
            .collection("notes")
            .document(noteId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Note deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting note", e)
            }
    }


    fun getNotesFromFirestore(userId: String, onResult: (List<Note>) -> Unit) {
        val db = FirebaseFirestore.getInstance()


        db.collection("users")
            .document(userId)
            .collection("notes")
            .get()
            .addOnSuccessListener { documents ->
                val notes = documents.map { document ->
                    Note(
                        noteId = document.id,
                        title = document.getString("title") ?: "",
                        description = document.getString("description") ?: ""
                    )
                }
                Log.d(TAG, "Đã lấy note thành công: ${notes.size} ghi chú")
                onResult(notes)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }


}
