package ca.airspeed.timesheet.tsheets

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations=['/applicationContext-TEST.xml'])
class TestTSheetsToken {

	@Autowired
	TSheetsToken tsheetsToken

	@After
	void tearDown() {
		if (tsheetsToken != null) {
			tsheetsToken.logout();
		}
	}

	@Test
	void testGetToken() {
		Assert.assertTrue "Expected some sort of token.", StringUtils.isNotBlank(tsheetsToken.getToken())
	}
}
