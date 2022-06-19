package org.hse.finallaba.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

public class TaskWithHumanEntity {

    @Embedded
    public TaskEntity taskEntity;
    @Relation(
            parentColumn = "human_id",
            entityColumn = "id"
    )
    public HumanEntity humanEntity;
}
