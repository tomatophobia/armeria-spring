package more.practice.armeriaspring.controller;

import more.practice.armeriaspring.model.BlogPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);
    private final Map<Long, BlogPost> blogPosts = new ConcurrentHashMap<>();

    @GetMapping("/{id}")
    public BlogPost get(@PathVariable Long id) {
        logger.debug(blogPosts.get(id).toString());
        return blogPosts.get(id);
    }

    @GetMapping
    public List<BlogPost> getAll() {
        return blogPosts.values().stream().toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BlogPost create(@RequestBody BlogPost post) {
        logger.debug(post.toString());
        blogPosts.put(post.getId(), post);
        return post;
    }

    @PutMapping("/{id}")
    public BlogPost update(@PathVariable Long id, @RequestBody BlogPost post) {
        if (id != post.getId()) {
            throw new IllegalArgumentException("The id of post is not same with given id in the path parameter.");
        }
        blogPosts.put(id, post);
        return post;
    }

    @DeleteMapping("/{id}")
    public BlogPost delete(@PathVariable Long id) {
        if (!blogPosts.containsKey(id)) {
            throw new IllegalArgumentException("Id is not exist.");
        }
        return blogPosts.remove(id);
    }

}
