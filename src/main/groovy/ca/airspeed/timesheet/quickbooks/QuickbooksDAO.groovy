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

import java.text.SimpleDateFormat;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.apache.commons.lang.NotImplementedException
import ca.airspeed.timesheet.dao.ITimesheetDAO
import ca.airspeed.canonical.TimesheetEntry


class QuickbooksDAO implements ITimesheetDAO {
	private SimpleJdbcTemplate simpleJdbcTemplate

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource)
	}

	List<TimesheetEntry> read(Date from, Date to) {
		throw new NotImplementedException()
	}

	TimesheetEntry create(TimesheetEntry entry) {
		throw new NotImplementedException()
	}

	List<TimesheetEntry> create(List<TimesheetEntry> entries) {
		def userMap = ["bschalme":"Brian Schalme", "tconsultant":"Test Consultant"]
		SimpleDateFormat dfHM = new SimpleDateFormat("HHmm")
		def sql = "INSERT INTO TimeTracking (EntityRefFullName, DurationMinutes,TxnDate,CustomerRefFullName,ItemServiceRefFullName,Notes, BillableStatus) VALUES (?, ?, ?, ?, ?, ?, ?)"
		simpleJdbcTemplate.getJdbcOperations().execute("sp_batchstart TimeTracking")
		entries.each {entry ->
			def matcher = entry.notes =~ /^[0-9]{4}-[0-9]{4}:.*/
			def notes = ""
			if (matcher.matches()) {
				notes = entry.notes
			}
			else {
				notes = dfHM.format(entry.startTime) + "-" + dfHM.format(entry.endTime) + ":" + entry.notes
			}
			simpleJdbcTemplate.update(sql, userMap.get(entry.username), entry.durationMinutes, 
				entry.startTime, entry.customerName + ":" + entry.jobName, 
				entry.itemName,	notes, "Billable")
		}
		simpleJdbcTemplate.getJdbcOperations().execute("sp_batchupdate TimeTracking")
		return entries
	}

	TimesheetEntry update(TimesheetEntry entry) {
		throw new NotImplementedException()
	}

	void delete(TimesheetEntry entry) {
		throw new NotImplementedException()
	}
}