package com.example.james.ultimatewordfindernew;

/**
 * Created by James on 11/11/2015.
 */
public enum PlayerStatus {
    WINNING("Winning"), LOSING("Losing"), TIED("Tied");

    private String playerStatus;

    private PlayerStatus(String playerStatus){
        this.playerStatus = playerStatus;
    }
}
