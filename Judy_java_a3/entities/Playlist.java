package entities;

import exceptions.InvalidFormatException;
import exceptions.PlaylistFullException;
import java.util.ArrayList;

public class Playlist {
    private String name;
    private String mediaType;
    private String fileName;
    private ArrayList<Media> mediaList = new ArrayList<>();

    public Playlist(String name, String mediaType, String fileName) throws InvalidFormatException {
        String upperMediaType = mediaType.toUpperCase();
        if (!upperMediaType.equals("SONG") && !upperMediaType.equals("PODCAST") && !upperMediaType.equals("SHORTCLIP")) {
            throw new InvalidFormatException("Incorrect Media Type. Skipping this line.");
        }
        this.name = name;
        this.mediaType = mediaType;
        this.fileName = fileName;
    }

    // GETTERS
    public String getName() {return name;}
    public String getMediaType() {return mediaType;}
    public String getFileName() {return fileName;}
    public ArrayList<Media> getMediaList() {return mediaList;}


    public void addMedia(Media media) throws PlaylistFullException {
        if (mediaList.size() > 5) {
            throw new PlaylistFullException("Playlist " + name + " is full. You cannot add new " + mediaType + " to this playlist.");
        }
        mediaList.add(media);
    }

    @Override
    public String toString() {
        return name + "," + mediaType + "," + fileName;
    }    
    
}
