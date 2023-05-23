package more.practice.armeriaspring.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BlogPost {
    private final long id;
    private final String title;
    private final String content;
    private final long createdAt;
    private final long updatedAt;

    @JsonCreator
    public BlogPost(@JsonProperty("id") long id, @JsonProperty("title") String title, @JsonProperty("content") String content) {
        this(id, title, content, System.currentTimeMillis());
    }

    public BlogPost(long id, String title, String content, long createdAt) {
        this(id, title, content, createdAt, createdAt);
    }

    public BlogPost(long id, String title, String content, long createdAt, long updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "BlogPost{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
