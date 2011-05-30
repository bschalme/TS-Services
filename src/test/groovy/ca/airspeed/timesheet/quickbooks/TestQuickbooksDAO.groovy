/*
 Copyright 2011 Airspeed Consulting
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package ca.airspeed.timesheet.quickbooks

import ca.airspeed.canonical.TimesheetEntry
import static org.junit.Assert.*

import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.SimpleDateFormat
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations=['/Context-TestQuickbooksDAO.xml'])
class TestQuickbooksDAO {

	@Autowired
	QuickbooksDAO qbDAO

	private SimpleJdbcTemplate simpleJdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource)
	}

	@Before
	void setUp() {
		def sql = "SELECT COUNT(*) FROM Customer WHERE FullName = ?"
		int count = simpleJdbcTemplate.getJdbcOperations().queryForInt(sql, "Test Customer:Test Job")
		if (count == 0) {
			sql = "INSERT INTO Customer (Name) VALUES (?)"
			simpleJdbcTemplate.update(sql, "Test Customer")
			sql = "INSERT INTO Customer (Name, ParentRefFullName) VALUES (?, ?)"
			simpleJdbcTemplate.update(sql, "Test Job", "Test Customer")
		}

		sql = "SELECT COUNT(*) FROM ItemService WHERE FullName = ?"
		count = simpleJdbcTemplate.getJdbcOperations().queryForInt(sql, "TestServiceItem:TestSubItem")
		if (count == 0) {
			sql = "INSERT INTO ItemService (Name, IsActive, SalesOrPurchaseDesc, SalesOrPurchasePrice, SalesOrPurchaseAccountRefFullName) VALUES (?, ?, ?, ?, ?)"
			simpleJdbcTemplate.update(sql, "TestServiceItem", 1, "Test Service Item", 100, "Consulting Income")
			sql = "INSERT INTO ItemService (Name, IsActive, ParentRefFullName, SalesOrPurchaseDesc, SalesOrPurchasePrice, SalesOrPurchaseAccountRefFullName) VALUES (?, ?, ?, ?, ?, ?)"
			simpleJdbcTemplate.update(sql, "TestSubItem", 1, "TestServiceItem", "Test Sub Item", 100, "Consulting Income")
		}

		sql = "SELECT COUNT(*) FROM OtherName WHERE Name = ?"
		count = simpleJdbcTemplate.getJdbcOperations().queryForInt(sql, "Test Consultant")
		if (count == 0) {
			sql = "INSERT INTO OtherName (Name, IsActive) VALUES (?, ?)"
			simpleJdbcTemplate.update(sql, "Test Consultant", 1)
		}
	}

	@After
	void tearDown() {
		def sql = "DELETE FROM TimeTracking WHERE CustomerRefFullName LIKE ?"
		simpleJdbcTemplate.update(sql, "Test Customer%")

		/*sql = "DELETE FROM Customer WHERE Name = ?"
		simpleJdbcTemplate.update(sql, "Test Job")
		simpleJdbcTemplate.update(sql, "Test Customer")

		sql = "DELETE FROM ItemService WHERE Name = ?"
		simpleJdbcTemplate.update(sql, "TestSubItem")
		simpleJdbcTemplate.update(sql, "TestServiceItem")

		sql = "DELETE FROM OtherName WHERE Name = ?"
		simpleJdbcTemplate.update(sql, "Test Consultant")*/
	}

	@Test
	void testCreateList() {
		assertTrue "Something went wrong!", true

		SimpleDateFormat dfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd")
		def entries = []

		def sql = "SELECT CompanyName FROM Company"
		RowMapper<String> mapper = new RowMapper<String>() {
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString("CompanyName");
					}
				};
		def companyName = simpleJdbcTemplate.queryForObject(sql, mapper)
		assertEquals "CompanyName prefix;", "TEST ", StringUtils.substring(companyName, 0, 5)

		def entry = new TimesheetEntry(id:12345, sourceSystemName:'TSheets', username:'tconsultant', customerName:'Test Customer', jobName:'Test Job')
		entry.jobName = 'TestServiceItem:TestSubItem'
		entry.startTime = dfTime.parse("2011-05-29 09:00:00")
		entry.endTime = dfTime.parse("2011-05-29 10:30:00")
		entry.durationMinutes = 90
		byte []  bt = new byte [4] ;
		bt[0] = new Integer(204).byteValue()
		bt[1] = new Integer(187).byteValue()
		bt[2] = new Integer(150).byteValue()
		bt[3] = new Integer(30).byteValue()
		entry.ipIn = InetAddress.getByAddress(bt)
		entry.ipOut = InetAddress.getByAddress(bt)
		entry.customerName = "Test Customer"
		entry.jobName = "Test Job"
		entry.itemName = "TestServiceItem:TestSubItem"
		entry.notes = "ActivityCode:Researched messaging systems."
		entries.add(entry)

		entry = new TimesheetEntry(id:12346, sourceSystemName:'TSheets', username:'tconsultant', customerName:'Test Customer', jobName:'Test Job')
		entry.jobName = 'TestServiceItem:TestSubItem'
		entry.startTime = dfTime.parse("2011-05-29 10:30:00")
		entry.endTime = dfTime.parse("2011-05-29 12:30:00")
		entry.durationMinutes = 120
		bt = new byte [4] ;
		bt[0] = new Integer(204).byteValue()
		bt[1] = new Integer(187).byteValue()
		bt[2] = new Integer(150).byteValue()
		bt[3] = new Integer(30).byteValue()
		entry.ipIn = InetAddress.getByAddress(bt)
		entry.ipOut = InetAddress.getByAddress(bt)
		entry.customerName = "Test Customer"
		entry.jobName = "Test Job"
		entry.itemName = "TestServiceItem:TestSubItem"
		entry.notes = "1030-1230:ActivityCode:Meeting - debate architecture models."
		entries.add(entry)

		TimesheetEntry[] results = qbDAO.create(entries)

		sql = "SELECT * FROM TimeTracking WHERE CustomerRefFullName LIKE ? ORDER BY TxnDate, Notes"
		SqlRowSet rowSet = simpleJdbcTemplate.getJdbcOperations().queryForRowSet(sql, "Test Customer%")
		assertTrue "No rows entered.", rowSet.last()
		assertEquals "Number of rows entered;", 2, rowSet.getRow()
		rowSet.first()
		assertEquals "TxnDate;", dfDate.parse("2011-05-29"), rowSet.getDate("TxnDate")
		assertEquals "CustomerRefFullName;", "Test Customer:Test Job", rowSet.getString("CustomerRefFullName")
		assertEquals "EntityRefFullName;", "Test Consultant", rowSet.getString("EntityRefFullName")
		assertEquals "ItemServiceRefFullName;", "TestServiceItem:TestSubItem", rowSet.getString("ItemServiceRefFullName")
		assertEquals "DurationMinutes;", 90, rowSet.getInt("DurationMinutes")
		assertEquals "Notes;", "0900-1030:ActivityCode:Researched messaging systems.", rowSet.getString("Notes")
		assertEquals "BillableStatus;", "Billable", rowSet.getString("BillableStatus")

		rowSet.next()
		assertEquals "TxnDate;", dfDate.parse("2011-05-29"), rowSet.getDate("TxnDate")
		assertEquals "CustomerRefFullName;", "Test Customer:Test Job", rowSet.getString("CustomerRefFullName")
		assertEquals "EntityRefFullName;", "Test Consultant", rowSet.getString("EntityRefFullName")
		assertEquals "ItemServiceRefFullName;", "TestServiceItem:TestSubItem", rowSet.getString("ItemServiceRefFullName")
		assertEquals "DurationMinutes;", 120, rowSet.getInt("DurationMinutes")
		assertEquals "Notes;", "1030-1230:ActivityCode:Meeting - debate architecture models.", rowSet.getString("Notes")
		assertEquals "BillableStatus;", "Billable", rowSet.getString("BillableStatus")
	}
}