package com.example.myapplication;

import StudentAdapter
import StudentModel
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var studentAdapter: StudentAdapter
    private val students = mutableListOf<StudentModel>()
    private var recentlyDeletedStudent: StudentModel? = null
    private var recentlyDeletedPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize student list
        students.addAll(listOf(
            StudentModel("Nguyễn Văn An", "SV001"),
            StudentModel("Trần Thị Bảo", "SV002"),
            // Add more students here
        ))

        studentAdapter = StudentAdapter(students, ::onEditStudent, ::onDeleteStudent)

        findViewById<RecyclerView>(R.id.recycler_view_students).run {
            adapter = studentAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        findViewById<Button>(R.id.btn_add_new).setOnClickListener {
            showStudentDialog(null) // Open dialog to add a new student
        }
    }

    private fun showStudentDialog(student: StudentModel?) {
        // Inflate custom dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_student, null)
        val editName = dialogView.findViewById<EditText>(R.id.edit_student_name)
        val editId = dialogView.findViewById<EditText>(R.id.edit_student_id)

        if (student != null) {
            editName.setText(student.studentName)
            editId.setText(student.studentId)
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val name = editName.text.toString()
                val id = editId.text.toString()

                if (student == null) {
                    // Adding a new student
                    students.add(StudentModel(name, id))
                } else {
                    // Editing an existing student
                    student.studentName = name
                    student.studentId = id
                }
                studentAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun onEditStudent(student: StudentModel) {
        showStudentDialog(student)
    }

    private fun onDeleteStudent(position: Int) {
        // Confirm before deleting
        AlertDialog.Builder(this)
            .setTitle("Delete Student")
            .setMessage("Are you sure you want to delete this student?")
            .setPositiveButton("Yes") { _, _ ->
                // Remove student and show Snackbar
                recentlyDeletedStudent = students[position]
                recentlyDeletedPosition = position
                students.removeAt(position)
                studentAdapter.notifyItemRemoved(position)

                Snackbar.make(findViewById(R.id.main), "Student deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        // Restore the deleted student
                        recentlyDeletedStudent?.let {
                            students.add(recentlyDeletedPosition, it)
                            studentAdapter.notifyItemInserted(recentlyDeletedPosition)
                        }
                    }.show()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
