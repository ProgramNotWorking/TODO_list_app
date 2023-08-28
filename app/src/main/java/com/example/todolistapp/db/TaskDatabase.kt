package com.example.todolistapp.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
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

    companion object {
        private const val TABLE_NAME = "tasks"
        private const val COLUMN_TASK = "task"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_DATE = "date"
    }

}