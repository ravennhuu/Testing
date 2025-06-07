import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Toolkit;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EVotingSystem extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EVotingSystem frame = new EVotingSystem();
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
	public EVotingSystem() {
		setTitle("Bulldog's Choice E-Vote System");
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\avryl\\Downloads\\NATIONALIAN (1).png"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 844, 565);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton adminButton = new JButton("Admin");
		adminButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AdminLogin().setVisible(true);
				
			}
		});
		adminButton.setIcon(null);
		adminButton.setForeground(new Color(255, 255, 0));
		adminButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
		adminButton.setBackground(new Color(51, 51, 153));
		adminButton.setBounds(486, 403, 138, 51);
		contentPane.add(adminButton);
		
		JButton clientButton = new JButton("Client");
		clientButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VoterLogin().setVisible(true);
				
			}
		});
		clientButton.setForeground(new Color(51, 51, 153));
		clientButton.setBackground(new Color(255, 255, 0));
		clientButton.setIcon(null);
		clientButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
		clientButton.setBounds(221, 403, 138, 51);
		contentPane.add(clientButton);
		
		JLabel LOGO = new JLabel("");
		LOGO.setIcon(new ImageIcon("C:\\Users\\avryl\\OneDrive\\Pictures\\NATIONALIAN (1).2.png"));
		LOGO.setBounds(341, 28, 180, 173);
		contentPane.add(LOGO);
		
		JLabel BGPhoto = new JLabel("");
		BGPhoto.setIcon(new ImageIcon("C:\\Users\\avryl\\OneDrive\\Pictures\\bggg (6).1.png"));
		BGPhoto.setBounds(0, 0, 828, 526);
		contentPane.add(BGPhoto);
		
		JLabel BGPhoto2 = new JLabel("");
		BGPhoto2.setIcon(new ImageIcon("C:\\Users\\avryl\\OneDrive\\Pictures\\bggg (4).1.png"));
		BGPhoto2.setBounds(0, 0, 828, 526);
		contentPane.add(BGPhoto2);
	}
}
