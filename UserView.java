import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class UserView extends JFrame {
    private User user;
    private DefaultListModel<String> followingsModel;
    private DefaultListModel<String> followersModel;
    private DefaultListModel<String> newsFeedModel;

    private static Map<String, UserView> openViews = new HashMap<>();

    public UserView(User user) {
        this.user = user;
        setTitle("User View - " + user.getUserID());
        setSize(600, 400); // Adjusted size for better layout
        setLayout(new BorderLayout(10, 10)); // Add gaps between components

        // Followings list
        followingsModel = new DefaultListModel<>();
        refreshFollowings(); // Initialize the followings list
        JList<String> followingsList = new JList<>(followingsModel);
        JScrollPane followingsPane = new JScrollPane(followingsList);
        followingsPane.setBorder(BorderFactory.createTitledBorder("Your Followings"));
        followingsPane.setPreferredSize(new Dimension(150, 300)); // Set preferred size

        // Followers list
        followersModel = new DefaultListModel<>();
        refreshFollowers(); // Initialize the followers list
        JList<String> followersList = new JList<>(followersModel);
        JScrollPane followersPane = new JScrollPane(followersList);
        followersPane.setBorder(BorderFactory.createTitledBorder("Your Followers"));
        followersPane.setPreferredSize(new Dimension(150, 300)); // Set preferred size

        // News feed list
        newsFeedModel = new DefaultListModel<>();
        refreshNewsFeed(); // Initialize the news feed list
        JList<String> newsFeedList = new JList<>(newsFeedModel);
        JScrollPane newsFeedPane = new JScrollPane(newsFeedList);
        newsFeedPane.setBorder(BorderFactory.createTitledBorder("News Feed"));
        newsFeedPane.setPreferredSize(new Dimension(250, 300)); // Set preferred size

        // Text areas and buttons
        JTextField followUserField = new JTextField();
        followUserField.setPreferredSize(new Dimension(100, 25)); // Set preferred size
        JButton followUserButton = new JButton("Follow");
        followUserButton.setPreferredSize(new Dimension(80, 25)); // Set preferred size
        JTextField tweetField = new JTextField();
        tweetField.setPreferredSize(new Dimension(100, 25)); // Set preferred size
        JButton postTweetButton = new JButton("Post Tweet");
        postTweetButton.setPreferredSize(new Dimension(100, 25)); // Set preferred size

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for better control
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding

        // Adding components to action panel
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2; gbc.fill = GridBagConstraints.HORIZONTAL;
        actionPanel.add(new JLabel("Follow User ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.8;
        actionPanel.add(followUserField, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.2;
        actionPanel.add(followUserButton, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        actionPanel.add(new JLabel("Tweet:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.8;
        actionPanel.add(tweetField, gbc);
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.2;
        actionPanel.add(postTweetButton, gbc);

        // Add components to the frame
        add(followingsPane, BorderLayout.WEST);
        add(followersPane, BorderLayout.EAST);
        add(newsFeedPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        // Action listeners
        followUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userIDToFollow = followUserField.getText();
                User userToFollow = AdminControlPanel.getInstance().findUserByID(userIDToFollow);
                if (userToFollow != null) {
                    user.follow(userToFollow);
                    refreshFollowings();
                    refreshNewsFeed();
                } else {
                    JOptionPane.showMessageDialog(null, "User not found");
                }
            }
        });

        postTweetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = tweetField.getText();
                user.postTweet(message);
                refreshNewsFeed();
            }
        });

        openViews.put(user.getUserID(), this);
    }

    // Refresh the followings list
    private void refreshFollowings() {
        followingsModel.clear();
        for (User u : user.getFollowings()) {
            followingsModel.addElement(u.getUserID());
        }
    }

    // Refresh the followers list
    private void refreshFollowers() {
        followersModel.clear();
        for (User u : user.getFollowers()) {
            followersModel.addElement(u.getUserID());
        }
    }

    // Refresh the news feed list
    private void refreshNewsFeed() {
        newsFeedModel.clear();
        for (String message : user.getNewsFeed()) {
            newsFeedModel.addElement(message);
        }
    }

    // Override the setVisible method to refresh lists before showing the window
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            refreshFollowings();
            refreshFollowers();
            refreshNewsFeed();
        }
        super.setVisible(visible);
    }

    public static UserView getUserView(User user) {
        if (openViews.containsKey(user.getUserID())) {
            UserView existingView = openViews.get(user.getUserID());
            existingView.refreshFollowings();
            existingView.refreshFollowers();
            existingView.refreshNewsFeed();
            return existingView;
        } else {
            UserView newUserView = new UserView(user);
            openViews.put(user.getUserID(), newUserView);
            return newUserView;
        }
    }
}
