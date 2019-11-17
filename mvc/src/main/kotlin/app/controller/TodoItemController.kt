package app.controller

import app.model.TodoItem
import app.model.TodoItemDTO
import app.service.TodoItemService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class TodoItemController(private val service: TodoItemService) {
    private fun prepareModel(model: Model, items: List<TodoItem>) {
        model.addAttribute("todo_items", items)
        model.addAttribute("dto_item", TodoItemDTO())
    }

    @GetMapping("/todo-list")
    fun getTodoList(model: Model): String {
        prepareModel(model, service.getList())
        return "todo_list"
    }

    @PostMapping("/add-todo")
    fun addTodo(@ModelAttribute("dto_item") newItem: TodoItemDTO): String {
        if (newItem.name.isNotBlank() && newItem.description.isNotBlank()) {
            service.addItem(newItem)
        }
        return "redirect:/todo-list";
    }

    @PostMapping("/remove-todo")
    fun removeTodoList(@RequestParam("id") id: Long): String {
        println("ID == $id")
        service.removeItem(id)
        return "redirect:/todo-list"
    }

    @PostMapping("/change-todo-status")
    fun changeTodoStatus(@RequestParam("id") id: Long, @RequestParam("status") status: Boolean): String {
        service.setItemStatus(id, status)
        return "redirect:/todo-list"
    }
}