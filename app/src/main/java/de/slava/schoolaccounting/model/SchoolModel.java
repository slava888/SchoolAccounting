package de.slava.schoolaccounting.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author by V.Sysoltsev
 */
@ToString
public class SchoolModel extends BasicEntity{
    @Getter
    private final Set<Room> rooms = new HashSet<>();
    @Getter
    private final Set<Child> children = new HashSet<>();

    @Getter @Setter
    private Room defaultRoom;

}
