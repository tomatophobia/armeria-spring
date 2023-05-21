package more.practice.armeriaspring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/blogs")
public class BlogService {

    private final Map<Long, BlogPost> blogPosts = new ConcurrentHashMap<>();

    @GetMapping("/{id}")
    public BlogPost getPost(@PathVariable Long id) {
        return blogPosts.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestBody BlogPost post) {
        blogPosts.put(post.getId(), post);
    }

}
