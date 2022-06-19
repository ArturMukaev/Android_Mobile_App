package org.hse.finallaba.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(tableName = "human", indices = {@Index(value = {"login"}, unique = true)} , foreignKeys = {
        @ForeignKey(entity = TeamEntity.class, parentColumns = "id", childColumns = "team_id", onDelete = CASCADE)
})
public class HumanEntity {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "login")
    @NonNull
    public String login = "";

    @ColumnInfo(name = "password")
    @NonNull
    public String password = "";

    @ColumnInfo(name = "name")
    public String name = "";

    @ColumnInfo(name = "surname")
    public String surname = "";

    @ColumnInfo(name = "team_id", index = true)
    public int teamId;
}