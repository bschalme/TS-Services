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
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.1')

package ca.airspeed.timesheet.tsheets

import org.apache.commons.lang.StringUtils;

import groovyx.net.http.RESTClient
// import groovy.json.JsonSlurper
import net.sf.json.groovy.JsonSlurper

class TSheetsToken implements ITSheetsToken {
	String url
	String apiKey
	String username
	String password
	final String token

	public TSheetsToken(String url, String apiKey, String username, String password) {
		this.url = url
		RESTClient tsheets = new RESTClient( url )
		def resp = tsheets.get( query: [ action:'get_token', api_key:apiKey, username:username, password:password, output_format:'json' ] )
		assert resp.status == 200, 'TSheets get_token failed with ' + resp.status
		def result = new JsonSlurper().parseText(resp.data.toString())
		assert result.status == 'ok'
		token = result.token
	}

	public String logout() {
		String result = 'ok'
		if (StringUtils.isNotBlank(token)) {
			RESTClient tsheets = new RESTClient( url )
			def resp = tsheets.get( query: [ action:'logout', token:token, output_format:'json' ] )
			def content = new JsonSlurper().parseText(resp.data.toString())
			result = content.status
		}
		return result
	}
}
