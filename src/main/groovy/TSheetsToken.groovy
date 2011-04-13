@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.1')
@Grab('net.sf.json-lib:json-lib:2.3:jdk15')

import groovyx.net.http.RESTClient
import net.sf.json.groovy.JsonSlurper
 
tsheets = new RESTClient( '' )
 
def resp = tsheets.get( query: [ action:'get_token', api_key:'', username:'', password:'', output_format:'json' ] )
assert resp.status == 200
println resp.status
println resp.contentType
def result = new JsonSlurper().parseText(resp.data.toString())
assert resp.status == 200
println "status = ${result.status}, ${result.message}"
def token = result.token
println "token = ${token}"

resp = tsheets.get( query: [action:'get_timesheets', token:token, start_date:'2011-03-11', end_date:'2011-03-12' ] )
assert resp.status == 200
result = new JsonSlurper().parseText(resp.data.toString())
println "status = ${result.status}, ${result.message}"
println(result.getClass().getCanonicalName())
println result.toString(2)

resp = tsheets.get( query: [ action:'logout', token:result.token, output_format:'json' ] )
result = new JsonSlurper().parseText(resp.data.toString())
println "status = ${result.status}, ${result.note}"
