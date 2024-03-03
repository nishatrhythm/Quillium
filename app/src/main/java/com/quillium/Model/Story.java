package com.quillium.Model;

import java.util.ArrayList;

public class Story {

    private String storyBy;

    public String getStoryBy() {
        return storyBy;
    }

    public void setStoryBy(String storyBy) {
        this.storyBy = storyBy;
    }

    public String getStoryAt() {
        return storyAt;
    }

    public Story() {
    }

    public void setStoryAt(String storyAt) {
        this.storyAt = storyAt;
    }

    public ArrayList<UserStories> getStories() {
        return stories;
    }

    public void setStories(ArrayList<UserStories> stories) {
        this.stories = stories;
    }

    private String storyAt;
    private long  storyAtLong;

    public long getStoryAtLong() {
        return storyAtLong;
    }

    public void setStoryAtLong(long storyAtLong) {
        this.storyAtLong = storyAtLong;
    }

    public Story(long storyAtLong) {
        this.storyAtLong = storyAtLong;
    }

    ArrayList<UserStories> stories;

}
