import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Panel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

public class VoterLogin extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField emailTxtField;
	private JPasswordField passwordPassField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VoterLogin frame = new VoterLogin();
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
	public VoterLogin() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\avryl\\Downloads\\NATIONALIAN (1).png"));
		setTitle("Voter Login | Bulldog's Choice");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 844, 565);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
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
		
		emailTxtField = new JTextField();
		emailTxtField.setBounds(339, 307, 180, 25);
		contentPane.add(emailTxtField);
		emailTxtField.setColumns(10);
		
		passwordPassField = new JPasswordField();
		passwordPassField.setBounds(339, 343, 180, 25);
		contentPane.add(passwordPassField);
		
		JLabel lblNewLabel_1 = new JLabel("Email");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(204, 307, 125, 25);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("Password");
		lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1_1.setBounds(204, 343, 125, 25);
		contentPane.add(lblNewLabel_1_1);
		
		JButton btnNewButton_1 = new JButton("Register");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 new VoterRegistration().setVisible(true);
		            dispose();
			}
		});
		
		JButton btnNewButton = new JButton("Login");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String email = emailTxtField.getText().trim();
	            String password = new String(passwordPassField.getPassword());
	            
	            if (email.isEmpty() || password.isEmpty()) {
	                JOptionPane.showMessageDialog(null, "Email and password are required!");
	                return;
	            }
	            
	            try (Connection conn = DBConnection.getConnection()) {
	                String sql = "SELECT * FROM voters WHERE email = ? AND password = ?";
	                PreparedStatement stmt = conn.prepareStatement(sql);
	                stmt.setString(1, email);
	                stmt.setString(2, password); 
	                
	                ResultSet rs = stmt.executeQuery();
	                
	                if (rs.next()) {
	                    if (rs.getBoolean("has_voted")) {
	                        JOptionPane.showMessageDialog(null, "You have already voted!");
	                        return;
	                    }
	                    
	                    int voterId = rs.getInt("voter_id");
	                    new VotingPanel(voterId).setVisible(true);
	                    dispose();
	                } else {
	                    JOptionPane.showMessageDialog(null, "Invalid email or password!");
	                }
	            } catch (SQLException ex) {
	                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
	            }
			}
		});
		btnNewButton.setBackground(new Color(51, 51, 153));
		btnNewButton.setForeground(new Color(255, 255, 255));
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnNewButton.setBounds(339, 385, 180, 25);
		contentPane.add(btnNewButton);
		btnNewButton_1.setBackground(new Color(255, 255, 0));
		btnNewButton_1.setForeground(new Color(51, 51, 153));
		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnNewButton_1.setBounds(435, 421, 86, 15);
		contentPane.add(btnNewButton_1);
		
		JLabel lblNewLabel_2 = new JLabel("Not Registered?");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(341, 421, 101, 16);
		contentPane.add(lblNewLabel_2);
	}
}
