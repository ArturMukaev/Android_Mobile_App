package org.hse.finallaba;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import org.hse.finallaba.entities.HumanEntity;
import org.hse.finallaba.entities.TaskEntity;
import org.hse.finallaba.entities.TeamEntity;


@Database(entities = {TeamEntity.class, HumanEntity.class, TaskEntity.class},
        version = 1,
        exportSchema = false
)
public abstract class DatabaseHelper extends RoomDatabase {

    public static final String DATABASE_NAME = "app_for_teams";

    public abstract MyDao myDao();
}
