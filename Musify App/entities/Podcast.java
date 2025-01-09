package entities;

import exceptions.InvalidFormatException;
import java.util.ArrayList;
//import util.Constants;

public class Podcast extends Media {
    private ArrayList<String> hosts = new ArrayList<String>();
    private String category;
    private String seriesName;
    private int episodeNumber;

    public Podcast(String name, String description, ArrayList<String> hosts, String category, String seriesName, int episodeNumber, int duration, String captionFileName) throws InvalidFormatException {
        super(name, description, duration, captionFileName);
        String upperCategory = category.toUpperCase();
        if (!upperCategory.equals("HEALTH") && !upperCategory.equals("EDUCATION") && !upperCategory.equals("TECHNOLOGY")) {
            throw new InvalidFormatException("Incorrect Category for Podcast. Skipping this line.");
        }
        if (episodeNumber <= 0) {
            throw new InvalidFormatException("Episode number not in correct format. Skipping this line.");
        }
        this.hosts = hosts;
        this.category = category;
        this.seriesName = seriesName;
        this.episodeNumber = episodeNumber;
    }

    // GETTERS
    public ArrayList<String> getHosts() {return hosts;}
    public String getCategory() {return category;}
    public String getSeriesName() {return seriesName;}
    public int getEpisodeNumber() {return episodeNumber;}

    @Override
    public String toString() {
        return name + "," + description + "," + String.join("#", hosts) + "," + category + "," + seriesName + "," + episodeNumber + "," + duration + "," + captionFileName;
    }
}
