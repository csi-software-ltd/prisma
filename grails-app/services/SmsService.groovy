import org.codehaus.groovy.grails.commons.ConfigurationHolder
import grails.converters.JSON
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
/*
import groovy.json.JsonSlurper
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.PostMethod
*/

class SmsService {

	static transactional = false        	            

  def sendVerifySms(oUser) {    
		def jSonBody = [:]
		//jSonBody.apikey = "59S9QNLO7I8521U9QZYG1P7C4A4OSO383378IL6O941B74D018Y7F3TY4045UGI4"
		jSonBody.apikey = (ConfigurationHolder.config.SMSgate.apikey)?ConfigurationHolder.config.SMSgate.apikey.trim():"XXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZXXXXXXXXXXXXYYYYYYYYYYYYZZZZZZZZ"
		jSonBody.send = []
		def sendBody = [:]
		sendBody.id = getNewSMSid(oUser)
		sendBody.from = ((ConfigurationHolder.config.SMSgate.from)?ConfigurationHolder.config.SMSgate.from:'staytoday.ru')
		if(sendBody.from.size()>11)
			sendBody.from = sendBody.from[0..10]
		sendBody.to = oUser.tel.replace('+','').replace('(','').replace(')','').replace(' ','').replace('-','')
		sendBody.text = oUser.smscode
		jSonBody.send << sendBody

		def error = 0
		def servId = ''
		def http = new HTTPBuilder('http://smspilot.ru')
		http.request(POST, JSON) {
	  	uri.path = '/api2.php'
	  	uri.query = [json:(jSonBody as JSON)]
	  	headers.Accept = 'application/json'
	  	response.success = { resp, json ->
				def tempResponse = json.text
				def parsedJSON = JSON.parse(tempResponse)
				if (parsedJSON){
		  		try{
						if (parsedJSON.send[0].error!='0'){
			  			error = (parsedJSON.send[0].error as int)
						}
						servId = parsedJSON.send[0]?.server_id?:''
		  		} catch (Exception e){
						try{
			  			if (parsedJSON.error.code!='0'){
								error = (parsedJSON.error.code as int)
			  			}
						} catch (Exception er){
			  			log.debug ('\nError parsing json sms gate response: '+er)
			  			error = 500
						}
		  		}
				} else {
		  		try {
						def parsedXML = new XmlSlurper().parseText(tempResponse)
						error = ((parsedXML.code[0]?:404).toString() as int)
		  		} catch (Exception e){
						log.debug ('\nError parsing xml sms gate response: '+e)
						error = 500
		  		}
				}
	  	}
	  	response.failure = { resp ->
				error = 404
	  	}
		}
		updateSmsStatus(sendBody.id,error,servId)
		return error
  }  

  def updateSmsStatus(lId,iStatus,sServerId) {
		def oSms = Sms.get(lId)
		if(oSms)
	  	oSms.updateStatusAndServerId(iStatus,sServerId)
		else {
	  	log.debug ('\nError updating sms status')
		}
  }
  
  def getNewSMSid(oUser) {
		def oSms = new Sms(oUser)
		if(!oSms.save(flush:true)) {
	  	log.debug(" Error on add Sms:")
	  	oSms.errors.each{log.debug(it)}
	  	return oUser.id
		}else{
	  	return oSms.id
		}
  }

  def getNewSMSid(lId, sTel, sSmscode) {
		def oSms = new Sms(lId, sTel, sSmscode)
		if(!oSms.save(flush:true)) {
	  	log.debug(" Error on add Sms:")
	  	oSms.errors.each{log.debug(it)}
	  	return lId
		}else{
	  	return oSms.id
		}
  }  
}