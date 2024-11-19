package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.interfaces.CategoryService;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.CategoryDto;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        if (from < 0) {
            throw new BadRequestException("Request parameter from must be greater than 0, now from=" + from);
        }
        if (size < 0) {
            throw new BadRequestException("Request parameter 'size' must be greater than 0, now size=" + size);
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<Category> categotyDtoList = categoryRepository.findAllCategories(pageable);
        return categotyDtoList.stream().map(CategoryMapper::fromCategoryToCategoryDto).toList();
    }

    @Override
    public CategoryDto getCategoriesById(Long catId) {
        idValidation(catId, "catId");
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id=" + catId + " was not found"));
        return CategoryMapper.fromCategoryToCategoryDto(category);
    }

    private void idValidation(Long id, String fieldName) {
        if (id < 0) {
            throw new BadRequestException("Field " + fieldName + " must be greater than 0, " +
                    "now " + fieldName + "=" + id);
        }
    }

}
