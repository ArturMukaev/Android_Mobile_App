package org.hse.finallaba.pages;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.hse.finallaba.MainViewModel;
import org.hse.finallaba.R;
import org.hse.finallaba.entities.TaskWithHumanEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainPage extends AppCompatActivity {
    public static final String ARG_LOGIN = "login";
    public static final String ARG_TEAM = "team";
    public static final Integer DEFAULT_ID = -1;
    private Integer humanId;
    private Integer teamId;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private TextView time;
    private TextView noTasks;
    private ImageButton exit;
    private ImageButton profile;
    private ImageButton plus;
    protected MainViewModel mainViewModel;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        setContentView(R.layout.main_page);

        humanId = getIntent().getIntExtra(ARG_LOGIN, DEFAULT_ID);
        teamId = getIntent().getIntExtra(ARG_TEAM, DEFAULT_ID);
        exit = findViewById(R.id.exit);
        profile = findViewById(R.id.profile);
        plus = findViewById(R.id.plus);
        time = findViewById(R.id.timeMainPage);
        noTasks = findViewById(R.id.noTasks);
        recyclerView = findViewById(R.id.ListView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new ItemAdapter(this::onTaskItemClick);
        recyclerView.setAdapter(adapter);

        exit.setOnClickListener(v -> unRegister());
        profile.setOnClickListener(v -> showProfile());
        plus.setOnClickListener(v -> addTask());

        final Observer<String> timeObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newTime) {
                time.setText(newTime);
            }
        };

        mainViewModel.getCurrentTime().observe(this, timeObserver);
        filerItem();
    }

    private void unRegister() {
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

    private void showProfile() {
        Intent intent = new Intent(this, ProfilePage.class);
        intent.putExtra(ARG_LOGIN, humanId);
        intent.putExtra(ProfilePage.ARG_TIME, time.getText());
        startActivity(intent);
    }

    private void addTask() {
        Intent intent = new Intent(this, TaskPage.class);
        intent.putExtra(ARG_TEAM, teamId);
        intent.putExtra(TaskPage.ARG_EDIT, -1);
        startActivity(intent);
    }



    private void onTaskItemClick(TaskItem item) {
        Intent intent = new Intent(this, TaskPage.class);
        intent.putExtra(ARG_TEAM, teamId);
        intent.putExtra(TaskPage.ARG_EDIT, 1);
        intent.putExtra(TaskPage.ARG_TASK, item.name);
        startActivity(intent);
    }

    interface OnItemClick {
        void onClick(TaskItem data);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void filerItem() {
        mainViewModel.getTasks(teamId).observe(this, new Observer<List<TaskWithHumanEntity>>() {
            @Override
            public void onChanged(@Nullable List<TaskWithHumanEntity> taskEntities) {
                if (taskEntities.isEmpty()) {
                    adapter.setEmptyList();
                    return;
                }
                List<TaskItem> list = new ArrayList<>();
                final TaskItem[] taskItem = new TaskItem[1];
                for (TaskWithHumanEntity listEntity : taskEntities) {
                    taskItem[0] = new TaskItem();
                    taskItem[0].name = listEntity.taskEntity.name;
                    taskItem[0].description = listEntity.taskEntity.description;
                    taskItem[0].status = listEntity.taskEntity.status;
                    taskItem[0].numberOfHours = listEntity.taskEntity.numberOfHours;
                    taskItem[0].responsible = listEntity.humanEntity.login;
                    list.add(taskItem[0]);
                }
                adapter.clear();
                for (TaskItem task : list) {
                    adapter.add(task);
                }
            }
        });
    }



    public final class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<TaskItem> dataList = new ArrayList<>();
        private OnItemClick onItemClick;

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder viewHolder, int position) {
            TaskItem data = dataList.get(position);
            ((ViewHolder) viewHolder).bind(data);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public ItemAdapter(OnItemClick onItemClick) {
            this.onItemClick = onItemClick;
        }

        public void setEmptyList() {
            noTasks.setText("Задач нет!");
        }

        public void add(TaskItem item) {
            dataList.add(item);
        }

        public void clear() {
            dataList = new ArrayList<>();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private Context context;
            private OnItemClick onItemClick;
            private TextView name;
            private TextView description;
            private TextView status;
            private TextView hours;
            private TextView author;

            public ViewHolder(View itemView, Context context, OnItemClick onItemClick) {
                super(itemView);
                this.context = context;
                this.onItemClick = onItemClick;
                name = (TextView) itemView.findViewById(R.id.name);
                description = (TextView) itemView.findViewById(R.id.description);
                status = (TextView) itemView.findViewById(R.id.status);
                hours = (TextView) itemView.findViewById(R.id.hours);
                author = (TextView) itemView.findViewById(R.id.author);
            }

            public void bind(final TaskItem data) {
                name.setText(data.getName());
                description.setText(data.getDescription());
                status.setText(data.getStatus());
                hours.setText(String.valueOf(data.getNumberOfHours()));
                author.setText(data.getResponsible());
            }
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.task, parent, false);
            return new ViewHolder(contactView, context, onItemClick);
        }

    }

    private class TaskItem {
        private String name;
        private String description;
        private String status;
        private Integer numberOfHours;
        private String responsible;

        public String getName(){return name;}
        public void setName (String name){this.name=name;}
        public String getDescription(){return description;}
        public void setDescription (String description){this.description=description;}
        public String getStatus(){return status;}
        public void setStatus (String status){this.status=status;}
        public Integer getNumberOfHours(){return numberOfHours;}
        public void setNumberOfHours (Integer numberOfHours){this.numberOfHours=numberOfHours;}
        public String getResponsible(){return responsible;}
        public void setResponsible (String responsible){this.responsible=responsible;}
    }
}
