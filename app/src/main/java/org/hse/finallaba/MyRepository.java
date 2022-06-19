package org.hse.finallaba;

import android.content.Context;

import androidx.lifecycle.LiveData;

import org.hse.finallaba.entities.HumanEntity;
import org.hse.finallaba.entities.TaskEntity;
import org.hse.finallaba.entities.TaskWithHumanEntity;
import org.hse.finallaba.entities.TeamEntity;

import java.util.List;

public class MyRepository {

    private DatabaseManager dataBaseManager;
    private MyDao dao;

    public MyRepository(Context context) {
        dataBaseManager = DatabaseManager.getInstance(context);
        dao = dataBaseManager.getMyDao();
    }

    public List<TeamEntity> getTeams() {return dao.getAllTeams();}

    public List<HumanEntity> login(String login, String password) {return dao.login(login, password);}

    public List<HumanEntity> getHuman(int id) {return dao.getHuman(id);}

    public void updateHuman(String name, String surname, int id) { dao.updateHuman(name, surname, id);}

    public void updateTask(String name, String description, String status, int numberHours, int human)
    { dao.updateTask(name, description, status, numberHours, human);}

    public void insertHuman(HumanEntity data) {dao.insertHuman(data);}

    public void insertTask(TaskEntity data) {dao.insertTask(data);}

    public LiveData<List<TaskWithHumanEntity>> getTasks(int team_id) {return dao.getTasks(team_id);}

    public List<HumanEntity> getHumans(int team_id) {return dao.getHumans(team_id);}
}
