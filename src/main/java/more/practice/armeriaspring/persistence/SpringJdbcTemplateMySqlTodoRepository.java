package more.practice.armeriaspring.persistence;

import com.linecorp.armeria.common.annotation.Nullable;
import more.practice.armeriaspring.model.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SpringJdbcTemplateMySqlTodoRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private SimpleJdbcInsert insertTodo;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.insertTodo = new SimpleJdbcInsert(dataSource)
                .withTableName("todo")
                .usingGeneratedKeyColumns("id");
    }

    @Autowired
    public SpringJdbcTemplateMySqlTodoRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Nullable
    public Todo get(Integer id) {
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT id, value, checked_at, created_at, updated_at FROM todo WHERE id = :id",
                new MapSqlParameterSource("id", id),
                (resultSet, rowNum) -> new Todo(
                        resultSet.getInt("id"),
                        resultSet.getString("value"),
                        resultSet.getLong("checked_at"),
                        resultSet.getLong("created_at"),
                        resultSet.getLong("updated_at")
                )
        );
    }

    public List<Todo> getAll() {
        return namedParameterJdbcTemplate.query(
                "SELECT id, value, checked_at, created_at, updated_at FROM todo",
                (resultSet, rowNum) -> new Todo(
                        resultSet.getInt("id"),
                        resultSet.getString("value"),
                        resultSet.getLong("checked_at"),
                        resultSet.getLong("created_at"),
                        resultSet.getLong("updated_at")
                )
        );
    }

    public int create(Todo todo) {
        return namedParameterJdbcTemplate.update(
            "INSERT INTO todo(id, value, checked_at, created_at, updated_at) VALUES(:id, :value, :checkedAt, :createdAt, :updatedAt)",
                new BeanPropertySqlParameterSource(todo)
        );
    }

    public int delete(Integer id) {
        return namedParameterJdbcTemplate.update(
                "DELETE FROM todo WHERE id = :id",
                new MapSqlParameterSource("id", id)
        );
    }

    public int replaceOrCreate(Todo todo) {
        // https://dev.mysql.com/doc/refman/8.0/en/replace.html
        return namedParameterJdbcTemplate.update(
                "REPLACE INTO todo VALUES(:id, :value, :checkedAt, :createdAt, :updatedAt)",
                new BeanPropertySqlParameterSource(todo)
        );
    }
}
