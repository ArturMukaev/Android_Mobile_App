package org.hse.finallaba;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.hse.finallaba.entities.HumanEntity;
import org.hse.finallaba.entities.TaskEntity;
import org.hse.finallaba.entities.TaskWithHumanEntity;
import org.hse.finallaba.entities.TeamEntity;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final MyRepository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new MyRepository(application);
    }

    private MutableLiveData<String> timeText;

    public MutableLiveData<String> getCurrentTime() {
        if (timeText == null) {
            timeText = new MutableLiveData<String>();
        }
        return timeText;
    }

    public List<TeamEntity> getAllTeams() { return repository.getTeams(); }

    public List<HumanEntity> login(String login, String password) { return repository.login(login,password); }

    public void insertHuman(HumanEntity data) {repository.insertHuman(data);}

    public void insertTask(TaskEntity data) {repository.insertTask(data);}

    public LiveData<List<TaskWithHumanEntity>> getTasks(int team_id) {return repository.getTasks(team_id);}

    public List<HumanEntity> getHuman(int id) {return repository.getHuman(id);}

    public void updateHuman(String name, String surname, int id) {repository.updateHuman(name, surname, id);}

    public void updateTask(String name, String description, String status, int numberHours, int human)
    { repository.updateTask(name, description, status, numberHours, human);}

    public List<HumanEntity> getHumans(int team_id) {return repository.getHumans(team_id);}

}
