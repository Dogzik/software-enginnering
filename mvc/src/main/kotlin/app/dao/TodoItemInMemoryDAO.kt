package app.dao

import app.model.TodoItem
import app.model.TodoItemDTO
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Component
class TodoItemInMemoryDAO : TodoItemDAO {
    private val maxId = AtomicLong(0)
    private val storage = ConcurrentHashMap<Long, TodoItem>()

    override fun getAllItems() = storage.elements().toList()

    override fun addItem(newItem: TodoItemDTO) {
        val newId = maxId.incrementAndGet()
        storage[newId] = TodoItem(newId, newItem.name, newItem.description, false)
    }

    override fun removeItem(id: Long) {
        storage.remove(id)
    }

    override fun updateItem(id: Long, mapper: (TodoItem) -> TodoItem) {
        storage.compute(id) { _, value -> value?.let(mapper) }
    }
}