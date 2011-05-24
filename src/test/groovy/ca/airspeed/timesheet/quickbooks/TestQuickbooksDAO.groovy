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

import static org.junit.Assert.*

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Test
    void testCanary() {
        assertTrue "Something went wrong!", true
        def sql = "SELECT CompanyName FROM Company"
        RowMapper<String> mapper = new RowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("CompanyName");
            }
        };
        def companyName = simpleJdbcTemplate.queryForObject(sql, mapper)
        assertEquals "CompanyName prefix;", "TEST ", StringUtils.substring(companyName, 0, 5)
    }
}