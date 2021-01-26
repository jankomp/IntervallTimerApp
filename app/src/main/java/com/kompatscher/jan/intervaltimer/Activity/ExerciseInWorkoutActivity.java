package com.kompatscher.jan.intervaltimer.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kompatscher.jan.intervaltimer.Database.AppRoomDatabase;
import com.kompatscher.jan.intervaltimer.Database.Entity.ExerciseInWorkout;
import com.kompatscher.jan.intervaltimer.Database.Entity.ExerciseWithFeatureNumber;
import com.kompatscher.jan.intervaltimer.R;
import com.kompatscher.jan.intervaltimer.Timer.Time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExerciseInWorkoutActivity extends AppCompatActivity {

    private RecyclerView exerciseInWorkoutRecycler;
    private ArrayList<ExerciseWithFeatureNumber> exerciseList;
    private PresetAdapter exerciseAdapter;

    private View progressView;

    private TextView nameView;

    private Long workoutId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_in_workout_list);

        exerciseInWorkoutRecycler = findViewById(R.id.exerciseInWorkoutRecyclerView);
        progressView = findViewById(R.id.progressView);
        nameView = findViewById(R.id.workoutNameTextView);

        exerciseList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        exerciseInWorkoutRecycler.setLayoutManager(layoutManager);
        exerciseAdapter = new PresetAdapter(exerciseList, this);
        exerciseInWorkoutRecycler.setAdapter(exerciseAdapter);

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN , 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                int positionDragged = dragged.getAdapterPosition();
                int positionTarget = target.getAdapterPosition();

                exerciseAdapter.notifyItemsSwappedAndChangeDataInDb(positionDragged, positionTarget);
                Collections.swap(exerciseList, positionDragged, positionTarget);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) { }
        });
        helper.attachToRecyclerView(exerciseInWorkoutRecycler);

        Bundle b = getIntent().getExtras();

        if(b != null) {
            workoutId = b.getLong("workoutId");
        }
        setWorkoutName();

        fillData(workoutId);
        registerForContextMenu(exerciseInWorkoutRecycler);
    }

    public void onAddExerciseButtonClick(View view){
        Intent exerciseListIntent = new Intent (this, ExercisePresetsListActivity.class);
        Bundle b = new Bundle();
        b.putLong("workoutId", workoutId);
        exerciseListIntent.putExtras(b);
        startActivityForResult(exerciseListIntent, 1);
        finish();
    }

    private void showProgress(boolean progress) {
        exerciseInWorkoutRecycler.setVisibility(progress ? View.GONE : View.VISIBLE);
        progressView.setVisibility(progress ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    private void fillData(final long workoutId) {
        showProgress(true);

        // run database action in background. don't block UI thread
        new AsyncTask<Void, Void, List<ExerciseWithFeatureNumber>>() {
            @Override
            protected List<ExerciseWithFeatureNumber> doInBackground(Void... voids) {
                return AppRoomDatabase.getDatabase(getApplicationContext()).
                        exerciseDao().findAllExercisesInAWorkout(workoutId);
            }

            @Override
            protected void onPostExecute(List<ExerciseWithFeatureNumber> exercises) {
                // you can update the UI-Thread with your results here
                exerciseList.clear();
                exerciseList.addAll(exercises);
                showProgress(false);
            }
        }.execute();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    private void setWorkoutName(){
        // run database action in background. don't block UI thread
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return AppRoomDatabase.getDatabase(getApplicationContext()).
                        workoutDao().getName(workoutId);
            }

            @Override
            protected void onPostExecute(String s) {
                nameView.setText(s);
            }
        }.execute();
    }



    class PresetAdapter extends RecyclerView.Adapter<PresetAdapter.PresetHolder> {

        // List to store all the contact details
        private ArrayList<ExerciseWithFeatureNumber> presetList;
        private Context mContext;

        // Counstructor for the Class
        public PresetAdapter(ArrayList<ExerciseWithFeatureNumber> presetList, Context context) {
            this.presetList = presetList;
            this.mContext = context;
        }

        // This method creates views for the RecyclerView by inflating the layout
        // Into the viewHolders which helps to display the items in the RecyclerView
        @Override
        public PresetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

            // Inflate the layout view you have created for the list rows here
            View view = layoutInflater.inflate(R.layout.list_item_exercise_in_workout, parent, false);
            return new PresetHolder(view);
        }

        @Override
        public int getItemCount() {
            return presetList == null? 0: presetList.size();
        }

        //Method called when the exerciseItems are dragged around to persist the order
        public void notifyItemsSwappedAndChangeDataInDb(int i, int j) {
            super.notifyItemMoved(i, j);
            final ExerciseInWorkout firstItem = presetList.get(i).getExerciseInWorkoutAndAssignNewFeatureNumber(j);
            final ExerciseInWorkout secondItem = presetList.get(j).getExerciseInWorkoutAndAssignNewFeatureNumber(i);
            new AsyncTask<Void, Void, ExerciseInWorkout>() {
                @Override
                protected ExerciseInWorkout doInBackground(Void... voids) {
                    AppRoomDatabase.getDatabase(getApplicationContext()).exerciseDao().update(firstItem);
                    return firstItem;

                }
            }.execute();
            new AsyncTask<Void, Void, ExerciseInWorkout>() {
                @Override
                protected ExerciseInWorkout doInBackground(Void... voids) {
                    AppRoomDatabase.getDatabase(getApplicationContext()).exerciseDao().update(secondItem);
                    return secondItem;

                }
            }.execute();
        }

        // This method is called when binding the data to the views being created in RecyclerView
        @Override
        public void onBindViewHolder(@NonNull PresetHolder holder, final int position) {
            final ExerciseWithFeatureNumber exerciseInWorkout = presetList.get(position);

            // Populate the data into the template view using the data object
            holder.nameTxtField.setText(exerciseInWorkout.getName());
            if(exerciseInWorkout.getTotalWorkSeconds() != null)
                holder.workTimeTxtField.setText(Time.totalSecondsToTimeString(exerciseInWorkout.getTotalWorkSeconds()));
            if(exerciseInWorkout.getTotalWorkSeconds() != null)
                holder.breakTimeTxtField.setText(Time.totalSecondsToTimeString(exerciseInWorkout.getTotalBreakSeconds()));
            holder.setNumberTxtField.setText("" + exerciseInWorkout.getSets());


            //Set onClickListeners of Buttons
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AsyncTask<Void, Void, ExerciseWithFeatureNumber>() {
                        @Override
                        protected ExerciseWithFeatureNumber doInBackground(Void... voids) {
                            AppRoomDatabase.getDatabase(getApplicationContext()).
                                    exerciseDao().removeExerciseFromWorkout(exerciseInWorkout.getEiwId());
                            return exerciseInWorkout;
                        }

                        @Override
                        protected void onPostExecute(ExerciseWithFeatureNumber exerciseRoom) {
                            exerciseAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Deleted entry", Toast.LENGTH_LONG).show(); //shows notification with the eiwId of the entry
                            fillData(workoutId);
                        }
                    }.execute();
                }
            });

            holder.moveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: move the position of the exercise in the workout (featureNumber)
                }
            });
            // You can set click listners to indvidual items in the viewholder here
            // make sure you pass down the listner or make the Data members of the viewHolder public

        }

        // This is your ViewHolder class that helps to populate data to the view
        public class PresetHolder extends RecyclerView.ViewHolder {

            public TextView txtName;
            public TextView nameTxtField;
            public TextView workTimeTxtField;
            public TextView breakTimeTxtField;
            public TextView setNumberTxtField;
            public Button deleteButton;
            public Button moveButton;

            public PresetHolder(View itemView) {
                super(itemView);

                txtName = itemView.findViewById(R.id.nameTextView);
                nameTxtField = itemView.findViewById(R.id.nameTextView);
                workTimeTxtField = itemView.findViewById(R.id.workTimeTextView);
                breakTimeTxtField = itemView.findViewById(R.id.breakTimeTextView);
                setNumberTxtField = itemView.findViewById(R.id.setNumberTextView);
                deleteButton = itemView.findViewById(R.id.deleteButton);
                moveButton = itemView.findViewById(R.id.moveButton);
            }
        }
    }
}
