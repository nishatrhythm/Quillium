package com.quillium.Model;

public class UserStories {
    private String image;
    private long StoryAtLong;

    public long getStoryAtLong() {
        return StoryAtLong;
    }

    public UserStories(long storyAtLong) {
        StoryAtLong = storyAtLong;
    }

    public void setStoryAtLong(long storyAtLong) {
        StoryAtLong = storyAtLong;
    }

    private String storyAt;

    public UserStories(String image, String storyAt) {
        this.image = image;
        this.storyAt = storyAt;
    }

    public UserStories() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(String storyAt) {
        this.storyAt = storyAt;
    }
}
