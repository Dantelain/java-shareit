package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available; //статус о том, доступна или нет вещь для аренды
    private Long request; //ссылка на запрос другого пользователя по которому была создана вещь

}
