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
package ca.airspeed.timesheet.service

import java.text.SimpleDateFormat

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import static org.junit.Assert.*
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ca.airspeed.timesheet.dao.ITimesheetDAO
import ca.airspeed.canonical.Control
import ca.airspeed.canonical.TimesheetEntry
import ca.airspeed.control.IControlDAO
import org.springframework.beans.factory.annotation.Autowired;
import static org.easymock.EasyMock.*

class TestTSheetsService {
	def mockControl = createStrictControl()

	@Test
	void testFetchTwoDays() {
		TSheetsService service = new TSheetsService()
		def mockTimesheetDao = mockControl.createMock(ITimesheetDAO.class)
		def mockControlDao = mockControl.createMock(IControlDAO.class)
		service.setTimesheetDao(mockTimesheetDao)
		service.setControlDao(mockControlDao)

		DateTime now = new DateTime()
		DateTime yesterday = now.minusDays(1)
		DateTime twoDaysAgo = now.minusDays(2)
		DateTime threeDaysAgo = now.minusDays(3)
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd")
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
		def dtYesterday = sdf.parse(fmt.print(yesterday))
		def dtTwoDaysAgo = sdf.parse(fmt.print(twoDaysAgo))

		Control oldControl = new Control(id:1, name:"timesheet.last.fetch.date", value:fmt.print(threeDaysAgo))
		Control newControl = new Control(id:1, name:"timesheet.last.fetch.date", value:fmt.print(yesterday))
		expect(mockControlDao.read("timesheet.last.fetch.date")).andReturn(oldControl)
		expect(mockTimesheetDao.read(dtTwoDaysAgo, dtYesterday)).andReturn(new ArrayList<TimesheetEntry>())
		expect(mockControlDao.update(newControl)).andReturn(newControl)

		mockControl.replay()
		def timesheetEntries = service.fetchNewTimesheetEntries()
		mockControl.verify()
	}

	@Test
	void testFetchOneDay() {
		TSheetsService service = new TSheetsService()
		def mockTimesheetDao = mockControl.createMock(ITimesheetDAO.class)
		def mockControlDao = mockControl.createMock(IControlDAO.class)
		service.setTimesheetDao(mockTimesheetDao)
		service.setControlDao(mockControlDao)

		DateTime now = new DateTime()
		DateTime yesterday = now.minusDays(1)
		DateTime twoDaysAgo = now.minusDays(2)
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd")
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
		def dtYesterday = sdf.parse(fmt.print(yesterday))

		Control oldControl = new Control(id:1, name:"timesheet.last.fetch.date", value:fmt.print(twoDaysAgo))
		Control newControl = new Control(id:1, name:"timesheet.last.fetch.date", value:fmt.print(yesterday))
		expect(mockControlDao.read("timesheet.last.fetch.date")).andReturn(oldControl)
		expect(mockTimesheetDao.read(dtYesterday, dtYesterday)).andReturn(new ArrayList<TimesheetEntry>())
		expect(mockControlDao.update(newControl)).andReturn(newControl)

		mockControl.replay()
		def timesheetEntries = service.fetchNewTimesheetEntries()
		mockControl.verify()
	}

	/**
	 * Ensures a re-run does not even try to fetch any data. When the last fetch date is yesterday, 
	 * don't even bother to check for new timesheets since the start date will be later than the
	 * end date.
	 */
	@Test
	void testFetchNothing() {
		TSheetsService service = new TSheetsService()
		def mockTimesheetDao = mockControl.createMock(ITimesheetDAO.class)
		def mockControlDao = mockControl.createMock(IControlDAO.class)
		service.setTimesheetDao(mockTimesheetDao)
		service.setControlDao(mockControlDao)

		DateTime now = new DateTime()
		DateTime yesterday = now.minusDays(1)
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd")
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
		def dtYesterday = sdf.parse(fmt.print(yesterday))

		Control oldControl = new Control(id:1, name:"timesheet.last.fetch.date", value:fmt.print(yesterday))
		expect(mockControlDao.read("timesheet.last.fetch.date")).andReturn(oldControl)

		mockControl.replay()
		def timesheetEntries = service.fetchNewTimesheetEntries()
		mockControl.verify()
	}
	
	@Test
	void testCreateFromList() {
		TSheetsService service = new TSheetsService()
		def mockTimesheetDao = mockControl.createMock(ITimesheetDAO.class)
		def mockControlDao = mockControl.createMock(IControlDAO.class)
		service.setTimesheetDao(mockTimesheetDao)
		service.setControlDao(mockControlDao)

		def entries = []
		entries.add(new TimesheetEntry())
		entries.add(new TimesheetEntry())
		entries.add(new TimesheetEntry())
		expect(mockTimesheetDao.create(entries)).andReturn(entries)
		
		mockControl.replay()
		def results = service.create(entries)
		mockControl.verify()
	}
	
	@Test
	void testCreateFromEmptyList() {
		TSheetsService service = new TSheetsService()
		def mockTimesheetDao = mockControl.createMock(ITimesheetDAO.class)
		def mockControlDao = mockControl.createMock(IControlDAO.class)
		service.setTimesheetDao(mockTimesheetDao)
		service.setControlDao(mockControlDao)

		def entries = []

		mockControl.replay()
		def results = service.create(entries)
		mockControl.verify()
	}
}