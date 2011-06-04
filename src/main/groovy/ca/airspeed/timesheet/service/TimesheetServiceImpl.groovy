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

import ca.airspeed.timesheet.dao.ITimesheetDAO
import ca.airspeed.control.IControlDAO
import ca.airspeed.canonical.Control
import ca.airspeed.canonical.TimesheetEntry

import org.apache.commons.lang.NotImplementedException
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;


class TimesheetServiceImpl implements ITimesheetService {
	private static final String YMD = "yyyy-MM-dd"
	private static final String LAST_FETCHED = "timesheet.last.fetch.date"

	@Autowired
	ITimesheetDAO timesheetDao

	@Autowired
	IControlDAO controlDao

	TimesheetEntry[] create(List<TimesheetEntry> entries) {
		if (entries.size() > 0) {
			entries = timesheetDao.create(entries)
		}
		return entries
	}

	TimesheetEntry[] read(Date from, Date to) {
		throw new NotImplementedException()
	}

	TimesheetEntry[] fetchNewTimesheetEntries() {
		Control ctrl = controlDao.read(LAST_FETCHED)
		DateTimeFormatter fmt = DateTimeFormat.forPattern(YMD)
		DateTime lastFetchedDT = fmt.parseDateTime(ctrl.value)
		String yesterdayStr = fmt.print(new DateTime().minusDays(1))
		DateTime yesterday = fmt.parseDateTime(yesterdayStr)

		def results =  []
		// If the last fetch date is before yesterday, grab the new TimeSheetEntries, and
		// update last fetch date:
		if (lastFetchedDT.compareTo(yesterday) < 0) {
			results = timesheetDao.read(lastFetchedDT.plusDays(1).toDate(), yesterday.toDate())
			ctrl.value = yesterdayStr
			controlDao.update(ctrl)
		}
		return results
	}
}