package app.service

import app.dao.TodoItemDAO
import app.model.TodoItemDTO
import org.springframework.stereotype.Service

@Service
class TodoItemService(private val dao: TodoItemDAO) {
    fun getList() = dao.getAllItems()
    fun addItem(item: TodoItemDTO) = dao.addItem(item)
    fun removeItem(id: Long) = dao.removeItem(id)
    fun setItemStatus(id: Long, status: Boolean) = dao.setItemStatus(id, status)
}