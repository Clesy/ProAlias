package com.gerasimenko.alias.team;

public class Team {
    public int totalScore;
    private String teamName;
    private int teamColor;

    public Team(String teamName, int teamColor) {
        this.teamName = teamName;
        this.teamColor = teamColor;
        this.totalScore = 0;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(int teamColor) {
        this.teamColor = teamColor;
    }
}
