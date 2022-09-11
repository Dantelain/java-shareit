package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class Item {

    private Long id;
    private String name;
    private String description;
    private Boolean available; //статус о том, доступна или нет вещь для аренды
    private User owner;
    private Long request; //ссылка на запрос другого пользователя по которому была создана вещь

}
