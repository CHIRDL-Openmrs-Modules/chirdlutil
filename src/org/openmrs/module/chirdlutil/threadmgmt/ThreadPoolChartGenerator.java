package org.openmrs.module.chirdlutil.threadmgmt;

import java.awt.Color;
import java.awt.GradientPaint;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Used to generate a chart with thread pool usage.
 *
 */
public class ThreadPoolChartGenerator {
	
	public static final String POOL_TYPE_PROCESS = "process";
	public static final String POOL_TYPE_PRINTER = "printer";
	
    public JFreeChart getChart(String poolType) {
    	CategoryDataset dataset = null;
    	String chartTitle = null;
    	String domainLabel = null;
    	String rangeLabel = null;
    	if (POOL_TYPE_PRINTER.equals(poolType)) {
			PrinterThreadManager printerThreadManager = PrinterThreadManager.getInstance();
			Map<String, Integer> threadStats = printerThreadManager.getThreadPoolUsage();
			dataset = createDataset(threadStats, "Print Jobs");
			chartTitle = "Printer Pool Monitor";
			domainLabel = "Printer";
			rangeLabel = "Print Jobs in Queue";
		} else if (POOL_TYPE_PROCESS.equals(poolType)) {
			ThreadManager threadManager = ThreadManager.getInstance();
			Map<String, Integer> threadStats = threadManager.getThreadPoolUsage();
			dataset = createDataset(threadStats, "Tasks");
			chartTitle = "Thread Pool Monitor";
			domainLabel = "Pool";
			rangeLabel = "# Tasks in Queue";
		}
    	
    	if (dataset == null) {
    		return null;
    	}
    	
        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart3D(
            chartTitle,    			  // chart title
            domainLabel,              // domain axis label
            rangeLabel,               // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            false,                    // include legend
            true,                     // tooltips?
            false                     // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customization...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(true);
        
        // set up gradient paints for series...
        final GradientPaint gp0 = new GradientPaint(
            0.0f, 0.0f, Color.blue, 
            0.0f, 0.0f, Color.darkGray
        );
        renderer.setSeriesPaint(0, gp0);

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
        );
        // OPTIONAL CUSTOMISATION COMPLETED.
        
        return chart;
        
    }
    
    private CategoryDataset createDataset(Map<String, Integer> threadStats, String xAxisName) {
        // create the dataset...
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Set<Entry<String, Integer>> entrySet = threadStats.entrySet();
		Iterator<Entry<String, Integer>> iter = entrySet.iterator();
		while (iter.hasNext()) {
			Entry<String, Integer> entry = iter.next();
			String printer = entry.getKey();
			Integer count = entry.getValue();
			dataset.addValue(count, xAxisName, printer);
		}
        
        threadStats.clear();
        return dataset;
    }
}

