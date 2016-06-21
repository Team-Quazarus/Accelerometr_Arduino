package main;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

public class MyDemoPanel extends JPanel implements ChartMouseListener{

	
	private ChartPanel chartPanel;
    private Crosshair xCrosshair;
    private Crosshair yCrosshair;
    private XYSeries ser;
    
    public MyDemoPanel(XYSeries ser)
    {
      super();
      this.ser=ser;
      JFreeChart localJFreeChart = createChart(createDataset());
      this.chartPanel = new ChartPanel(localJFreeChart);
      this.chartPanel.addChartMouseListener(this);
      CrosshairOverlay localCrosshairOverlay = new CrosshairOverlay();
      this.xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0.0F));
      this.xCrosshair.setLabelVisible(true);
      this.yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0.0F));
      this.yCrosshair.setLabelVisible(true);
      localCrosshairOverlay.addDomainCrosshair(this.xCrosshair);
      localCrosshairOverlay.addRangeCrosshair(this.yCrosshair);
      this.chartPanel.addOverlay(localCrosshairOverlay);
      add(this.chartPanel);
    }
    
    
    private JFreeChart createChart(XYDataset paramXYDataset)
    {
      JFreeChart localJFreeChart = ChartFactory.createXYLineChart("CrosshairOverlayDemo1", "X", "Y", paramXYDataset);
      return localJFreeChart;
    }
    
    private XYDataset createDataset()
    {
     
      XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection(ser);
      return localXYSeriesCollection;
    }
    
    public void chartMouseClicked(ChartMouseEvent paramChartMouseEvent) {}
    
    public void chartMouseMoved(ChartMouseEvent paramChartMouseEvent)
    {
      Rectangle2D localRectangle2D = this.chartPanel.getScreenDataArea();
      JFreeChart localJFreeChart = paramChartMouseEvent.getChart();
      XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
      ValueAxis localValueAxis = localXYPlot.getDomainAxis();
      double d1 = localValueAxis.java2DToValue(paramChartMouseEvent.getTrigger().getX(), localRectangle2D, RectangleEdge.BOTTOM);
      if (!localValueAxis.getRange().contains(d1)) {
        d1 = Double.NaN;
      }
      double d2 = DatasetUtilities.findYValue(localXYPlot.getDataset(), 0, d1);
      this.xCrosshair.setValue(d1);
      this.yCrosshair.setValue(d2);
    }
	

}
