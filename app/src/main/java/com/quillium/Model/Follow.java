package com.quillium.Model;

public class Follow {
    private String followedBy;
    private String followedAt;

    public Follow() {
    }

    public String getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(String followedBy) {
        this.followedBy = followedBy;
    }

    public String getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(String followedAt) {
        this.followedAt = followedAt;
    }
}
