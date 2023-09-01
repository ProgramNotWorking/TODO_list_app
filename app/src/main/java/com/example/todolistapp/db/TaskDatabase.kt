package com.example.todolistapp.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.todolistapp.dataclasses.Item

class TaskDatabase(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun insertTask(item: Item) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TASK, item.thing)
            put(COLUMN_TIME, item.time)
            put(COLUMN_DATE, item.date)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    private fun addItem(item: Item, db: SQLiteDatabase) {
        val values = ContentValues().apply {
            put(COLUMN_TASK, item.thing)
            put(COLUMN_TIME, item.time)
            put(COLUMN_DATE, item.date)
        }
        db.insert(TABLE_NAME, null, values)
    }

    @SuppressLint("Range")
    fun getData(): MutableList<Item> {
        val itemsList = mutableListOf<Item>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        cursor.use {
            while (it.moveToNext()) {
                val item = Item(
                    it.getString(it.getColumnIndex(COLUMN_TASK)),
                    it.getString(it.getColumnIndex(COLUMN_TIME)),
                    it.getString(it.getColumnIndex(COLUMN_DATE))
                )
                itemsList.add(item)
            }
        }
        db.close()
        return itemsList
    }

    fun repopulateTable(itemsList: MutableList<Item>?) {
        val db = dbHelper.writableDatabase

        db?.beginTransaction()
        try {
            db?.delete(TABLE_NAME, null, null)
            if (itemsList  != null) {
                for (item in itemsList) {
                    addItem(item, db)
                }
            }
            db?.setTransactionSuccessful()
        } finally {
            db?.endTransaction()
        }
        db.close()
    }

    fun deleteData() { // <- test fun just for clearing table (for now i guess)
        val db = dbHelper.writableDatabase
        db?.delete(TABLE_NAME, null , null)
    }

    fun deleteTask(item: Item) { // <- thing about it (or do smth like repopulation all table)
        val db = dbHelper.writableDatabase
    }

    companion object {
        private const val TABLE_NAME = "tasks"
        private const val COLUMN_TASK = "task"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_DATE = "date"
    }

}