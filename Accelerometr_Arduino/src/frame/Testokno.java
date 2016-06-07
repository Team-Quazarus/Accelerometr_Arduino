package frame;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Testokno extends JFrame {
public	XYSeries series = new XYSeries("sin(a)");
	public Testokno (){
		super("name");
		setDefaultCloseOperation(3);
	}
	
	
	public void updategraph(){
		 XYDataset xyDataset = new XYSeriesCollection(series);
		    JFreeChart chart = ChartFactory
		        .createXYLineChart("y = sin(x)", "x", "y",
		                           xyDataset, 
		                           PlotOrientation.VERTICAL,
		                           true, true, true);
		   
		    // �������� ������ �� �����
		    this.getContentPane().removeAll();
		    this.getContentPane().revalidate();
		    this.getContentPane().add(new ChartPanel(chart));
		    //setSize(400,300);
		    show();
		  
	}

}
