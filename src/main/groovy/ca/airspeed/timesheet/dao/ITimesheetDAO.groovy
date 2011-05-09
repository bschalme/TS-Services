package ca.airspeed.timesheet.dao

import ca.airspeed.canonical.TimesheetEntry

interface ITimesheetDAO {
	List<TimesheetEntry> read(Date from, Date to);
	
	TimesheetEntry create(TimesheetEntry entry)
	
	List<TimesheetEntry> create(List<TimesheetEntry> entries)
	
	TimesheetEntry update(TimesheetEntry entry)
	
	void delete(TimesheetEntry entry)
}