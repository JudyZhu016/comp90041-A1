package entities;

import exceptions.InvalidFormatException;

public abstract class Media {
    protected String name;
    protected String description;
    protected int duration;
    protected String captionFileName;

    public Media(String name, String description, int duration, String captionFileName) throws InvalidFormatException {
        if (duration <= 0) {
            throw new InvalidFormatException("Duration in mins not in correct format. Skipping this line.");
        }
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.captionFileName = captionFileName;
    }

    // GETTERS
    public String getName() {return name;}
    public String getDescription() {return description;}
    public int getDuration() {return duration;}
    public String getCaptionFileName() {return captionFileName;}

    @Override
    public abstract String toString();
}
