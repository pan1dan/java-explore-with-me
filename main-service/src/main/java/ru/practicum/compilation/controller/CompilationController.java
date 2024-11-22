package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.interfaces.CompilationService;
import ru.practicum.compilation.model.CompilationDto;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /compilations : pinned={}, from={}, size={}", pinned, from, size);
        List<CompilationDto> returnableList = compilationService.getCompilations(pinned, from, size);
        log.info("GET /compilations : pinned={}, from={}, size={} \n return: {}", pinned, from, size, returnableList);
        return returnableList;
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(@PathVariable(name = "compId") Long compId) {
        log.info("GET /compilations/{}", compId);
        CompilationDto compilationDto = compilationService.getCompilationById(compId);
        log.info("GET /compilations/{} \n return: {}", compId, compilationDto);
        return compilationDto;
    }

}
