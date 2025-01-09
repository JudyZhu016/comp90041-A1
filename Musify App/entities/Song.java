package entities;

import exceptions.InvalidFormatException;
import java.util.ArrayList;


public class Song extends Media {
    private ArrayList<String> artists = new ArrayList<String>();
    private String genre;

    public Song(String name, String description, ArrayList<String> artists, String genre, int duration, String captionFileName) throws InvalidFormatException {
        super(name, description, duration, captionFileName);
        String upperGenre = genre.toUpperCase();
        if (!upperGenre.equals("POP") && !upperGenre.equals("ROCK") && !upperGenre.equals("JAZZ")) {
            throw new InvalidFormatException("Incorrect Genre for Song. Skipping this line.");
        }
        this.artists = artists;
        this.genre = genre;
    }

    // GETTERS
    public ArrayList<String> getArtists() {return artists;}
    public String getGenre() {return genre;}

    @Override
    public String toString() {
        return name + "," + description + "," + String.join("#", artists) + "," + genre + "," + duration + "," + captionFileName;
    }
}
