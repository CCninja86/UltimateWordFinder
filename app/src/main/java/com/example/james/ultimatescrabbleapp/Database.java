package com.example.james.ultimatescrabbleapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by James on 18/11/2015.
 */
public class Database {

    public Connection conn;
    public static String url = "jdbc:derby:WordDatabase";
    public static String username = "CCninja86";
    public static String password = "49e7FryO";
    private Dictionary dictionary;
    private Statement statement;

    /**
     * Creates a new Database with a Dictionary object
     *
     * @param dictionary A dictionary object that will contain all the words
     */
    public Database(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * Establishes a connection to the database.txt
     */
    public void establishConnection() {
        try {
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected...");
            this.statement = conn.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Gets the entire list of words from the dictionary
     *
     * @return the entire list of words from the dictionary
     */
    public ArrayList<String> getWordsInDatabase() {
        ArrayList<String> words = new ArrayList<String>();

        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM word");

            while (rs.next()) {
                String word = rs.getString("word");
                words.add(word);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return words;
    }

    public ResultSet getResultSet(String word) {
        ResultSet rs = null;

        try {
            rs = statement.executeQuery("SELECT * FROM word WHERE word LIKE '" + word + "'");
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rs;
    }
}
