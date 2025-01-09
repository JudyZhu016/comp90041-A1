/**
* Author: Tong Zhu
* Student Id: 1368179 
* Email: tonzhu1@student.unimelb.edu.au
*/

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import entities.*;
import exceptions.*;
import util.Constants;

public class MusifyApp {

    private String userName;
    private ArrayList<Playlist> playlists = new ArrayList<>();

    public static void main(String[] args)  {
        MusifyApp app = new MusifyApp();
        
        if (args.length == 0) {
            app.userName = "Stranger";
            System.out.println("No Playlist data found to load.");
        } else if (args.length == 2){
            app.userName = args[0];
            try {
                app.handleFiles("data/" + args[1]);
            } catch(InvalidLineException e) {
                System.err.println(e.getMessage());
            }
            
            System.out.println("Data loading complete.");
        }
        
        app.displayWelcomeMessage();
        app.runMainMenu();

    }

    
    private void handleFiles(String filename) throws InvalidLineException{
        Scanner inputStream = null;

        try {
            inputStream = new Scanner(new FileInputStream(filename));
        
            while (inputStream.hasNextLine()) {
                String line = inputStream.nextLine();
                try {
                    String[] parts = line.split(",");
                    if (parts.length < 3) {
                        throw new InvalidLineException("Invalid Playlist data. Skipping this line.");
                    }
                    String playlistName = parts[0];
                    String mediaType = parts[1];
                    String mediaFileName = parts[2];

                    Playlist playlist = new Playlist(playlistName, mediaType, mediaFileName);
                    playlists.add(playlist);

                    handleMediaFile("data/playlist/" + mediaFileName, mediaType, playlist);
                } catch(InvalidLineException | InvalidFormatException e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Invalid or missing file.");
        } finally{
            if(inputStream != null) {
                inputStream.close();
            }
        }
    }

    private void handleMediaFile(String filename, String mediaType, Playlist playlist) throws InvalidLineException{
        Scanner inputStream = null;
        try {
            inputStream = new Scanner(new FileInputStream(filename));
        
            while (inputStream.hasNextLine()) {
                String line = inputStream.nextLine();
                try{
                    String[] parts = line.split(",");
                    switch(mediaType) {
                        case Constants.SONG:
                            if (parts.length < 6) {
                                throw new InvalidLineException("Song details incomplete. Skipping this line.");
                            }
                            int songDuration = Integer.parseInt(parts[4]); 
                            // split name('#')
                            String[] artistArray = parts[2].split("#");
                            ArrayList<String> artists = new ArrayList<String>();
                            for (String artist : artistArray) {
                                artists.add(artist);
                            }
                            Song song = new Song(parts[0], parts[1], artists, parts[3], songDuration, parts[5]);
                            playlist.addMedia(song); //Adds the media to the playlist
                            // try-catch to report MediaNotFoundException
                            try {
                                ArrayList<String> captions = handleCaptionFile("data/mediatext/" + parts[5], mediaType);
                                if (captions != null) {
                                    for (String caption : captions) {
                                        System.out.println(caption);
                                    }
                                }
                            } catch (MediaNotFoundException e) {
                                System.err.println(e.getMessage());
                            }

                            break;
                        case Constants.PODCAST:
                            if (parts.length < 8) {
                                throw new InvalidLineException("Podcast details incomplete. Skipping this line.");
                            }
                            // split name('#')
                            String[] hostArray = parts[2].split("#");
                            ArrayList<String> hosts = new ArrayList<String>();
                            for (String host : hostArray) {
                                hosts.add(host);
                            }
                            int episodeNumber = Integer.parseInt(parts[5]);
                            int podcastDuration = Integer.parseInt(parts[6]);
                            Podcast podcast = new Podcast(parts[0], parts[1], hosts, parts[3], parts[4], episodeNumber, podcastDuration, parts[7]);
                            playlist.addMedia(podcast);

                            // try-catch to report MediaNotFoundException
                            try {
                                ArrayList<String> captions = handleCaptionFile("data/mediatext/" + parts[7], mediaType);
                                if (captions != null) {
                                    for (String caption : captions) {
                                        System.out.println(caption);
                                    }
                                }
                            } catch (MediaNotFoundException e) {
                                System.err.println(e.getMessage());
                            }
                            
                            break;
                        case Constants.SHORTCLIP:
                            if (parts.length < 5) {
                                throw new InvalidLineException("ShortClip details incomplete. Skipping this line.");
                            }
                            
                            int shortClipDuration = Integer.parseInt(parts[3]);
                            ShortClip shortClip = new ShortClip(parts[0], parts[1], parts[2], shortClipDuration, parts[4]);
                            playlist.addMedia(shortClip);

                            // try-catch to report MediaNotFoundException
                            try {
                                ArrayList<String> captions = handleCaptionFile("data/mediatext/" + parts[4], mediaType);
                                if (captions != null) {
                                    for (String caption : captions) {
                                        System.out.println(caption);
                                    }
                                }
                            } catch (MediaNotFoundException e) {
                                System.err.println(e.getMessage());
                            }
                            
                            break;
                    } 
                } catch (NumberFormatException e) {
                    System.err.println("Duration in mins not in correct format. Skipping this line.");
                } catch (InvalidLineException | InvalidFormatException | PlaylistFullException e) {
                    System.err.println(e.getMessage());
                }            
            }
        } catch (FileNotFoundException e) {
            System.err.println("Invalid or missing file.");
        } finally{
            if(inputStream != null) {
                inputStream.close();
            }
        }
    }


    private ArrayList<String> handleCaptionFile(String filename, String mediaType) throws MediaNotFoundException {
        Scanner inputStream = null;
        ArrayList<String> captions = new ArrayList<>();
        try {
            inputStream = new Scanner(new FileInputStream(filename));
            while (inputStream.hasNextLine()) {
                String line = inputStream.nextLine();
                captions.add(line);
            }
            if (captions.isEmpty()) {
                throw new MediaNotFoundException(getMediaMessage(mediaType));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Invalid or missing caption file.");
            return null;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return captions;
    }

    private String getMediaMessage(String mediaType) {
        switch (mediaType) {
            case Constants.SONG:
                return "Cannot show lyrics. Media not found.";
            case Constants.PODCAST:
                return "Cannot show captions for podcast. Media not found.";
            case Constants.SHORTCLIP:
                return "Cannot show captions for short clip. Media not found.";
        }
        return "Media not found."; //
    }

    /**
     * Main Menu
     */
    private void runMainMenu() {
        Scanner keyboard = Constants.keyboard;
        boolean exit = false;
        while (!exit) {
            printMainMenu();
            int option = keyboard.nextInt();
            keyboard.nextLine();
            
            switch(option) {
                case Constants.CREATE_PLAYLIST:
                    createNewPlaylist(keyboard);
                    break;
                case Constants.VIEW_PLAYLIST:
                    viewAllPlaylists();
                    break;
                case Constants.VIEW_CONTENTS:
                    viewPlaylistContents(keyboard);
                    break;
                case Constants.REMOVE_PLAYLIST:
                    removePlaylist(keyboard);
                    break;
                case Constants.MODIFY_PLAYLIST:
                    modifyPlaylist(keyboard);
                    break;
                case Constants.PLAY_CONTENTS:
                    try {
                        playPlaylistContents(keyboard);
                    } catch (MediaNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case Constants.QUIT_MAIN:
                    savePlaylist(new String[0]);
                    System.out.println("Playlist data saved.");
                    System.out.println("Exiting Musify. Goodbye, " + userName + ".");
                    exit = true;
                    break;
                default:
                    System.out.println(Constants.INVALID_INPUT);

            }
        }
    }

    private void printMainMenu() {
        System.out.println("Please select one of the options.");
        System.out.println("1. Create a new playlist.");
        System.out.println("2. View all playlist.");
        System.out.println("3. View contents of a playlist.");
        System.out.println("4. Remove a playlist.");
        System.out.println("5. Modify a playlist.");
        System.out.println("6. Play contents of a playlist.");
        System.out.println("7. Exit Musify.");
    }

    private void displayWelcomeMessage() {
        System.out.print("Welcome " + userName + ". Choose your music, podcasts or watch short clips.");
    }

    // Option 1
    private void createNewPlaylist(Scanner keyboard){
        //
        System.out.print("Enter Playlist Name: ");
        String playlistName = keyboard.nextLine();
        System.out.print("Enter Playlist Type: ");
        String playlistType = keyboard.nextLine();
        System.out.print("Enter a filename to save the playlist: ");
        String filename = keyboard.nextLine();
        
        try {
            Playlist newPlaylist = new Playlist(playlistName, playlistType, filename);
            playlists.add(newPlaylist);

            System.out.println("Add some " + playlistType + " to your Playlist.");
            boolean addingMedia = true;
            while (addingMedia) {
                System.out.print("Enter A to add a " + playlistType + " to the playlist or Q to quit adding: ");
                String choice = keyboard.nextLine();
                if (choice.equalsIgnoreCase(Constants.QUIT_KEY)) {
                    addingMedia = false;
                } else if (choice.equalsIgnoreCase(Constants.ADD_KEY)) {
                    addMediaToPlaylist(keyboard, newPlaylist, playlistType);
                }
            }
            // save the new playlist file
            savePlaylistFile(newPlaylist);
        } catch(InvalidFormatException e) {
            System.err.println(e.getMessage());
        }
    }

    private void addMediaToPlaylist(Scanner keyboard, Playlist playlist, String mediaType) {
        try {
            switch (mediaType.toUpperCase()) {
                case Constants.SONG:
                    System.out.print("Enter the title: ");
                    String songTitle = keyboard.nextLine();
                    System.out.print("Enter the description: ");
                    String songDescription = keyboard.nextLine();
                    System.out.print("Enter duration in mins: ");
                    int songDuration = keyboard.nextInt();
                    
                    keyboard.nextLine(); // newline
                    System.out.print("Enter the filename for the captions or lyrics: ");
                    String songCaptionFileName = keyboard.nextLine();
                    //validate caption file
                    try {
                        validateFile(songCaptionFileName);
                    } catch (FileNotFoundException e) {
                        System.out.println("Invalid or missing caption file.");
                    }

                    System.out.print("Add the Genre: ");
                    String genre = keyboard.nextLine();

                    System.out.print("Enter the artist Name or Q to stop entering the artist name: ");
                    ArrayList<String> artists = new ArrayList<String>();
                    boolean addingArtist = true;
                    while(addingArtist) {
                        String artist = keyboard.nextLine();
                        if (artist.equalsIgnoreCase("Q")) {
                            break;
                        }
                        artists.add(artist);
                        System.out.print("Enter the artist Name or Q to stop entering the artist name: ");
                    }

                    Song song = new Song(songTitle, songDescription, artists, genre, songDuration, songCaptionFileName);
                    playlist.addMedia(song);
                    break;
                case Constants.PODCAST:
                    System.out.print("Enter the title: ");
                    String podcastTitle = keyboard.nextLine();
                    System.out.print("Enter the description: ");
                    String podcastDescription = keyboard.nextLine();
                    System.out.print("Enter duration in mins: ");
                    int podcastDuration = keyboard.nextInt();

                    keyboard.nextLine(); // newline
                    System.out.print("Enter the filename for the captions or lyrics: ");
                    String podcastCaptionFileName = keyboard.nextLine();
                    //validate caption file
                    try {
                        validateFile(podcastCaptionFileName);
                    } catch (FileNotFoundException e) {
                        System.out.println("Invalid or missing caption file.");
                    }

                    System.out.print("Add the category: ");
                    String category = keyboard.nextLine();

                    System.out.print("Enter the host Name or Q to stop entering the host name: ");
                    ArrayList<String> hosts = new ArrayList<String>();
                    boolean addingHost = true;
                    while(addingHost) {
                        String host = keyboard.nextLine();
                        if (host.equalsIgnoreCase("Q")) {
                            break;
                        }
                        hosts.add(host);
                        System.out.print("Enter the host Name or Q to stop entering the host name: ");
                    }
                    
                    System.out.print("Enter the series Name: ");
                    String seriesName = keyboard.nextLine();
                    System.out.print("Enter the episode Number: ");
                    int episodeNumber = keyboard.nextInt();

                    keyboard.nextLine(); // newline
                    Podcast podcast = new Podcast(podcastTitle, podcastDescription, hosts, category, seriesName, episodeNumber, podcastDuration, podcastCaptionFileName);
                    playlist.addMedia(podcast);
                    break;
                    
                case Constants.SHORTCLIP:
                    System.out.print("Enter the title: ");
                    String shortClipTitle = keyboard.nextLine();
                    System.out.print("Enter the description: ");
                    String shortClipDescription = keyboard.nextLine();
                    System.out.print("Enter duration in mins: ");
                    int shortClipDuration = keyboard.nextInt();
                    keyboard.nextLine(); // newline
                    System.out.print("Enter the filename for the captions or lyrics: ");
                    String shortClipCaptionFileName = keyboard.nextLine();
                    //validate caption file
                    try {
                        validateFile(shortClipCaptionFileName);
                    } catch (FileNotFoundException e) {
                        System.out.println("Invalid or missing caption file.");
                    }

                    System.out.print("Enter the artist Name: ");
                    String artistName = keyboard.nextLine();

                    ShortClip shortClip = new ShortClip(shortClipTitle, shortClipDescription, artistName, shortClipDuration, shortClipCaptionFileName);
                    playlist.addMedia(shortClip);
                    break;
            }
        } catch (InvalidFormatException | PlaylistFullException e) {
            System.err.println(e.getMessage());
        }
    }

    private void validateFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
    }

    // Option 2
    private void viewAllPlaylists() {
        // No Playlist found
        if (playlists.isEmpty()) {
            System.out.println("No playlists found.");
            return;
        }
        
        System.out.println("Here are your playlists-");
        System.out.printf(Constants.PLAYLIST_HEADER_FORMATTER, "#", "Type", "Playlist Name");
        System.out.println("----------------------------------------------");
        for (int i = 0; i < playlists.size(); i++) {
            Playlist playlist = playlists.get(i);
            System.out.printf(Constants.PLAYLIST_FORMATTER, i + 1, playlist.getMediaType(), playlist.getName());
        }
    }

    // Option 3
    private void viewPlaylistContents(Scanner keyboard) {
        // No Playlist found
        if (playlists.isEmpty()) {
            System.out.println("No playlists found.");
            return;
        }
        System.out.print("Enter Playlist Name: ");
        String playlistName = keyboard.nextLine();

        Playlist playlist = null;
        for (Playlist p : playlists) {
            if (p.getName().equalsIgnoreCase(playlistName)) {
                playlist = p;
                break;
            }
        }
        // Incorrect Playlist as input
        if (playlist == null) {
            System.out.println("No such playlist found with name: " + playlistName);
            return;
        }
        // View playlist in the option 5
        viewMedialistContents(playlist);
    }

    private void viewMedialistContents(Playlist playlist) {
        ArrayList<Media> mediaList = playlist.getMediaList();
        if (mediaList.isEmpty()) {
            System.out.println("No " + playlist.getMediaType().toLowerCase() + " in the playlist to view.");
            return;
        }

        String mediaType = playlist.getMediaType().toUpperCase();
        switch(mediaType) {
            case Constants.SONG:
                System.out.printf(Constants.SONG_PLAYLIST_HEADER, "Id", "Title", "Artist Name", "Description", "Genre", "Duration In Mins");
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
                for (int i = 0; i < mediaList.size(); i++) {
                    Song song = (Song) mediaList.get(i); // object
                    System.out.printf(Constants.SONG_PLAYLIST_DATA_FORMATTER, i + 1, song.getName(), String.join(",", song.getArtists()), song.getDescription(), song.getGenre(), song.getDuration());
                }
                break;
            case Constants.PODCAST:
                System.out.printf(Constants.PODCAST_PLAYLIST_HEADER, "Id", "Title", "Host Name(s)", "Description", "Category", "Series Name", "Episode#", "Duration In Mins");
                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------");
                for (int i = 0; i < mediaList.size(); i++) {
                    Podcast podcast = (Podcast) mediaList.get(i); // object
                    System.out.printf(Constants.PODCAST_DATA_FORMATTER, i + 1, podcast.getName(), String.join(",", podcast.getHosts()), podcast.getDescription(), podcast.getCategory(), podcast.getSeriesName(), podcast.getEpisodeNumber(), podcast.getDuration());
                }
                break;
            case Constants.SHORTCLIP:
                System.out.printf(Constants.SHORTCLIP_PLAYLIST_HEADER, "Id", "Title", "Artist Name", "Description", "Duration In Mins");
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
                for (int i = 0; i < mediaList.size(); i++) {
                    ShortClip shortClip = (ShortClip) mediaList.get(i); // object
                    System.out.printf(Constants.SHORTCLIP_DATA_FORMATTER, i + 1, shortClip.getName(), shortClip.getArtistName(), shortClip.getDescription(), shortClip.getDuration());
                }
                break;
        }

    }

    // Option 4
    private void removePlaylist(Scanner keyboard) {
        // No Playlist found
        if (playlists.isEmpty()) {
            System.out.println("No playlists found.");
            return;
        }
        System.out.print("Enter Playlist Name: ");
        String playlistName = keyboard.nextLine();

        Playlist playlistToRemove = null;
        for (Playlist p : playlists) {
            if (p.getName().equalsIgnoreCase(playlistName)) {
                playlistToRemove = p;
                break;
            }
        }

        // Incorrect Playlist as input
        if (playlistToRemove == null) {
            System.out.println("No such playlist found with name: " + playlistName);
            return;
        }

        playlists.remove(playlistToRemove);
        String filename = "data/playlist" + playlistToRemove.getFileName();
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
        System.out.println("Playlist removed successfully.");

    }


    /**
     * Playlist SubMenu
     */
    private void printSubMenu(String mediaType) {
        System.out.println("Please select one of the options.");
        System.out.println("1. View the playlist.");
        System.out.println("2. Add a new " + mediaType + ".");
        System.out.println("3. Remove a " + mediaType + ".");
        System.out.println("4. Exit and go back to main menu.");
    }

    // Option 5
    private void modifyPlaylist(Scanner keyboard) {
        // No Playlist found
        if (playlists.isEmpty()) {
            System.out.println("No playlists found.");
            return;
        }
        System.out.print("Enter Playlist Name: ");
        String playlistName = keyboard.nextLine();

        Playlist playlistToModify = null;
        for (Playlist p : playlists) {
            if (p.getName().equalsIgnoreCase(playlistName)) {
                playlistToModify = p;
                break;
            }
        }
        // Incorrect Playlist as input
        if (playlistToModify == null) {
            System.out.println("No such playlist found with name: " + playlistName);
            return;
        }

        boolean backToMain = false;
        String mediaType = playlistToModify.getMediaType().toLowerCase();
        while(!backToMain) {
            printSubMenu(mediaType);
            int subOption = keyboard.nextInt();
            keyboard.nextLine();

            switch(subOption) {
                case Constants.SUBMENU_VIEW_PLAYLIST:
                    viewMedialistContents(playlistToModify);
                    break;
                case Constants.ADD_MEDIA:
                    addMediaToPlaylist(keyboard, playlistToModify, mediaType);
                    savePlaylistFile(playlistToModify);
                    break;
                case Constants.REMOVE_MEDIA:
                    removeMediaFromPlaylist(keyboard, playlistToModify);
                    savePlaylistFile(playlistToModify);
                    break;
                case Constants.QUIT_SUBMENU:
                    backToMain = true;
                    break;
            }
        }

    }

    // Option 5: Scenario 3: remove the media
    private void removeMediaFromPlaylist(Scanner keyboard, Playlist playlist) {
        // remove media from an empty playlist
        if (playlist.getMediaList().isEmpty()) {
            System.out.println("The playlist is empty. No media to remove.");
            return;
        }

        System.out.print("Enter the " + playlist.getMediaType().toLowerCase() + " to remove: ");
        String mediaTitle = keyboard.nextLine();

        Media mediaToRemove = null;
        for (Media media : playlist.getMediaList()) {
            if (media.getName().equalsIgnoreCase(mediaTitle)) {
                mediaToRemove = media;
                break;
            }
        }
        // remove media that doesn't exist in the playlist
        if (mediaToRemove == null) {
            System.out.println("No such media found with title: " + mediaTitle);
            return;
        }

        playlist.getMediaList().remove(mediaToRemove);
        System.out.println("Media removed successfully.");

    }

    // Option 6
    private void playPlaylistContents(Scanner keyboard) throws MediaNotFoundException{
        // No Playlist found
        if (playlists.isEmpty()) {
            System.out.println("No playlists found.");
            return;
        }
        System.out.print("Enter Playlist Name: ");
        String playlistName = keyboard.nextLine();

        Playlist playlistToPlay = null;
        for (Playlist p : playlists) {
            if (p.getName().equalsIgnoreCase(playlistName)) {
                playlistToPlay = p;
                break;
            }
        }
        // Incorrect Playlist as input
        if (playlistToPlay == null) {
            System.out.println("No such playlist found with name: " + playlistName);
            return;
        }

        ArrayList<Media> mediaList = playlistToPlay.getMediaList();
        for (Media media : mediaList) {
            System.out.println("-----------------------------------------------------------------------------------");
            if (media instanceof Song) {
                Song song = (Song) media;
                System.out.println("Playing Song: " + song.getName() + " by " + String.join(",", song.getArtists()) + " for " + song.getDuration() + " mins.");
                ArrayList<String> captions = handleCaptionFile("data/mediatext/" + song.getCaptionFileName(), Constants.SONG);
                if (captions == null) {
                    throw new MediaNotFoundException(getMediaMessage(Constants.SONG));
                } else {
                    System.out.println("Here are the lyrics to sing along.");
                    for (String line : captions) {
                        System.out.println(line);
                    }
                }              
            } else if (media instanceof Podcast) {
                Podcast podcast = (Podcast) media;
                System.out.println("Playing Podcast: " + podcast.getName() + " by " + String.join(",", podcast.getHosts()) + " for " + podcast.getDuration() + " mins. This podcast is about " + podcast.getDescription());
                ArrayList<String> captions = handleCaptionFile("data/mediatext/" + podcast.getCaptionFileName(), Constants.PODCAST);
                if (captions == null) {
                    throw new MediaNotFoundException(getMediaMessage(Constants.PODCAST));
                } else {
                    System.out.println("Here are the contents of the podcast.");
                    for (String line : captions) {
                        System.out.println(line);
                    }
                }
            } else if (media instanceof ShortClip) {
                ShortClip shortClip = (ShortClip) media;
                System.out.println("Playing short clip: " + shortClip.getName() + " by " + shortClip.getArtistName() + " for " + shortClip.getDuration() + " mins.");
                
                ArrayList<String> captions = handleCaptionFile("data/mediatext/" + shortClip.getCaptionFileName(), Constants.SHORTCLIP);
                if (captions == null) {
                    throw new MediaNotFoundException(getMediaMessage(Constants.SHORTCLIP));
                } else {
                    System.out.println("Here are the contents of the short clip.");
                    for (String line : captions) {
                        System.out.println(line);
                    }
                }
            }
            System.out.println("-----------------------------------------------------------------------------------");
        }
        
    }


    // Option 7
    private void savePlaylist(String[] args) {
        String filename = args.length > 1 ? args[1] : "playlists.txt";
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(filename))) {
            for (Playlist playlist : playlists) {
                writer.println(playlist.toString());
                savePlaylistFile(playlist);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void savePlaylistFile(Playlist playlist) {
        String filename = "data/playlist/" + playlist.getFileName();
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(filename))) {
            for (Media media : playlist.getMediaList()) {
                writer.println(media.toString());
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}



