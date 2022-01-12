package org.openmrs.module.chirdlutil.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.openmrs.module.chirdlutil.threadmgmt.ThreadPoolChartGenerator;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;

/**
 * Servlet used to monitor thread pools.
 *
 * @author Steve McKee
 */
public class ThreadPoolMonitorServlet extends HttpServlet {
    private static final String PARAM_POOL_TYPE = "poolType";
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolMonitorServlet.class);
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        genGraph(req, resp);
    }
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        genGraph(req, resp);
    }
    
    /**
     * Generates a graph with the thread pool usage.
     * 
     * @param req The http request information.
     * @param resp The http response information.
     */
    public void genGraph(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
        resp.setDateHeader(ChirdlUtilConstants.HTTP_HEADER_EXPIRES, 0);
        int width = 625;
        String poolType = req.getParameter(PARAM_POOL_TYPE);
        if (ThreadPoolChartGenerator.POOL_TYPE_PRINTER.equals(poolType)) {
            width = 900;
        }
        
        try {
            OutputStream out = resp.getOutputStream();
            ThreadPoolChartGenerator barChart = new ThreadPoolChartGenerator();
            JFreeChart chart = barChart.getChart(poolType);
            if (chart != null) {
                resp.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_IMAGE_PNG);
                ChartUtilities.writeChartAsPNG(out, chart, width, 500);
            } else {
                resp.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_HTML);
                PrintWriter pw = resp.getWriter();
                pw.write("<p>The server wasn't able to produce a chart with poolType " + poolType + "</p>");
            }
        } catch (IOException e) {
            LOG.error("Error creating thread pool manager chart", e);
        }
    }
}
