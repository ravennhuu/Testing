import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VotingPanel extends JFrame {
    private int voterId;
    private Map<String, List<Candidate>> candidatesByPosition;
    private Map<String, JComboBox<String>> positionComboBoxes;
    private JButton voteButton, logoutButton;
    
    public VotingPanel(int voterId) {
    	setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\avryl\\Downloads\\NATIONALIAN (1).png"));
        this.voterId = voterId;
        setTitle("Voting Panel | Bulldog's Choice");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        candidatesByPosition = loadCandidatesByPosition();
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Please vote for each position:", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel votingPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        
        positionComboBoxes = new HashMap<>();
        
        // Create a combo box for each position
        for (String position : candidatesByPosition.keySet()) {
            JPanel positionPanel = new JPanel(new BorderLayout(10, 5));
            positionPanel.add(new JLabel(position + ":", JLabel.LEFT), BorderLayout.WEST);
            
            JComboBox<String> comboBox = new JComboBox<>();
            comboBox.addItem("-- Select " + position + " --");
            for (Candidate candidate : candidatesByPosition.get(position)) {
                comboBox.addItem(candidate.getName() + " - " + candidate.getParty());
            }
            positionComboBoxes.put(position, comboBox);
            positionPanel.add(comboBox, BorderLayout.CENTER);
            
            votingPanel.add(positionPanel);
        }
        
        JScrollPane scrollPane = new JScrollPane(votingPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new VoterLogin().setVisible(true);
            dispose();
        });
        buttonPanel.add(logoutButton);
        
        voteButton = new JButton("Submit Votes");
        voteButton.addActionListener(new VoteListener());
        buttonPanel.add(voteButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private Map<String, List<Candidate>> loadCandidatesByPosition() {
        Map<String, List<Candidate>> candidatesMap = new HashMap<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM candidates ORDER BY position, name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String position = rs.getString("position");
                Candidate candidate = new Candidate(
                    rs.getInt("candidate_id"),
                    rs.getString("name"),
                    rs.getString("party"),
                    position,
                    rs.getInt("votes")
                );
                
                if (!candidatesMap.containsKey(position)) {
                    candidatesMap.put(position, new ArrayList<>());
                }
                candidatesMap.get(position).add(candidate);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading candidates: " + e.getMessage());
        }
        return candidatesMap;
    }
    
    private class VoteListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Verify all positions have been voted for
            for (String position : positionComboBoxes.keySet()) {
                if (positionComboBoxes.get(position).getSelectedIndex() <= 0) {
                    JOptionPane.showMessageDialog(null, 
                        "Please select a candidate for " + position, 
                        "Incomplete Voting", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            // Show confirmation with all selected candidates
            StringBuilder confirmation = new StringBuilder("You are about to vote for:\n\n");
            Map<String, Candidate> selectedCandidates = new HashMap<>();
            
            for (String position : positionComboBoxes.keySet()) {
                int selectedIndex = positionComboBoxes.get(position).getSelectedIndex() - 1;
                Candidate candidate = candidatesByPosition.get(position).get(selectedIndex);
                confirmation.append(position).append(": ")
                             .append(candidate.getName()).append(" (").append(candidate.getParty()).append(")\n");
                selectedCandidates.put(position, candidate);
            }
            
            confirmation.append("\nAre you sure you want to submit these votes?");
            
            int confirm = JOptionPane.showConfirmDialog(
                null, 
                confirmation.toString(), 
                "Confirm Votes", 
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    // Start transaction
                    conn.setAutoCommit(false);
                    
                    // Record votes for each position
                    for (String position : selectedCandidates.keySet()) {
                        Candidate candidate = selectedCandidates.get(position);
                        
                        // Record vote
                        String voteSql = "INSERT INTO votes (voter_id, candidate_id, position) VALUES (?, ?, ?)";
                        PreparedStatement voteStmt = conn.prepareStatement(voteSql);
                        voteStmt.setInt(1, voterId);
                        voteStmt.setInt(2, candidate.getId());
                        voteStmt.setString(3, position);
                        voteStmt.executeUpdate();
                        
                        // Update candidate vote count
                        String updateSql = "UPDATE candidates SET votes = votes + 1 WHERE candidate_id = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setInt(1, candidate.getId());
                        updateStmt.executeUpdate();
                    }
                    
                    // Mark voter as voted
                    String voterSql = "UPDATE voters SET has_voted = TRUE WHERE voter_id = ?";
                    PreparedStatement voterStmt = conn.prepareStatement(voterSql);
                    voterStmt.setInt(1, voterId);
                    voterStmt.executeUpdate();
                    
                    // Commit transaction
                    conn.commit();
                    
                    JOptionPane.showMessageDialog(null, "Votes recorded successfully!");
                    new VoterLogin().setVisible(true);
                    dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error recording votes: " + ex.getMessage());
                    try {
                        Connection conn = DBConnection.getConnection();
                        if (conn != null) {
                            conn.rollback();
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
    
    private static class Candidate {
        private int id;
        private String name;
        private String party;
        private String position;
        private int votes;
        
        public Candidate(int id, String name, String party, String position, int votes) {
            this.id = id;
            this.name = name;
            this.party = party;
            this.position = position;
            this.votes = votes;
        }
        
        public int getId() { return id; }
        public String getName() { return name; }
        public String getParty() { return party; }
        public String getPosition() { return position; }
        public int getVotes() { return votes; }
    }
}