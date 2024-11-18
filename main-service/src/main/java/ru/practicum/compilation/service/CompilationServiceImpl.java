package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.model.CompilationDto;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.compilation.interfaces.CompilationService;
import ru.practicum.event.model.EventShortDto;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        if (from < 0) {
            throw new BadRequestException("Request parameter from must be greater than 0, now from=" + from);
        }
        if (size < 0) {
            throw new BadRequestException("Request parameter 'size' must be greater than 0, now size=" + size);
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<CompilationDto> compilationDtoList = compilationRepository.findAllCompilations(pinned, pageable);

        if (!compilationDtoList.isEmpty()) {
            for (CompilationDto compilationDto : compilationDtoList) {
                List<EventShortDto> eventShortDtoList = eventRepository.findEventsByCompilationId(compilationDto.getId());
                compilationDto.setEvents(eventShortDtoList);
            }
        }
        return compilationDtoList;
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        if (compId < 0) {
            throw new BadRequestException("Compilation id must be greater than 0, now compId=" + compId);
        }

//        return compilationRepository.findCompilationById(compId)
//                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
//
//
        CompilationDto compilationDto = compilationRepository.findCompilationById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        List<EventShortDto> eventShortDtoList = eventRepository.findEventsByCompilationId(compId);
        compilationDto.setEvents(eventShortDtoList);
        return compilationDto;

    }

}
