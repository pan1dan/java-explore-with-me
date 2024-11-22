package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.interfaces.CategoryService;
import ru.practicum.category.model.CategoryDto;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /categories, from={}, size={}", from, size);
        List<CategoryDto> categoryDtoList = categoryService.getCategories(from, size);
        log.info("GET /categories, from={}, size={}\n return: {}", from, size, categoryDtoList);
        return categoryDtoList;
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoriesById(@PathVariable(name = "catId") Long catId) {
        log.info("GET /categories/{}", catId);
        CategoryDto categoryDto = categoryService.getCategoriesById(catId);
        log.info("GET /categories/{}\n return: {}", catId, categoryDto);
        return categoryDto;
    }

}
