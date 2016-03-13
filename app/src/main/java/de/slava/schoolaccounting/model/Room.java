package de.slava.schoolaccounting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author by V.Sysoltsev
 */
@AllArgsConstructor(suppressConstructorProperties=true)
@Data
@Accessors(chain=true)
public class Room {
    private Integer id;
    private String name;

    public Room() {
    }
}
