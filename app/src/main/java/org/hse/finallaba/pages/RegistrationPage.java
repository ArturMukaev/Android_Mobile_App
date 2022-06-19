package org.hse.finallaba.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.hse.finallaba.MainViewModel;
import org.hse.finallaba.R;
import org.hse.finallaba.entities.HumanEntity;
import org.hse.finallaba.entities.TeamEntity;

import java.util.ArrayList;
import java.util.List;

public class RegistrationPage extends AppCompatActivity {
    // Элементы с экрана
    private Button enter;
    private EditText inputLogin;
    private EditText inputPassword;
    private Spinner spinner;


    private ArrayAdapter<Team> adapter;
    protected MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        setContentView(R.layout.registration);
        inputLogin = findViewById(R.id.reg_log);
        inputPassword = findViewById(R.id.reg_pass);
        enter = findViewById(R.id.btn_reg);
        spinner = findViewById(R.id.teamList);

        List<Team> teams = new ArrayList<>();
        initTeamList(teams);


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teams);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        enter.setOnClickListener(v -> register());
    }

    // Функция для регистрации
    private void register() {
        String loginText = inputLogin.getText().toString();
        String loginPassword = inputPassword.getText().toString();
        Object selectedTeam = spinner.getSelectedItem();
        if (loginText.trim().isEmpty() || loginPassword.trim().isEmpty() || selectedTeam == null) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Заполните все поля!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        Team myTeam = (Team) selectedTeam;
        HumanEntity human = new HumanEntity();
        human.login = loginText;
        human.password = loginPassword;
        human.teamId = myTeam.getId();
        mainViewModel.insertHuman(human);
        Toast toast = Toast.makeText(getApplicationContext(),
                "Регистрация успешна!", Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

    private void initTeamList(final List<Team> teams) {
        List<TeamEntity> teamList = mainViewModel.getAllTeams();
        for (TeamEntity listEntity : teamList) {
            teams.add(new Team(listEntity.id, listEntity.name));
        }
    }



    static class Team {
        private Integer id;
        private String name;

        public Team(Integer id, String name) {
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
