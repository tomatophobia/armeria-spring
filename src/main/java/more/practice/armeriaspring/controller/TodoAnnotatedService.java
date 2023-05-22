package more.practice.armeriaspring.controller;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@PathPrefix("/todos")
@ConsumesJson
public class TodoAnnotatedService {

    private static final Logger logger = LoggerFactory.getLogger(TodoAnnotatedService.class);
    private final Map<Long, Todo> todos = new ConcurrentHashMap<>();

    @Get("/:id")
    public HttpResponse get(@Param Long id) {
        return HttpResponse.ofJson(todos.get(id));
    }

    @Get
    @ProducesJson
    public List<Todo> getAll() {
        return todos.values().stream().toList();
    }

    @Post
    @ProducesJson
    public Todo create(Todo todo) {
        logger.debug(todo.toString());
        todos.put(todo.getId(), todo);
        return todo;
    }

    @Put("/:id")
    @ProducesJson
    public Todo update(@Param Long id, Todo todo) {
        if (id != todo.getId()) {
            throw new IllegalArgumentException("The id of post is not same with given id in the path parameter.");
        }
        todos.put(id, todo);
        return todo;
    }

    @Delete("/:id")
    @ProducesJson
    public Todo delete(@Param Long id) {
        if (!todos.containsKey(id)) {
            throw new IllegalArgumentException("Id is not exist.");
        }
        return todos.remove(id);
    }
}
