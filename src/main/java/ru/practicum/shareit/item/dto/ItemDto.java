package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {

    Long id;
    //@NotBlank
    String name;
    //@NotBlank
    String description;
    //@NonNull
    Boolean available; //статус о том, доступна или нет вещь для аренды
    Long request; //ссылка на запрос другого пользователя по которому была создана вещь

}
