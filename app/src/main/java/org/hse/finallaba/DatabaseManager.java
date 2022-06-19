package org.hse.finallaba;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.hse.finallaba.entities.TeamEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class DatabaseManager {

    private DatabaseHelper db;

    private static DatabaseManager instance;

    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseManager(Context context) {
        db = Room.databaseBuilder(context,
                DatabaseHelper.class, DatabaseHelper.DATABASE_NAME)
                .allowMainThreadQueries()
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                initData(context);
                            }
                        });
                    }
                }).build();
    }

    public MyDao getMyDao() {return db.myDao();}

    private void initData(Context context) {
        List<TeamEntity> teams = new ArrayList<>();
        TeamEntity team = new TeamEntity();
        team.id = 1;
        team.name = "Modeling";
        teams.add(team);
        team = new TeamEntity();
        team.id = 2;
        team.name = "Stars";
        teams.add(team);
        team = new TeamEntity();
        team.id = 3;
        team.name = "Losers";
        teams.add(team);
        DatabaseManager.getInstance(context).getMyDao().insertTeam(teams);
    }
}
