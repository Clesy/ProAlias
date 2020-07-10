package com.gerasimenko.alias;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gerasimenko.alias.main.MainActivity;
import com.gerasimenko.alias.team.Team;
import com.gerasimenko.alias.team.TeamAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.gerasimenko.alias.ConstantsHolder.*;


public class SettingNewGameActivity extends AppCompatActivity implements View.OnClickListener {

    private String[] teamNames;
    private int[] teamColors;
    private TeamAdapter teamAdapter;
    private ArrayList<Team> teamsOnTheScreen = new ArrayList<>();
    private List<String> teamNamesNotOnScreen = new ArrayList<>();
    private Button btnPlusTime;
    private Button btnMinusTime;
    private Button btnPlusWord;
    private Button btnMinusWord;
    private TextView tvTimeLimit;
    private TextView tvWordAmount;
    private CheckBox CBLastWord;
    private SharedPreferences sharedPreferences;
    private Button selectDictionaryBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.play_new_game));
        setContentView(R.layout.activity_new_game);

        Random random = new Random();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Button addTeamBtn = findViewById(R.id.btn_add_new_team);
        tvTimeLimit = findViewById(R.id.tv_timeLimit);
        tvWordAmount = findViewById(R.id.tv_wordsAmount);
        btnPlusTime = findViewById(R.id.btn_plus_time);
        btnMinusTime = findViewById(R.id.btn_minus_time);
        btnPlusWord = findViewById(R.id.btn_plus_words);
        btnMinusWord = findViewById(R.id.btn_minus_words);
        CBLastWord = findViewById(R.id.CBLastWord);
        selectDictionaryBtn = findViewById(R.id.select_dictionary_btn);

        teamNames = getResources().getStringArray(R.array.teams_array);
        teamColors = getResources().getIntArray(R.array.color_array);

        btnPlusTime.setOnClickListener(this);
        btnMinusTime.setOnClickListener(this);
        btnPlusWord.setOnClickListener(this);
        btnMinusWord.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final int first = random.nextInt(teamNames.length - 1);
        int second = random.nextInt(teamNames.length - 1);
        while ((second == first))
        {
            second = random.nextInt(teamNames.length - 1);
        }

        teamsOnTheScreen.add(new Team(teamNames[first], teamColors[first]));
        teamsOnTheScreen.add(new Team(teamNames[second], teamColors[second]));
        teamAdapter = new TeamAdapter(teamsOnTheScreen);
        teamAdapter.setListener(new TeamAdapter.ITeamListener() {
            @Override
            public void onTeamClicked(View view) {
                String text = getResources().getString(R.string.swipe_to_delete);
                Toast.makeText(SettingNewGameActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                teamsOnTheScreen.remove(viewHolder.getAdapterPosition());
                teamAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        ////
        recyclerView.setAdapter(teamAdapter);

        selectDictionaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dlg;
                final AlertDialog.Builder builder = new AlertDialog.Builder(SettingNewGameActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView  = inflater.inflate(R.layout.setting_dictionary_layout,null);
                builder.setView(dialogView);

                final ArrayList<String> dictionariesNamesList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.categories)));
                final ArrayAdapter<String> dictionariesAdapter =
                        new ArrayAdapter<String>(SettingNewGameActivity.this,R.layout.dictionary_word_layout,dictionariesNamesList);

                Button enDialogBtn = dialogView.findViewById(R.id.en_btn);
                Button ruDialogBtn = dialogView.findViewById(R.id.ru_btn);
                enDialogBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dictionariesNamesList.clear();
                        dictionariesNamesList.addAll(Arrays.asList(getResources().getStringArray(R.array.categoriesEN)));
                        dictionariesAdapter.notifyDataSetChanged();
                    }
                });
                ruDialogBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dictionariesNamesList.clear();
                        dictionariesNamesList.addAll(Arrays.asList(getResources().getStringArray(R.array.categoriesRU)));
                        dictionariesAdapter.notifyDataSetChanged();
                    }
                });

                ListView dictionariesListView = dialogView.findViewById(R.id.dictionaries_list);
                dictionariesListView.setAdapter(dictionariesAdapter);
                dlg = builder.create();
                dictionariesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectDictionaryBtn.setText(dictionariesNamesList.get(i));
                        dlg.dismiss();
                    }
                });
                dlg.show();
            }
        });


        addTeamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(teamsOnTheScreen.size() < MAX_TEAMS_NUMBER)
                {
                    teamNamesNotOnScreen.clear();
                    for(String teamName : teamNames){
                        boolean ifOnScreen = false;
                        for(Team team : teamsOnTheScreen)
                        {
                            if(teamName.equals(team.getTeamName()))
                            {
                                ifOnScreen = true;
                                break;
                            }
                        }

                        if(!ifOnScreen)
                        {
                            teamNamesNotOnScreen.add(teamName);
                        }
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingNewGameActivity.this);
                    builder.setItems(teamNamesNotOnScreen.toArray(new String[0]), new MyItemsDialogListener()).show();
                }
                else
                {
                    String text = getResources().getString(R.string.teams_number_limit);
                    Toast.makeText(SettingNewGameActivity.this, text, Toast.LENGTH_LONG).show();
                }
            }
        });
        sharedPreferences = getSharedPreferences(CURRENT_GAME_DETAILS, MODE_PRIVATE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void saveData() {
        int secondsLimit = Integer.parseInt(tvTimeLimit.getText().toString());
        int wordsAmount = Integer.parseInt(tvWordAmount.getText().toString());
        boolean ifWithLastWord = CBLastWord.isChecked();
        String selectedDictionary = selectDictionaryBtn.getText().toString();

        String dictionaryName = selectedDictionary.replaceAll(" ", "");
        int resourseId = this.getResources().getIdentifier(dictionaryName, "array", this.getPackageName());
        String[] words = getResources().getStringArray(resourseId);
        ArrayList<String> wordsList = new ArrayList<String>(Arrays.asList(words));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String JsonTeamList = gson.toJson(teamsOnTheScreen);
        editor.putString(TEAMS, JsonTeamList);
        String JsonWordsList = gson.toJson(wordsList);
        editor.putString(WORDS_LIST, JsonWordsList);
        editor.putString(CONST_WORDS_LIST, JsonWordsList);
        editor.putInt(TIME_LIMIT, secondsLimit);
        editor.putInt(WORDS_AMOUNT, wordsAmount);
        editor.putBoolean(IF_LAST_WORD_FOR_EVERYONE, ifWithLastWord);
        editor.putBoolean(IF_NEW_GAME, false);
        editor.apply();
    }

    private void makeButtonEnabled(Button btn)
    {
        if(!btn.isEnabled())
        {
            btn.setEnabled(true);
            btn.setAlpha(1f);
            btn.setClickable(true);
        }
    }

    private void makeButtonDisabled(Button btn)
    {
        btn.setEnabled(false);
        btn.setAlpha(.5f);
        btn.setClickable(false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        int seconds;
        int words;
        int minWords = 10;
        int maxWords = 200;
        int minTime = 15;
        int maxTime = 990;
        switch (viewId){
            case R.id.btn_plus_time:
                seconds = Integer.parseInt(tvTimeLimit.getText().toString());
                if(seconds < maxTime)
                {
                    makeButtonEnabled(btnMinusTime);
                    seconds += 15;
                    tvTimeLimit.setText(seconds + "");
                }
                if(seconds == maxTime)
                {
                    makeButtonDisabled(btnPlusTime);
                }
                break;
            case R.id.btn_minus_time:
                seconds = Integer.parseInt(tvTimeLimit.getText().toString());
                if(seconds > minTime)
                {
                    makeButtonEnabled(btnPlusTime);
                    seconds -= 15;
                    tvTimeLimit.setText(seconds + "");
                }
                if(seconds == minTime)
                {
                    makeButtonDisabled(btnMinusTime);
                }
                break;
            case R.id.btn_plus_words:
                words = Integer.parseInt(tvWordAmount.getText().toString());
                if (words < maxWords)
                {
                    makeButtonEnabled(btnMinusWord);
                    words += 10;
                    tvWordAmount.setText(words + "");
                }
                if(words == maxWords)
                {
                    makeButtonDisabled(btnPlusWord);
                }
                break;
            case R.id.btn_minus_words:
                words = Integer.parseInt(tvWordAmount.getText().toString());
                if(words > minWords)
                {
                    makeButtonEnabled(btnPlusWord);
                    words -= 10;
                    tvWordAmount.setText(words + "");
                }
                if(words == minWords)
                {
                    makeButtonDisabled(btnMinusWord);
                }
                break;
        }
    }

    public void startGameOnClick(View view) {
        if(view.getId() == R.id.btn_start_game)
        {
            if(selectDictionaryBtn.getText().equals(getResources().getString(R.string.select_dictionary)))
            {
                Toast.makeText(this, getResources().getString(R.string.dictionary_not_selected), Toast.LENGTH_SHORT).show();
            }
            else if(teamsOnTheScreen.size() < 2)
            {
                Toast.makeText(this, getResources().getString(R.string.min_two_teams), Toast.LENGTH_SHORT).show();
            }
            else
            {
                saveData();
                Intent intent = new Intent(this, ScoreManagerActivity.class);
                startActivity(intent);
            }
        }
    }

    private class MyItemsDialogListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            String teamNameToAdd = teamNamesNotOnScreen.get(i);
            for(int j = 0; j < teamNames.length; j++)
            {
                if(teamNames[j].equals(teamNameToAdd))
                {
                    teamsOnTheScreen.add(new Team(teamNames[j], teamColors[j]));
                    teamAdapter.notifyItemInserted(teamsOnTheScreen.size() - 1);
                    break;
                }
            }
        }
    }
}
