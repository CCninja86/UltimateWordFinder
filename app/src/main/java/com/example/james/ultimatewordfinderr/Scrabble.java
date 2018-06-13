package com.example.james.ultimatewordfinderr;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by James on 11/11/2015.
 */
public class Scrabble implements Serializable {
    private ArrayList<Player> players;
    private ArrayList<Tile> tiles;
    private ArrayList<String> wordHistory;
    private int totalTiles = 100;

    private Map<String, Player> playerMap;

    public Scrabble() {
        this.players = new ArrayList<>();
        this.tiles = new ArrayList<>();
        this.wordHistory = new ArrayList<>();
        this.playerMap = new HashMap<>();

        for (Player player : players) {
            playerMap.put(player.getName(), player);
        }
    }

    public void initialiseTiles() {
        this.tiles.add(new Tile("?", 0, false, 2));
        this.tiles.add(new Tile("A", 1, true, 9));
        this.tiles.add(new Tile("B", 3, false, 2));
        this.tiles.add(new Tile("C", 3, false, 2));
        this.tiles.add(new Tile("D", 2, false, 4));
        this.tiles.add(new Tile("E", 1, true, 12));
        this.tiles.add(new Tile("F", 4, false, 2));
        this.tiles.add(new Tile("G", 2, false, 3));
        this.tiles.add(new Tile("H", 4, false, 2));
        this.tiles.add(new Tile("I", 1, true, 9));
        this.tiles.add(new Tile("J", 8, false, 1));
        this.tiles.add(new Tile("K", 5, false, 1));
        this.tiles.add(new Tile("L", 1, false, 4));
        this.tiles.add(new Tile("M", 3, false, 2));
        this.tiles.add(new Tile("N", 1, false, 6));
        this.tiles.add(new Tile("O", 1, true, 8));
        this.tiles.add(new Tile("P", 3, false, 2));
        this.tiles.add(new Tile("Q", 10, false, 1));
        this.tiles.add(new Tile("R", 1, false, 6));
        this.tiles.add(new Tile("S", 1, false, 4));
        this.tiles.add(new Tile("T", 1, false, 6));
        this.tiles.add(new Tile("U", 1, true, 4));
        this.tiles.add(new Tile("V", 4, false, 2));
        this.tiles.add(new Tile("W", 4, false, 2));
        this.tiles.add(new Tile("X", 8, false, 1));
        this.tiles.add(new Tile("Y", 4, false, 2));
        this.tiles.add(new Tile("Z", 10, false, 1));
    }

    public void removeTile(Tile tile) {
        if (this.getTileCount() > 0) {
            for (Tile tileInArray : this.tiles) {
                if (tile.getLetter().equals(tileInArray.getLetter())) {
                    tileInArray.removeTile();
                }
            }
        }
    }

    public boolean enoughTilesForWord(String word) {
        return this.getTileCount() - word.length() >= 0;
    }

    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    public void clearPlayers() {
        this.players = new ArrayList<>();
    }

    public Map<String, Player> getPlayerMap() {
        return this.playerMap;
    }

    public Player getPlayerByName(String name) {
        Player player = null;

        for (Player p : this.players) {
            if (p.getName().equals(name)) {
                player = p;
            }
        }

        return player;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void addWordToHistory(String word) {
        this.wordHistory.add(word);
    }

    public String[] getPlayerNames() {
        String[] playerNames = new String[this.players.size()];

        for (int i = 0; i < this.players.size(); i++) {
            playerNames[i] = this.players.get(i).getName();
        }

        return playerNames;
    }

    public ArrayList<String> getPlayerNamesAsArrayList() {
        ArrayList<String> playerNames = new ArrayList<>();

        for (Player player : this.players) {
            playerNames.add(player.getName());
        }

        return playerNames;
    }

    public void removePlayer(String playerName) {
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).getName() == playerName) {
                this.players.remove(i);
            }
        }
    }

    public int getNumPlayers() {
        int numPlayers = 0;

        for (Player player : this.players) {
            numPlayers++;
        }

        return numPlayers;
    }

    public ArrayList<Tile> getTiles() {
        return this.tiles;
    }

    public Tile getTileByLetter(String letter) {
        Tile tileToReturn = null;

        for (Tile tile : this.tiles) {
            if (tile.getLetter().equals(letter)) {
                tileToReturn = tile;
            }
        }

        return tileToReturn;
    }

    public ArrayList<String> getWordHistory() {
        return this.wordHistory;
    }

    public int getTileCount() {
        int total = 0;

        for (int i = 0; i < this.tiles.size(); i++) {
            total += this.tiles.get(i).getQuantity();
        }
        return total;
    }

    public int getTotalTiles() {
        return this.totalTiles;
    }

    public Player getWinningPlayer() {
        int largestScore = 0;
        Player winningPlayer = null;

        for (int i = 0; i < this.players.size(); i++) {
            Player player = this.players.get(i);

            if (player.getScore() >= largestScore) {
                largestScore = player.getScore();
            }
        }

        for (int i = 0; i < this.players.size(); i++) {
            Player player = this.players.get(i);

            if (player.getScore() == largestScore) {
                winningPlayer = player;
            }
        }

        return winningPlayer;
    }

    public boolean saveGame() {
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("SaveGame.txt")));
            writer.write("PlayerDataStart");
            writer.write("\n");
            String playerData = "";
            String tiles = "Tiles: ";

            for (Player player : this.players) {
                playerData = player.getName() + "/" + player.getScore() + "/";
                String wordsArray = "";

                for (String word : player.getPlayerWordHistory()) {
                    wordsArray += word + ",";
                }

                wordsArray = wordsArray.substring(0, wordsArray.length() - 1);
                playerData += wordsArray;
                writer.write(playerData);
                writer.write("\n");


            }

            writer.write("PlayerDataEnd");

            writer.write("\n");
            writer.write("\n");

            Gson gson = new Gson();
            tiles += gson.toJson(this.tiles);
            writer.write(tiles);
            writer.write("\n");


        } catch (FileNotFoundException ex) {
            Logger.getLogger(Scrabble.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(Scrabble.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(Scrabble.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return true;
    }

    /*public void loadGame() {
        BufferedReader bufferedReader = null;
        InputStream is = null;

        try {
            is = new FileInputStream("./SaveGame.txt");
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals("PlayerDataStart")) {
                    while (!line.equals("PlayerDataEnd")) {
                        line = bufferedReader.readLine();
                        Player player = null;
                        String playerName = "";
                        int playerScore = 0;
                        ArrayList<String> playerWordHistory = new ArrayList<>();

                        String[] playerProperties = line.split("/");

                        if (playerProperties.length > 1) {
                            playerName = playerProperties[0];
                            playerScore = Integer.parseInt(playerProperties[1]);
                            String[] words = playerProperties[2].split(",");

                            for (String word : words) {
                                playerWordHistory.add(word);
                            }

                            player = new Player(playerName, this);
                            player.setScore(playerScore);
                            player.setPlayerWordHistory(playerWordHistory);
                            this.players.add(player);
                        }

                    }
                } else if (line.contains("Tiles")) {
                    String tiles = line.substring(7);
                    Gson gson = new Gson();
                    ArrayList<Tile> array = (ArrayList<Tile>) gson.fromJson(tiles, new TypeToken<ArrayList<Tile>>() {
                    }.getType());
                    this.tiles = array;
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Scrabble.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Scrabble.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ex) {
                Logger.getLogger(Scrabble.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }*/
}
