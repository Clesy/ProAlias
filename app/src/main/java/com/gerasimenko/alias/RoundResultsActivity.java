package com.gerasimenko.alias;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gerasimenko.alias.main.MainActivity;
import com.gerasimenko.alias.team.Team;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import static com.gerasimenko.alias.ConstantsHolder.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class RoundResultsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private ArrayList<String> guessedWords;
    private ArrayList<String> passedWords;
    private String unreadLastWord;
    private ArrayList<String> unreadWords = new ArrayList<>();
    private String currTeamName;
    private int score;
    private int turnOfTeam;
    private boolean lastWordForEveryone;

    private TextView lastForEveryoneTv;
    private TextView guessedByTv;
    private TextView scoreTv;

    private Button nextGamer;

    private ArrayAdapter<String> guessedWordsAdapter;
    private ArrayAdapter<String> passedWordsAdapter;
    private ArrayAdapter<String> unreadWordsAdapter;
    private ArrayList<Team> teamsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_results);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        sharedPreferences = getSharedPreferences(CURRENT_GAME_DETAILS, MODE_PRIVATE);

        scoreTv = findViewById(R.id.points_in_round_tv);
        ListView guessedWordsLv = findViewById(R.id.guessed_words_lv);
        ListView passedWordsLv = findViewById(R.id.passed_words_lv);
        ListView unreadWordsLv = findViewById(R.id.unread_words_lv);
        lastForEveryoneTv = findViewById(R.id.word_for_everyone_tv);
        guessedByTv = findViewById(R.id.last_guessed_by_tv);

        loadData();
        calcPoints();
        guessedWordsAdapter = new ArrayAdapter<String>(this,R.layout.custom_text_view_for_gamewords,guessedWords);
        passedWordsAdapter = new ArrayAdapter<String>(this,R.layout.custom_text_view_for_gamewords,passedWords);
        unreadWordsAdapter = new ArrayAdapter<String>(this,R.layout.custom_text_view_for_gamewords, unreadWords);
        guessedWordsLv.setAdapter(guessedWordsAdapter);
        passedWordsLv.setAdapter(passedWordsAdapter);
        unreadWordsLv.setAdapter(unreadWordsAdapter);

        MyOnListItemLongClickListener listItemListener = new MyOnListItemLongClickListener();///if not long click show toast
        guessedWordsLv.setOnItemLongClickListener(listItemListener);
        passedWordsLv.setOnItemLongClickListener(listItemListener);
        unreadWordsLv.setOnItemLongClickListener(listItemListener);

        if(lastWordForEveryone)
        {
            String[] teamNames = new String[teamsList.size()];
            for(int i = 0; i < teamNames.length; i++)
            {
                teamNames[i] = teamsList.get(i).getTeamName();
            }

            String msg = getResources().getString(R.string.last_word_guessed) + " " + unreadLastWord + ". "
                    + getResources().getString(R.string.guessed_by) + ":";

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(msg).setItems(teamNames, new DialogInterface.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    lastForEveryoneTv.setText(unreadLastWord);
                    String guessedMsg = getResources().getString(R.string.guessed_by) + " " + teamsList.get(i).getTeamName();
                    guessedByTv.setText(guessedMsg);
                    unreadWords.remove(unreadLastWord);
                    unreadWordsAdapter.notifyDataSetChanged();
                    int score = teamsList.get(i).totalScore;
                    score++;
                    teamsList.get(i).totalScore = score;

                    if(teamsList.get(i).getTeamName().equals(currTeamName))
                    {
                        int currScore = Integer.parseInt(scoreTv.getText().toString());
                        currScore++;
                        scoreTv.setText(currScore + "");
                    }

                    String msg = getResources().getString(R.string.plus_one_point_to) + " " + teamsList.get(i).getTeamName();
                    Toast.makeText(RoundResultsActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton(R.string.nobody, null).setIcon(R.drawable.ic_main).show();
        }
    }

    private void loadData()
    {
        Gson gson = new Gson();
        String JsonTeamsList = sharedPreferences.getString(TEAMS, null);
        Type type = new TypeToken<ArrayList<Team>>() {}.getType();
        teamsList = gson.fromJson(JsonTeamsList, type);

        turnOfTeam = sharedPreferences.getInt(ROUND_TEAMS_TURN, 0);
        currTeamName = getIntent().getStringExtra(CURRENT_TEAM);
        lastWordForEveryone = sharedPreferences.getBoolean(IF_LAST_WORD_FOR_EVERYONE, false);

        Objects.requireNonNull(getSupportActionBar()).setTitle(currTeamName);
        guessedWords = getIntent().getStringArrayListExtra(GUESSED_WORDS_LIST);
        passedWords = getIntent().getStringArrayListExtra(PASSED_WORDS_LIST);
        unreadLastWord = getIntent().getStringExtra(UNREAD_WORD);
        unreadWords.add(unreadLastWord);
    }

//    public void btnNext(View view) {
//        if (view.getId() == R.id.btn_next) {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            Gson gson = new Gson();
//            String JsonTeamList = gson.toJson(teamsList);
//            editor.putString(TEAMS, JsonTeamList);
//            editor.putBoolean(START_GAME, false);
//
//            if (turnOfTeam == teamsList.size() - 1) {
//                turnOfTeam = 0;
//            } else {
//                turnOfTeam++;
//            }
//
//            editor.putInt(ROUND_TEAMS_TURN, turnOfTeam);
//            editor.apply();
//
//            Intent intent = new Intent(this, ScoreManagerActivity.class);
//            intent.putExtra(SCORED_POINTS, score);
//            startActivity(intent);
//        }
//    }
//        else if(view.getId() == android.R.id.home)
//            {
//                showAlertDlg();
//                return true;
//            }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_apply)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String JsonTeamList = gson.toJson(teamsList);
            editor.putString(TEAMS, JsonTeamList);
            editor.putBoolean(START_GAME, false);

            if(turnOfTeam == teamsList.size() - 1)
            {
                turnOfTeam = 0;
            }
            else
            {
                turnOfTeam++;
            }

            editor.putInt(ROUND_TEAMS_TURN, turnOfTeam);
            editor.apply();

            Intent intent = new Intent(this, ScoreManagerActivity.class);
            intent.putExtra(SCORED_POINTS, score);
            startActivity(intent);
        }
        else if(item.getItemId() == android.R.id.home)
        {
            showAlertDlg();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void calcPoints() {
        score = guessedWords.size() - passedWords.size();
        scoreTv.setText(score + "");
    }

    @Override
    public void onBackPressed() {
        showAlertDlg();
    }

    private void showAlertDlg(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.exit_tomenu_quest).setMessage(R.string.progress_will_be_deleted)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(RoundResultsActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton(R.string.no,null).setIcon(R.drawable.ic_main).show();
    }

    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.round_results_menu,menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    private class MyOnListItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
            Context wrapper = new ContextThemeWrapper(RoundResultsActivity.this, R.style.PopupMenu);
            final PopupMenu popupMenu = new PopupMenu(wrapper, view);
            int menuId = 0;
            switch (adapterView.getId())
            {
                case R.id.guessed_words_lv:
                    menuId = R.menu.guessed_word_menu;
                    break;
                case R.id.passed_words_lv:
                    menuId = R.menu.passed_word_menu;
                    break;
                case R.id.unread_words_lv:
                    menuId = R.menu.unread_word_menu;
                    break;
            }
            popupMenu.getMenuInflater().inflate(menuId, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId())
                    {
                        case R.id.from_guessed_to_passed:
                            updateDataInListViews(guessedWords, passedWords, i, passedWordsAdapter, guessedWordsAdapter);
                            break;
                        case R.id.from_guessed_to_unread:
                            updateDataInListViews(guessedWords, unreadWords, i, unreadWordsAdapter, guessedWordsAdapter);
                            break;
                        case R.id.from_passed_to_guessed:
                            updateDataInListViews(passedWords, guessedWords, i, passedWordsAdapter, guessedWordsAdapter);
                            break;
                        case R.id.from_passed_to_unread:
                            updateDataInListViews(passedWords, unreadWords, i, passedWordsAdapter, unreadWordsAdapter);
                            break;
                        case R.id.from_unread_to_guessed:
                            updateDataInListViews(unreadWords, guessedWords, i, guessedWordsAdapter, unreadWordsAdapter);
                            break;
                        case R.id.from_unread_to_passed:
                            updateDataInListViews(unreadWords, passedWords, i, passedWordsAdapter, unreadWordsAdapter);
                            break;
                    }

                    calcPoints();

                    return false;
                }
            });
            popupMenu.show();

            return false;
        }

        private void updateDataInListViews(ArrayList<String> listFrom, ArrayList<String> listTo, int itemIdx,
                                           ArrayAdapter<String> firstAdapter, ArrayAdapter<String> secondAdapter){
            listTo.add(listFrom.get(itemIdx));
            listFrom.remove(itemIdx);
            firstAdapter.notifyDataSetChanged();
            secondAdapter.notifyDataSetChanged();
        }
    }
}
