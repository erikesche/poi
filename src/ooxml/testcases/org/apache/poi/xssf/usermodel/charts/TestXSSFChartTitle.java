/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.xssf.usermodel.charts;

import junit.framework.TestCase;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.charts.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.XSSFTestDataSamples;
import org.apache.poi.xssf.usermodel.*;

import java.util.List;

/**
 * Test get/set chart title.
 */
public class TestXSSFChartTitle extends TestCase {
    private Workbook createWorkbookWithChart() {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("linechart");
        final int NUM_OF_ROWS = 3;
        final int NUM_OF_COLUMNS = 10;

        // Create a row and put some cells in it. Rows are 0 based.
        Row row;
        Cell cell;
        for (int rowIndex = 0; rowIndex < NUM_OF_ROWS; rowIndex++) {
            row = sheet.createRow((short) rowIndex);
            for (int colIndex = 0; colIndex < NUM_OF_COLUMNS; colIndex++) {
                cell = row.createCell((short) colIndex);
                cell.setCellValue(colIndex * (rowIndex + 1));
            }
        }

        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 10, 15);

        Chart chart = drawing.createChart(anchor);
        ChartLegend legend = chart.getOrCreateLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        LineChartData data = chart.getChartDataFactory().createLineChartData();

        // Use a category axis for the bottom axis.
        ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(0, 0, 0, NUM_OF_COLUMNS - 1));
        ChartDataSource<Number> ys1 = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(1, 1, 0, NUM_OF_COLUMNS - 1));
        ChartDataSource<Number> ys2 = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(2, 2, 0, NUM_OF_COLUMNS - 1));

        data.addSeries(xs, ys1);
        data.addSeries(xs, ys2);

        chart.plot(data, bottomAxis, leftAxis);

        return wb;
    }

    /**
     * Gets the first chart from the named sheet in the workbook.
     */
    private XSSFChart getChartFromWorkbook(Workbook wb, String sheetName) {
        Sheet sheet = wb.getSheet(sheetName);
        if (sheet instanceof XSSFSheet) {
            XSSFSheet xsheet = (XSSFSheet) sheet;
            XSSFDrawing drawing = xsheet.getDrawingPatriarch();
            if (drawing != null) {
                List<XSSFChart> charts = drawing.getCharts();
                if (charts != null && charts.size() > 0) {
                    return charts.get(0);
                }
            }
        }
        return null;
    }

    public void testNewChart() {
        Workbook wb = createWorkbookWithChart();
        XSSFChart chart = getChartFromWorkbook(wb, "linechart");
        assertNotNull(chart);
        assertNull(chart.getTitle());
        final String myTitle = "My chart title";
        chart.setTitle(myTitle);
        XSSFRichTextString queryTitle = chart.getTitle();
        assertNotNull(queryTitle);
        assertEquals(myTitle, queryTitle.toString());
    }

    public void testExistingChartWithTitle() {
        Workbook wb = XSSFTestDataSamples.openSampleWorkbook("chartTitle_withTitle.xlsx");
        XSSFChart chart = getChartFromWorkbook(wb, "Sheet1");
        assertNotNull(chart);
        XSSFRichTextString originalTitle = chart.getTitle();
        assertNotNull(originalTitle);
        final String myTitle = "My chart title";
        assertFalse(myTitle.equals(originalTitle.toString()));
        chart.setTitle(myTitle);
        XSSFRichTextString queryTitle = chart.getTitle();
        assertNotNull(queryTitle);
        assertEquals(myTitle, queryTitle.toString());
    }

    public void testExistingChartNoTitle() {
        Workbook wb = XSSFTestDataSamples.openSampleWorkbook("chartTitle_noTitle.xlsx");
        XSSFChart chart = getChartFromWorkbook(wb, "Sheet1");
        assertNotNull(chart);
        assertNull(chart.getTitle());
        final String myTitle = "My chart title";
        chart.setTitle(myTitle);
        XSSFRichTextString queryTitle = chart.getTitle();
        assertNotNull(queryTitle);
        assertEquals(myTitle, queryTitle.toString());
    }

}
