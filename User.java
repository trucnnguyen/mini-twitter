import java.util.ArrayList;
import java.util.List;

public class User {
    private String userID;
    private List<User> followers;
    private List<User> followings;
    private List<String> ownTweets;
    private List<String> newsFeed;

    public User(String userID) {
        this.userID = userID;
        this.followers = new ArrayList<>();
        this.followings = new ArrayList<>();
        this.ownTweets = new ArrayList<>();
        this.newsFeed = new ArrayList<>();
    }

    public String getUserID() {
        return userID;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public List<User> getFollowings() {
        return followings;
    }

    public List<String> getOwnTweets() {
        return ownTweets;
    }

    public List<String> getNewsFeed() {
        return newsFeed;
    }

    public void follow(User user) {
        if (!followings.contains(user)) {
            followings.add(user);
            user.addFollower(this);
            updateNewsFeed();
        }
    }

    private void addFollower(User user) {
        if (!followers.contains(user)) {
            followers.add(user);
        }
    }

    public void postTweet(String message) {
        String formattedMessage = userID + ": " + message;
        ownTweets.add(formattedMessage);
        updateNewsFeed();
        notifyFollowers(formattedMessage);
    }

    private void notifyFollowers(String message) {
        for (User follower : followers) {
            follower.updateNewsFeed();
        }
    }

    public void updateNewsFeed() {
        newsFeed.clear();
        // Add user's own tweets
        newsFeed.addAll(ownTweets);
        // Add tweets from followings
        for (User following : followings) {
            newsFeed.addAll(following.getOwnTweets());
        }
    }
}
