/**
 * Wolves Vs Goats by Andre Rosa and Fernando Alves is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 * Based on a work at http://wvg.i3portal.net.
 * <p>
 * Learn how to share your work with existing communities that have enabled Creative Commons licensing.
 * <p>
 * Creative Commons is a non-profit organization.
 *
 * @author Andre Rosa
 * @author Fernando Alves
 * @version 0.1
 */
package com.wolvesvsgoats.data;

import com.wolvesvsgoats.objectives.Objective;
import com.wolvesvsgoats.utils.Position;

import java.util.ArrayList;

public class Game {

    private ArrayList<Objective> wolfObjectives;
    private ArrayList<Objective> goatObjectives;
    private ArrayList<Objective> globalObjectives;
    private String gameName;
    private ArrayList<Player> players;
    private ArrayList<Position> objectivePositions;

    public Game(String name) {
        gameName = name;
        goatObjectives = new ArrayList<Objective>();
        wolfObjectives = new ArrayList<Objective>();
        globalObjectives = new ArrayList<Objective>();
        players = new ArrayList<Player>();
        objectivePositions = new ArrayList<Position>();
    }

    public ArrayList<Position> getObjectivePositions() {
        return objectivePositions;
    }

    public void setObjectivePositions(ArrayList<Position> objectivePositions) {
        this.objectivePositions = objectivePositions;
    }

    public void addObjectivePosition(Position pos) {
        objectivePositions.add(pos);
    }

    public ArrayList<Objective> getWolfObjectives() {
        return wolfObjectives;
    }

    public void setWolfObjectives(ArrayList<Objective> objectives) {
        this.wolfObjectives = objectives;
    }

    public void addWolfObjective(Objective obj) {
        wolfObjectives.add(obj);
    }

    public void setWolfObjectiveDone(Objective obj) {
        wolfObjectives.remove(obj);
    }

    public ArrayList<Objective> getGoatObjectives() {
        return goatObjectives;
    }

    public void setGoatObjectives(ArrayList<Objective> objectives) {
        this.goatObjectives = objectives;
    }

    public void addGoatObjective(Objective obj) {
        goatObjectives.add(obj);
    }

    public void setGoatObjectiveDone(Objective obj) {
        goatObjectives.remove(obj);
    }

    public ArrayList<Objective> getGlobalObjectives() {
        return globalObjectives;
    }

    public void setGlobalObjectives(ArrayList<Objective> globalObjectives) {
        this.globalObjectives = globalObjectives;
    }

    public void addGlobalObjective(Objective obj) {
        goatObjectives.add(obj);
        wolfObjectives.add(obj);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public String getGameName() {
        return gameName;
    }

}
