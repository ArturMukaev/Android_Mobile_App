package org.hse.finallaba.pages;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import org.hse.finallaba.MainViewModel;
import org.hse.finallaba.R;
import org.hse.finallaba.entities.HumanEntity;
import org.hse.finallaba.entities.TaskEntity;
import org.hse.finallaba.entities.TeamEntity;

import java.util.ArrayList;
import java.util.List;

public class TaskPage extends AppCompatActivity {
    public static final String ARG_EDIT = "edit";
    public static final String ARG_TASK = "task";
    public static final Integer DEFAULT_ID = -1;
    private Integer teamId;
    private Integer editMode;
    private String taskReceived;

    private Button enter;
    private EditText inputName;
    private EditText inputDescription;
    private EditText numberOfHours;
    private Spinner spinner;
    private Spinner spinner1;
    protected MainViewModel mainViewModel;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<Human> adapter1;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        setContentView(R.layout.edit_task);

        List<String> statuses = new ArrayList<>();
        statuses.add("Proposed");
        statuses.add("Active");
        statuses.add("Resolved");
        statuses.add("Closed");

        teamId = getIntent().getIntExtra(MainPage.ARG_TEAM, DEFAULT_ID);
        editMode = getIntent().getIntExtra(ARG_EDIT, DEFAULT_ID);
        taskReceived = getIntent().getStringExtra(ARG_TASK);
        enter = findViewById(R.id.btn_reg2);
        inputName = findViewById(R.id.taskname);
        inputDescription = findViewById(R.id.decribtion);
        numberOfHours = findViewById(R.id.hoursEnter);
        spinner = findViewById(R.id.responsible_select);
        spinner1 = findViewById(R.id.status_add);

        List<Human> humans = new ArrayList<>();
        initHumanList(humans);

        adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, humans);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);

        enter.setOnClickListener(v -> editTask());
    }

    private void initHumanList(final List<Human> humans) {
        List<HumanEntity> humanList = mainViewModel.getHumans(teamId);
        for (HumanEntity listEntity : humanList) {
            humans.add(new Human(listEntity.id, listEntity.login));
        }
    }

    // Функция для редактирования задачи
    private void editTask() {
        String nameText = inputName.getText().toString();
        String descriptionText = inputDescription.getText().toString();
        Object selectedHuman = spinner.getSelectedItem();
        Object selectedStatus = spinner1.getSelectedItem();

        if (nameText.trim().isEmpty()
                || descriptionText.trim().isEmpty()
                || selectedHuman == null || selectedStatus == null
                || numberOfHours.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Заполните все поля!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        Integer numberHours = Integer.parseInt(numberOfHours.getText().toString());
        Human myHuman = (Human) selectedHuman;
        String myStatus = (String) selectedStatus;

        if (editMode == 1) {
            mainViewModel.updateTask(nameText, descriptionText, myStatus, numberHours, myHuman.getId());
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Задача обновлена!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        TaskEntity task = new TaskEntity();
        task.name = nameText;
        task.description = descriptionText;
        task.humanId = myHuman.getId();
        task.status = myStatus;
        task.numberOfHours = numberHours;
        task.teamId = teamId;
        mainViewModel.insertTask(task);
        Toast toast = Toast.makeText(getApplicationContext(),
                "Задача добавлена!", Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(this, MainPage.class);
        startActivity(intent);
    }

    static class Human {
        private Integer id;
        private String name;

        public Human(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
