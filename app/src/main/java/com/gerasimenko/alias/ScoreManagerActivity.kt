package com.gerasimenko.alias

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gerasimenko.alias.ConstantsHolder.*
import com.gerasimenko.alias.main.MainActivity
import com.gerasimenko.alias.team.Team
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class ScoreManagerActivity : AppCompatActivity() {
    private var winnerTeam: TextView? = null
    private var wordsAmount = 0
    private var turnOfNextTeam = 0
    private var playedTeamScore = 0
    private var teamsList: ArrayList<Team>? = null
    private var adapter: SimpleAdapter? = null
    private var sharedPreferences: SharedPreferences? = null
    private var tvPlayerTeam: TextView? = null
    private var teamsScoreLV: ListView? = null
    private var playNextBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.score_activity)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.elevation = 0F
        tvPlayerTeam = findViewById(R.id.TV_player_team)
        teamsScoreLV = findViewById(R.id.teams_score_LV)
        sharedPreferences = getSharedPreferences(CURRENT_GAME_DETAILS, MODE_PRIVATE)
        loadData()
        val resumed: Boolean = intent.getBooleanExtra(ON_RESUME, false)
        if (!resumed) {
            manageScore()
            intent.putExtra(ON_RESUME, false) ///////////////////
        }
        val nextPlayerTeam = teamsList!![turnOfNextTeam]
        tvPlayerTeam!!.text = nextPlayerTeam.teamName
        fillListView()
        val colors = intArrayOf(
            ContextCompat.getColor(this, R.color.seaStar),
            ContextCompat.getColor(this,
                R.color.darkGoldStar
            ),
            ContextCompat.getColor(this, R.color.goldStar)
        )
        val valueAnim = ValueAnimator.ofObject(
            ArgbEvaluator(),
            colors[0],
            colors[1],
            colors[2]
        )
        valueAnim.duration = 4000
        valueAnim.addUpdateListener { valueAnimator ->
            val color = valueAnimator.animatedValue as Int
        }
        valueAnim.start()
    }

    private fun fillListView() {
        val teamsData: MutableList<Map<String, Any?>> = ArrayList()
        val playedRes: String = resources.getString(R.string.played_in_round)
        val teamConstStr = "team"
        val playedConstStr = "played"
        val scoreConsStr = "score"
        var teamIdx = 0
        for (team in teamsList!!) {
            val map =
                HashMap<String, Any?>()
            val ifPlayed =
                if (teamIdx < turnOfNextTeam) playedRes else ""
            map[teamConstStr] = team.teamName
            map[scoreConsStr] = team.totalScore
            teamsData.add(map)
            teamIdx++
        }
        val from = arrayOf(teamConstStr, scoreConsStr)
        val to = intArrayOf(
            R.id.team_name_score_tv,
            R.id.score_tv
        )
        adapter = SimpleAdapter(this, teamsData, R.layout.score_team_layout, from, to)
        teamsScoreLV!!.adapter = adapter
    }

    override fun onBackPressed() {
        saveData()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun loadData() {
        val gson = Gson()
        val JsonTeamsList = sharedPreferences!!.getString(TEAMS, null)
        val type = object : TypeToken<ArrayList<Team?>?>() {}.type
        teamsList = gson.fromJson(JsonTeamsList, type)
        playedTeamScore = intent.getIntExtra(SCORED_POINTS, 0)
        wordsAmount = sharedPreferences!!.getInt(WORDS_AMOUNT, 0) ////////
        turnOfNextTeam = sharedPreferences!!.getInt(ROUND_TEAMS_TURN, 0)
    }

    private fun manageScore() {
        val startGame = sharedPreferences!!.getBoolean(START_GAME, true)
        if (!startGame) {
            var finished = false
            val playedTeamIdx =
                if (turnOfNextTeam == 0) teamsList!!.size - 1 else turnOfNextTeam - 1
            if (playedTeamScore != 0) {
                var totalTeamScore = teamsList!![playedTeamIdx].totalScore
                totalTeamScore += playedTeamScore
                teamsList!![playedTeamIdx].totalScore = totalTeamScore
            }
            if (playedTeamIdx == teamsList!!.size - 1) {
                finished = checkIfGotToFinish()
            }
            if (finished) {
                findWinners()
            }
        }
    }

    private fun checkIfGotToFinish(): Boolean {
        var finished = false
        for (team in teamsList!!) {
            if (team.totalScore >= wordsAmount) {
                finished = true
                break
            }
        }
        return finished
    }

    private fun findWinners() {
        val winnersList = ArrayList<Team>()
        for (team in teamsList!!) {
            if (team.totalScore >= wordsAmount) {
                winnersList.add(team)
            }
        }
        if (winnersList.size > 1) {
            winnersList.sortWith(Comparator { t1, t2 ->
                var returningValue = 0
                if (t1.totalScore > t2.totalScore) {
                    returningValue = -1
                } else if (t1.totalScore < t2.totalScore) {
                    returningValue = 1
                }
                returningValue
            })
        }
        val bestResulter = winnersList[0]
        if (winnersList.size == 1) {
            showWinnerAndFinishGame(bestResulter)
        } else if (bestResulter.totalScore > winnersList[1].totalScore) {
            showWinnerAndFinishGame(bestResulter)
        } else {
            val bestResultersList = ArrayList<Team>()
            for (team in winnersList) {
                if (team.totalScore == bestResulter.totalScore) {
                    bestResultersList.add(team)
                } else {
                    break
                }
            }
            teamsList = bestResultersList
            saveData()
        }
    }

    private fun showWinnerAndFinishGame(bestResulter: Team) {
        val inflater = layoutInflater
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val msg = bestResulter.teamName + "" + resources.getString(R.string.won)
        val dialogView: View = inflater.inflate(R.layout.winner_layout, null)
        builder.setView(dialogView)
        builder.setMessage(msg)
        builder.show()
        val applaudsMp: MediaPlayer = MediaPlayer.create(this,
            R.raw.winner
        )
        applaudsMp.setOnCompletionListener { applaudsMp.release() }
        applaudsMp.start()
    }

    fun playOnClick(view: View) {
        if (view.id == R.id.play_now_btn) {
            val editor = sharedPreferences!!.edit()
            editor.clear()
            editor.apply()
            val intent = Intent(this@ScoreManagerActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveData() {
        val editor = sharedPreferences!!.edit()
        val gson = Gson()
        val JsonTeamList = gson.toJson(teamsList)
        editor.putString(TEAMS, JsonTeamList)
        editor.putInt(ROUND_TEAMS_TURN, turnOfNextTeam)
        editor.apply()
    }

    fun startGameScore(view: View) {
        saveData()
        if (view.id == R.id.start_game_score) {
            val intent = Intent(this, GameBoardActivity::class.java)
            intent.putExtra(CURRENT_TEAM, teamsList!![turnOfNextTeam].teamName)
            startActivity(intent)
        }
    }
}