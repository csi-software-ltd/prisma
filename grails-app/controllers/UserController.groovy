import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class UserController {  
  def requestService  
  def smsService  
  
  def beforeInterceptor = [action:this.&checkUser,except:['login','index','smscode','verifySMSCode','verifyUser','justtest']]

  def checkUser() {
    if(session?.user?.id!=null){
      def oTemp_notification=Temp_notification.findWhere(id:1,status:1)
      session.attention_message=oTemp_notification?oTemp_notification.text:null
    }else{
      redirect(action:'index')
      return false;
    }
  }
     
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////
  def index = {    
    if (session?.user?.id){
      redirect(action:'panel')
      return
    } else return params
  }
///////////////////////////////////////////////////////////////////////////////////////////////////////////
  def checkAccess(iActionId){
    def bDenied = true
    if(session?.user)
      session.user.menu.each{
	      if (iActionId==it.id) bDenied = false
	    }  
    if (bDenied) {    
	    redirect(controller:'user',action:'panel')
	    return
	  }
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Login >>>/////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def login = {            
    if(Temp_ipblock.findWhere(userip:request.remoteAddr,status:1)){
      redirect(controller:'user',action:'index')
      return
    }    
    requestService.init(this)
    
    def sUser=requestService.getStr('login')
    def sPassword=requestService.getStr('password')	    
    if (sUser==''){
      flash.error = 1 // set login
      redirect(controller:'user',action:'index')
      return
    }
    def oUserlog = new Userlog()
    def blocktime = Tools.getIntVal(ConfigurationHolder.config.user.blocktime,900)
    def unsuccess_log_limit = Tools.getIntVal(ConfigurationHolder.config.user.unsuccess_log_limit,3)
    sPassword = Tools.hidePsw(sPassword)
    def oUser = User.find('from User where login=:login or email=:login and modstatus=1',[login:sUser.toLowerCase()])
    if(!oUser){
      flash.error=2 // Wrong password or User does not exists
      redirect(controller:'user',action:'index')
      return      
    }else if (oUser.is_block || oUserlog.csiCountUnsuccessDurationLogs(oUser.id)[0]>=Tools.getIntVal(ConfigurationHolder.config.user.unsuccess_duration_log_limit,30)){
      flash.error=5 // User blocked
      oUserlog = new Userlog(user_id:oUser.id,logtime:new Date(),ip:request.remoteAddr,success:3,success_duration:-1)
      if (!oUserlog.save(flush:true)){
        log.debug('error on save Userlog in User:login')
        oUserlog.errors.each{log.debug(it)}
      }
      if(!oUser.is_block){
        oUser.is_block=1
        if (!oUser.save(flush:true)){
          log.debug('error on save User in User:login')
          oUser.errors.each{log.debug(it)}
        }            
      }
      redirect(controller:'user',action:'index')
      return	
    }else if (oUserlog.csiCountUnsuccessLogs(oUser.id, new Date(System.currentTimeMillis()-blocktime*1000))[0]>=unsuccess_log_limit){
      flash.error=3 // User blocked
      oUserlog = new Userlog(user_id:oUser.id,logtime:new Date(),ip:request.remoteAddr,success:2,success_duration:-1)
      if (!oUserlog.save(flush:true)){
        log.debug('error on save Userlog in User:login')
        oUserlog.errors.each{log.debug(it)}
      }
      redirect(controller:'user',action:'index')
      return	
    }else if (oUser.password != sPassword) {
      flash.error=2 // Wrong password or User does not exists
      oUserlog = new Userlog(user_id:oUser.id,logtime:new Date(),ip:request.remoteAddr,success:0,success_duration:0)
      if (!oUserlog.save(flush:true)){
        log.debug('error on save Userlog in User:login')
        oUserlog.errors.each{log.debug(it)}
      }
      redirect(controller:'user',action:'index')
      return
    }else if (Tools.getIntVal(Dynconfig.findByName('system.activity.status')?.value,0)!=1 && oUser.accesslevel != 1) {
      flash.error=5 // System blocked
      redirect(controller:'user',action:'index')
      return
    }

    if(Tools.checkIpRange(request.remoteAddr) || (oUser.is_remote && !oUser.tel)){
      oUserlog = new Userlog(user_id:oUser.id,logtime:new Date(),ip:request.remoteAddr,success:1,success_duration:1)
      if (!oUserlog.save(flush:true)){
        log.debug('error on save Userlog in User:login')
        oUserlog.errors.each{log.debug(it)}
      }
      
      login_func(oUser,false)    
      redirect(action:'panel'/*,params:[ext:1]*/)      
      return              
    }else if(oUser.is_remote){           
      sendVerifyUser(oUser)
      flash.user_id=oUser.id
      redirect(controller:'user',action:'smscode')
      return
    }else{
      flash.error=4
      redirect(controller:'user',action:'index')
      return
    }    
  }
  def login_func(oUser,bLoginAsUser){ 
    if(!bLoginAsUser){
      new Userlog().resetSuccessDuration(oUser.id)  
      oUser.lastdate=new Date()      
      if (!oUser.save(flush:true)){
        log.debug('error on save User in User:login')
        oUser.errors.each{log.debug(it)}
      }      
    }
    def oUsermenu = new Usermenu()

    session.user = [ id     : oUser.id,
                     login  : oUser.login,
                     email  : oUser.email,
                     name   : oUser.name,
                     department_id : oUser.department_id,
                     group         : Usergroup.get(oUser.usergroup_id),
                     menu          : oUsermenu.csiGetMenu(oUser.usergroup_id),
                     is_leader     : oUser.is_leader,
                     accesslevel   : oUser.accesslevel,
                     cashaccess    : oUser.cashaccess,
                     confaccess    : oUser.confaccess
                   ]
    session.user.addit=[]
    for(menu in session.user.menu){
      if(!menu.is_main)
        session.user.addit<<menu.id
    }    
  }  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def logout = {
    requestService.init(this)
    session.user = null
    redirect(controller:'user',action:'index')
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def smscode={  
    //requestService.init(this)
    if(!flash.user_id){
      redirect(controller:'user',action:'index')
      return
    } 
    return    
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def verifySMSCode={
    requestService.init(this)    
    
    def hsRes=[:]
    def oUser = User.get(requestService.getLongDef('id',0))       
    hsRes+=requestService.getParams(['smscode'])
    
    def oUserlog = new Userlog()    
    def blocktime = Tools.getIntVal(ConfigurationHolder.config.user.sms.blocktime,900)
    def unsuccess_log_limit = Tools.getIntVal(ConfigurationHolder.config.user.sms.unsuccess_log_limit,3)
    
    if(oUser){
      if(!hsRes.inrequest.smscode){ 
        render (["error":true,"error_type":1] as JSON)
        return
      }            
      if (oUserlog.csiCountUnsuccessSmsLogs(oUser.id, new Date(System.currentTimeMillis()-blocktime*1000))[0]>=unsuccess_log_limit){       
        oUserlog = new Userlog(user_id:oUser.id,logtime:new Date(),ip:request.remoteAddr,success:-2,success_duration:-2,success_sms:2)
        if (!oUserlog.save(flush:true)){
          log.debug('error on save Userlog in User:login')
          oUserlog.errors.each{log.debug(it)}
        }
        render (["error":true,"error_type":3] as JSON)
        return	
      }else if(oUser.smscode == hsRes.inrequest.smscode.toString()){
        if(!Sms.isSMSsend(oUser?.tel?:'')){         
          oUserlog = new Userlog(user_id:oUser.id,logtime:new Date(),ip:request.remoteAddr,success:-2,success_duration:-2,success_sms:3)
          if (!oUserlog.save(flush:true)){
            log.debug('error on save Userlog in User:login')
            oUserlog.errors.each{log.debug(it)}
          }          
          render (["error":true,"error_type":4] as JSON)
          return
        }
      
        oUserlog = new Userlog(user_id:oUser.id,logtime:new Date(),ip:request.remoteAddr,success:1,success_duration:1,success_sms:1)
        if (!oUserlog.save(flush:true)){
          log.debug('error on save Userlog in User:login')
          oUserlog.errors.each{log.debug(it)}
        }         

        login_func(oUser,false)
                      
        render (["error":false] as JSON)      
        return                   
      } else {      
        oUserlog = new Userlog(user_id:oUser.id,logtime:new Date(),ip:request.remoteAddr,success:-2,success_duration:-2,success_sms:0)
        if (!oUserlog.save(flush:true)){
          log.debug('error on save Userlog in User:login')
          oUserlog.errors.each{log.debug(it)}
        }      
        render (["error":true,"error_type":2] as JSON)
        return
      }
    }else{
      render (["error":true,"error_type":5] as JSON)
      return
    }    
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def sendVerifyUser(oUser){               
    def readyToSms = false         
    readyToSms = oUser.validateTelNumber()//генерация кода         
    
    if (readyToSms){     
      return !(boolean)smsService.sendVerifySms(oUser)
    }  
    else   
      return false
  }
  
  def verifyUser={
    requestService.init(this)    

    def oUser = User.get(requestService.getLongDef('user_id',0))
    if(oUser)                
      render(contentType:"application/json"){[error:(!sendVerifyUser(oUser) as boolean)]}
    else
      render(contentType:"application/json"){[error:true]}
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Login <<</////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def menu = {
    requestService.init(this)
    def iPage = requestService.getIntDef('id',1)
    if(iPage==1 && requestService.getIntDef('menu',0)) 
      iPage=requestService.getIntDef('menu',0)    
    switch (iPage){	
      case 1: redirect(action:'panel'); return
      case 2: redirect(action:'groupuser'); return
      case 3: redirect(action:'users'); return
      case 4: redirect(action:'pers'); return     
      case 6: redirect(controller:'catalog',action:'index'); return
      case 13: redirect(controller:'catalog',action:'clients'); return
      case 14: redirect(controller:'catalog',action:'projects'); return
      case 15: redirect(controller:'catalog',action:'agents'); return
      case 16: redirect(controller:'catalog',action:'configs'); return
      case 18: redirect(controller:'catalog',action:'departments'); return
      case 19: redirect(controller:'catalog',action:'expensetypes'); return
      case 20: redirect(controller:'feedback',action:'index'); return
      default: redirect(action:'panel'); return
    }
    return [user:session.user,action_id:iPage]
  }
  
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////  
  def panel = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)

    def oValutarate = new Valutarate()
    hsRes.rates_current = oValutarate.csiSearchToday()
    hsRes.rates_next = oValutarate.csiSearchTomorrow()

    return hsRes
  }

  def tasks = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)

    hsRes.task=[:]
    hsRes.task_exists=false
    for(oTasktype in Tasktype.list()){
      hsRes.task."${oTasktype.id}"=Task.findAll("FROM Task WHERE taskstatus in (1,3,5) AND (executor=:user OR (department_id=:dep_id and executor=0)) AND tasktype_id=:tasktype_id AND term=CURDATE()",[user:session.user.id,dep_id:session.user.department_id,tasktype_id:oTasktype?.id])
      if((hsRes.task."${oTasktype.id}"?:[]).size())
        hsRes.task_exists=true
    }

    hsRes.task_old=[:]
    hsRes.task_old_exists=false
    for(oTasktype in Tasktype.list()){
      hsRes.task_old."${oTasktype.id}"=Task.findAll("FROM Task WHERE taskstatus in (1,3,5) AND (executor=:user OR (department_id=:dep_id and executor=0)) AND tasktype_id=:tasktype_id AND term<CURDATE()",[user:session.user.id,dep_id:session.user.department_id,tasktype_id:oTasktype?.id])
      if((hsRes.task_old."${oTasktype.id}"?:[]).size())
        hsRes.task_old_exists=true
    }

    return hsRes
  }

  def saldo = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)

    if (session.user.group.is_payt){
      hsRes.paytsaldo = (Client.findByIs_t(1)?.computeCurSaldo()?:0)+(Payrequest.findAll{ modstatus >= 0 && client_id == (Client.findByIs_t(1)?.id?:-1) && paytype != 3 && agent_id==0 }.sum{ it.computeClientdelta() }?:0)
      hsRes.dopcardsaldo = Holding.findByName('dopcardsaldo')?.cashsaldo
    }

    return hsRes
  }

  def kredits = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    if(!session.user.group.is_kreditinfo) {
      response.sendError(403)
      return
    }

    hsRes.newtechkredits = new KreditSearch().csiSelectNewTechKredits()
    hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code.toLowerCase();map}

    return hsRes
  }

  def personal = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    if(!hsRes.user.is_leader) {
      response.sendError(403)
      return
    }

    hsRes.personal = new UserpersSearch().csiFindByDepartment(hsRes.user.department_id)

    return hsRes
  }

  def salary = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)

    hsRes.psalary = new Psalary().csiFindPsalary(hsRes.user.pers_id)

    return hsRes
  }

  def accounts = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)

    hsRes.accounts = Persaccount.findAllByPers_idAndModstatus(hsRes.user.pers_id,1,[sort:'is_main',order:'desc'])

    return hsRes
  }

  def debt = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    if(session.user.cashaccess!=2) {
      response.sendError(403)
      return
    }

    hsRes.debt = User.findAllByDepartment_idAndModstatusAndSaldoGreaterThan(hsRes.user.department_id,1,0)

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Pers >>>/////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def pers = {
    checkAccess(4)
    requestService.init(this)    
    def hsRes = requestService.getContextAndDictionary()    
    hsRes.user = session.user    
    hsRes.action_id = 4
    
    def fromEdit = requestService.getIntDef('fromEdit',0)
   
    if (fromEdit && session.perslastRequest){
      session.perslastRequest.fromEdit = fromEdit
      hsRes.inrequest = session.perslastRequest
    }
  
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def perslist = {
    checkAccess(4)
    requestService.init(this)
    def hsRes=[:]
    hsRes.action_id = 4
    hsRes.user = session.user
    
    if (session.perslastRequest?.fromEdit?:0){
      hsRes.inrequest = session.perslastRequest
      session.perslastRequest.fromEdit = 0
    } else {
      hsRes+=requestService.getParams(['is_sys_user','perstype','offset'],['user_id'],['shortname','snils'])      
      session.perslastRequest = [:]
      session.perslastRequest = hsRes.inrequest
    }   
  
    hsRes.pers = new Pers().csiGetPersList(hsRes.inrequest.user_id?:0l,hsRes.inrequest.shortname?:'',
                                             hsRes.inrequest.snils?:'',hsRes.inrequest.is_sys_user?:0,
                                             hsRes.inrequest.perstype?:0,20,hsRes.inrequest.offset?:0)
    hsRes.compositions = Composition.list().inject([:]){map, composition -> map[composition.id]=composition.name;map}

    return hsRes
  }
