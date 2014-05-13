package br.dev.view;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Label;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;

import br.dev.func.Function;
import br.dev.func.Util;

import com.sun.jmx.snmp.tasks.Task;

public class Prototype {

	private JFrame frmTimesheet;
	JTextPane console;
	
	private static int wordPerSecond = 13;
	private static int updateSeconds = 1;
	private static int clockUpdateInterval = 5;
	
	private Thread updateThreadSimpleTime = null;
	private Thread updateThreadIdleTime = null;
	
	private Function func;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Prototype window = new Prototype();
					window.frmTimesheet.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Prototype() {		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		String osInfo = System.getProperty("os.name");
		
		try {
			if(osInfo.toLowerCase().contains("windows"))
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			else
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		frmTimesheet = new JFrame();
		frmTimesheet.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
		frmTimesheet.setTitle("TimeSheet "+Util.getVersionNumber());
		frmTimesheet.setIconImage(Toolkit.getDefaultToolkit().getImage(Prototype.class.getResource("/resources/document-open-recent.png")));
		frmTimesheet.setResizable(false);
		frmTimesheet.setBounds(100, 100, 744, 578);
		frmTimesheet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTimesheet.getContentPane().setLayout(null);
		frmTimesheet.setLocationRelativeTo(null);
		
		console = new JTextPane();
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Console", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(400, 6, 328, 514);
		frmTimesheet.getContentPane().add(panel);
		panel.setLayout(null);
		
		JPanel penelPredicted = new JPanel();
		penelPredicted.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Time Predicted", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		penelPredicted.setBounds(18, 45, 371, 121);
		frmTimesheet.getContentPane().add(penelPredicted);
		penelPredicted.setLayout(null);
				
		final Label textPredicted = new Label();
		textPredicted.setAlignment(Label.CENTER);
		textPredicted.setBounds(8, 16, 355, 95);
		penelPredicted.add(textPredicted);
		textPredicted.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
		textPredicted.setEnabled(false);
		
		console.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));
		console.setBounds(10, 16, 308, 467);
		panel.add(console);
		console.setEnabled(false);
		console.setEditable(false);	
		
		JPanel panelStartTime = new JPanel();
		panelStartTime.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Check-in", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelStartTime.setBounds(18, 208, 174, 46);
		frmTimesheet.getContentPane().add(panelStartTime);
		panelStartTime.setLayout(null);
		
		final JLabel textStartTime = new JLabel();
		textStartTime.setHorizontalAlignment(SwingConstants.CENTER);
		textStartTime.setBounds(6, 16, 166, 23);
		panelStartTime.add(textStartTime);
		textStartTime.setEnabled(false);
		
		JPanel panelEndTime = new JPanel();
		panelEndTime.setLayout(null);
		panelEndTime.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Check out", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelEndTime.setBounds(213, 208, 177, 46);
		frmTimesheet.getContentPane().add(panelEndTime);
		
		final JLabel textEndTime = new JLabel();
		textEndTime.setHorizontalAlignment(SwingConstants.CENTER);
		textEndTime.setEnabled(false);
		textEndTime.setBounds(6, 16, 168, 23);
		panelEndTime.add(textEndTime);
		
		JPanel panelRemaining = new JPanel();
		panelRemaining.setLayout(null);
		panelRemaining.setBorder(new TitledBorder(null, "Time Remaining", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelRemaining.setBounds(18, 265, 174, 46);
		frmTimesheet.getContentPane().add(panelRemaining);
		
		final JLabel textRemain = new JLabel();
		textRemain.setHorizontalAlignment(SwingConstants.CENTER);
		textRemain.setEnabled(false);
		textRemain.setBounds(6, 16, 166, 23);
		panelRemaining.add(textRemain);
		
		JPanel panelElapsed = new JPanel();
		panelElapsed.setLayout(null);
		panelElapsed.setBorder(new TitledBorder(null, "Time Elapsed", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelElapsed.setBounds(213, 265, 177, 46);
		frmTimesheet.getContentPane().add(panelElapsed);
		
		final JLabel textElapsed = new JLabel();
		textElapsed.setHorizontalAlignment(SwingConstants.CENTER);
		textElapsed.setEnabled(false);
		textElapsed.setBounds(6, 16, 167, 23);
		panelElapsed.add(textElapsed);
		
		JPanel panelIdle = new JPanel();
		panelIdle.setLayout(null);
		panelIdle.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Time Idle", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelIdle.setBounds(18, 322, 174, 46);
		frmTimesheet.getContentPane().add(panelIdle);
		
		final JLabel textIdle = new JLabel();
		textIdle.setHorizontalAlignment(SwingConstants.CENTER);
		textIdle.setEnabled(false);
		textIdle.setBounds(6, 16, 166, 23);
		panelIdle.add(textIdle);
		
		final JTextPane txtSession = new JTextPane();
		txtSession.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));
		txtSession.setText("Session not initialized.");
		txtSession.setEnabled(false);
		txtSession.setEditable(false);
		txtSession.setBounds(19, 493, 174, 20);
		frmTimesheet.getContentPane().add(txtSession);
		
		final JLabel textTimeBase = new JLabel();
		textTimeBase.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));
		textTimeBase.setEnabled(false);
		textTimeBase.setText("0h 0m 0s");
		textTimeBase.setBounds(113, 178, 76, 20);
		frmTimesheet.getContentPane().add(textTimeBase);
				
