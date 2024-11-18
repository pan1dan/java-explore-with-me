package ru.practicum.category.mapper;

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

}
