package more.practice.armeriaspring.service;

import com.linecorp.armeria.common.annotation.Nullable;
import more.practice.armeriaspring.model.Todo;
import more.practice.armeriaspring.persistence.SpringJdbcTemplateMySqlTodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {

    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);

    private final SpringJdbcTemplateMySqlTodoRepository todoRepository;

    @Autowired
    public TodoService(SpringJdbcTemplateMySqlTodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Nullable
    public Todo get(Integer id) {
        return todoRepository.get(id);
    }

    public List<Todo> getAll() {
        return todoRepository.getAll();
    }

    public int create(Todo todo) {
        return todoRepository.create(todo);
    }

    public int update(Integer id, Todo todo) {
        return todoRepository.replaceOrCreate(todo);
    }

    public int delete(Integer id) {
        return todoRepository.delete(id);
    }
}
