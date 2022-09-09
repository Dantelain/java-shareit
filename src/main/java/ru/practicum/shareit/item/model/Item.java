package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {

    Long id;
    String name;
    String description;
    Boolean available; //статус о том, доступна или нет вещь для аренды
    User owner;
    Long request; //ссылка на запрос другого пользователя по которому была создана вещь

}
