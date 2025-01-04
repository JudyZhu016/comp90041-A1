package entities;

import exceptions.InvalidFormatException;


public class ShortClip extends Media {
    private String artistName;

    public ShortClip(String name, String description, String artistName, int duration, String captionFileName) throws InvalidFormatException{
        super(name, description, duration, captionFileName);
        this.artistName = artistName;
    }

    // GETTERS
    public String getArtistName() {return artistName;}


    @Override
    public String toString() {
        return name + "," + description + "," + artistName + "," + duration + "," + captionFileName;
    }
}
