package root.dev.vaadintable.controllers;


import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import root.dev.vaadintable.services.FileService;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/product_files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping()
    public void create() {
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable String id) {
        return fileService.delete(id);
    }

    @GetMapping("/original/{id}")
    public void getOriginalFileById(@PathVariable("id") UUID id, HttpServletResponse response) {
        fileService.getOriginalFileById(id, response);
    }

    @GetMapping("/compressed/{id}")
    public void getCompressedFileById(@PathVariable("id") UUID id, HttpServletResponse response) {
        fileService.getCompressedFileById(id, response);
    }

}
