import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;


public class AdminDashboard extends JFrame {
	private JTabbedPane tabbedPane;

    public AdminDashboard() {
    	setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\avryl\\Downloads\\NATIONALIAN (1).png"));
        setTitle("Admin Dashboard | Bulldog's Choice");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Add tabs
        tabbedPane.addTab("Manage Candidates", new ManageCandidatesPanel());
        tabbedPane.addTab("View Results", new ResultsPanel());

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            // Assuming AdminLogin is your main login screen
            new AdminLogin().setVisible(true);
            dispose();
        });

        // Add components to frame
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(logoutButton, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Panel for managing candidates, including adding, removing, and refreshing the list.
     */
    private class ManageCandidatesPanel extends JPanel {
        private JTable candidatesTable;
        private JButton addButton, removeButton, refreshButton;

        public ManageCandidatesPanel() {
            setLayout(new BorderLayout());

            // Initialize the table. We override getColumnClass to handle ImageIcon rendering.
            candidatesTable = new JTable() {
                @Override
                public Class<?> getColumnClass(int column) {
                    // Assuming image is the 5th column (0-based index 4)
                    if (column == 4) {
                        return ImageIcon.class;
                    }
                    return Object.class;
                }
            };
            
            // Populate the table initially
            refreshTable();

            // Set custom renderer for the image column to display ImageIcon objects
            candidatesTable.setRowHeight(100); // Increase row height to accommodate images
            TableColumn imageColumn = candidatesTable.getColumnModel().getColumn(4);
            imageColumn.setCellRenderer(new ImageRenderer());

            // Add table to a scroll pane for scrollability
            JScrollPane scrollPane = new JScrollPane(candidatesTable);
            add(scrollPane, BorderLayout.CENTER);

            // Create and add buttons for actions
            JPanel buttonPanel = new JPanel();

            addButton = new JButton("Add Candidate");
            // Add action listener to show the dialog for adding a new candidate
            addButton.addActionListener(e -> showAddCandidateDialog());
            buttonPanel.add(addButton);

            removeButton = new JButton("Remove Selected");
            // Add action listener to remove the selected candidate from the table and database
            removeButton.addActionListener(e -> removeSelectedCandidate());
            buttonPanel.add(removeButton);

            refreshButton = new JButton("Refresh");
            // Add action listener to refresh the candidate list from the database
            refreshButton.addActionListener(e -> refreshTable());
            buttonPanel.add(refreshButton);

            add(buttonPanel, BorderLayout.SOUTH);
        }

        /**
         * Refreshes the candidates table by fetching the latest data from the database.
         */
        private void refreshTable() {
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT candidate_id, name, party, position, image FROM candidates ORDER BY candidate_id")) {

                // Create a new DefaultTableModel for the table
                DefaultTableModel model = new DefaultTableModel() {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false; // Make all cells non-editable
                    }
                };

                // Define column names for the table
                model.addColumn("ID");
                model.addColumn("Name");
                model.addColumn("Party");
                model.addColumn("Position");
                model.addColumn("Image");

                // Iterate through the ResultSet and add data to the table model
                while (rs.next()) {
                    Object[] row = new Object[5];
                    row[0] = rs.getInt("candidate_id");
                    row[1] = rs.getString("name");
                    row[2] = rs.getString("party");
                    row[3] = rs.getString("position");
                    
                    // Handle image blob: retrieve, convert to ImageIcon, and scale
                    Blob imageBlob = rs.getBlob("image");
                    if (imageBlob != null) {
                        try (InputStream in = imageBlob.getBinaryStream()) {
                            BufferedImage image = ImageIO.read(in);
                            if (image != null) {
                                ImageIcon icon = new ImageIcon(image.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
                                row[4] = icon;
                            } else {
                                row[4] = null; // No image data found or invalid format
                            }
                        }
                    } else {
                        row[4] = null; // No image in database
                    }
                    
                    model.addRow(row);
                }

                // Set the updated model to the candidates table
                candidatesTable.setModel(model);

            } catch (SQLException | IOException e) {
                // Display error message if refreshing the table fails
                JOptionPane.showMessageDialog(this, "Error refreshing candidates table: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        /**
         * Displays a dialog for adding a new candidate to the database.
         */
        private void showAddCandidateDialog() {
            JDialog dialog = new JDialog(AdminDashboard.this, "Add Candidate", true);
            dialog.setSize(500, 450); // Increased size to accommodate image preview
            dialog.setLocationRelativeTo(AdminDashboard.this);

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5); // Padding
            gbc.anchor = GridBagConstraints.WEST; // Align components to the left
            gbc.fill = GridBagConstraints.HORIZONTAL; // Make components fill horizontally

            // Candidate Name Field
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("Name:"), gbc);
            gbc.gridx = 1;
            JTextField nameField = new JTextField(20);
            panel.add(nameField, gbc);

            // Party Field
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(new JLabel("Party:"), gbc);
            gbc.gridx = 1;
            JTextField partyField = new JTextField(20);
            panel.add(partyField, gbc);

            // Position Dropdown
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(new JLabel("Position:"), gbc);
            gbc.gridx = 1;
            // Define available positions
            JComboBox<String> positionCombo = new JComboBox<>(new String[]{
                "President", "Vice President", "Secretary", 
                "Treasurer", "Auditor"
            });
            panel.add(positionCombo, gbc);

            // Image Upload Section
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(new JLabel("Image:"), gbc);
            gbc.gridx = 1;
            JButton uploadButton = new JButton("Upload Image");
            JLabel imagePreview = new JLabel(); // Label to display image preview
            imagePreview.setPreferredSize(new Dimension(150, 150));
            imagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            imagePreview.setHorizontalAlignment(SwingConstants.CENTER); // Center the image

            JPanel imagePanel = new JPanel(new BorderLayout());
            imagePanel.add(uploadButton, BorderLayout.NORTH);
            imagePanel.add(imagePreview, BorderLayout.CENTER);
            panel.add(imagePanel, gbc);

            // Use a final array to hold the mutable byte array for imageData.
            // This allows the lambda (uploadButton.addActionListener) to modify it,
            // and the saveButton.addActionListener to access the modified value.
            final byte[][] imageDataWrapper = {null}; 
            
            // Action listener for the upload button
            uploadButton.addActionListener(e -> {
                final JFileChooser fileChooser = new JFileChooser();
                fileChooser.setAcceptAllFileFilterUsed(false); // Only allow specific file types
                fileChooser.addChoosableFileFilter(
                    new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
                
                int returnValue = fileChooser.showOpenDialog(dialog);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        BufferedImage img = ImageIO.read(selectedFile);
                        if (img != null) {
                            // Scale the image for preview and storage
                            Image scaledImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                            BufferedImage bufferedScaledImg = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);
                            Graphics2D g2d = bufferedScaledImg.createGraphics();
                            g2d.drawImage(scaledImg, 0, 0, null);
                            g2d.dispose();
                            
                            // Convert the scaled image to a byte array (JPG format)
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(bufferedScaledImg, "jpg", baos);
                            imageDataWrapper[0] = baos.toByteArray(); // Store image data in the wrapper
                            
                            // Update the image preview label
                            imagePreview.setIcon(new ImageIcon(scaledImg));
                        }
                    } catch (IOException ex) {
                        // Handle errors during image processing
                        JOptionPane.showMessageDialog(dialog,
                            "Error processing image: " + ex.getMessage(),
                            "Image Error",
                            JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            });

            // Buttons for Cancel and Save
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 2; // Span across two columns
            gbc.fill = GridBagConstraints.NONE; // Do not fill horizontally
            gbc.anchor = GridBagConstraints.CENTER; // Center buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dialog.dispose()); // Close dialog on cancel
            buttonPanel.add(cancelButton);

            JButton saveButton = new JButton("Save");
            // Action listener for the save button
            saveButton.addActionListener(e -> {
                String name = nameField.getText().trim();
                String party = partyField.getText().trim();
                String position = (String) positionCombo.getSelectedItem();

                // Validate input
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name is required!", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try (Connection conn = DBConnection.getConnection()) {
                    // SQL INSERT statement to add a new candidate
                    String sql = "INSERT INTO candidates (name, party, position, image) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, name);
                    stmt.setString(2, party);
                    stmt.setString(3, position);
                    
                    // Set the image data if available, otherwise set as NULL
                    if (imageDataWrapper[0] != null) {
                        stmt.setBinaryStream(4, new ByteArrayInputStream(imageDataWrapper[0]), imageDataWrapper[0].length);
                    } else {
                        stmt.setNull(4, Types.BLOB); // Use Types.BLOB for SQL NULL in blob column
                    }

                    int rowsAffected = stmt.executeUpdate(); // Execute the insert operation

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(dialog, "Candidate added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshTable(); // Refresh the main table to show the new candidate
                        dialog.dispose(); // Close the dialog
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to add candidate.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    // Handle database errors during save
                    JOptionPane.showMessageDialog(dialog, "Error adding candidate: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });
            buttonPanel.add(saveButton);

            panel.add(buttonPanel, gbc);

            dialog.add(panel);
            dialog.setVisible(true); // Make the dialog visible
        }

        /**
         * Removes the currently selected candidate from the table and the database.
         */
        private void removeSelectedCandidate() {
            int selectedRow = candidatesTable.getSelectedRow();
            if (selectedRow < 0) {
                // If no row is selected, show a warning
                JOptionPane.showMessageDialog(this, "Please select a candidate to remove!", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get the candidate ID from the selected row (assuming ID is in the first column)
            int candidateId = (Integer)candidatesTable.getValueAt(selectedRow, 0);

            // Confirm with the user before proceeding with removal
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove this candidate? This action cannot be undone.",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    // SQL DELETE statement
                    String sql = "DELETE FROM candidates WHERE candidate_id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, candidateId);

                    int rowsAffected = stmt.executeUpdate(); // Execute the delete operation

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Candidate removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshTable(); // Refresh the table after removal
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to remove candidate. Candidate not found or already removed.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    // Handle database errors during removal
                    JOptionPane.showMessageDialog(this, "Error removing candidate: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Panel for viewing election results, including candidate names, parties, positions, votes, and images.
     */
    private class ResultsPanel extends JPanel {
        private JTable resultsTable;
        private JButton refreshButton;

        public ResultsPanel() {
            setLayout(new BorderLayout());

            // Initialize the table for results, overriding getColumnClass for image display
            resultsTable = new JTable() {
                @Override
                public Class<?> getColumnClass(int column) {
                    if (column == 4) { // Image column is the 5th column
                        return ImageIcon.class;
                    }
                    return Object.class;
                }
            };
            
            resultsTable.setRowHeight(100); // Set row height for images
            refreshResults(); // Populate results initially

            // Set custom renderer for the image column
            TableColumn imageColumn = resultsTable.getColumnModel().getColumn(4);
            imageColumn.setCellRenderer(new ImageRenderer());

            JScrollPane scrollPane = new JScrollPane(resultsTable);
            add(scrollPane, BorderLayout.CENTER);

            // Refresh button for results
            refreshButton = new JButton("Refresh Results");
            refreshButton.addActionListener(e -> refreshResults());

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(refreshButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        /**
         * Refreshes the results table by querying candidate data and votes from the database.
         */
        private void refreshResults() {
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 // Order by position and then by votes in descending order
                 ResultSet rs = stmt.executeQuery("SELECT name, party, position, votes, image FROM candidates ORDER BY position, votes DESC")) {

                String[] columnNames = {"Name", "Party", "Position", "Votes", "Image"};

                List<Object[]> data = new ArrayList<>();
                while (rs.next()) {
                    Object[] row = new Object[5];
                    row[0] = rs.getString("name");
                    row[1] = rs.getString("party");
                    row[2] = rs.getString("position");
                    row[3] = rs.getInt("votes"); // Get integer for votes
                    
                    // Handle image blob similarly to ManageCandidatesPanel
                    Blob imageBlob = rs.getBlob("image");
                    if (imageBlob != null) {
                        try (InputStream in = imageBlob.getBinaryStream()) {
                            BufferedImage image = ImageIO.read(in);
                            if (image != null) {
                                ImageIcon icon = new ImageIcon(image.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
                                row[4] = icon;
                            } else {
                                row[4] = null;
                            }
                        }
                    } else {
                        row[4] = null;
                    }
                    
                    data.add(row);
                }

                // Create a DefaultTableModel that is non-editable
                DefaultTableModel model = new DefaultTableModel(data.toArray(new Object[0][]), columnNames) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

                resultsTable.setModel(model);

            } catch (SQLException | IOException e) {
                // Display error message if loading results fails
                JOptionPane.showMessageDialog(this,
                    "Error loading results: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private static class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            // If the cell value is an ImageIcon, create a JLabel to display it.
            if (value instanceof ImageIcon) {
                JLabel label = new JLabel((ImageIcon) value);
                label.setHorizontalAlignment(JLabel.CENTER); // Center the image within the cell
                return label;
            }
            // For other types of values, use the default renderer.
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    static class DBConnection {
        public static Connection getConnection() throws SQLException {
            // --- Configuration for your MySQL Database ---
            String URL = "jdbc:mysql://localhost:3306/evoting_system";
            String USER = "root";
            String PASSWORD = "ravenServer#1";
            // ---------------------------------------------

            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); 
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Make sure it's in your classpath.", e);
            }
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }


    public static void main(String[] args) {
        // Ensure that Swing components are created and updated on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard().setVisible(true);
        });
    }

}
