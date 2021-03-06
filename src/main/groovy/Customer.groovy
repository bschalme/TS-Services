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
// @Grab('commons-lang:commons-lang:2.6')

import org.apache.commons.lang.builder.ToStringBuilder
import org.apache.commons.lang.builder.ToStringStyle

class Customer {
    // properties
    Integer id
    String name
    Date dob
    
    public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE ).append("id",id).append("name", name).append("dob",dob).toString()
    }

    // sample code
    static void main(args) {
        def customer = new Customer(id:1, name:"Gromit", dob:new Date())
        def xml = new NodeBuilder()
        xml.Customer() {
          Id(customer.id)
          Name(customer.name)
          DateOfBirth(customer.dob)
        }
        println("Hello ${customer.name}")
        println customer
        new NodePrinter().print(xml.getCurrentNode())
    }
}