package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тест-класс для контроллера ItemController.
 * Проверяет обработку HTTP‑запросов для работы с предметами (items):
 * - создание, обновление, получение;
 * - поиск по владельцу и по тексту;
 * - добавление комментариев.
 */
@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper; // Для сериализации/десериализации JSON

    @MockBean
    ItemService itemService; // Мок сервиса для изоляции тестов


    @Autowired
    MockMvc mvc; // Эмулирует HTTP‑запросы к контроллеру


    private RequestItemDto requestItemDto; // DTO для создания/обновления предмета
    private ResponseItemDto responseItemDto; // DTO для ответа


    /**
     * Подготовка тестовых данных перед каждым тестом.
     */
    @BeforeEach
    public void setup() {
        requestItemDto = new RequestItemDto(
                "testName",
                "testDescription",
                true,
                null
        );

        responseItemDto = new ResponseItemDto(
                1L,
                requestItemDto.getName(),
                requestItemDto.getDescription(),
                requestItemDto.getAvailable(),
                null,
                null,
                null
        );
    }

    /**
     * Тест создания предмета (POST /items).
     * Проверяет:
     * - статус 200 OK;
     * - возврат корректного DTO с ID, именем и описанием.
     */
    @Test
    public void createTest() throws Exception {
        when(itemService.create(any(), anyLong()))
                .thenReturn(responseItemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(requestItemDto))
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(responseItemDto.getName())))
                .andExpect(jsonPath("$.description", is(responseItemDto.getDescription())));
    }

    /**
     * Тест обновления предмета (PATCH /items/{id}).
     * Проверяет:
     * - статус 2 prepared OK;
     * - возврат обновлённого DTO.
     */
    @Test
    public void updateTest() throws Exception {
        when(itemService.update(anyLong(), any(), anyLong()))
                .thenReturn(responseItemDto);

        mvc.perform(patch("/items/{id}", 1L)
                        .content(mapper.writeValueAsString(requestItemDto))
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(responseItemDto.getName())))
                .andExpect(jsonPath("$.description", is(responseItemDto.getDescription())));
    }

    /**
     * Тест получения предмета по ID (GET /items/{id}).
     * Проверяет:
     * - статус 200 OK;
     * - возврат DTO с корректными полями.
     */
    @Test
    public void findByIdTest() throws Exception {
        when(itemService.findById(anyLong())).thenReturn(responseItemDto);


        mvc.perform(get("/items/{id}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(responseItemDto.getName())))
                .andExpect(jsonPath("$.description", is(responseItemDto.getDescription())));
    }

    /**
     * Тест получения всех предметов владельца (GET /items).
     * Проверяет:
     * - статус 200 OK;
     * - возврат списка из 1 элемента с корректными полями.
     */
    @Test
    public void findAllByOwnerTest() throws Exception {
        OwnerItemDto ownerItemDto = getOwnerItemDto();

        when(itemService.findAllByOwner(anyLong())).thenReturn(List.of(ownerItemDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].id", is(ownerItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(ownerItemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(ownerItemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(ownerItemDto.getAvailable())));
    }

    /**
     * Тест поиска предметов по тексту (GET /items/search).
     * Проверяет:
     * - статус 200 OK;
     * - возврат списка из 1 элемента.
     */
    @Test
    public void findBySearchTest() throws Exception {
        when(itemService.findBySearch(anyString())).thenReturn(List.of(responseItemDto));

        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    /**
     * Тест добавления комментария к предмету (POST /items/{id}/comment).
     * Проверяет:
     * - статус 200 OK.
     */
    @Test
    public void postCommentTest() throws Exception {
        CreateCommentDto createDto = new CreateCommentDto();
        createDto.setText("text");


        when(itemService.postComment(any(), anyLong(), anyLong())).thenReturn(null);

        mvc.perform(post("/items/{id}/comment", 1L)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Тест обработки ошибки при обновлении предмета (некорректные условия).
     * Проверяет:
     * - статус 400 Bad Request;
     * - возврат JSON с описанием ошибки.
     */
    @Test
    public void handleBadRequest() throws Exception {
        when(itemService.update(anyLong(), any(), anyLong()))
                .thenThrow(new ConditionsNotMetException("error"));

        mvc.perform(patch("/items/{id}", 1L)
                        .content(mapper.writeValueAsString(requestItemDto))
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Некорректный ввод")))
                .andExpect(jsonPath("$.description", is("error")));
    }

    /**
     * Вспомогательный метод для создания OwnerItemDto с тестовыми датами.
     *
     * @return экземпляр OwnerItemDto
     */
    private OwnerItemDto getOwnerItemDto() {
        LocalDateTime lastStart = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime lastEnd = LocalDateTime.of(2025, 1, 2, 0, 0, 0);
        LocalDateTime nextStart = LocalDateTime.of(2025, 1, 3, 0, 0, 0);
        LocalDateTime nextEnd = LocalDateTime.of(2025, 1, 4, 0, 0, 0);

        return new OwnerItemDto(
                1L,
                responseItemDto.getName(),
                responseItemDto.getDescription(),
                responseItemDto.getAvailable(),
                lastStart,
                lastEnd,
                nextStart,
                nextEnd
        );
    }
}

