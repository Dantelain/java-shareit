package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available; //статус о том, доступна или нет вещь для аренды
    private Long requestId; //ссылка на запрос другого пользователя по которому была создана вещь

}