////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def persdetail={
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    hsRes.action_id = 4
    hsRes.user = User.get(session.user.id)

    def lId=requestService.getLongDef('id',0)    
    hsRes.pers_user = Pers.get(lId)
    if ((!hsRes.pers_user&&lId) || (!lId && !session.user?.group?.is_persinsert)) {
      response.sendError(404)
      return
    }        
    return hsRes
  }
////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def savePersDetail = {
    checkAccess(4)
    requestService.init(this)
    def hsRes=[:]  
    hsRes.user = session.user

    def lId = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams(['perstype','is_fixactsalary'],null,['inn','shortname','fullname','birthdate','birthcity',
                                     'passport','passdate','passorg','propiska','citizen','education','snilsdpf','kodpodr'])

    def oPers
    if(lId)
      oPers=Pers.get(lId)
    else
      oPers=new Pers()
    
    if ((!oPers&&lId) || (lId && !session.user?.group?.is_persedit) || (!lId && !session.user?.group?.is_persinsert)) {
      render(contentType:"application/json"){[error:true]}
      return
    }
     
    hsRes.result=[errorcode:[]]
    if(!hsRes.inrequest.fullname)     
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.shortname)     
      hsRes.result.errorcode<<2     
    if(hsRes.inrequest.birthdate && !(hsRes.inrequest.birthdate).matches('\\d{2}\\.\\d{2}\\.\\d{4}'))     
      hsRes.result.errorcode<<3      
    if(hsRes.inrequest.inn && !hsRes.inrequest.inn.matches('\\d{12}'))
      hsRes.result.errorcode<<4
    if(hsRes.inrequest.snilsdpf && !(hsRes.inrequest.snilsdpf).matches('\\d{3}\\-\\d{3}\\-\\d{3}\\s\\d{2}'))
      hsRes.result.errorcode<<5
    if(hsRes.inrequest.passport && !(hsRes.inrequest.passport).matches('\\d{2}\\s\\d{2}\\s\\d{6}'))
      hsRes.result.errorcode<<6
    /*if(hsRes.inrequest.passdate && !(hsRes.inrequest.passdate).matches('\\d{2}\\.\\d{2}\\.\\d{4}'))     
      hsRes.result.errorcode<<7*/
    /*if(requestService.getStr('actsalary') && requestService.getStr('actsalary')!='0' && !hsRes.inrequest.actsalary)     
      hsRes.result.errorcode<<8*/
    if(hsRes.inrequest.shortname && oPers.shortname!=hsRes.inrequest.shortname && Pers.findByShortname(hsRes.inrequest.shortname))
      hsRes.result.errorcode<<9  
    if(!lId && !hsRes.inrequest.perstype)     
      hsRes.result.errorcode<<10
    if(hsRes.inrequest.kodpodr && !hsRes.inrequest.kodpodr.matches('\\d{3}-\\d{3}'))
      hsRes.result.errorcode<<11
    
    if(hsRes.result.errorcode){
      render(contentType:"application/json"){[error:true,errorcode:hsRes.result.errorcode]}
      return
    }        

    if(hsRes.user.confaccess!=2)
      hsRes.inrequest.is_fixactsalary=oPers.is_fixactsalary         

    oPers.csiSetPers(hsRes.inrequest,lId?0:1).save(flush:true,failOnError:true)    

    flash.persedit_success=1
    render(contentType:"application/json"){[error:false,pers_id:!lId?oPers.id:0]}
    return      
  }  
  def psalarylist={
    checkAccess(4)
    requestService.init(this)     
    def hsRes=[psalary:[]]  
    hsRes.user = User.get(session.user.id)
    hsRes.inrequest=[:]
    
    hsRes.inrequest.pers_id=requestService.getLongDef('id',0)
    hsRes.pers_user = Pers.get(hsRes.inrequest.pers_id)

    if(hsRes.inrequest.pers_id && (hsRes.user.confaccess || (hsRes.pers_user?.perstype==2 && hsRes.user.is_tehdirleader))){
      hsRes.psalary=new Psalary().csiFindPsalary(hsRes.inrequest.pers_id)
    }  
    return hsRes  
  }
  def psalary={
    checkAccess(4)
    requestService.init(this)     
    def hsRes=[inrequest:[:]]
    hsRes.user = session.user

    hsRes.inrequest.id=requestService.getLongDef('id',0)
    hsRes.psalary=Psalary.get(hsRes.inrequest.id?:0)

    return hsRes  
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def savePsalaryDetail = {
    checkAccess(4)
    requestService.init(this)
    def hsRes=[:]  
    hsRes.user = User.get(session.user.id)

    def lId = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams(null,['pers_id','id','actsalary'],['comment'],['pdate']) //'is_main'                                   
    
    def oPsalary
    if(lId)
      oPsalary=Psalary.get(lId)
    else
      oPsalary=new Psalary()
      
    def oPers=Pers.get(hsRes.inrequest?.pers_id?:0)
    
    if ((!oPsalary&&lId) || !oPers || (hsRes.user.confaccess!=2 && (oPers?.perstype!=2 || !hsRes.user.is_tehdirleader))) {
      render(contentType:"application/json"){[error:true]}
      return
    }
     
    hsRes.result=[errorcode:[]]        
    
    if(!hsRes.inrequest.pdate)     
      hsRes.result.errorcode<<1        
      
    if(hsRes.result.errorcode){
      render(contentType:"application/json"){[error:true,errorcode:hsRes.result.errorcode]}
      return
    }                
   
    oPsalary.csiSetPsalary(hsRes.inrequest,hsRes.user.id).save(flush:true,failOnError:true)

    flash.show_psalary=1
    render(contentType:"application/json"){[error:false,refresh:true]}
    return
  }

  def deletepsalary = {
    checkAccess(4)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 4

    if (hsRes.user.confaccess!=2) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Psalary.findByPers_idAndId(requestService.getIntDef('pers_id',0),requestService.getIntDef('id',0))?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in User/deletepsalary\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def comperslist={
    checkAccess(4)
    requestService.init(this)     
    def hsRes=[compers:[]]
    hsRes.user = session.user
    
    def lPers_id=requestService.getLongDef('id',0)
    if(lPers_id)
      hsRes.compers=Compers.findAll("FROM Compers WHERE pers_id=:lPers_id ORDER BY modstatus DESC, company_id, position_id, jobstart DESC",[lPers_id:lPers_id])      
      
    return hsRes  
  }
  def puser={
    checkAccess(4)
    requestService.init(this)     
    def hsRes=[:]
    hsRes.user = session.user
    
    def lPers_id=requestService.getLongDef('id',0)
    if(lPers_id)
      hsRes.puser=User.findByPers_idAndModstatus(lPers_id,1)
    return hsRes  
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Pers <<</////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////  
   ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Persaccount >>>/////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  def persaccountlist={
    checkAccess(4)
    requestService.init(this)     
    def hsRes=[searchresult:[],inrequest:[:]]
    hsRes.user = session.user
    
    hsRes.inrequest.pers_id=requestService.getLongDef('id',0)
    if(hsRes.inrequest.pers_id && session.user?.group?.is_persaccount)
      hsRes.searchresult=Persaccount.findAll("FROM Persaccount WHERE pers_id=:lPers_id ORDER BY  is_main DESC, modstatus DESC",[lPers_id:hsRes.inrequest.pers_id])      
      
    return hsRes  
  }
  def persaccount={
    checkAccess(4)
    requestService.init(this)     
    def hsRes=[inrequest:[:]]
    hsRes.user = session.user
    
    hsRes.inrequest.id=requestService.getLongDef('id',0)
    if(hsRes.inrequest.id && session.user?.group?.is_persaccount)
      hsRes.persaccount=Persaccount.get(hsRes.inrequest.id?:0)

    hsRes.month=[]
    hsRes.year=[]
    
    for(def i=1;i<10;i++)
      hsRes.month<<[id:'0'+i]
    for(def i=10;i<13;i++)
      hsRes.month<<[id:i]
      
    for(def i=14;i<26;i++)
      hsRes.year<<[id:'20'+i]  
   
    return hsRes  
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def savePersaccountDetail = {
    checkAccess(4)
    requestService.init(this)
    def hsRes=[:]  
    hsRes.user = session.user

    def lId = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams(['modstatus','is_main'],['pers_id'],
                                    ['bank_id','nomer','paccount','validmonth','validyear','pin']) //'is_main'                                   
    
    def oPersaccount
    if(lId)
      oPersaccount=Persaccount.get(lId)
    else
      oPersaccount=new Persaccount()
      
    def oPers=Pers.get(hsRes.inrequest?.pers_id?:0)
    
    if ((!oPersaccount&&lId) || !session.user?.group?.is_persaccountedit || !oPers) {
      render(contentType:"application/json"){[error:true]}
      return
    }
     
    hsRes.result=[errorcode:[]]
    
    if(hsRes.inrequest.modstatus){
      if(oPers.perstype==Pers.PERSTYPE_SOTRUDNIK && Persaccount.findByPers_idAndModstatusAndIdNotEqual(hsRes.inrequest.pers_id,1,lId))
        hsRes.result.errorcode<<7 
      else if(oPers.perstype==Pers.PERSTYPE_SPECIALIST && Persaccount.findByPers_idAndModstatusAndIdNotEqual(hsRes.inrequest.pers_id,1,lId))
        hsRes.result.errorcode<<8 
      else if(oPers.perstype==Pers.PERSTYPE_DIRECTOR && Persaccount.findByPers_idAndModstatusAndIs_mainAndIdNotEqual(hsRes.inrequest.pers_id,1,oPersaccount.is_main,lId))
        hsRes.result.errorcode<<9
    }
    if(hsRes.result.errorcode){
      render(contentType:"application/json"){[error:true,errorcode:hsRes.result.errorcode]}
      return
    }

    if(!hsRes.inrequest.bank_id)
      hsRes.result.errorcode<<1
    else if(oPers.perstype!=Pers.PERSTYPE_DIRECTOR&&hsRes.inrequest.bank_id=='000000000')
      hsRes.result.errorcode<<10
    if(hsRes.inrequest.nomer && !hsRes.inrequest.nomer.matches('\\d{4}\\s\\d{4}\\s\\d{4}\\s\\d{4}((\\s\\d{2})|$)'))
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.paccount)
      hsRes.result.errorcode<<3
    else if(hsRes.inrequest.paccount && !hsRes.inrequest.paccount.replace('.','').matches('\\d{5}810\\d{12}'))
      hsRes.result.errorcode<<4
    if(hsRes.inrequest.validmonth&&hsRes.inrequest.validyear&&new Date(hsRes.inrequest?.validyear.toInteger()-1900,hsRes.inrequest?.validmonth.toInteger(),1) < new Date())
      hsRes.result.errorcode<<12
    if(hsRes.inrequest.pin && !(hsRes.inrequest.pin).matches('\\d{4}'))
      hsRes.result.errorcode<<13
    if(hsRes.result.errorcode){
      render(contentType:"application/json"){[error:true,errorcode:hsRes.result.errorcode]}
      return
    }

    oPersaccount.csiSetPersaccount(hsRes.inrequest,lId?0:1,hsRes.user.id).csiSetPIN(hsRes.inrequest.pin).save(flush:true,failOnError:true)

    render(contentType:"application/json"){[error:false]}
    return      
  }
  def setmainpersaccount = {
    checkAccess(4)
    requestService.init(this)
    def hsRes = [:]
    hsRes.user = User.get(session.user.id)    

    def lId = requestService.getLongDef('pers_id',0)
    hsRes+=requestService.getParams(null,['id'],null)

    hsRes.pers = Pers.get(lId)
    hsRes.persaccount = Persaccount.findByPers_idAndId(lId,hsRes.inrequest.id?:0)
    if (!hsRes.pers || !hsRes.persaccount || !session.user?.group?.is_persaccountedit || hsRes.pers.perstype!=Pers.PERSTYPE_DIRECTOR) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.persaccount.csiSetMain()?.save(failOnError:true)     
    } catch(Exception e) {
      log.debug("Error save data in User/setmainpersaccount\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def deletepersaccount = {
    checkAccess(4)
    requestService.init(this)
    def hsRes = [:]
    hsRes.user = User.get(session.user.id)

    hsRes.persaccount = Persaccount.findByPers_idAndId(Pers.get(requestService.getLongDef('pers_id',0))?.id?:0,requestService.getLongDef('id',0))
    if (hsRes.persaccount?.modstatus!=0 || !session.user?.group?.is_persaccountedit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.persaccount.delete()
    } catch(Exception e) {
      log.debug("Error save data in User/deletepersaccount\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
 ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Persaccont <<</////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////  

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Users >>>/////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def users = {
    checkAccess(3)
    requestService.init(this)    
    def hsRes = requestService.getContextAndDictionary()    
    hsRes.user = session.user    
    hsRes.action_id = 3
    
    def fromEdit = requestService.getIntDef('fromEdit',0)
   
    if (fromEdit&&session.userlastRequest){
      session.userlastRequest.fromEdit = fromEdit
      hsRes.inrequest = session.userlastRequest
    }
    hsRes.usergroup=Usergroup.findAll("FROM Usergroup WHERE is_superuser!=1 ORDER BY name")
    hsRes.departments = Department.findAllByIs_extra(0,[sort:'name',order:'asc'])
  
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def userlist = {
    checkAccess(3)
    requestService.init(this)
    def hsRes=[:]
    hsRes.action_id = 3
    hsRes.user = session.user
    
    if (session.userlastRequest?.fromEdit?:0){
      hsRes.inrequest = session.userlastRequest
      session.userlastRequest.fromEdit = 0
    } else {
      hsRes+=requestService.getParams(['offset','is_block','department_id'],['user_id'],['login','pers']) 
      hsRes.inrequest.modstatus=requestService.getIntDef('modstatus',-1)      
      session.userlastRequest = [:]
      session.userlastRequest = hsRes.inrequest
    }   
  
    hsRes.users = new User().csiGetUsersList(hsRes.inrequest.user_id?:0l,hsRes.inrequest.login?:'',hsRes.inrequest.pers?:'',0,
                                             hsRes.inrequest.department_id?:0,hsRes.inrequest.modstatus,
                                             hsRes.inrequest.is_block?:0,20,hsRes.inrequest.offset?:0)   
    
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def userdetail={
    checkAccess(3)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    hsRes.action_id = 3
    hsRes.user = session.user        

    def lId=requestService.getLongDef('id',0)    
    hsRes.useredit = User.get(lId)
    if (!hsRes.useredit&&lId) {
      response.sendError(404)
      return
    } 

    if(!lId){    
      hsRes.inrequest=[:]
      hsRes.inrequest.pers_id=requestService.getLongDef('pers_id',0)   
    }  
    
    hsRes.department=Department.findAll("FROM Department ORDER BY name")
    hsRes.usergroup=Usergroup.findAll("FROM Usergroup WHERE is_superuser!=1 ORDER BY name") 
    hsRes+=[loginlength:Tools.getIntVal(ConfigurationHolder.config.user.loginlength,4),passwordlength:Tools.getIntVal(ConfigurationHolder.config.user.passwordlength,7)]

    hsRes.cashaccess=[[id:1,name:'Подотчетное лицо без отдела'],[id:2,name:'кассир отдела'],[id:3,name:'главный кассир'],[id:4,name:'менеджер пополнения'],[id:5,name:'директор кассы'],[id:6,name:'куратор кассы'],[id:7,name:'партнер']]
    hsRes.iscaninsert = session.user.group?.is_userinsert?true:false
    
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveUserDetail = {
    checkAccess(3)
    requestService.init(this)
    def hsRes=[:]
    hsRes.action_id = 3
    hsRes.user = session.user

    def lId = requestService.getLongDef('id',0)
    def oUser
    if(lId)
      oUser=User.get(lId)
    else
      oUser=new User()
    
    if ((!oUser&&lId) || (lId && !session?.user?.group?.is_useredit) || (!lId && !session?.user?.group?.is_userinsert) || Usergroup.findWhere(id:oUser.usergroup_id?:0,is_superuser:1)) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes+=requestService.getParams(['is_block','is_loan','is_parking'],['pers_id'],
                                    ['name','login','email','tel','birthdate','password','confirm_pass'])

    def iscaninsert = session.user.group?.is_userinsert
    hsRes.inrequest.department_id = iscaninsert?requestService.getIntDef('department_id',0):oUser.department_id
    hsRes.inrequest.usergroup_id = iscaninsert?requestService.getIntDef('usergroup_id',0):oUser.usergroup_id
    hsRes.inrequest.is_remote = iscaninsert?requestService.getIntDef('is_remote',0):oUser.is_remote
    hsRes.inrequest.is_leader = iscaninsert?requestService.getIntDef('is_leader',0):oUser.is_leader
    hsRes.inrequest.cashaccess = iscaninsert?requestService.getIntDef('cashaccess',0):oUser.cashaccess
    hsRes.inrequest.confaccess = iscaninsert?requestService.getIntDef('confaccess',0):oUser.confaccess
    hsRes.inrequest.is_tehdirleader = iscaninsert?requestService.getIntDef('is_tehdirleader',0):oUser.is_tehdirleader

    hsRes+=[loginlength:Tools.getIntVal(ConfigurationHolder.config.user.loginlength,4),passwordlength:Tools.getIntVal(ConfigurationHolder.config.user.passwordlength,7)] 
    hsRes.result=[errorcode:[]]
    if(!hsRes.inrequest.name)     
      hsRes.result.errorcode<<1  
    if(!hsRes.inrequest.login?:'')
      hsRes.result.errorcode<<2
    else if((hsRes.inrequest.login?:'').size()<hsRes.loginlength)
      hsRes.result.errorcode<<3
    else if(!(hsRes.inrequest.login?:'').matches('.*(?=.*[0-9])(?=.*[A-z])(?!.*[\\W_А-я]).*') && !(hsRes.inrequest.login?:'').matches('.*(?=.*[A-z])(?!.*[\\W_А-я]).*'))
      hsRes.result.errorcode<<3  
    else if((!lId || oUser.login!=hsRes.inrequest.login) && User.findWhere(login:hsRes.inrequest.login))
      hsRes.result.errorcode<<4             
    if(hsRes.inrequest.email && !Tools.checkEmailString(hsRes.inrequest.email))     
      hsRes.result.errorcode<<5 
    if(!lId && !hsRes.inrequest.pers_id)
      hsRes.result.errorcode<<6  
    if(!hsRes.inrequest.usergroup_id)     
      hsRes.result.errorcode<<7 
    if(hsRes.inrequest.tel && !hsRes.inrequest.tel.matches('\\+\\d{11}'))
      hsRes.result.errorcode<<8
    if(!lId && !hsRes.inrequest.password)  
      hsRes.result.errorcode<<9            
    else if(hsRes.inrequest.password){
      if((hsRes.inrequest.password?:'').size()<hsRes.passwordlength)
        hsRes.result.errorcode<<10      
      else if(!(hsRes.inrequest.password?:'').matches('.*(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?!.*[\\W_А-я]).*'))
        hsRes.result.errorcode<<10
      else if((hsRes.inrequest.password?:'')!=(hsRes.inrequest.confirm_pass?:''))
        hsRes.result.errorcode<<11      
    }
    if(hsRes.inrequest.department_id && Pers.get(hsRes.inrequest.pers_id)?.perstype==2)     
      hsRes.result.errorcode<<12    
    
    if(hsRes.result.errorcode){
      render(contentType:"application/json"){[error:true,errorcode:hsRes.result.errorcode]}
      return
    }         
    if(oUser.is_block && !hsRes.inrequest.is_block)
      new Userlog().resetSuccessDuration(oUser.id)      
    
    oUser.csiSetUser(hsRes.inrequest,lId?0:1).save(flush:true,failOnError:true)       
    flash.useredit_success=1
    
    if(requestService.getIntDef('print_login',0)){
      flash.login=hsRes.inrequest.login
      session.tmp_password=hsRes.inrequest.password
    }  
    render(contentType:"application/json"){[error:false,user_id:!lId?oUser.id:0]}
    return      
  }
  def set_user_modstatus={
    checkAccess(3)
    requestService.init(this)
    def hsRes=[:]
    hsRes.user = session.user
    def lId=requestService.getLongDef('id',0)
    def iModstatus=requestService.getIntDef('modstatus',0)
    
    def oUser=User.get(lId)
    
    if ((!oUser) || !session?.user?.group?.is_useredit || Usergroup.findWhere(id:oUser.usergroup_id?:0,is_superuser:1)) {
      render(contentType:"application/json"){[error:true]}
      return
    }    
    
    oUser.csiSetModstatus(iModstatus?0:1).save(flush:true,failOnError:true)
      
    render(contentType:"application/json"){[error:false]}  
    return  
  }
  def generateUserPassword={
    checkAccess(3)
    requestService.init(this)
 
    render(contentType:"application/json"){[password:Tools.generatePassword()]}  
    return    
  }
  
  def userloglist={
    checkAccess(3)
    requestService.init(this)     
    def hsRes=[inrequest:[:],userlog:[]]
    hsRes.inrequest.id=requestService.getLongDef('id',0)
    if(hsRes.inrequest.id)
      hsRes.userlog=new Userlog().csiGetUserLog(hsRes.inrequest.id,20,requestService.getOffset())
      
    return hsRes  
  }
  
  def userprojectlist={
    checkAccess(3)
    requestService.init(this)
    def hsRes=[inrequest:[:],userprojects:[]]

    hsRes.useredit = User.get(requestService.getLongDef('id',0))
    if (!hsRes.useredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.userprojects=new Project().csiSearchUserProject(hsRes.useredit.id)
      
    return hsRes  
  }
  
  def userproject = {
    checkAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 3
  
    hsRes.useredit = User.get(requestService.getLongDef('user_id',0))
    if (!hsRes.useredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.projects = Project.findAllByIdNotInList(new Project().csiSearchUserProject(hsRes.useredit.id).collect{it.id}?:[0])

    return hsRes
  }

  def adduserproject = {
    checkAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 3
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['project_id'])

    hsRes.project = Project.get(hsRes.inrequest.project_id)
    hsRes.useredit = User.get(requestService.getLongDef('id',0))
    if (!hsRes.project||!hsRes.useredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.project_id || User2project.findByUser_idAndProject_id(hsRes.useredit.id,hsRes.inrequest.project_id))
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        new User2project(user_id:hsRes.useredit.id,project_id:hsRes.inrequest.project_id).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in User/adduserproject\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def removeuserproject = {
    checkAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 3

    try {
      User2project.findByUser_idAndProject_id(requestService.getLongDef('user_id',0),requestService.getLongDef('id',0))?.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in User/removeuserproject\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def userexpensetypes = {
    checkAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 3

    hsRes.useredit = User.get(requestService.getIntDef('id',0))
    if (!hsRes.useredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.expensetypeRazdel = new ExpensetypeSearch().csiGetRazdel()
    hsRes.expensetypes = Expensetype.list()
    hsRes.userexptypes = new ExpensetypeSearch().csiGetUserTypes(hsRes.useredit.id)

    return hsRes
  }

  def updateuserexpensetypes = {
    checkAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 3

    hsRes.useredit = User.get(requestService.getIntDef('id',0))
    if (!hsRes.useredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try{
      Expense2user.findAllByUser_id(hsRes.useredit.id).each{ it.delete(flush:true) }
      Expensetype.list().each { exptype ->
        if(requestService.getIntDef("exp_id_"+exptype.id,0)){
          new Expense2user(expensetype_id:exptype.id,user_id:hsRes.useredit.id).save(failOnError:true)
        }
      }
    } catch(Exception e){
      log.debug("Error save data in User/updateuserexpensetypes\n"+e.toString())
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def printLogin={
    checkAccess(3)
    requestService.init(this)
    def hsRes=[:]
    hsRes.user = session.user 
    
    hsRes.login=requestService.getStr('login')
   
    def oUser=User.findWhere(login:hsRes.login,password:Tools.hidePsw(session.tmp_password))
    if(oUser && (session?.user?.group?.is_useredit || session?.user?.group?.is_userinsert)){    
      hsRes.password=session.tmp_password                   
    }
    session.tmp_password=null
    return hsRes
  }
  
  def loginAsUser={
    checkAccess(3)
    requestService.init(this)    
    
    if(session?.user?.group?.is_usergroupenter){    
      def lId=requestService.getLongDef('id',0)
      def oUser = User.get(lId)
      if(oUser)
        login_func(oUser,true)
    }
    render(contentType:"application/json"){[error:false]}
  }  
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Users <<<//////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////////////// 
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////Group administration >>>//////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////
  def groupuser = {
    checkAccess(2)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 2

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails&&session.usergrouplastRequest){
      session.usergrouplastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.usergrouplastRequest
    }

    hsRes.departments = Department.findAllByIs_extra(0,[sort:'name',order:'asc'])

    return hsRes
  }

  def grouplist = {
    checkAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 2

    if (session.usergrouplastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.usergrouplastRequest
      session.usergrouplastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['department_id'],null,['name'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.usergrouplastRequest = hsRes.inrequest
    }

    hsRes.searchresult = new Usergroup().csiFindUsergroup(hsRes.inrequest.name?:'',hsRes.inrequest.department_id?:0,20,requestService.getOffset())

    return hsRes
  }

  def groupuserlist = {
    checkAccess(2)
    requestService.init(this)
    def hsRes = [:]
    hsRes.user = session.user  
    hsRes.action_id = 2
    
    hsRes.inrequest=[:]
    
    hsRes.inrequest.usergroup_id=requestService.getIntDef('usergroup_id',0)

    hsRes.searchresult = new User().csiGetUsersList(0l,'','',hsRes.inrequest.usergroup_id,0,1,0,20,requestService.getOffset())
    hsRes.group=Usergroup.get(hsRes.inrequest.usergroup_id?:0)    
    
    return hsRes
  }  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def grouprights = {
    checkAccess(2)
    requestService.init(this)
    def hsRes = [:] 
    hsRes.user = session.user  
    
    hsRes.inrequest = [id:requestService.getLongDef('id',0)]
    hsRes.group = Usergroup.get(hsRes.inrequest.id)
    hsRes.formaccess=Formaccess.findAllByParent(0,[sort:'id',order:'asc'])
    
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def groupRightssave = {
    checkAccess(2)
    requestService.init(this)
    
    def hsRes = [:]  
    hsRes.user = session.user  

    if(!session.user?.group?.is_usergroupedit) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    
    def hParams = requestService.getParams(['id','is_pers','is_groupmanage','is_users','is_catalog','is_agr','is_license',
                                            'is_company','is_arenda','is_lizing','is_trade','is_kredit','is_cession',
                                            'is_cassa','is_cashdep','is_agent','is_task','is_taskmy','is_taskall','is_taskpay',
                                            'is_taskpayall','is_payment','is_persedit','is_persinsert','is_payproject',
                                            'is_persaccount','is_persaccountedit','is_bankedit','is_bankinsert',
                                            'is_taxedit','is_taxinsert','is_okvedinsert','is_oktmoinsert','is_kbkinsert',
                                            'is_expenseedit','is_project','is_projectedit','is_departmentedit',
                                            'is_holidayedit','is_agrtypeedit','is_spacediredit','is_useredit',
                                            'is_userinsert','is_usergroupedit','is_usergroupinsert','is_usergroupenter',
                                            'is_arendaedit','is_lizing','is_lizingedit','is_lizingpaymentedit',
                                            'is_tradeedit','is_kreditedit','is_kreditpaymentedit','is_cessionedit',
                                            'is_cessionpaymentedit','is_agentedit','is_licenseedit','is_companyedit',
                                            'is_companyinsert','is_companycard','is_companyaccount','is_companyaccountedit',
                                            'is_companyrequisit','is_companystaff','is_companystaffedit','is_payplan',
                                            'is_payplanedit','is_payplantask','is_payplanexec','is_salary','is_salaryavans',
                                            'is_salaryedit','is_salarybuh','is_salarybuhedit','is_salaryoff',
                                            'is_salaryoffedit','is_salaryalldep','is_salaryapprove','is_rep_allsalary',
                                            'is_rep_dirsalary','is_payedit','is_paytag','is_client','is_clientedit',
                                            'is_config','is_clientpayment','is_clientpaymentedit','is_payrequestdelete',
                                            'is_enquiry','is_enquiryedit','is_service','is_smr','is_loan','is_serviceedit',
                                            'is_smredit','is_loanedit','is_payaccept','is_viewbudgpayplantask',
                                            'is_viewkredpayplantask','is_viewrentpayplantask','is_viewgnrlpayplantask',
                                            'is_paynalog','is_paynalogedit','is_payt','is_paytedit','is_kreditinfo',
                                            'is_realkredit','is_rep_documents','is_dopcardpayment','is_kreditclient',
                                            'is_rep_clientpay','is_payordering','is_paysaldo','is_prolongpermit',
                                            'is_prolongwork','is_cgroup','is_positionedit','is_rep_service','is_rep_cash',
                                            'is_rep_booh','is_deposit','is_depositedit','is_finlizing','is_finlizingedit',
                                            'is_salarynal','is_salarynaledit','is_clientpaynew','is_visualgroup',
                                            'is_department','is_rep_payment'])
    if (hParams.inrequest.id>0){
      def oUsergroup = Usergroup.get(hParams.inrequest.id)
      if(oUsergroup.is_superuser)
        return
      try {
        def sMenu = '1,12,19,20,'
        oUsergroup.is_panel = 1
        if (hParams.inrequest.is_groupmanage)      {oUsergroup.is_groupmanage=1;    sMenu += '2,'}
        else oUsergroup.is_groupmanage=0
        if (hParams.inrequest.is_users)      {oUsergroup.is_users=1;    sMenu += '3,'}
        else oUsergroup.is_users=0
        if (hParams.inrequest.is_pers)      {oUsergroup.is_pers=1;    sMenu += '4,'}
        else oUsergroup.is_pers=0
        if (hParams.inrequest.is_company)      {oUsergroup.is_company=1;    sMenu += '5,'}
        else oUsergroup.is_company=0
        if (hParams.inrequest.is_catalog)   {oUsergroup.is_catalog=1; sMenu += '6,'}
        else oUsergroup.is_catalog=0
        if (hParams.inrequest.is_agr)      {oUsergroup.is_agr=1;    sMenu += '7,'}
        else oUsergroup.is_agr=0
        if (hParams.inrequest.is_agr&&hParams.inrequest.is_license)      {oUsergroup.is_license=1}
        else oUsergroup.is_license=0
        if (hParams.inrequest.is_agr&&hParams.inrequest.is_arenda)      {oUsergroup.is_arenda=1}
        else oUsergroup.is_arenda=0
        if (hParams.inrequest.is_agr&&hParams.inrequest.is_lizing)      {oUsergroup.is_lizing=1}
        else oUsergroup.is_lizing=0
        if (hParams.inrequest.is_agr&&hParams.inrequest.is_trade)      {oUsergroup.is_trade=1}
        else oUsergroup.is_trade=0
        if (hParams.inrequest.is_agr&&hParams.inrequest.is_kredit)      {oUsergroup.is_kredit=1}
        else oUsergroup.is_kredit=0
        if (hParams.inrequest.is_agr&&hParams.inrequest.is_cession)      {oUsergroup.is_cession=1}
        else oUsergroup.is_cession=0
        if (hParams.inrequest.is_agr&&hParams.inrequest.is_service)      {oUsergroup.is_service=1}
        else oUsergroup.is_service=0
        if (hParams.inrequest.is_agr&&hParams.inrequest.is_smr)      {oUsergroup.is_smr=1}
        else oUsergroup.is_smr=0
        if (hParams.inrequest.is_agr&&hParams.inrequest.is_loan)      {oUsergroup.is_loan=1}
        else oUsergroup.is_loan=0
        if (hParams.inrequest.is_agr&&hParams.inrequest.is_deposit) oUsergroup.is_deposit = 1
        else oUsergroup.is_deposit = 0
        if (hParams.inrequest.is_agr&&hParams.inrequest.is_finlizing) oUsergroup.is_finlizing = 1
        else oUsergroup.is_finlizing = 0
        if (hParams.inrequest.is_cassa) { oUsergroup.is_cassa=1; sMenu += '8,' }
        else oUsergroup.is_cassa = 0
        if (hParams.inrequest.is_cassa&&hParams.inrequest.is_cashdep) oUsergroup.is_cashdep = 1
        else oUsergroup.is_cashdep = 0
        if (hParams.inrequest.is_task)      {oUsergroup.is_task=1;    sMenu += '9,'}
        else oUsergroup.is_task=0
        if (hParams.inrequest.is_payment)      {oUsergroup.is_payment=1;    sMenu += '10,'}
        else oUsergroup.is_payment=0
        if (hParams.inrequest.is_payment&&hParams.inrequest.is_payedit) oUsergroup.is_payedit = 1
        else oUsergroup.is_payedit = 0
        if (hParams.inrequest.is_payment&&hParams.inrequest.is_paytag) oUsergroup.is_paytag = 1
        else oUsergroup.is_paytag = 0
        if (hParams.inrequest.is_payment&&hParams.inrequest.is_payordering) oUsergroup.is_payordering = 1
        else oUsergroup.is_payordering = 0
        if (hParams.inrequest.is_payment&&hParams.inrequest.is_paysaldo) oUsergroup.is_paysaldo = 1
        else oUsergroup.is_paysaldo = 0
        if (hParams.inrequest.is_salary)      {oUsergroup.is_salary=1;    sMenu += '11,'}
        else oUsergroup.is_salary=0
        if (hParams.inrequest.is_client)      {oUsergroup.is_client=1;    sMenu += '13,'}
        else oUsergroup.is_client=0
        if (hParams.inrequest.is_client&&hParams.inrequest.is_clientedit)      {oUsergroup.is_clientedit=1}
        else oUsergroup.is_clientedit=0
        if (hParams.inrequest.is_project)    {oUsergroup.is_project=1;    sMenu += '14,'}
        else oUsergroup.is_project=0
        if (hParams.inrequest.is_project&&hParams.inrequest.is_projectedit)    oUsergroup.is_projectedit=1;
        else oUsergroup.is_projectedit=0
        if (hParams.inrequest.is_agent)      {oUsergroup.is_agent=1;    sMenu += '15,'}
        else oUsergroup.is_agent=0
        if (hParams.inrequest.is_agent&&hParams.inrequest.is_agentedit)    oUsergroup.is_agentedit=1;
        else oUsergroup.is_agentedit=0
        if (hParams.inrequest.is_config)      {oUsergroup.is_config=1;    sMenu += '16,'}
        else oUsergroup.is_config=0
        if (hParams.inrequest.is_department) { oUsergroup.is_department = 1; sMenu += '18,' }
        else oUsergroup.is_department = 0
        if (hParams.inrequest.is_department&&hParams.inrequest.is_departmentedit) oUsergroup.is_departmentedit = 1
        else oUsergroup.is_departmentedit = 0

        oUsergroup.menu = sMenu

////salary >>> ////////////
        if (hParams.inrequest.is_salary&&hParams.inrequest.is_salaryavans) oUsergroup.is_salaryavans = 1
        else oUsergroup.is_salaryavans = 0
        if ((hParams.inrequest.is_salaryavans || hParams.inrequest.is_salaryalldep) && hParams.inrequest.is_salaryedit) oUsergroup.is_salaryedit = 1
        else oUsergroup.is_salaryedit = 0
        if (hParams.inrequest.is_salary&&hParams.inrequest.is_salarybuh) oUsergroup.is_salarybuh = 1
        else oUsergroup.is_salarybuh = 0
        if (hParams.inrequest.is_salarybuh&&hParams.inrequest.is_salarybuhedit) oUsergroup.is_salarybuhedit = 1
        else oUsergroup.is_salarybuhedit = 0
        if (hParams.inrequest.is_salaryoff) oUsergroup.is_salaryoff = 1
        else oUsergroup.is_salaryoff = 0
        if ((hParams.inrequest.is_salarybuh || hParams.inrequest.is_salaryoff) && hParams.inrequest.is_salaryoffedit) oUsergroup.is_salaryoffedit = 1
        else oUsergroup.is_salaryoffedit = 0
        if (hParams.inrequest.is_salary&&hParams.inrequest.is_salarynal) oUsergroup.is_salarynal = 1
        else oUsergroup.is_salarynal = 0
        if (hParams.inrequest.is_salarynal&&hParams.inrequest.is_salarynaledit) oUsergroup.is_salarynaledit = 1
        else oUsergroup.is_salarynaledit = 0
        if (hParams.inrequest.is_salaryalldep) oUsergroup.is_salaryalldep = 1
        else oUsergroup.is_salaryalldep = 0
        if (hParams.inrequest.is_salaryapprove) oUsergroup.is_salaryapprove = 1
        else oUsergroup.is_salaryapprove = 0
////salary <<< ////////////
////reports >>> ///////////
        if (hParams.inrequest.is_rep_allsalary) oUsergroup.is_rep_allsalary = 1
        else oUsergroup.is_rep_allsalary = 0
        if (hParams.inrequest.is_rep_dirsalary) oUsergroup.is_rep_dirsalary = 1
        else oUsergroup.is_rep_dirsalary = 0
        if (hParams.inrequest.is_rep_documents) oUsergroup.is_rep_documents = 1
        else oUsergroup.is_rep_documents = 0
        if (hParams.inrequest.is_rep_agentagrprofit) oUsergroup.is_rep_agentagrprofit = 1
        else oUsergroup.is_rep_agentagrprofit = 0
        if (hParams.inrequest.is_rep_clientpay) oUsergroup.is_rep_clientpay = 1
        else oUsergroup.is_rep_clientpay = 0
        if (hParams.inrequest.is_rep_service) oUsergroup.is_rep_service = 1
        else oUsergroup.is_rep_service = 0
        if (hParams.inrequest.is_rep_cash) oUsergroup.is_rep_cash = 1
        else oUsergroup.is_rep_cash = 0
        if (hParams.inrequest.is_rep_booh) oUsergroup.is_rep_booh = 1
        else oUsergroup.is_rep_booh = 0
        if (hParams.inrequest.is_rep_payment) oUsergroup.is_rep_payment = 1
        else oUsergroup.is_rep_payment = 0
////reports <<< ///////////

        if (hParams.inrequest.is_persedit)      oUsergroup.is_persedit=1; 
        else oUsergroup.is_persedit=0
        if (hParams.inrequest.is_persinsert)    oUsergroup.is_persinsert=1; 
        else oUsergroup.is_persinsert=0
        if (hParams.inrequest.is_persaccount)   oUsergroup.is_persaccount=1; 
        else oUsergroup.is_persaccount=0
        if (hParams.inrequest.is_persaccountedit)      oUsergroup.is_persaccountedit=1; 
        else oUsergroup.is_persaccountedit=0    

////catalog >>> ///////////
        if (hParams.inrequest.is_catalog&&hParams.inrequest.is_cgroup) oUsergroup.is_cgroup = 1
        else oUsergroup.is_cgroup = 0
        if (hParams.inrequest.is_catalog&&hParams.inrequest.is_oktmoinsert) oUsergroup.is_oktmoinsert = 1
        else oUsergroup.is_oktmoinsert = 0
        if (hParams.inrequest.is_catalog&&hParams.inrequest.is_kbkinsert) oUsergroup.is_kbkinsert = 1
        else oUsergroup.is_kbkinsert = 0
        if (hParams.inrequest.is_catalog&&hParams.inrequest.is_okvedinsert) oUsergroup.is_okvedinsert = 1
        else oUsergroup.is_okvedinsert = 0
        if (hParams.inrequest.is_catalog&&hParams.inrequest.is_taxedit) oUsergroup.is_taxedit = 1
        else oUsergroup.is_taxedit = 0
        if (hParams.inrequest.is_catalog&&hParams.inrequest.is_taxinsert) oUsergroup.is_taxinsert = 1
        else oUsergroup.is_taxinsert = 0
        if (hParams.inrequest.is_catalog&&hParams.inrequest.is_bankedit) oUsergroup.is_bankedit = 1
        else oUsergroup.is_bankedit = 0
        if (hParams.inrequest.is_catalog&&hParams.inrequest.is_bankinsert) oUsergroup.is_bankinsert = 1
        else oUsergroup.is_bankinsert = 0
        if (hParams.inrequest.is_catalog&&hParams.inrequest.is_expenseedit) oUsergroup.is_expenseedit = 1
        else oUsergroup.is_expenseedit = 0
        if (hParams.inrequest.is_catalog&&hParams.inrequest.is_spacediredit) oUsergroup.is_spacediredit = 1
        else oUsergroup.is_spacediredit = 0
        if (hParams.inrequest.is_catalog&&hParams.inrequest.is_holidayedit) oUsergroup.is_holidayedit = 1
        else oUsergroup.is_holidayedit = 0
        if (hParams.inrequest.is_catalog&&hParams.inrequest.is_agrtypeedit) oUsergroup.is_agrtypeedit = 1
        else oUsergroup.is_agrtypeedit = 0
        if (hParams.inrequest.is_catalog&&hParams.inrequest.is_positionedit) oUsergroup.is_positionedit = 1
        else oUsergroup.is_positionedit = 0
        if (hParams.inrequest.is_catalog&&hParams.inrequest.is_visualgroup) oUsergroup.is_visualgroup = 1
        else oUsergroup.is_visualgroup = 0
////catalog <<< ///////////
        if (hParams.inrequest.is_useredit)    oUsergroup.is_useredit=1; 
        else oUsergroup.is_useredit=0 
        if (hParams.inrequest.is_userinsert)    oUsergroup.is_userinsert=1; 
        else oUsergroup.is_userinsert=0 
        if (hParams.inrequest.is_usergroupenter)    oUsergroup.is_usergroupenter=1; 
        else oUsergroup.is_usergroupenter=0                 
        
        if (hParams.inrequest.is_usergroupedit)    oUsergroup.is_usergroupedit=1; 
        else oUsergroup.is_usergroupedit=0 
        if (hParams.inrequest.is_usergroupinsert)    oUsergroup.is_usergroupinsert=1; 
        else oUsergroup.is_usergroupinsert=0 

        if (hParams.inrequest.is_licenseedit)    oUsergroup.is_licenseedit=1; 
        else oUsergroup.is_licenseedit=0
////spaces >>> ////////////
        if (hParams.inrequest.is_arenda&&hParams.inrequest.is_arendaedit) oUsergroup.is_arendaedit = 1
        else oUsergroup.is_arendaedit = 0
        if (hParams.inrequest.is_arenda&&hParams.inrequest.is_prolongpermit) oUsergroup.is_prolongpermit = 1
        else oUsergroup.is_prolongpermit = 0
        if (hParams.inrequest.is_arenda&&hParams.inrequest.is_prolongwork) oUsergroup.is_prolongwork = 1
        else oUsergroup.is_prolongwork = 0
////spaces <<< ////////////
////kredits >>> ///////////
        if (hParams.inrequest.is_kredit&&hParams.inrequest.is_kreditedit) oUsergroup.is_kreditedit = 1
        else oUsergroup.is_kreditedit = 0
        if (hParams.inrequest.is_kredit&&hParams.inrequest.is_kreditpaymentedit) oUsergroup.is_kreditpaymentedit = 1
        else oUsergroup.is_kreditpaymentedit = 0
        if (hParams.inrequest.is_kredit&&hParams.inrequest.is_kreditinfo) oUsergroup.is_kreditinfo = 1
        else oUsergroup.is_kreditinfo = 0
        if (hParams.inrequest.is_kredit&&hParams.inrequest.is_realkredit) oUsergroup.is_realkredit = 1
        else oUsergroup.is_realkredit = 0
        if (hParams.inrequest.is_kredit&&hParams.inrequest.is_kreditclient) oUsergroup.is_kreditclient = 1
        else oUsergroup.is_kreditclient = 0
////kredits <<< ///////////
////bankdeposit >>> ///////
        if (hParams.inrequest.is_deposit&&hParams.inrequest.is_depositedit) oUsergroup.is_depositedit = 1
        else oUsergroup.is_depositedit = 0
////bankdeposit <<< ///////
////finlizing >>> /////////
        if (hParams.inrequest.is_finlizing&&hParams.inrequest.is_finlizingedit) oUsergroup.is_finlizingedit = 1
        else oUsergroup.is_finlizingedit = 0
////finlizing <<< /////////
////lizing >>> ////////////
        if (hParams.inrequest.is_lizing&&hParams.inrequest.is_lizingedit) oUsergroup.is_lizingedit = 1
        else oUsergroup.is_lizingedit = 0
        if (hParams.inrequest.is_lizing&&hParams.inrequest.is_lizingpaymentedit) oUsergroup.is_lizingpaymentedit = 1
        else oUsergroup.is_lizingpaymentedit = 0
////lizing <<< ////////////
        if (hParams.inrequest.is_tradeedit)    oUsergroup.is_tradeedit=1; 
        else oUsergroup.is_tradeedit=0
        if (hParams.inrequest.is_cessionedit)    oUsergroup.is_cessionedit=1; 
        else oUsergroup.is_cessionedit=0
        if (hParams.inrequest.is_cessionpaymentedit)    oUsergroup.is_cessionpaymentedit=1; 
        else oUsergroup.is_cessionpaymentedit=0
        if (hParams.inrequest.is_serviceedit)    oUsergroup.is_serviceedit=1; 
        else oUsergroup.is_serviceedit=0
        if (hParams.inrequest.is_smredit)    oUsergroup.is_smredit=1; 
        else oUsergroup.is_smredit=0
        if (hParams.inrequest.is_loanedit)    oUsergroup.is_loanedit=1; 
        else oUsergroup.is_loanedit=0

        if (hParams.inrequest.is_companyedit)    oUsergroup.is_companyedit=1; 
        else oUsergroup.is_companyedit=0 
        if (hParams.inrequest.is_companyinsert)    oUsergroup.is_companyinsert=1; 
        else oUsergroup.is_companyinsert=0 
        if (hParams.inrequest.is_companycard)    oUsergroup.is_companycard=1; 
        else oUsergroup.is_companycard=0 
        if (hParams.inrequest.is_companyaccount)    oUsergroup.is_companyaccount=1; 
        else oUsergroup.is_companyaccount=0 
        if (hParams.inrequest.is_companyaccountedit)    oUsergroup.is_companyaccountedit=1; 
        else oUsergroup.is_companyaccountedit=0 
        if (hParams.inrequest.is_companystaff)    oUsergroup.is_companystaff=1; 
        else oUsergroup.is_companystaff=0
        if (hParams.inrequest.is_companystaffedit)    oUsergroup.is_companystaffedit=1; 
        else oUsergroup.is_companystaffedit=0 
        if (hParams.inrequest.is_companyrequisit)    oUsergroup.is_companyrequisit=1; 
        else oUsergroup.is_companyrequisit=0                

        if (hParams.inrequest.is_taskmy)    oUsergroup.is_taskmy=1; 
        else oUsergroup.is_taskmy=0
        if (hParams.inrequest.is_taskall)    oUsergroup.is_taskall=1; 
        else oUsergroup.is_taskall=0   
        if (hParams.inrequest.is_taskpay) oUsergroup.is_taskpay = 1
        else oUsergroup.is_taskpay = 0
        if (hParams.inrequest.is_taskpay&&hParams.inrequest.is_taskpayall) oUsergroup.is_taskpayall = 1
        else oUsergroup.is_taskpayall = 0

////clientpayment >>> /////
        if (hParams.inrequest.is_clientpayment) oUsergroup.is_clientpayment = 1
        else oUsergroup.is_clientpayment=0
        if (hParams.inrequest.is_clientpayment&&hParams.inrequest.is_clientpaymentedit) oUsergroup.is_clientpaymentedit = 1
        else oUsergroup.is_clientpaymentedit = 0
        if (hParams.inrequest.is_clientpayment&&hParams.inrequest.is_payrequestdelete) oUsergroup.is_payrequestdelete = 1
        else oUsergroup.is_payrequestdelete = 0
        if (hParams.inrequest.is_clientpayment&&hParams.inrequest.is_clientpaynew) oUsergroup.is_clientpaynew = 1
        else oUsergroup.is_clientpaynew = 0
////clientpayment <<< /////
////enquiry >>> ///////////
        if (hParams.inrequest.is_task&&hParams.inrequest.is_enquiry) oUsergroup.is_enquiry = 1
        else oUsergroup.is_enquiry = 0
        if (hParams.inrequest.is_task&&hParams.inrequest.is_enquiry&&hParams.inrequest.is_enquiryedit) oUsergroup.is_enquiryedit = 1
        else oUsergroup.is_enquiryedit = 0
////enquiry <<< ///////////
////payplan >>> ///////////
        if (hParams.inrequest.is_payment&&hParams.inrequest.is_payplan) oUsergroup.is_payplan = 1
        else oUsergroup.is_payplan = 0
        if (hParams.inrequest.is_payplan&&hParams.inrequest.is_payplanedit) oUsergroup.is_payplanedit = 1
        else oUsergroup.is_payplanedit = 0
        if (hParams.inrequest.is_payplan&&hParams.inrequest.is_payplantask) oUsergroup.is_payplantask = 1
        else oUsergroup.is_payplantask = 0
        if (hParams.inrequest.is_payplan&&hParams.inrequest.is_payplanexec) oUsergroup.is_payplanexec = 1
        else oUsergroup.is_payplanexec = 0
        if (hParams.inrequest.is_payplan&&hParams.inrequest.is_payaccept) oUsergroup.is_payaccept = 1
        else oUsergroup.is_payaccept = 0
        if (hParams.inrequest.is_payplan&&hParams.inrequest.is_viewbudgpayplantask) oUsergroup.is_viewbudgpayplantask = 1
        else oUsergroup.is_viewbudgpayplantask = 0
        if (hParams.inrequest.is_payplan&&hParams.inrequest.is_viewkredpayplantask) oUsergroup.is_viewkredpayplantask = 1
        else oUsergroup.is_viewkredpayplantask = 0
        if (hParams.inrequest.is_payplan&&hParams.inrequest.is_viewrentpayplantask) oUsergroup.is_viewrentpayplantask = 1
        else oUsergroup.is_viewrentpayplantask = 0
        if (hParams.inrequest.is_payplan&&hParams.inrequest.is_viewgnrlpayplantask) oUsergroup.is_viewgnrlpayplantask = 1
        else oUsergroup.is_viewgnrlpayplantask = 0
////payplan <<< ///////////
////tax >>> ///////////////
        if (hParams.inrequest.is_paynalog) oUsergroup.is_paynalog = 1
        else oUsergroup.is_paynalog = 0
        if (hParams.inrequest.is_paynalog&&hParams.inrequest.is_paynalogedit) oUsergroup.is_paynalogedit = 1
        else oUsergroup.is_paynalogedit = 0
////tax <<< ///////////////
////t dep >>> /////////////
        if (hParams.inrequest.is_payt) oUsergroup.is_payt = 1
        else oUsergroup.is_payt = 0
        if (hParams.inrequest.is_payt&&hParams.inrequest.is_paytedit) oUsergroup.is_paytedit = 1
        else oUsergroup.is_paytedit = 0
////t dep <<< /////////////
////project payment >>> ///
        if (hParams.inrequest.is_payproject) oUsergroup.is_payproject = 1
        else oUsergroup.is_payproject = 0
////project payment <<< ///
////dopcard payment >>> ///
        if (hParams.inrequest.is_dopcardpayment) oUsergroup.is_dopcardpayment = 1
        else oUsergroup.is_dopcardpayment = 0
////dopcard payment <<< ///

        oUsergroup.save(flush:true,failOnError:true)
      } catch(Exception e){
        log.debug('error in User.groupRightssave')
        log.debug(e.toString())
        
        render ([error:true] as JSON)
        return
      }
      render ([error:false] as JSON)
      return
    }

    render ([error:true] as JSON)
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveGroupDetail = {
    checkAccess(2)
    requestService.init(this)
    
    def hsRes = [:]  
    hsRes.user = session.user        
    
    hsRes+= [error:false]
    hsRes.errorcode=[]   
    hsRes+= requestService.getParams(['department_id','visualgroup_id'],null,['description','name'])   
    def iId = requestService.getIntDef('id',0)        
    
    if (!hsRes.inrequest.name) {
      hsRes = [error:true,errorcode:3]
      render hsRes as JSON
      return      
    }
    
    def oUsergroup
    def bFlag=0
    
    if(iId){
      oUsergroup=Usergroup.get(iId)
      bFlag=1                  
    }else{           
      if (!Usergroup.findAllWhere(name:hsRes.inrequest.name)){
        bFlag=1        
        oUsergroup = new Usergroup()
      }else{
        hsRes = [error:true,errorcode:2]
      }
    }
    if(bFlag){     
      if ((!oUsergroup&&iId) || (iId && !session.user?.group?.is_usergroupedit) || (!iId && !session.user?.group?.is_usergroupinsert)) {
        render(contentType:"application/json"){[error:true]}
        return
      }    
      try {
        oUsergroup.csiSetData(hsRes.inrequest).save(flush:true,failOnError:true)
        hsRes.group_id=oUsergroup.id
      } catch(Exception e) {
        log.debug("Error save data in User/creategroup\n"+e.toString())      
        hsRes = [error:true,errorcode:1]
      }
    }                        
    
    if(!hsRes.error)
      flash.groupedit_success=1
    render hsRes as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def groupdetail = {
    checkAccess(2)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    hsRes.user = session.user

    hsRes.group = Usergroup.get(requestService.getIntDef('id',0))
    hsRes.department = Department.findAllByIs_extra(0,[sort:'name',order:'asc'])

    return hsRes
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////Group administration <<<//////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////
  def updatesystemstatus = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)

    if (hsRes.user.accesslevel!=1) {
      response.sendError(403)
      return
    }

    try {
      Dynconfig.findByName('system.activity.status').updateValue(requestService.getStr('status')).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in User/updatesystemstatus\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def justtest = {
    def text = request.reader.text
    def parsedXML
    if (text) {
      log.debug("User/justtest parsed.XML:\n"+text.decodeURL())
      parsedXML = new XmlSlurper().parseText(text.decodeURL())
    }
    render "<reservations />"
  }

}