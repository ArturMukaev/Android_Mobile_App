package org.hse.finallaba.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(tableName = "task", indices = {@Index(value = {"name"}, unique = true)} , foreignKeys = {
        @ForeignKey(entity = HumanEntity.class, parentColumns = "id", childColumns = "human_id", onDelete = CASCADE),
        @ForeignKey(entity = TeamEntity.class, parentColumns = "id", childColumns = "team_id", onDelete = CASCADE),
})
public class TaskEntity {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "name")
    @NonNull
    public String name = "";

    @ColumnInfo(name = "description")
    public String description = "";

    @ColumnInfo(name = "status")
    public String status = "Proposed";

    @ColumnInfo(name = "numberOfHours")
    public int numberOfHours = 0;

    @ColumnInfo(name = "human_id", index = true)
    public int humanId;

    @ColumnInfo(name = "team_id", index = true)
    public int teamId;
}
