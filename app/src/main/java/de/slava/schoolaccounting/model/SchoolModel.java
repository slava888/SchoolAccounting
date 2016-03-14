package de.slava.schoolaccounting.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import lombok.EqualsAndHashCode;
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
    private final Set<Scholar> scholars = new HashSet<>();

    @Getter @Setter
    private Room defaultRoom;

}
