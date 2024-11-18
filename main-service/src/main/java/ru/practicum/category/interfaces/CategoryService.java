package ru.practicum.category.interfaces;

import ru.practicum.category.model.CategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoriesById(Long catId);

}
