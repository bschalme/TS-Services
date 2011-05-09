package ca.airspeed.timesheet.tsheets

import groovy.json.*
import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.JSON
import org.apache.commons.lang.NotImplementedException
import java.text.SimpleDateFormat
import java.util.Date;
import java.util.List;
import ca.airspeed.timesheet.dao.ITimesheetDAO
import ca.airspeed.canonical.TimesheetEntry
import org.springframework.beans.factory.annotation.Autowired

class TSheetsDAO implements ITimesheetDAO {
	@Autowired
	ITSheetsToken token

	String apiUrl

	List<TimesheetEntry> read(Date from, Date to) {
		def tsheets = new RESTClient(apiUrl)
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd")
		// def ymd = "yyyy-MM-dd"
		def resp = tsheets.get(query : [ action:'get_timesheets', token:token.getToken(), start_date:df.format(from), end_date:df.format(to), output_format:'json' ])
		assert "Problem with get_timesheets return status", resp.status == 200
		// assert resp.contentType == JSON.toString()
		def doc = new JsonSlurper().parseText(resp.data.toString())
		def results = []
		SimpleDateFormat dfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		for ( data in doc.data ) {
			def entry = new TimesheetEntry(id:data.ts_id, sourceSystemName:'TSheets', username:data.username)
			entry.startTime = dfTime.parse(data.start_time)
			entry.endTime = dfTime.parse(data.end_time)
			
			def ip = data.ip_in.tokenize(".")
			byte []  bt = new byte [4]
			bt[0] = new Integer(ip.get(0)).byteValue() 
			bt[1] = new Integer(ip.get(1)).byteValue()
			bt[2] = new Integer(ip.get(2)).byteValue()
			bt[3] = new Integer(ip.get(3)).byteValue()
			entry.ipIn = InetAddress.getByAddress(bt)
			
			ip = data.ip_out.tokenize(".")
			bt[0] = new Integer(ip.get(0)).byteValue() 
			bt[1] = new Integer(ip.get(1)).byteValue()
			bt[2] = new Integer(ip.get(2)).byteValue()
			bt[3] = new Integer(ip.get(3)).byteValue()
			entry.ipOut = InetAddress.getByAddress(bt)
			
			def jobcode = data.jobcode.tokenize(">>")
			entry.customerName = jobcode.get(0).trim()
			entry.jobName = jobcode.get(1).trim()
			entry.itemName = jobcode.get(2).trim()
			entry.durationMinutes = new Integer(data.total_seconds) / 60
			entry.notes = data.notes
			
			results << entry
		}
		return results
	}

	TimesheetEntry create(TimesheetEntry entry) {
		throw new NotImplementedException()
	}

	List<TimesheetEntry> create(List<TimesheetEntry> entries) {
		throw new NotImplementedException()
	}

	TimesheetEntry update(TimesheetEntry entry) {
		throw new NotImplementedException()
	}

	void delete(TimesheetEntry entry) {
		throw new NotImplementedException()
	}
}