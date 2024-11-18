package ru.practicum.category.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.category.model.CategoryDto;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryDto, Long> {

    @Query("SELECT c " +
           "FROM CategoryDto c")
    List<CategoryDto> findAllCategories(Pageable pageable);

}
