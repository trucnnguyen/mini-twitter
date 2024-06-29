import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class AdminControlPanel extends JFrame {
    private static AdminControlPanel instance = null;
    private JTree treeView;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private Map<String, User> users;
    private Map<String, UserGroup> groups;

    private static final List<String> POSITIVE_WORDS = Arrays.asList("vibing", "chilling", "lit", "sick af", "good", "great", "excellent", "awesome", "fantastic", "amazing");

    // Method to count total tweets
    private int getTotalTweets() {
        return users.values().stream()
                .mapToInt(user -> user.getOwnTweets().size())
                .sum();
    }

    // Method to count positive tweets
    private int getPositiveTweetsCount() {
        return (int) users.values().stream()
                .flatMap(user -> user.getOwnTweets().stream())
                .filter(tweet -> POSITIVE_WORDS.stream().anyMatch(tweet.toLowerCase()::contains))
                .count();
    }

    // Method to calculate the percentage of positive tweets
    private double getPositiveTweetsPercentage() {
        int totalTweets = getTotalTweets();
        int positiveTweets = getPositiveTweetsCount();
        return totalTweets > 0 ? (double) positiveTweets / totalTweets * 100 : 0.0;
    }

    // Method to show feed analysis
    private void showFeedAnalysis() {
        int totalTweets = getTotalTweets();
        double positivePercentage = getPositiveTweetsPercentage();
        String message = String.format("Total Tweets: %d\nPositive Tweets: %.2f%%", totalTweets, positivePercentage);
        JOptionPane.showMessageDialog(this, message, "Feed Analysis", JOptionPane.INFORMATION_MESSAGE);
    }

    // Add to control panel
    private void addFeedAnalysisButton(JPanel controlPanel) {
        JButton feedAnalysisButton = new JButton("Analyze Feed");
        feedAnalysisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFeedAnalysis();
            }
        });
        controlPanel.add(feedAnalysisButton);
    }

    private AdminControlPanel() {
        setTitle("Admin Control Panel");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        users = new HashMap<>();
        groups = new HashMap<>();

        // Initialize Root Node
        rootNode = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(rootNode);
        treeView = new JTree(treeModel);
        treeView.setCellRenderer(new CustomTreeCellRenderer()); // Set custom renderer
        JScrollPane treeViewPane = new JScrollPane(treeView);

        // Control Panel
        JPanel controlPanel = new JPanel(new GridLayout(3, 3)); // Updated to 3x3 grid
        JButton addUserButton = new JButton("Add User");
        JButton addGroupButton = new JButton("Add Group");
        JButton addUserGroupButton = new JButton("Add User To Group");
        JButton addGroupToGroupButton = new JButton("Add Group to Group");
        JButton showTotalUsersButton = new JButton("Show Total Users");
        JButton showTotalGroupsButton = new JButton("Show Total Groups");
        JButton showTotalTweetsButton = new JButton("Show Total Tweets");
        JButton showUserViewButton = new JButton("Show User View");
        JButton feedAnalysisButton = new JButton("Analyze Feed");
        JButton verificationButton = new JButton("Verify");
        JButton lastUpdatedUserButton = new JButton("Last Updated User");

        controlPanel.add(addUserButton);
        controlPanel.add(addGroupButton);
        controlPanel.add(addUserGroupButton);
        controlPanel.add(addGroupToGroupButton);
        controlPanel.add(showTotalUsersButton);
        controlPanel.add(showTotalGroupsButton);
        controlPanel.add(showTotalTweetsButton);
        controlPanel.add(showUserViewButton);
        controlPanel.add(feedAnalysisButton);
        controlPanel.add(verificationButton);
        controlPanel.add(lastUpdatedUserButton);

        // Add components to the frame
        add(treeViewPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // Action listeners for buttons
        lastUpdatedUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String lastUpdatedUser = getLastUpdatedUser();
                JOptionPane.showMessageDialog(AdminControlPanel.this, "Last Updated User: " + lastUpdatedUser);
            }
        });

        verificationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateID();
            }
        });

        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userID = JOptionPane.showInputDialog("Enter User ID:");

                if (userID != null && !userID.trim().isEmpty() && !users.containsKey(userID)) {
                    User user = new User(userID);
                    users.put(userID, user);
                    DefaultMutableTreeNode userNode = new DefaultMutableTreeNode(userID);
                    rootNode.add(userNode);
                    treeModel.reload();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid or Duplicate User ID");
                }
            }
        });

        addGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupID = JOptionPane.showInputDialog("Enter Group ID:");
                if (groupID != null && !groupID.trim().isEmpty() && !groups.containsKey(groupID)) {
                    UserGroup group = new UserGroup(groupID);
                    groups.put(groupID, group);
                    DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(groupID);
                    rootNode.add(groupNode);
                    treeModel.reload();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid or Duplicate Group ID");
                }
            }
        });

        addUserGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupID = JOptionPane.showInputDialog("Enter groupID to Add users:");
                String userID = JOptionPane.showInputDialog("Enter User ID to Add Group:");
                if (userID != null && groupID != null && users.containsKey(userID) && groups.containsKey(groupID)) {
                    User user = users.get(userID);
                    UserGroup group = groups.get(groupID);
                    group.addMember(user);
                    DefaultMutableTreeNode groupNode = findNode(rootNode, groupID);
                    if (groupNode != null) {
                        DefaultMutableTreeNode userNode = new DefaultMutableTreeNode(userID);
                        groupNode.add(userNode);
                        treeModel.reload();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid or Duplicate User ID and Group ID");
                }
            }
        });

        addGroupToGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupID = JOptionPane.showInputDialog("Enter Group ID to add a Sub-group:");
                String subGroupID = JOptionPane.showInputDialog("Enter Sub-group ID to add:");
                if (groupID != null && subGroupID != null && groups.containsKey(groupID) && groups.containsKey(subGroupID)) {
                    UserGroup group = groups.get(groupID);
                    UserGroup subGroup = groups.get(subGroupID);

                    // Check to prevent adding a group to itself
                    if (group == subGroup) {
                        JOptionPane.showMessageDialog(null, "Cannot add a group to itself.");
                        return;
                    }

                    // Add sub-group to the parent group
                    group.addMember(subGroup);

                    // Find the parent group node in the tree
                    DefaultMutableTreeNode parentNode = findNode(rootNode, groupID);
                    if (parentNode != null) {
                        // Add the sub-group node and its members to the parent group node
                        DefaultMutableTreeNode subGroupNode = new DefaultMutableTreeNode(subGroupID);
                        parentNode.add(subGroupNode);
                        addGroupMembersToTree(subGroup, subGroupNode);
                        treeModel.reload();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Group ID or Sub-group ID");
                }
            }
        });

        showTotalUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Total Users: " + users.size());
            }
        });

        showTotalGroupsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Total Groups: " + groups.size());
            }
        });

        showTotalTweetsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalTweets = users.values().stream().mapToInt(user -> user.getOwnTweets().size()).sum();
                JOptionPane.showMessageDialog(null, "Total Tweets: " + totalTweets);
            }
        });

        showUserViewButton.addActionListener(new ActionListener() { // Action listener for the new button
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeView.getLastSelectedPathComponent();
                if (selectedNode != null && users.containsKey(selectedNode.getUserObject().toString())) {
                    User user = users.get(selectedNode.getUserObject().toString());
                    UserView userView = UserView.getUserView(user);
                    userView.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a user from the tree");
                }
            }
        });

        feedAnalysisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFeedAnalysis();
            }
        });
    }

    public static AdminControlPanel getInstance() {
        if (instance == null) {
            instance = new AdminControlPanel();
        }
        return instance;
    }

    private String getLastUpdatedUser() {
        return users.values().stream()
                .max(Comparator.comparingLong(User::getUpdatedAt))
                .map(User::getUserID).orElse(null);
    }

    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode rootNode, String id) {
        Enumeration<TreeNode> enumeration = rootNode.depthFirstEnumeration();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) enumeration.nextElement();
            if (id.equals(currentNode.getUserObject().toString())) {
                return currentNode;
            }
        }
        return null;
    }

    private void addGroupMembersToTree(UserGroup group, DefaultMutableTreeNode parentNode) {
        for (Object member : group.getMembers()) {
            if (member instanceof User) {
                DefaultMutableTreeNode userNode = new DefaultMutableTreeNode(((User) member).getUserID());
                parentNode.add(userNode);
            } else if (member instanceof UserGroup) {
                DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(((UserGroup) member).getGroupID());
                parentNode.add(groupNode);
                addGroupMembersToTree((UserGroup) member, groupNode);
            }
        }
    }

    private void validateID() {
        StringBuilder result = new StringBuilder();
        boolean valid = true;

        for (String userID : users.keySet()) {
            if (userID.contains(" ")) {
                result.append(userID).append(" User ID contains whitespace");
                valid = false;
            }
        }

        for (String groupID : groups.keySet()) {
            if (groupID.contains(" ")) {
                result.append(groupID).append(" Group ID contains whitespace");
                valid = false;
            }
        }

        if (valid) {
            result.append("All userID and groupID are valid");
        }

        JOptionPane.showMessageDialog(this, result.toString(), "ID Validation", JOptionPane.INFORMATION_MESSAGE);
    }

    public User findUserByID(String userID) {
        return users.get(userID);
    }

    // Inner class for custom tree cell renderer
    private class CustomTreeCellRenderer extends DefaultTreeCellRenderer {

        private Icon userIcon;
        private Icon groupIcon;

        public CustomTreeCellRenderer() {
            // Load icons from resources
            userIcon = new ImageIcon(getClass().getResource("icon/user.png"));
            groupIcon = new ImageIcon(getClass().getResource("icon/users.png"));
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object nodeObject = node.getUserObject();
                if (nodeObject != null) {
                    if (users.containsKey(nodeObject.toString())) {
                        setIcon(userIcon);
                    } else if (groups.containsKey(nodeObject.toString())) {
                        setIcon(groupIcon);
                    }
                }
            }

            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminControlPanel.getInstance().setVisible(true);
        });
    }
}
