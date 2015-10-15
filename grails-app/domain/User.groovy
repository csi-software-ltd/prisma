class User {  
  def searchService
  
  static constraints = {  
    lastdate(nullable:true)
  }  
  static mapping = {
    version false    
  }

  Long id 
  Long pers_id
  Integer is_remote = 0
  Integer is_block = 0  
  Integer modstatus
  Integer usergroup_id
  Integer department_id
  Integer is_leader
  Integer is_tehdirleader = 0
  Integer accesslevel
  Integer cashaccess = 0
  Integer confaccess = 0
  String login  
	String name
  String email
	String password  
	String tel = ''        	 
  String smscode = ''  
  Date inputdate
  Date lastdate
  Long saldo = 0l
  Long cassadebt = 0l
  Long precassadebt = 0l
  Integer is_loan = 0
  Integer is_parking = 0
  Long loansaldo = 0l
  Long penalty = 0l

  /////////////////////////////////////////////////////////////////////////////
  Boolean validateTelNumber(){
    this.smscode = Tools.generateSMScode()
    if (!this.save(flush:true)){
      log.debug('error on save User in User:validateTelNumber')
      this.errors.each{log.debug(it)}
      return false
    }    
      
    return true
  }
  
  def csiGetUsersList(lId,sLogin,sPers,iUserGroup,iDepartment,iModstatus,isBlock,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]    
    def hsString=[:]
    def hsInt=[:]

    hsSql.select="*, user.id as id"
    hsSql.from="user, usergroup, pers"     
    hsSql.where="user.usergroup_id=usergroup.id AND usergroup.is_superuser!=1 AND user.pers_id=pers.id"+
      ((iUserGroup>0)?' AND usergroup_id=:iUserGroup':'')+
      ((iDepartment>0)?' AND (user.department_id=:iDepartment OR user.department_id IN (SELECT id FROM department WHERE parent=:iDepartment))':'')+
      ((lId>0)?' AND user.id=:lId':'')+  
      ((sLogin!='')?' AND login like CONCAT(:sLogin,"%")':'')+ 
      ((sPers!='')?' AND shortname like CONCAT(:sPers,"%")':'')+
      ((iModstatus>-1)?' AND modstatus =:iModstatus':'')+
      ((isBlock)?' AND is_block=1':'')      
                
    hsSql.order="user.id desc"    
    
    if(lId>0)
      hsLong['lId']=lId      
    if(sLogin!='')
      hsString['sLogin']=sLogin
    if(sPers!='')
      hsString['sPers']=sPers  
    if(iModstatus>-1)  
      hsInt['iModstatus']=iModstatus
    if(iUserGroup>0)
      hsInt['iUserGroup']=iUserGroup
    if(iDepartment>0)
      hsInt['iDepartment']=iDepartment  

    return searchService.fetchDataByPages(hsSql,null,hsLong,hsInt,hsString,
      null,null,iMax,iOffset,'user.id',true,User.class)
  } 
  def csiSetUser(hsInrequest,bNew){
    if(bNew){
      inputdate=new Date()
      modstatus=1
      pers_id = hsInrequest?.pers_id?:0
    }  
    
    is_remote = hsInrequest?.is_remote?:0
    is_block = hsInrequest?.is_block?:0      
    usergroup_id = hsInrequest?.usergroup_id?:0    
    accesslevel = hsInrequest?.accesslevel?:0
    login = hsInrequest?.login?:''
    name = hsInrequest?.name?:''
    email = hsInrequest?.email?:''
    if(hsInrequest?.password)
	    password = Tools.hidePsw(hsInrequest?.password?:'')
	  tel = hsInrequest?.tel?:''
    cashaccess = hsInrequest?.cashaccess?:0
    confaccess = hsInrequest?.confaccess?:0
    is_loan = hsInrequest?.is_loan?:0
    is_parking = hsInrequest?.is_parking?:0
    is_tehdirleader = hsInrequest?.is_tehdirleader?:0

    if(pers_id){
      department_id = hsInrequest?.department_id?:0
      is_leader = hsInrequest?.is_leader?:0
    }
    
    this
  }
  
  def csiSetModstatus(iMod){
    modstatus=iMod
    this
  }    

  User changeSaldo(lSaldo){
    saldo += lSaldo
    this
  }

  User changeLoansaldo(lSaldo){
    loansaldo += lSaldo
    this
  }

  User changePenalty(lSumma){
    penalty += lSumma
    this
  }

  User updatePredebt(lSumma){
    precassadebt += lSumma
    this
  }

  User updateCassadebt(lSumma){
    cassadebt += lSumma
    this
  }
  
  def csiFindExecutorTaskpay(sName,iMax){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="*, user.id AS id"
    hsSql.from='user, taskpay'              
    hsSql.where="taskpay.executor = user.id"+                
                ((sName!='')?' AND name like concat("%",:name,"%")':'')
    hsSql.order="name asc"
    hsSql.group="user.id"
  
    if(sName!='')
      hsString['name']=sName             

    return searchService.fetchData(hsSql,null,null,hsString,null,User.class,iMax)
  }

}
