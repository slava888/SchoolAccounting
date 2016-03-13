package de.slava.schoolaccounting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author by V.Sysoltsev
 */
@Data @Accessors(chain=true)
@AllArgsConstructor(suppressConstructorProperties=true)
public class Scholar {
    private Integer id;
    private String nameFull;
    private Room room;

    public Scholar() {
    }
}
