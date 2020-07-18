import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Hashtable;

public class BaseballElimination {
    private Hashtable<String, Integer> teams;
    private int[] wins;
    private int[] losses;
    private int[] remaining;
    private int[][] games;
    private Hashtable<String, ArrayList<String>> eliminatedTeams;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        // takes in input from division
        In in = new In(filename);
        int n = Integer.parseInt(in.readLine());
        // initializes arrays to store input information
        teams = new Hashtable<>(n);
        games = new int[n][n];
        wins = new int[n];
        losses = new int[n];
        wins = new int[n];
        remaining = new int[n];
        eliminatedTeams = new Hashtable<>(n);
        int j = 0;
        // adds input information to requisite arrays or hash table
        while (in.hasNextLine()) {
            String s = in.readLine();
            String[] x = s.trim().split("\\s+");
            for (int i = 0; i < x.length; i++) {
                if (i == 0) teams.put(x[i], j);
                else if (i == 1) wins[j] = Integer.parseInt(x[i]);
                else if (i == 2) losses[j] = Integer.parseInt(x[i]);
                else if (i == 3) remaining[j] = Integer.parseInt(x[i]);
                else games[j][i - 4] = Integer.parseInt(x[i]);
            }
            j++;
        }
    }

    // number of teams
    public int numberOfTeams() {
        return teams.size();
    }

    // all teams
    public Iterable<String> teams() {
        return teams.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        if (!isTeam(team)) throw new IllegalArgumentException("Must be a valid team");
        return wins[teams.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        if (!isTeam(team)) throw new IllegalArgumentException("Must be a valid team");
        return losses[teams.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (!isTeam(team)) throw new IllegalArgumentException("Must be a valid team");
        return remaining[teams.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!isTeam(team1) || !isTeam(team2)) throw new IllegalArgumentException("Must be a valid team");
        return games[teams.get(team1)][teams.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (!isTeam(team)) throw new IllegalArgumentException("Must be a valid team");
        int verticeSize = teams.size() + 1 + chooseTwo(teams.size() - 1);
        FlowNetwork elimination = new FlowNetwork(verticeSize);
        /**
         * 0 is s
         * 1 is t
         * 2 through teams.size() + 1 is the teams
         * everything after is a game, running through the games array by row
         * creating a directed graph with two extra nodes s and t to calculate max flow,
         * with each team connected to t by the maximum number of games they can win without beating the parameter team
         * and s connected to each matchup by the number of remaining games to be played
         * and each matchup connected to the two teams involved
         */
        // initializes iterable of the teams and the index of the parameter team
        Iterable<String> teamsIt = this.teams();
        int teamInt = teams.get(team);
        // checks if paramater team can't even beat some teams current win value with their remaining games
        if (checkTrivialElimination(team, teamInt, teamsIt)) return true;
        // connect every team to t with the capacity of w[x] + r[x] - w[i]
        int equalizer = 0;
        for (String s : teamsIt) {
            if (!s.equals(team)) {
                if (teams.get(s) > teamInt) equalizer = 1;
                elimination.addEdge(new FlowEdge(teams.get(s) + 2 - equalizer, 1, wins[teamInt] + remaining[teamInt] - wins[teams.get(s)]));
            }
            equalizer = 0;
        }
        // connect s to every team combination with capacity of the number of games
        // then connect those team combinations to the two teams with infinity capacity
        int Jequalizer = 0;
        int Iequalizer = 0;
        int gameNumber = 0;
        int totGames = 0;
        for (int i = 0; i < teams.size(); i++) {
            if (i == teamInt) {
                Iequalizer++;
                continue;
            }
            if (i > teamInt) Iequalizer = 1;
            for (int j = i + 1; j < teams.size(); j++) {
                if (j == teamInt) {
                    continue;
                }
                if (j > teamInt) Jequalizer = 1;
                elimination.addEdge(new FlowEdge(0, teams.size() + 1 + gameNumber, games[i][j]));
                totGames += games[i][j];
                elimination.addEdge(new FlowEdge(teams.size() + gameNumber + 1, 2 + i - Iequalizer, Double.POSITIVE_INFINITY));
                elimination.addEdge(new FlowEdge(teams.size() + gameNumber + 1, 2 + j - Jequalizer, Double.POSITIVE_INFINITY));
                gameNumber++;
                Jequalizer = 0;
            }
            Iequalizer = 0;
        }
        // run FordFulkerson to get the max-flow and calculate the min cut
        FordFulkerson maxFlow = new FordFulkerson(elimination, 0, 1);
        // check if the max flow has every edge from s to the games full (all games are played with parameter team having the most wins)
        if (maxFlow.value() < totGames) {
            // team is mathematically eliminated because playing all games would cause some team to have more wins than possible for the team
            // so determine the teams in the mincut to find the certificate of elimination and return true
            ArrayList<String> eliminatingBracket = new ArrayList<>();
            for (String s : teamsIt) {
                if (s.equals(team)) continue;
                if (teams.get(s) > teamInt) equalizer = 1;
                if (maxFlow.inCut(teams.get(s) + 2 - equalizer)) {
                    eliminatingBracket.add(s);
                }
                equalizer = 0;
            }
            eliminatedTeams.put(team, eliminatingBracket);
            return true;
        }
        return false;
    }

    // checks if paramater team can't even beat some teams current win value with their remaining games
    private boolean checkTrivialElimination(String team, int teamInt, Iterable<String> teamIt) {
        String beatingTeam = "";
        for (String s : teamIt) {
            if (s.equals(team)) continue;
            if (wins[teamInt] + remaining[teamInt] < wins[teams.get(s)]) {
                beatingTeam = s;
            }
        }
        if (beatingTeam.equals("")) return false;
        else {
            ArrayList<String> eliminatingBracket = new ArrayList<>();
            eliminatingBracket.add(beatingTeam);
            eliminatedTeams.put(team, eliminatingBracket);
            return true;
        }
    }

    // calculates n choose 2 for creation of the FlowNetwork
    private int chooseTwo(int n) {
        return (int) Math.ceil(fact(n) / (fact(n - 2) * 2));
    }

    // calculates the factorial of n
    private double fact(int n) {
        double res = 1;
        for (int i = 2; i <= n; i++)
            res = res * i;
        return res;
    }

    // checks if a team is part of the division
    private boolean isTeam(String team) {
        return teams.get(team) != null;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!isTeam(team)) throw new IllegalArgumentException("Must be a valid team");
        // check if isEliminated has already been run
        ArrayList<String> x = eliminatedTeams.get(team);
        // if it has return that set
        if (x != null) return x;
        // check if it has been eliminated
        boolean y = isEliminated(team);
        // if it hasn't, return null
        if (!y) return null;
            // otherwise return the set
        else return eliminatedTeams.get(team);
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
