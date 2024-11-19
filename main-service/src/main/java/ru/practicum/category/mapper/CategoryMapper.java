package ru.practicum.category.mapper;

import ru.practicum.category.model.Category;
import ru.practicum.category.model.CategoryDto;
import ru.practicum.category.model.NewCategoryDto;

public class CategoryMapper {

    public static CategoryDto fromNewCategoryDtoToCategoryDto(NewCategoryDto newCategoryDto) {
        return CategoryDto.builder()
                .id(null)
                .name(newCategoryDto.getName())
                .build();
    }

    public static NewCategoryDto fromCategoryDtoToNewCategoryDto(CategoryDto categoryDto) {
        return NewCategoryDto.builder()
                .name(categoryDto.getName())
                .build();
    }

    public static CategoryDto fromCategoryToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category fromCategoryDtoToCategory(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public static Category fromNewCategoryDtoToCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .id(null)
                .name(newCategoryDto.getName())
                .build();
    }
}
