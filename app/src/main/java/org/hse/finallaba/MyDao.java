package org.hse.finallaba;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import org.hse.finallaba.entities.HumanEntity;
import org.hse.finallaba.entities.TaskEntity;
import org.hse.finallaba.entities.TaskWithHumanEntity;
import org.hse.finallaba.entities.TeamEntity;

import java.util.List;

@Dao
public interface MyDao {

    @Query("SELECT * FROM `team`")
    List<TeamEntity> getAllTeams();

    @Query("SELECT * FROM `human` WHERE team_id LIKE :team_id")
    List<HumanEntity> getHumans(int team_id);

    @Insert
    void insertTeam (List<TeamEntity> data);

    @Insert
    void insertHuman (HumanEntity data);

    @Insert
    void insertTask (TaskEntity data);

    @Query("SELECT * FROM `human` WHERE login LIKE :login AND password LIKE :password")
    List<HumanEntity> login(String login, String password);

    @Query("SELECT * FROM `human` WHERE id LIKE :id")
    List<HumanEntity> getHuman(int id);

    @Query("UPDATE `human` SET name = :name, surname = :surname WHERE id like :id")
    void updateHuman(String name, String surname, int id);

    @Query("UPDATE `task` SET name = :name, description = :description, status = :status, numberOfHours = :numberHours, human_id = :human WHERE name like :name")
    void updateTask(String name, String description, String status, int numberHours, int human);

    @Query("SELECT * FROM `task` WHERE team_id LIKE :team_id")
    LiveData<List<TaskWithHumanEntity>> getTasks(int team_id);
}
