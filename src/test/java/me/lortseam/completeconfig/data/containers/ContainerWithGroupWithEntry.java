package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.data.groups.GroupWithEntry;

public class ContainerWithGroupWithEntry implements ConfigContainer {

    @Transitive
    private final GroupWithEntry group = new GroupWithEntry();

}
