package root.dev.vaadintable.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import root.dev.vaadintable.services.FileService;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping()
    public void create() {}

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable String id) {
        return fileService.delete(id);
    }

}
