@GrabResolver(name='mvnRepository', root='http://central.maven.org/maven2/')
@Grab(group='org.slf4j', module='slf4j-api', version='1.7.6')
@Grab(group='org.slf4j', module='jcl-over-slf4j', version='1.7.6')
@Grab(group='net.sourceforge.nekohtml', module='nekohtml', version='1.9.14')
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.1')
@Grab(group='xerces', module='xercesImpl', version='2.9.1')
@Grab(group='org.apache.jena', module='jena-tdb', version='1.0.2')
@Grab(group='org.apache.jena', module='jena-core', version='2.11.2')
@Grab(group='org.apache.jena', module='jena-arq', version='2.11.2')
@Grab(group='org.apache.jena', module='jena-iri', version='1.0.2')
@Grab(group='org.apache.jena', module='jena-spatial', version='1.0.1')
@Grab(group='org.apache.jena', module='jena-security', version='2.11.2')
@Grab(group='org.apache.jena', module='jena-text', version='1.0.1')
@Grab(group='virtuoso', module='virtjena', version='2')
@Grab(group='virtuoso', module='virtjdbc', version='4.1')

import groovyx.net.http.*
// import groovyx.net.http.ContentType.URLENC
import groovyx.net.http.ContentType.*
import groovyx.net.http.Method.*
import groovyx.net.http.*
import org.apache.http.entity.mime.*
import org.apache.http.entity.mime.content.*
import java.nio.charset.Charset
import groovy.json.JsonOutput.*
import virtuoso.jena.driver.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.* ;
import com.hp.hpl.jena.graph.*;
import java.text.SimpleDateFormat
// import com.hp.hpl.jena.datatypes.xsd.XSDDatatype

// 
def config_file = new File('SocrataVirtBridge-config.groovy')

def config = new ConfigSlurper().parse(config_file.toURL())
if ( ! config.maxtimestamp ) {
  println("Intialise timestamp");
  config.maxtimestamp = 0
}

def reading_date_format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
// def since = "2015-01-01+00%3A00%3A00"
def since = reading_date_format.format(new Date(config.maxtimestamp))

println("Get readings since "+java.net.URLEncoder.encode(since,'UTF8'));


// SPARQL :: 
def qry = "http://apps.opensheffield.org/sparql?default-graph-uri=&query=select+%3Fobservation%2C+%3Fsensor%2C+%3Ftimestamp%2C+%3FobservationValue%0D%0Awhere+%7B%0D%0A++++%3Fobservation+%3Chttp%3A%2F%2Fpurl.oclc.org%2FNET%2Fssnx%2Fssn%23hasValue%3E+%3FobservationValue+.%0D%0A++++%3Fobservation+%3Chttp%3A%2F%2Fpurl.oclc.org%2FNET%2Fssnx%2Fssn%23endTime%3E+%3Ftimestamp+.%0D%0A++++%3Fobservation+%3Curi%3A%2F%2Fopensheffield.org%2Fproperties%23sensor%3E+%3Fsensor+.%0D%0A++++%3Fsensor+a+%3Curi%3A%2F%2Fopensheffield.org%2Ftypes%23realtimeMonitoringStation%3E+.%0D%0A++++FILTER+%28+xsd%3AdateTime%28%3Ftimestamp%29+%3E+xsd%3AdateTime%28%27"+since+"%27%29+%29%0D%0A%7D&format=text%2Fcsv&timeout=0&debug=on"

def qry_url = new URL(qry);

// Returns rows of format
// "observation","sensor","timestamp","observationValue"
// "uri://opensheffield.org/datagrid/sensors/Groundhog1/LD-Groundhog1_NO2.ic/201501011000","uri://opensheffield.org/datagrid/sensors/Groundhog1/LD-Groundhog1_NO2.ic","2015-01-01T10:00Z",6.59468

data_url.withReader { br ->
  def line=null
  while ( ( line = br.readLine() ) != null ) {
    println(line);
  }
}


println("Done.");

config_file.withWriter { writer ->
  config.writeTo(writer)
}

