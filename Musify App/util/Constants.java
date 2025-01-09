package util;

import java.util.Scanner;

public class Constants {

    public static final Scanner keyboard = new Scanner(System.in);
    public static final String INVALID_INPUT = "Invalid Input";
    public static final String PLAYLIST_HEADER_FORMATTER = "|%2s|%10s|%30s|%n";
    public static final String PLAYLIST_FORMATTER = "|%2s|%10s|%30s|%n";
    public static final String SONG_PLAYLIST_HEADER = "|%2s|%30s|%30s|%30s|%10s|%16s|%n";
    public static final String SONG_PLAYLIST_DATA_FORMATTER = "|%2d|%30s|%30s|%30s|%10s|%16d|%n";
    public static final String PODCAST_PLAYLIST_HEADER = "|%2s|%30s|%30s|%30s|%15s|%20s|%8s|%16s|%n";
    public static final String PODCAST_DATA_FORMATTER = "|%2d|%30s|%30s|%30s|%15s|%20s|%8d|%16d|%n";
    public static final String SHORTCLIP_PLAYLIST_HEADER = "|%2s|%30s|%30s|%30s|%16s|%n";
    public static final String SHORTCLIP_DATA_FORMATTER = "|%2d|%30s|%30s|%30s|%16d|%n";
   
   /* add more constants here */
    public static final int CREATE_PLAYLIST = 1;
    public static final int VIEW_PLAYLIST = 2;
    public static final int VIEW_CONTENTS = 3;
    public static final int REMOVE_PLAYLIST = 4;
    public static final int MODIFY_PLAYLIST = 5;
    public static final int PLAY_CONTENTS = 6;
    public static final int QUIT_MAIN = 7;

    public static final int SUBMENU_VIEW_PLAYLIST = 1;
    public static final int ADD_MEDIA = 2;
    public static final int REMOVE_MEDIA = 3;
    public static final int QUIT_SUBMENU = 4;

    public static final String SONG = "SONG";
    public static final String PODCAST = "PODCAST";
    public static final String SHORTCLIP = "SHORTCLIP";

    public static final String ADD_KEY = "A";
    public static final String QUIT_KEY = "Q";




}

