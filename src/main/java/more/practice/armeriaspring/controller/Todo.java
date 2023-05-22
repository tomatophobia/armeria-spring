package more.practice.armeriaspring.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.linecorp.armeria.common.annotation.Nullable;

public class Todo {
    private final long id;
    private final String value;
    @Nullable
    private final Long checkedAt;
    private final long createdAt;
    private final long updatedAt;

    @JsonCreator
    public Todo(@JsonProperty("id") long id, @JsonProperty("value") String value) {
        this(id, value, System.currentTimeMillis());
    }

    public Todo(long id, String value, long createdAt) {
        this(id, value, createdAt, createdAt);
    }

    public Todo(long id, String value, long createdAt, long updatedAt) {
        this.id = id;
        this.value = value;
        checkedAt = null;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public @Nullable Long getCheckedAt() {
        return checkedAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", checkedAt=" + checkedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
