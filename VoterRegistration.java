import java.awt.EventQueue;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Panel;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

public class VoterRegistration extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField firstNameTxtField;
	private JTextField LastNameTxtField;
	private JTextField emailTxtField;
	private JPasswordField passwordField;
	private JPasswordField ConfirmpasswordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VoterRegistration frame = new VoterRegistration();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VoterRegistration() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\avryl\\Downloads\\NATIONALIAN (1).png"));
		setTitle("Voter Registration | Bulldog's Choice");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 844, 565);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		firstNameTxtField = new JTextField();
		firstNameTxtField.setBounds(339, 235, 180, 25);
		contentPane.add(firstNameTxtField);
		firstNameTxtField.setColumns(10);
		
		LastNameTxtField = new JTextField();
		LastNameTxtField.setColumns(10);
		LastNameTxtField.setBounds(339, 271, 180, 25);
		contentPane.add(LastNameTxtField);
		
		emailTxtField = new JTextField();
		emailTxtField.setColumns(10);
		emailTxtField.setBounds(339, 307, 180, 25);
		contentPane.add(emailTxtField);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(339, 343, 180, 25);
		contentPane.add(passwordField);
		
		ConfirmpasswordField = new JPasswordField();
		ConfirmpasswordField.setBounds(339, 379, 180, 25);
		contentPane.add(ConfirmpasswordField);
		
		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String firstName = firstNameTxtField.getText().trim();
	            String lastName = LastNameTxtField.getText().trim();
	            String email = emailTxtField.getText().trim();
	            String password = new String(passwordField.getPassword());
	            String confirmPassword = new String(ConfirmpasswordField.getPassword());
	            
	            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
	                JOptionPane.showMessageDialog(null, "All fields are required!");
	                return;
	            }
	            
	            if (!password.equals(confirmPassword)) {
	                JOptionPane.showMessageDialog(null, "Passwords do not match!");
	                return;
	            }
	            
	            if (!EmailValidation.isValid(email)) {
	                JOptionPane.showMessageDialog(null, "Invalid email format!");
	                return;
	            }
	            
	            try (Connection conn = DBConnection.getConnection()) {
	                // Check if email already exists
	                String checkSql = "SELECT * FROM voters WHERE email = ?";
	                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
	                checkStmt.setString(1, email);
	                ResultSet rs = checkStmt.executeQuery();
	                
	                if (rs.next()) {
	                    JOptionPane.showMessageDialog(null, "Email already registered!");
	                    return;
	                }
	                
	                // Insert new voter
	                String insertSql = "INSERT INTO voters (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";
	                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
	                insertStmt.setString(1, firstName);
	                insertStmt.setString(2, lastName);
	                insertStmt.setString(3, email);
	                insertStmt.setString(4, password); // In real app, hash the password
	                
	                int rowsAffected = insertStmt.executeUpdate();
	                
	                if (rowsAffected > 0) {
	                    JOptionPane.showMessageDialog(null, "Registration successful! You can now login.");
	                    new VoterLogin().setVisible(true);
	                    dispose();
	                }
	            } catch (SQLException ex) {
	                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
	            }
			}
		});
		btnRegister.setBackground(new Color(51, 51, 153));
		btnRegister.setForeground(new Color(255, 255, 255));
		btnRegister.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnRegister.setBounds(338, 415, 181, 25);
		contentPane.add(btnRegister);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VoterLogin().setVisible(true);
                dispose();
			}
		});
		btnLogin.setBackground(new Color(255, 255, 0));
		btnLogin.setForeground(new Color(51, 51, 153));
		btnLogin.setBounds(452, 451, 67, 16);
		contentPane.add(btnLogin);
		
		Panel FooterBlue = new Panel();
		FooterBlue.setBackground(new Color(51, 51, 153));
		FooterBlue.setBounds(0, 486, 828, 40);
		contentPane.add(FooterBlue);
		
		Panel HeaderBlue = new Panel();
		HeaderBlue.setBackground(new Color(51, 51, 153));
		HeaderBlue.setBounds(0, 0, 828, 40);
		contentPane.add(HeaderBlue);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\avryl\\OneDrive\\Pictures\\NATIONALIAN (1).2.png"));
		lblNewLabel.setBounds(341, 86, 180, 122);
		contentPane.add(lblNewLabel);
		
		JLabel lblFirstN = new JLabel("First Name");
		lblFirstN.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblFirstN.setHorizontalAlignment(SwingConstants.CENTER);
		lblFirstN.setBounds(184, 235, 125, 25);
		contentPane.add(lblFirstN);
		
		JLabel lblLastN = new JLabel("Last Name");
		lblLastN.setHorizontalAlignment(SwingConstants.CENTER);
		lblLastN.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblLastN.setBounds(184, 271, 125, 25);
		contentPane.add(lblLastN);
		
		JLabel lblEmail = new JLabel("Email");
		lblEmail.setHorizontalAlignment(SwingConstants.CENTER);
		lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEmail.setBounds(184, 307, 125, 25);
		contentPane.add(lblEmail);
		
		JLabel lblPass = new JLabel("Password");
		lblPass.setHorizontalAlignment(SwingConstants.CENTER);
		lblPass.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPass.setBounds(184, 343, 125, 25);
		contentPane.add(lblPass);
		
		JLabel lblConfirmPass = new JLabel("Confirm Password");
		lblConfirmPass.setHorizontalAlignment(SwingConstants.CENTER);
		lblConfirmPass.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblConfirmPass.setBounds(184, 379, 125, 25);
		contentPane.add(lblConfirmPass);
		
		JLabel lblNewLabel_2 = new JLabel("Already Registered?");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(339, 451, 113, 16);
		contentPane.add(lblNewLabel_2);
	}
}
