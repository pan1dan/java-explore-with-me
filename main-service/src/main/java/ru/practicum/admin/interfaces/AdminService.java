package ru.practicum.admin.interfaces;

import ru.practicum.category.model.CategoryDto;
import ru.practicum.category.model.NewCategoryDto;
import ru.practicum.compilation.model.CompilationDto;
import ru.practicum.compilation.model.NewCompilationDto;
import ru.practicum.compilation.model.UpdateCompilationRequest;
import ru.practicum.event.model.EventFullDto;
import ru.practicum.event.model.EventShortDto;
import ru.practicum.event.model.UpdateEventAdminRequest;
import ru.practicum.user.model.NewUserRequest;
import ru.practicum.user.model.UserDto;

import java.util.List;

public interface AdminService {

    CategoryDto addNewCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(Long catId);

    CategoryDto updateCategoryById(Long catId, CategoryDto updateCategory);

    List<EventShortDto> searchEventByCondition(List<Long> usersIds,
                                               List<String> eventsStates,
                                               List<Long> categoriesIds,
                                               String startDate,
                                               String endDate,
                                               Integer from,
                                               Integer size);

    EventFullDto editingEventById(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<UserDto> getUsersInformationByCondition(List<Long> usersIds, Integer from, Integer size);

    UserDto addNewUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    CompilationDto addNewCompilations(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilation);

}
