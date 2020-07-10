package com.gerasimenko.alias.team

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gerasimenko.alias.R
import com.gerasimenko.alias.team.TeamAdapter.TeamViewHolder

class TeamAdapter(private val teamsList: List<Team>) : RecyclerView.Adapter<TeamViewHolder?>() {
    private var listener: ITeamListener? = null

    interface ITeamListener {
        fun onTeamClicked(view: View?)
    }

    fun setListener(listener: ITeamListener?) {
        this.listener = listener
    }

    inner class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var teamColorTextView: TextView = itemView.findViewById(R.id.team_color_tv)
        var teamNameTextView: TextView = itemView.findViewById(R.id.team_name_tv)

        init {
            itemView.setOnClickListener { view ->
                if (listener != null) {
                    listener!!.onTeamClicked(view)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.team_layout, parent, false)
        return TeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val team = teamsList[position]
        holder.teamColorTextView.background
            .setColorFilter(team.teamColor, PorterDuff.Mode.SRC_ATOP) //CHECK
        holder.teamNameTextView.text = team.teamName
    }

    override fun getItemCount(): Int {
        return teamsList.size
    }

}