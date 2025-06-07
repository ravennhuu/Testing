import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
import java.awt.Color;
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
import java.awt.event.ActionEvent;

public class AdminLogin extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField userNameTxtField;
	private JPasswordField passPasswordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AdminLogin frame = new AdminLogin();
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
	public AdminLogin() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\avryl\\Downloads\\NATIONALIAN (1).png"));
		setTitle("Admin Login | Bulldog's Choice");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 844, 565);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(51, 51, 153));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		Panel HeaderWhite = new Panel();
		HeaderWhite.setBackground(new Color(255, 255, 255));
		HeaderWhite.setBounds(0, 0, 828, 10);
		contentPane.add(HeaderWhite);
		
		Panel FooterWhite = new Panel();
		FooterWhite.setBackground(new Color(255, 255, 255));
		FooterWhite.setBounds(0, 486, 828, 40);
		contentPane.add(FooterWhite);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\avryl\\OneDrive\\Pictures\\NATIONALIAN (2).2.png"));
		lblNewLabel.setBounds(340, 86, 180, 164);
		contentPane.add(lblNewLabel);
		
		userNameTxtField = new JTextField();
		userNameTxtField.setBounds(339, 307, 180, 25);
		contentPane.add(userNameTxtField);
		userNameTxtField.setColumns(10);
		
		passPasswordField = new JPasswordField();
		passPasswordField.setBounds(339, 343, 180, 25);
		contentPane.add(passPasswordField);
		
		JLabel lblNewLabel_1 = new JLabel("Username");
		lblNewLabel_1.setForeground(new Color(255, 255, 255));
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(204, 307, 125, 25);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("Password");
		lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1_1.setForeground(Color.WHITE);
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1_1.setBounds(204, 343, 125, 25);
		contentPane.add(lblNewLabel_1_1);
		
		JButton btnNewButton = new JButton("Login");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = userNameTxtField.getText().trim();
	            String password = new String(passPasswordField.getPassword());
	            
	            // Hardcoded admin credentials (in real app, store securely)
	            if ("admin".equals(username) && "admin123".equals(password)) {
	                new AdminDashboard().setVisible(true);
	                dispose();
	            } else {
	                JOptionPane.showMessageDialog(null, "Invalid admin credentials!");
	            }
			}
		});
		btnNewButton.setBackground(new Color(255, 255, 0));
		btnNewButton.setForeground(new Color(51, 51, 153));
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnNewButton.setBounds(339, 385, 180, 25);
		contentPane.add(btnNewButton);
	}
}
