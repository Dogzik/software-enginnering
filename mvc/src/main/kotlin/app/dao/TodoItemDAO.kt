package app.dao

import app.model.TodoItem
import app.model.TodoItemDTO

interface TodoItemDAO {
    fun getAllItems(): List<TodoItem>
    fun addItem(newItem: TodoItemDTO)
    fun removeItem(id: Long)
    fun setItemStatus(id: Long, status: Boolean)
}