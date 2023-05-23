package more.practice.armeriaspring.controller;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.annotation.*;
import more.practice.armeriaspring.model.Todo;
import more.practice.armeriaspring.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@PathPrefix("/todos")
@ConsumesJson
public class TodoAnnotatedService {

    private static final Logger logger = LoggerFactory.getLogger(TodoAnnotatedService.class);
    private final TodoService todoService;

    @Autowired
    public TodoAnnotatedService(TodoService todoService) {
        this.todoService = todoService;
    }

    @Get("/:id")
    public HttpResponse get(@Param Integer id) {
        Todo todo = todoService.get(id);
        if (todo == null) {
            HttpResponse.of(HttpStatus.NO_CONTENT);
        }
        return HttpResponse.ofJson(todo);
    }

    @Get
    @ProducesJson
    public List<Todo> getAll() {
        return todoService.getAll();
    }

    @Post
    public HttpResponse create(Todo todo) {
        logger.debug(todo.toString());
        final int result = todoService.create(todo);
        if (result == 0) {
            return HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return HttpResponse.of(HttpStatus.CREATED);
    }

    @Put("/:id")
    public HttpResponse update(@Param Integer id, Todo todo) {
        if (id != todo.getId()) {
            throw new IllegalArgumentException("The id of post is not same with given id in the path parameter.");
        }
        final int result = todoService.update(id, todo);
        if (result == 0) {
            return HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return HttpResponse.of(HttpStatus.ACCEPTED);
    }

    @Delete("/:id")
    public HttpResponse delete(@Param Integer id) {
        final int result = todoService.delete(id);
        if (result == 0) {
            return HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return HttpResponse.of(HttpStatus.ACCEPTED);
    }
}
