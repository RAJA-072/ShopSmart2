package com.example.shopsmart

import android.content.Context
import android.database.Cursor

class DatabaseHelperLogin(context: Context) {

    private val dbHelper: DBHelper = DBHelper(context)

    fun checkUser(email: String, password: String): Boolean {
        try {
            val db = dbHelper.readableDatabase
            val query = "SELECT * FROM users WHERE email = ? AND password = ?"
            val cursor: Cursor = db.rawQuery(query, arrayOf(email, password))

            val userExists = cursor.count > 0
            cursor.close()
            db.close()
            return userExists
        } catch (e: Exception) {
            // Log or handle exceptions
            e.printStackTrace()
            return false
        }
    }

    fun registerUser(email: String, password: String): Boolean {

        val db = dbHelper.writableDatabase
        val query = "INSERT INTO users (email, password) VALUES (?, ?)"
        val statement = db.compileStatement(query)


        statement.bindString(1, email)
        statement.bindString(2, password)

        return try {
            statement.executeInsert()
            db.close()
            true
        } catch (e: Exception) {
            db.close()
            false
        }
    }

}
