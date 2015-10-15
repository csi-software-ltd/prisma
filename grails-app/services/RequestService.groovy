import org.codehaus.groovy.grails.commons.ConfigurationHolder
import javax.servlet.http.Cookie

class RequestService {
  boolean transactional = false
  static scope = "request" //!!!!
  static proxy = true
  def transient m_oController=null  
  def static final DATE_FORMAT='dd.MM.yyyy'  
  
  def init(oController){
    m_oController=oController
  }
  ////////////////////////////////////////////////////////////////////////////////////
  private checkInit(){
    if(m_oController==null)
      log.debug("Does not set controller object in Request Service. Call requestService.init(this)")
    return (m_oController==null)
  }  
  ////////////////////////////////////////////////////////////////////////////////////
  def getContextAndDictionary(bContextOnly=false,bGetInfotext=false) {
    if(checkInit()) return [:]
    
    def sServer=m_oController.request.getServerName().toLowerCase()	
    def iPort=m_oController.request.getServerPort() 
    def hsRes=[
      context:[           
        is_dev:(Tools.getIntVal(ConfigurationHolder.config.isdev,0)==1),
        serverURL:(ConfigurationHolder.config.grails.mailServerURL?:ConfigurationHolder.config.grails.serverURL),
        sequreServerURL:(ConfigurationHolder.config.grails.secureServerURL?:ConfigurationHolder.config.grails.serverURL),
        systemstatus:Tools.getIntVal(Dynconfig.findByName('system.activity.status')?.value,0)
      ]        
    ]
    /*def oValutarate = new Valutarate()      
    hsRes.rates_current = oValutarate.csiSearchToday()
    hsRes.rates_next = oValutarate.csiSearchTomorrow()
    */

    if(bContextOnly)
      return hsRes

    hsRes.notice = [:]
    if (m_oController.session.user.cashaccess==0) {
      hsRes.notice.cashreport_count = Cashreport.countByModstatusInListAndExecutor(-2..-1,m_oController.session.user.id)
      if(hsRes.notice.cashreport_count == 1)
        hsRes.notice.cashreport_id = Cashreport.findByModstatusInListAndExecutor(-2..-1,m_oController.session.user.id)?.id?:0
    } else if (m_oController.session.user.cashaccess==1) {
      hsRes.notice.cashreport_count = Cashreport.countByModstatusInListAndExecutor(-2..-1,m_oController.session.user.id)
      if(hsRes.notice.cashreport_count == 1)
        hsRes.notice.cashreport_id = Cashreport.findByModstatusInListAndExecutor(-2..-1,m_oController.session.user.id)?.id?:0
      hsRes.notice.cashzakaz_count = Cashzakaz.countByModstatusAndInitiator(3,m_oController.session.user.id)
      if(hsRes.notice.cashzakaz_count == 1)
        hsRes.notice.cashzakaz_id = Cashzakaz.findByModstatusAndInitiator(3,m_oController.session.user.id)?.id?:0
    } else if (m_oController.session.user.cashaccess==2) {
      hsRes.notice.cashreport_count = Cashreport.countByModstatusInListAndDepartment_id([0,-2],m_oController.session.user.department_id)
      if(hsRes.notice.cashreport_count == 1)
        hsRes.notice.cashreport_id = Cashreport.findByModstatusInListAndDepartment_id([0,-2],m_oController.session.user.department_id)?.id?:0
      hsRes.notice.cashzakaz_count = Cashzakaz.countByModstatusAndDepartment_id(3,m_oController.session.user.department_id)
      if(hsRes.notice.cashzakaz_count == 1)
        hsRes.notice.cashzakaz_id = Cashzakaz.findByModstatusAndDepartment_id(3,m_oController.session.user.department_id)?.id?:0
      hsRes.notice.cashsaldo = Department.get(m_oController.session.user.department_id)?.cashsaldo?:0
    } else if (m_oController.session.user.cashaccess==3) {
      hsRes.notice.cash_count = Cashrequest.countByModstatusInList([3,6])
      if(hsRes.notice.cash_count == 1)
        hsRes.notice.cash_id = Cashrequest.findByModstatusInList([3,6])?.id?:0
      hsRes.notice.cashreport_count = Cashreport.countByModstatus(1)
      if(hsRes.notice.cashreport_count == 1)
        hsRes.notice.cashreport_id = Cashreport.findByModstatus(1)?.id?:0
      hsRes.notice.cashzakaz_count = Cashzakaz.countByModstatus(1)
      if(hsRes.notice.cashzakaz_count == 1)
        hsRes.notice.cashzakaz_id = Cashzakaz.findByModstatus(1)?.id?:0
      hsRes.notice.cashsaldo = Holding.get(1).cashsaldo
    } else if (m_oController.session.user.cashaccess==4) {
      hsRes.notice.cash_count = Cashrequest.countByModstatus(4)
      if(hsRes.notice.cash_count == 1)
        hsRes.notice.cash_id = Cashrequest.findByModstatus(4)?.id?:0
    } else if(m_oController.session.user.cashaccess==5){
      hsRes.notice.cash_count = Cashrequest.countByModstatus(2)
      if(hsRes.notice.cash_count == 1)
        hsRes.notice.cash_id = Cashrequest.findByModstatus(2)?.id?:0
    }
    if (m_oController.session.user.group.is_salaryapprove) {
      hsRes.notice.salaryreport_count = Salaryreport.countByIs_confirmAndSalarytype_id(-1,5)
      if(hsRes.notice.salaryreport_count == 1)
        hsRes.notice.salaryreport_id = Salaryreport.findByIs_confirmAndSalarytype_id(-1,5)?.id?:0
    }
    if (m_oController.session.user.group.is_salarynaledit) {
      hsRes.notice.salarydecreport_count = Salaryreport.countByIs_confirmAndDepartment_idAndSalarytype_id(-1,m_oController.session.user.department_id,1)
      hsRes.notice.cashsalarydecreport_count = Salaryreport.countByIs_confirmAndDepartment_idAndSalarytype_id(-2,m_oController.session.user.department_id,5)
      if(hsRes.notice.salarydecreport_count == 1 || hsRes.notice.cashsalarydecreport_count == 1){
        hsRes.notice.salarydecreport_id = Salaryreport.findByIs_confirmAndDepartment_idAndSalarytype_id(-1,m_oController.session.user.department_id,1)?.id?:0
        hsRes.notice.cashsalarydecreport_id = Salaryreport.findByIs_confirmAndDepartment_idAndSalarytype_id(-2,m_oController.session.user.department_id,5)?.id?:0
      }
    }
    if (m_oController.session.user.group.is_payaccept) {
      hsRes.notice.taskpay_count = Taskpay.countByIs_acceptAndIs_clientAndIs_internal(0,0,0)
      if(hsRes.notice.taskpay_count == 1)
        hsRes.notice.taskpay_id = Taskpay.findByIs_acceptAndIs_clientAndIs_internal(0,0,0)?.id?:0
    }
    if (m_oController.session.user.group.is_payplanexec) {
      def paygroups = [0]
      if (m_oController.session.user.group.is_viewbudgpayplantask) paygroups << 1
      if (m_oController.session.user.group.is_viewkredpayplantask) paygroups << 2
      if (m_oController.session.user.group.is_viewrentpayplantask) paygroups << 3
      if (m_oController.session.user.group.is_viewgnrlpayplantask) paygroups << 4
      hsRes.notice.taskpaynotassign_count = Taskpay.countByPaygroupInListAndTaskpaystatusAndIs_accept(paygroups,0,1)+Taskpay.countByTaskpaystatusAndExecutor(1,m_oController.session.user.id)
      if(hsRes.notice.taskpaynotassign_count == 1)
        hsRes.notice.taskpaynotassign_id = (Taskpay.findByPaygroupInListAndTaskpaystatusAndIs_accept(paygroups,0,1)?.id?:0)?:Taskpay.findByTaskpaystatusAndExecutor(1,m_oController.session.user.id)?.id?:0
    }
    if (m_oController.session.user.group.is_prolongwork) {
      hsRes.notice.spaceprolong_count = Space.countByPermitstatusAndWorkstatusAndModstatusGreaterThanEquals(1,0,0)
      if(hsRes.notice.spaceprolong_count == 1)
        hsRes.notice.spaceprolong_id = Space.findByPermitstatusAndWorkstatusAndModstatusGreaterThanEquals(1,0,0)?.id?:0
    }
    if (m_oController.session.user.group.is_superuser) {
      hsRes.notice.question_count = Feedback.countByModstatus(0)
      if(hsRes.notice.question_count == 1)
        hsRes.notice.question_id = Feedback.findByModstatus(0)?.id?:0
    } else {
      hsRes.notice.question_count = Feedback.countByModstatusGreaterThanAndIs_readanswerAndUser_id(0,0,m_oController.session.user.id)
      if(hsRes.notice.question_count == 1)
        hsRes.notice.question_id = Feedback.findByModstatusGreaterThanAndIs_readanswerAndUser_id(0,0,m_oController.session.user.id)?.id?:0
    }
    if (m_oController.session.user.group.is_task) {
      hsRes.notice.task=Task.findAll("FROM Task WHERE taskstatus in (1,3,5) AND (executor=:user OR (department_id=:dep_id and executor=0))",[user:m_oController.session.user.id,dep_id:m_oController.session.user.department_id?:-1])
    }

    if(bGetInfotext){
    //infotext ---------------------------------		      	  	      	            
      //hsRes?.infotext=Infotext.findWhere(controller:m_oController.controllerName,action:m_oController.actionName)
      //hsRes?.clientmenu=Infotext.findAll('FROM Infotext WHERE itemplate_id=1 ORDER BY npage ASC')
    }
    
    return hsRes
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getRaw(sName){
    if(checkInit()) return null
    
    return m_oController.params[sName]
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getIds(sName){
    if(checkInit()) return null
    if(m_oController.params[sName]==null||m_oController.params[sName]=='')
      return null
    
    def lsRes=[]
    try{
      if(m_oController.params[sName] instanceof String){	  
        lsRes << m_oController.params[sName].toLong()
	    }
      else
        lsRes=m_oController.params[sName].collect{it.toLong()}
    }catch(Exception e){
      log.debug("getIds wrong Long \n"+e.toString())
      lsRes=null
    }
    return lsRes
  }

  def getIntIds(sName){
    if(checkInit()) return null
    if(m_oController.params[sName]==null||m_oController.params[sName]=='')
      return null
    
    def lsRes=[]
    try{
      if(m_oController.params[sName] instanceof String){    
        lsRes << m_oController.params[sName].toInteger()
      }
      else
        lsRes=m_oController.params[sName].collect{it.toInteger()}
    }catch(Exception e){
      log.debug("getIds wrong Int \n"+e.toString())
      lsRes=null
    }
    return lsRes
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getStringList(sName){
    if(checkInit()) return []
    if(m_oController.params[sName]==null)
      return []
    
    def lsRes=[]
    try{
      if(m_oController.params[sName] instanceof String)
        lsRes << m_oController.params[sName].trim()
      else
        lsRes=m_oController.params[sName].collect{it.trim()}
    }catch(Exception e){
      log.debug("Error in list param ${sName}\n"+e.toString())
      lsRes=null
    }
    return lsRes
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getLongDef(sName,iDef){
    try{
	    iDef=iDef.toLong()
	  }catch(Exception e){
      //exception here usual on convertion. Pass it.
    }
    if(checkInit()) return iDef
    
    if (m_oController.params==null) return iDef
    if (m_oController.params[sName]==null) return iDef
    if (m_oController.params[sName]=='') return iDef
    try{	
      return m_oController.params[sName].replace(' ','').toLong()
    }catch(Exception e){      
      try { return m_oController.params[sName].replace((160 as Character).toString(),'').toLong() } catch(Exception e2) {}
    }
    return iDef
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getFloatDef(sName,iDef){
    try{
	    iDef=iDef.toFloat()
	  }catch(Exception e){
      //exception here usual on convertion. Pass it.
    }
    if(checkInit()) return iDef
    
    if (m_oController.params==null) return iDef
    if (m_oController.params[sName]==null) return iDef
    if (m_oController.params[sName]=='') return iDef
    try{	
      return m_oController.params[sName].toFloat()
    }catch(Exception e){
      //exception here usual on convertion. Pass it.
    }
    return iDef
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getBigDecimalDef(sName,iDef){
    try{
	    iDef=iDef.toBigDecimal()
	  }catch(Exception e){
      //exception here usual on convertion. Pass it.
    }
    if(checkInit()) return iDef
    
    if (m_oController.params==null) return iDef
    if (m_oController.params[sName]==null) return iDef
    if (m_oController.params[sName]=='') return iDef
    try{	
      return m_oController.params[sName].replace(',','.').toBigDecimal()
    }catch(Exception e){
      try { return m_oController.params[sName].replace(',','.').replace((160 as Character).toString(),'').toBigDecimal() } catch(Exception e2) {}
      //exception here usual on convertion. Pass it.
    }
    return iDef
  }
   
  ///////////////////////////////////////////////////////////////////////////////////
  def getStr(sName){
    if(checkInit()) return ''
    
    try{
      if (m_oController.params==null)
        return ""
      if (m_oController.params[sName]==null)
        return ""
      return m_oController.params[sName].trim()
    }catch(Exception e){
      log.debug("Error in string param ${sName}\n"+e.toString())
    }
    return ""
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getPhoneCode(sName){
    def sPhone = getPreparedStr(sName)
    try{
      sPhone=sPhone.replace('(','').replace(')','').replace('+','').replace('-','').replace(' ','').replace('.','')
      def iTest=sPhone.toLong()
      return sPhone
    }catch(Exception e){
      return ''
    }
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getPhone(sName){
    def sPhone=getPhoneCode(sName)
    if(sPhone.size()>3)
      return sPhone[0..2]+'-'+sPhone[3..-1]
    else
      return sPhone
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getPreparedStrNull(sName){
    sName=getPreparedStr(sName)
    return (sName=='')?null:sName
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getPreparedStr(sName){
    return Tools.prepareSearchString(getStr(sName))
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getMax(){
    def iCfg=Tools.getIntVal(ConfigurationHolder.config.request.max)
    def iMax=getLongDef('max',iCfg).toInteger()
    return Math.min( iMax,iCfg)
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getOffset(){
    def iOffset=getLongDef('offset',0).toInteger()
    return Math.max(iOffset,0)
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getIntFromFloatDef(sName,iDef){
    if(checkInit()) return iDef
    
    if (m_oController.params==null) return iDef
    def sIn=m_oController.params[sName]
    if (sIn==null) return iDef
    if (sIn=='') return iDef
    sIn=sIn.replace(',','.')
    if (sIn=='.') return iDef
    def iPos=sIn.indexOf('.')
    if(iPos>=0)
      sIn=sIn.substring(0,iPos)
    if (sIn=='') return (iPos>=0)?0:iDef
    try{
      return sIn.toInteger()
    }catch(Exception e){
      //pass exception
    }
    return iDef
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getIntDef(sName,iDef){
    if(checkInit()) return iDef
    
    if (m_oController.params[sName]==null) return iDef
    if (m_oController.params[sName]=='') return iDef
    try{
      return m_oController.params[sName].replace(' ','').toInteger()
    }catch(Exception e){
      try { return m_oController.params[sName].replace((160 as Character).toString(),'').toInteger() } catch(Exception e2) {}
    }
    return iDef
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def setSessionIds(sSessionName,sParamName){
    if(checkInit()) return 
    def sId=getLongDef(sParamName,0).toString()
    if(sId!='0'){
      if(m_oController.session[sSessionName]==null)
        m_oController.session[sSessionName]=[]
      if(sId in m_oController.session[sSessionName]){
        while (sId in m_oController.session[sSessionName])
          m_oController.session[sSessionName].remove(sId)
      }else{
        m_oController.session[sSessionName] << sId
      }
    }
  }
  
  ///////////////////////////////////////////////////////////////////////////////////
  def setSessionParamIds(sSessionName,sId){
    if(checkInit()) return
    if(sId!='0'){
      if(m_oController.session[sSessionName]==null)
        m_oController.session[sSessionName]=[]
      if(sId in m_oController.session[sSessionName]){           
      }else{
        m_oController.session[sSessionName] << sId
      }
    }     
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def setCookie(sName,sValue,iTimeout,bDomain = true){   
    if(checkInit()) return
    
    if((m_oController.response==null)||(m_oController.request==null)){
      log.debug("ERROR: Call Request method setCookie without nessesary params")
      return
    }
    def oCookie = new Cookie(sName, sValue)
	
    def sServerURL = ConfigurationHolder.config.grails.serverURL
    
    if(sServerURL.find(/localhost/)==null&&!sServerURL.matches('.*/([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\u002E([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\u002E([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\u002E([01]?\\d\\d?|2[0-4]\\d|25[0-5]).*'))
      if (bDomain){ 
          def lsServerURL = sServerURL.tokenize(':')	 
        if(sServerURL.tokenize('.').size()>2){    
            oCookie.domain = sServerURL - sServerURL.tokenize('.')[0]-(lsServerURL.size()>2?':'+lsServerURL[2]:'')
        }else{	   
          oCookie.domain = sServerURL-'http://'-(lsServerURL.size()>2?':'+lsServerURL[2]:'')		
        }
    }
    oCookie.path = '/'	
    oCookie.maxAge = iTimeout //108000// 30 days
    m_oController.response.addCookie(oCookie)
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getCookie(sName){
    if(checkInit()) return ''
    
    if((m_oController.response==null)||(m_oController.request==null)){
      log.debug("ERROR: Call Request method getCookie without nessesary params")
      return null
    }	
    def hsCookie=m_oController.request.cookies.find{it.name == sName}
	
    if(hsCookie)
      return hsCookie.value
    return null
  }    
  ////////////////////////////////////////////////////////////////////////////////////
  def getDate(sName){
    def dDate=getStr(sName)
    if(!dDate)
      return null
    try{
      return Date.parse(DATE_FORMAT, dDate)
    }catch(Exception e){
      return null
    }
  }
  /////////////////////////////////////////////////////////////////////////////////////
  def getParams(intParams,liParams=[],lsParams=[],ldParams=[],bdParams=[]){
    def hRet=[long:[:],int:[:],string:[:],bigdecimal:[:],inrequest:[:]]
    for (elem in lsParams){
      hRet.string[elem]=getStr(elem)
      if(hRet.string[elem]!='')
      hRet.inrequest[elem]=hRet.string[elem]
    }
	  for (elem in liParams){
      hRet.long[elem]=getLongDef(elem,0)
      if(hRet.long[elem]!=0)
      hRet.inrequest[elem]=hRet.long[elem]
    }
    for (elem in intParams){
      hRet.int[elem]=getIntDef(elem,0)
      if(hRet.int[elem]!=0)
      hRet.inrequest[elem]=hRet.int[elem]
    }
    def datCurrent
    for (elem in ldParams){ //dates	
      datCurrent=getDate(elem)	  
      hRet.string[elem]=((datCurrent!=null)? datCurrent.format(DATE_FORMAT)
      : getStr(elem))
      if(hRet.string[elem]!='')
      hRet.inrequest[elem]=hRet.string[elem]
    }
    for (elem in bdParams){
      hRet.bigdecimal[elem]=getBigDecimalDef(elem,0)
      if(hRet.bigdecimal[elem]!=0)
      hRet.inrequest[elem]=hRet.bigdecimal[elem]
    }
    return hRet
  }
  //////////////////////////////////////////////////////////////////////////////////////
  def sendError(iStatusCode,sMessage = null){
    if(checkInit()) return
    
    if((m_oController.response==null)||(m_oController.request==null)){
      log.debug("ERROR: Call Request method sendError without nessesary params")
      return
    }
    try{
      if (sMessage) 
        m_oController.response.sendError(iStatusCode,sMessage)
      else
        m_oController.response.sendError(iStatusCode)
    }catch(Exception e){
      log.debug('Error in Request method sendError\n'+e.toString())
    }
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def getList(sName,bZero=false){
    if(checkInit()) return null
    if(m_oController.params[sName]==null) return null

    def lsRes = m_oController.params[sName].split(',')

    if(bZero){
      if(lsRes[0]==0 || lsRes[0]=='0' || lsRes[0]=='')
        lsRes=[]
    }
    return lsRes.collect{ it.toInteger() }
  }
}