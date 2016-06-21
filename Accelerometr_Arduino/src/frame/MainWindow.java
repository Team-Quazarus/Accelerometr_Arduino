package frame;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import connect.ComUtil;
import main.Main;

public class MainWindow extends JFrame implements ActionListener{
	
	
	private static final long serialVersionUID = 5589368050025351859L;
	private JScrollPane grafiki;
	private JPanel cube;
	private JButton start,stop;
	public JLabel ikasa, ygrek,zetik;// это после кузова
	private TimeSeries X,Y,Z;
	

	public MainWindow() throws HeadlessException {
		super("Тут все");
		setSize(1402,666);
		getContentPane().setLayout(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.Y = new TimeSeries("Y", Millisecond.class);
		this.X = new TimeSeries("X", Millisecond.class);
		this.Z = new TimeSeries("Z", Millisecond.class);
		
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(X);
		dataset.addSeries(Y);
		dataset.addSeries(Z);
	    final JFreeChart chart = createChart(dataset);

	    final ChartPanel chartPanel = new ChartPanel(chart);
		
		grafiki = new JScrollPane(chartPanel);
		grafiki.setBounds(12, 140, 882, 432);
		getContentPane().add(grafiki);
		grafiki.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		
		cube = new JPanel();
		cube.setBounds(941, 140, 430, 430);
		getContentPane().add(cube);
		cube.setBackground(Color.black);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(12, 578, 882, 37);
		getContentPane().add(panel_1);
		panel_1.setLayout(new GridLayout(0, 3, 0, 0));
		
		JCheckBox checkBox_1 = new JCheckBox("New check box");
		panel_1.add(checkBox_1);
		
		JCheckBox checkBox = new JCheckBox("New check box");
		panel_1.add(checkBox);
		
		JCheckBox checkBox_2 = new JCheckBox("New check box");
		panel_1.add(checkBox_2);
		
		/*JLabel label = new JLabel("New label");
		label.setBounds(39, 91, 590, 24);
		getContentPane().add(label);
		*/
		
		JLabel label_1 = new JLabel("Cube");
		label_1.setBounds(1035, 91, 145, 24);
		getContentPane().add(label_1);
		
		start = new JButton("Старт");
		start.setBounds(39, 91, 150, 25);
		start.addActionListener(this);
		getContentPane().add(start);
		
		stop=new JButton("Стоп");
		stop.setBounds(209, 91,150, 25);
		stop.addActionListener(this);
		getContentPane().add(stop);
		
		JLabel label_2 = new JLabel("Икс Игрек Зет");
		label_2.setBounds(599, 16, 200, 20);
		label_2.setFont(Main.progFont);
		getContentPane().add(label_2);
		
		ikasa = new JLabel("0.00");
		ikasa.setBounds(599,30,50,30);
		ikasa.setFont(Main.progFont);
		getContentPane().add(ikasa);
		
		ygrek = new JLabel("0.00");
		ygrek.setBounds(666,30,50,30);
		ygrek.setFont(Main.progFont);
		getContentPane().add(ygrek);
		
		zetik = new JLabel("0.00");
		zetik.setBounds(730,30,50,30);
		zetik.setFont(Main.progFont);
		getContentPane().add(zetik);
		
		JMenuBar ber = new JMenuBar();
		
		JMenu menu = new JMenu("Позовите лёшу");
		JMenuItem exit,returnn,calibrate,hole;
		exit = new JMenuItem("Выход");
		returnn = new JMenuItem("Назад");
		calibrate = new JMenuItem("Калибровка");
		hole = new JMenuItem("Черная дыра");
		menu.add(hole);
		menu.add(exit);
		menu.add(returnn);
		menu.add(calibrate);
		ber.add(menu);
		exit.addActionListener(this);
		exit.setActionCommand("exit");
		returnn.addActionListener(this);
		returnn.setActionCommand("return");
		calibrate.addActionListener(this);
		calibrate.setActionCommand("calibrate");
		hole.addActionListener(this);
		hole.setActionCommand("hole");
		
		this.setJMenuBar(ber);
		
	}
	private JFreeChart createChart(TimeSeriesCollection dataset) {
		 final JFreeChart result = ChartFactory.createTimeSeriesChart("Create name", "Time", "Acceleration",dataset,true, true, false);
		 final ValueAxis axis= result.getXYPlot().getDomainAxis();
		 axis.setAutoRange(true);
	     axis.setFixedAutoRange(60000.0);  // 60 seconds
	     result.getXYPlot().getRangeAxis().setRange(0.0, 3); 
		 
		return result;
	}
	public void updateVal(float Y,float X,float Z){
		this.Y.add(new Millisecond(), Y);
		this.X.add(new Millisecond(),X);
		this.Z.add(new Millisecond(),Z);
	}
	

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==start){
			ComUtil.sendCom('C');
		}
		else if(e.getSource()==stop){
			ComUtil.sendCom('D');
			ComUtil.sendCom('D');
		}
		switch(e.getActionCommand()){
		case "hole": 
			break;
		case "exit": System.exit(ABORT); 
			break;
		case "return": this.dispose();
		JFrame f = new ConnectFrame();
		f.setVisible(true);
		break;
		case "calibrate": setVisible(false);
		//ComUtil.sendCom('D');
		//ComUtil.sendCom('D');
		Main.calibrwind = new CalibrateWindow(this);
		Main.calibrwind.setVisible(true);
	
		}
		
		
	}
	
}
