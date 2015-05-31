package jadx.gui.ui;

import ch.qos.logback.classic.Level;
import jadx.gui.utils.LogCollector;
import jadx.gui.utils.NLS;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

class LogViewer extends JDialog {
	private static final long serialVersionUID = -2188700277429054641L;
	private static final Level[] LEVEL_ITEMS = {Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR};

	private static Level level = Level.WARN;
	private RSyntaxTextArea textPane;

	public LogViewer() {
		initUI();
		registerLogListener();
	}

	public final void initUI() {
		textPane = new RSyntaxTextArea();
		textPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		JPanel controlPane = new JPanel();
		controlPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		final JComboBox cb = new JComboBox(LEVEL_ITEMS);
		cb.setSelectedItem(level);
		cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = cb.getSelectedIndex();
				level = LEVEL_ITEMS[i];
				registerLogListener();
			}
		});
		JLabel levelLabel = new JLabel(NLS.str("log.level"));
		levelLabel.setLabelFor(cb);
		controlPane.add(levelLabel);
		controlPane.add(cb);

		JScrollPane scrollPane = new JScrollPane(textPane);

		JButton close = new JButton(NLS.str("tabs.close"));
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				close();
			}
		});
		close.setAlignmentX(0.5f);

		Container contentPane = getContentPane();
		contentPane.add(controlPane, BorderLayout.PAGE_START);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(close, BorderLayout.PAGE_END);

		setTitle("Log Viewer");
		pack();
		setSize(800, 600);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.MODELESS);
		setLocationRelativeTo(null);
	}

	private void registerLogListener() {
		LogCollector logCollector = LogCollector.getInstance();
		logCollector.resetListener();
		textPane.setText("");
		logCollector.registerListener(new LogCollector.ILogListener() {
			@Override
			public Level getFilterLevel() {
				return level;
			}

			@Override
			public void onAppend(final String logStr) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						textPane.append(logStr);
						textPane.updateUI();
					}
				});
			}
		});
	}

	private void close() {
		LogCollector.getInstance().resetListener();
		dispose();
	}

	public static void main(String[] args) {
		new LogViewer().setVisible(true);
	}
}
