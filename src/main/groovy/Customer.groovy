@Grab('commons-lang:commons-lang:2.6')

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