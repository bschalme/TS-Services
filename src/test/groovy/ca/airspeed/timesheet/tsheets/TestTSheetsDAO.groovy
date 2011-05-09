package ca.airspeed.timesheet.tsheets

import ca.airspeed.canonical.TimesheetEntry

import java.net.InetAddress;
import java.util.Calendar
import static org.junit.Assert.*
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests {@link ca.airspeed.timesheet.tsheets.TSheetsDAO}.
 * <p>Ensure you have a file on the classpath named {@code tsheets.properties}. It needs to have 
 * the following properties defined:</p>
 * <ul>
 * <li>tsheets.url=(Your URL to thr TSheets API)</li>
 * <li>tsheets.apiKey=(Your API key)</li>
 * <li>tsheets.username=</li>
 * <li>tsheets.password=</li>
 * </ul>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations=['/applicationContext-TEST.xml'])
class TestTSheetsDAO {

	@Autowired
	TSheetsDAO tsheetsDAO

	/**
	 * Tests a read by date range. This does a comprehensive test to ensure all properties of 
	 * {@link ca.airspeed.canonical.TimesheetEntry TimesheetEntry} are set with the expected values.
	 * 
	 */
	@Test
	void testReadByDateRange() {
		def testCase = new ConfigSlurper().parse(new File('src/test/resources/testCase.properties').toURL())
		assertEquals "Postmedia", testCase.TestTSheetsDAO.testReadByDateRange.customerName

		DateTime from = new DateTime("2011-04-01")
		DateTime to = new DateTime("2011-04-07")
		List<TimesheetEntry> results = tsheetsDAO.read(from.toCalendar().getTime(), to.toCalendar().getTime())
		assertTrue "Nothing returned", results != null
		assertTrue "Expected at least one TimesheetEntry in the result", results.size() > 0
		TimesheetEntry entry = results.get(0)
		assertEquals "Source System;", "TSheets", entry.getSourceSystemName()
		assertTrue "Expected a value for id", StringUtils.isNotBlank(entry.getId())
		assertEquals "Username;", "bschalme", entry.username
		assertTrue "Expected a value for startTime.", entry.getStartTime() != null
		def expectedStartTime = new Date().parse('yyyy-MM-dd HH:mm:ss', '2011-04-01 08:00:00')
		assertEquals "Start time;", expectedStartTime, entry.startTime
		def expectedEndTime = new Date().parse('yyyy-MM-dd HH:mm:ss', '2011-04-01 10:00:00')
		assertTrue "Expected a value for endTime.", entry.getEndTime() != null
		assertEquals "End time;", expectedEndTime, entry.endTime
		assertEquals "Customer Name;", testCase.TestTSheetsDAO.testReadByDateRange.customerName, entry.getCustomerName()
		assertEquals "Job Name;", testCase.TestTSheetsDAO.testReadByDateRange.jobName, entry.getJobName()
		assertEquals "Item Name;", testCase.TestTSheetsDAO.testReadByDateRange.itemName, entry.getItemName()
		assertEquals "Duration in minutes;", 120, entry.getDurationMinutes()
		assertTrue "Notes does not begin with the expected string", StringUtils.startsWith(entry.getNotes(), testCase.TestTSheetsDAO.testReadByDateRange.notes)

		entry = results.get(1)
		byte []  bt = new byte [4] ;
		bt[0] = new Integer(204).byteValue()
		bt[1] = new Integer(187).byteValue()
		bt[2] = new Integer(150).byteValue()
		bt[3] = new Integer(30).byteValue()
		InetAddress expected = InetAddress.getByAddress(bt)
		assertEquals "IP In", expected, entry.getIpIn()
		assertEquals "IP Out", expected, entry.getIpOut()

		assertEquals "Number of TimesheetEntries;", 27, results.size()
	}
}
