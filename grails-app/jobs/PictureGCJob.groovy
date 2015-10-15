import org.codehaus.groovy.grails.commons.ConfigurationHolder
class PictureGCJob {
	static triggers = {
		//simple repeatInterval: 60000 // execute job once in 60 seconds
		cron cronExpression: ((ConfigurationHolder.config.PGC.cron!=[:])?ConfigurationHolder.config.PGC.cron:"0 0 3 * * ?")
	}

  def execute() {
    log.debug("LOG>> PictureGCJob Start")
    Picture.findAllByIdNotInList((Cashzakaz.findAllByFile_idGreaterThan(0).collect{it.file_id}?:[0l])+Cashreport.findAllByFile_idGreaterThan(0).collect{it.file_id}+Cash.findAllByReceiptGreaterThan(0).collect{it.receipt}+Cashdepartment.findAllByReceiptGreaterThan(0).collect{it.receipt}+Salaryreport.findAllByFileGreaterThan(0).collect{it.file}+License.findAllByFileGreaterThan(0).collect{it.file}+Payrequest.findAllByFile_idGreaterThan(0).collect{it.file_id}+Feedback.findAllByFile_idGreaterThan(0).collect{it.file_id}).unique().each{ it.delete(flush:true) }
    log.debug("LOG>> PictureGCJob Finish")
  }

}