		JLabel lblPredictedStop = new JLabel("Pred Pause");
		lblPredictedStop.setIcon(new ImageIcon(Prototype.class.getResource("/resources/icon-pause-16.png")));
		lblPredictedStop.setFont(new Font("Segoe UI Semilight", Font.BOLD, 11));
		lblPredictedStop.setBounds(215, 178, 90, 20);
		frmTimesheet.getContentPane().add(lblPredictedStop);
		
		final JLabel label_PredPause = new JLabel();
		label_PredPause.setText("0h 0m 0s");
		label_PredPause.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));
		label_PredPause.setEnabled(false);
		label_PredPause.setBounds(316, 178, 76, 20);
		frmTimesheet.getContentPane().add(label_PredPause);
		
		final Task task2 = new Task(){
			boolean isDone;
			
			@Override
			public void run() {
				isDone = false;
				while(!isDone){
					try {
						Thread.sleep(updateSeconds * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					long value = func.updateTimeIdle(true, true, updateSeconds);
					textIdle.setText(Util.printTime(value, "%sh %sm %ss"));	
					
					if(value > func.getPredPause() && value < func.getPredPause() + 10){
						showMessage("Hey, Mandatory brake time is over, Back to Work!!", "Time Over!", "/resources/prisoner-32.png");			
					}
				}
			}

			@Override
			public void cancel() {
				isDone = true;				
			}
			
		};
		
		final Task task = new Task() {	
			boolean isDone;
						
			@Override
			public void run() {
				isDone = false;
				while(!isDone){
					try {
						Thread.sleep(updateSeconds * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			
					long timeElapsed = func.updateTimeElapsed(true, updateSeconds);
					textElapsed.setText(Util.printTime(timeElapsed, "%sh %sm %ss"));
				
					long timeRemain = func.updateTimeRemain(true, updateSeconds);
					textRemain.setText(Util.printTime(timeRemain, "%sh %sm %ss"));	
					
					if(timeRemain < 0 && timeRemain > -1000){
						showMessage("Hey, work time is Over, Enjoy your Freedom!!", "Time Over!", "/resources/running64.png");					
					}
				}				
			}
			
			@Override
			public void cancel() {
				isDone = true;				
			}
		};
				
		final JButton buttonInitialTime = new JButton("Check-in");
		buttonInitialTime.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));
		buttonInitialTime.setIcon(new ImageIcon(Prototype.class.getResource("/resources/591258-in-16.png")));
		final JButton buttonFinalTime = new JButton("Check out ");
		buttonFinalTime.setIcon(new ImageIcon(Prototype.class.getResource("/resources/591248-out-16.png")));
		buttonFinalTime.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));
		
		
		buttonFinalTime.setEnabled(false);
		buttonFinalTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CustomTime custon = new CustomTime();
				
				if(!custon.showDialog()){
					return;
				}
				
				Date customTime = custon.getCustomDate();			
				
				if(buttonFinalTime.isEnabled()){
										
					try {
						func.setFinalTime(customTime);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, "O tempo final n�o pode ser inferior ao inicial", "Erro de tempo", JOptionPane.WARNING_MESSAGE);
						return;
					}		
					
					long idleTime = func.updateTimeIdle(false, true, 0);
					textIdle.setText(Util.printTime(idleTime, "%sh %sm %ss"));
										
					try {
						task.cancel();
						updateThreadSimpleTime.join();
						
						updateThreadIdleTime = new Thread(task2);
						updateThreadIdleTime.start();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					long time = func.getTempTimePack().getEnd();
					textEndTime.setText(func.formatDate(null, time));
									
					buttonInitialTime.setEnabled(true);
					buttonFinalTime.setEnabled(false);
										
					long elapsedTime = func.updateTimeElapsed(false, 0);
					textElapsed.setText(Util.printTime(elapsedTime, "%sh %sm %ss"));
					
					long remainTime = func.updateTimeRemain(false, 0);
					textRemain.setText(Util.printTime(remainTime, "%sh %sm %ss"));
										
					updateConsole();
					
					buttonInitialTime.setEnabled(true);
					buttonFinalTime.setEnabled(false);	
					
				}
			}
		});
		buttonFinalTime.setBounds(284, 11, 106, 23);
		frmTimesheet.getContentPane().add(buttonFinalTime);
		
		buttonInitialTime.setEnabled(false);
		buttonInitialTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				CustomTime custon = new CustomTime();
				
				if(!custon.showDialog()){
					return;
				}
				
				Date customTime = custon.getCustomDate();	
				
				func.setInitialTime(customTime);
								
				try {
					if(updateThreadIdleTime != null){
						task2.cancel();
						updateThreadSimpleTime.join();
					}
					
					updateThreadSimpleTime = new Thread(task);					
					updateThreadSimpleTime.start();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}	
				
				long idleTime = func.updateTimeIdle(false, false,0);
				textIdle.setText(Util.printTime(idleTime, "%sh %sm %ss"));
				
				long time = func.getTempTimePack().getStart();					
				textStartTime.setText(func.formatDate(null, time));
				
				long predictedTime = func.getTimeSheet().getTimePredicted();
				textPredicted.setText(func.formatDate(null, predictedTime));
										
				buttonInitialTime.setEnabled(false);
				buttonFinalTime.setEnabled(true);	
				
				textEndTime.setText("");
				
				txtSession.setText("Session sequence: "+ (func.getTimeSheet().getTimePacks().size()+1));	
								
			}
		});
		buttonInitialTime.setBounds(171, 11, 106, 23);
		frmTimesheet.getContentPane().add(buttonInitialTime);
		
		final JButton buttonTimeSheet = new JButton("New TimeSheet");
		buttonTimeSheet.setIcon(new ImageIcon(Prototype.class.getResource("/resources/menu-alt-16.png")));
		buttonTimeSheet.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));
		buttonTimeSheet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				NewTimeSheet setTime = new NewTimeSheet();
				
				if(!setTime.showDialog(clockUpdateInterval, updateSeconds))
					return;
				
				updateSeconds = setTime.getUpdateSeconds();
				clockUpdateInterval = setTime.getClockUpdateSeconds();
								
				if(updateThreadSimpleTime != null)
					try {
						task.cancel();
						updateThreadSimpleTime.join();
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				
				func = new Function();				
				buttonFinalTime.setEnabled(false);
				
				func.setTimePerDay(setTime.getTimePerDay());
				func.setPredPause(setTime.getTimeIdle());
				textTimeBase.setText(Util.printTime(func.getTimePerDay(), "%sh %sm %ss"));
				label_PredPause.setText(Util.printTime(func.getPredPause(), "%sh %sm %ss"));
				
				console.setText("New TimeSheet Created.");
				buttonInitialTime.setEnabled(true);				
				
				textStartTime.setText("");
				textEndTime.setText("");
				textRemain.setText("");
				textElapsed.setText("");
				textPredicted.setText("");
				textIdle.setText("");
				txtSession.setText("Session not initialized.");
			}
		});
		buttonTimeSheet.setBounds(19, 11, 131, 23);
		frmTimesheet.getContentPane().add(buttonTimeSheet);
				
		JLabel lblTimeBase = new JLabel("Work Time");
		lblTimeBase.setIcon(new ImageIcon(Prototype.class.getResource("/resources/prisoner-16.png")));
		lblTimeBase.setFont(new Font("Segoe UI Semilight", Font.BOLD, 11));
		lblTimeBase.setBounds(23, 177, 90, 20);
		frmTimesheet.getContentPane().add(lblTimeBase);
		
		final JLabel labelHour = new JLabel();
		labelHour.setHorizontalAlignment(SwingConstants.RIGHT);
		labelHour.setText("0h 0m 0s");
		labelHour.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));
		labelHour.setEnabled(false);
		labelHour.setBounds(330, 493, 60, 20);
		frmTimesheet.getContentPane().add(labelHour);
		
		Thread thread = new Thread(new Runnable() {			
			@Override
			public void run() {
				SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
				while(true){
					try {					
						labelHour.setText(format.format(new Date()));	
						Thread.sleep(clockUpdateInterval * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		});
		
		thread.start();	
		
		JLabel lblNow = new JLabel("Now");
		lblNow.setIcon(new ImageIcon(Prototype.class.getResource("/resources/icon-ios7-clock-outline-16.png")));
		lblNow.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNow.setFont(new Font("Segoe UI Semilight", Font.BOLD, 11));
		lblNow.setBounds(271, 493, 60, 20);
		frmTimesheet.getContentPane().add(lblNow);
		
		final JTextArea lblhid = new JTextArea("");
		lblhid.setBounds(18, 380, 372, 90);
		frmTimesheet.getContentPane().add(lblhid);
		lblhid.setBackground(SystemColor.control);
		lblhid.setEditable(false);
		lblhid.setLineWrap(true);
		lblhid.setFont(new Font("Segoe UI Semibold", Font.BOLD, 11));
				
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				do{
					try {
						Thread.sleep(1000 * 5);
						String[] text = Util.getText();
						for (int i = 0; i < text.length; i++) {
							String[] t = text[i].split("#"); 
							lblhid.setText(t[0]);
							
							if(t.length == 1){
								Thread.sleep(1000 * text[i].split("").length / wordPerSecond);
							}else{
								Thread.sleep(1000 * Integer.parseInt(t[1]));
							}
						}					
						
						lblhid.setText("");
						
					} catch (InterruptedException e) {
						lblhid.setText("ERROR!");
					}				
				
				}while(true);
			}
		}).start();
		
				
		JMenuBar menuBar = new JMenuBar();
		frmTimesheet.setJMenuBar(menuBar);
		
		JMenu mnInfo = new JMenu("TimeSheet");
		menuBar.add(mnInfo);
		
		
		final JMenuItem mntmCheckOut = new JMenuItem("Check out");		
		mntmCheckOut.setIcon(new ImageIcon(Prototype.class.getResource("/resources/591248-out-16.png")));
		final JMenuItem mntmCheckin = new JMenuItem("Check-in");
		mntmCheckin.setIcon(new ImageIcon(Prototype.class.getResource("/resources/591258-in-16.png")));
		
		JMenuItem mntmNewTimesheet = new JMenuItem("New TimeSheet");
		mntmNewTimesheet.setIcon(new ImageIcon(Prototype.class.getResource("/resources/menu-alt-16.png")));
		mntmNewTimesheet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonTimeSheet.doClick();
			}
		});
		mntmNewTimesheet.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mnInfo.add(mntmNewTimesheet);
		
		mntmCheckin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonInitialTime.doClick();
			}
		});
		mntmCheckin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mnInfo.add(mntmCheckin);
		
		mntmCheckOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonFinalTime.doClick();
			}
		});
		mntmCheckOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
		mnInfo.add(mntmCheckOut);
		
		JMenuItem mntmUpdateConsole = new JMenuItem("Update Console");
		mntmUpdateConsole.setIcon(new ImageIcon(Prototype.class.getResource("/resources/update-16.png")));
		mntmUpdateConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateConsole();
			}
		});
		mntmUpdateConsole.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		mnInfo.add(mntmUpdateConsole);
		
		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);
		
		JMenuItem mntmVerifyUpdates = new JMenuItem("Check for updates");
		mntmVerifyUpdates.setIcon(new ImageIcon(Prototype.class.getResource("/resources/519929-27_Cloud-16.png")));
		mnAbout.add(mntmVerifyUpdates);
		mntmVerifyUpdates.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
		mntmVerifyUpdates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CheckUpdates check = new CheckUpdates();				
				check.showDialog();
				return;				
			}
		});
		
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.setIcon(new ImageIcon(Prototype.class.getResource("/resources/info-16.png")));
		mnAbout.add(mntmAbout);
		mntmAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK));
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				console.setText("\n TimeSheet 2014 - Version "+Util.getVersionNumber()+" \n Source: https://github.com/denisaguilar/TimeSheet"+ "\n\n Back to Console (Ctrl+Z)");
			}
		});
				
		mntmVerifyUpdates.doClick();
	}
	
	private void showMessage(final String message, final String title, final String icon){
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				JOptionPane jOptionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.CLOSED_OPTION, new ImageIcon(Prototype.class.getResource(icon)));
				JDialog dialog = jOptionPane.createDialog(null, title);
				dialog.setLocationRelativeTo(null);
				dialog.setAlwaysOnTop(true);						
				dialog.setVisible(true);
			}
		});
		
		thread.start();
	}
	
	private void updateConsole(){
		if(func != null)
			console.setText(func.generateInfo());
		else{
			console.setText("Go ahead... make my day.");
		}
	}
}
