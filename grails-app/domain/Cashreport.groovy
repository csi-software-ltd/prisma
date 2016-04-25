class Cashreport {
  static mapping = { version false }
  static constraints = {
    confirmdate(nullable:true)
  }

  Integer id
  Long summa
  Date repdate
  Date confirmdate
  Integer expensetype_id = 0
  Integer project_id = 9
  Integer car_id = 0
  String description
  Integer modstatus = 0
  Integer department_id
  Long file_id
  String comment_dep = ''
  String comment = ''
  Long executor
  Long initiator
  Integer type
  Integer cashdepartment_id = 0

  def transient adm_id

  def afterInsert(){
    new Cashevent(cashreport_id:id,cashzakaz_id:0,casheventtype_id:7,admin_id:executor).setData(properties).save(failOnError:true)
    if (modstatus==1) new Cashevent(cashreport_id:id,cashzakaz_id:0,casheventtype_id:8,admin_id:initiator).setData(properties).save(failOnError:true)
    if (modstatus==2) new Cashevent(cashreport_id:id,cashzakaz_id:0,casheventtype_id:9,admin_id:initiator).setData(properties).save(failOnError:true)
  }

  def beforeDelete(){
    Cashevent.withNewSession{
      Cashevent.findAllByCashreport_id(id).each{ it.delete(flush:true) }
    }
  }

  Cashreport csiSetDepartment(_request,_user){
    department_id = _user.cashaccess!=3?_user.department_id:type==1?_request.department_id:Department.get(_request.executor)?.id?:0
    this
  }

  Cashreport setData(_request,_accesslevel){
    summa = _request.summa
    repdate = _request.repdate
    expensetype_id = _request.expensetype_id?:expensetype_id
    car_id = !Expensetype.get(expensetype_id)?.is_car?0:_request.car_id?:car_id
    if (isDirty('expensetype_id')) Cashdepartment.get(cashdepartment_id)?.updateExpensetype_id(expensetype_id)?.save(failOnError:true)
    project_id = _request.project_id?:project_id
    description = _request.description?:''
    if(_accesslevel==3) comment = _request.comment?:''
    if(_accesslevel==2) comment_dep = _request.comment_dep?:''
    this
  }

  Cashreport csiSetAdmin(iAdmin){
    adm_id = iAdmin?:0
    this
  }

  Cashreport csiSetFileId(iFileId){
    file_id = iFileId?:file_id
    this
  }

  Cashreport csiSetModstatus(iStatus,iAccesslevel){
    if(iStatus==1&&modstatus<=0&&iAccesslevel in [1,2,6]){
      modstatus = 1
      cashdepartment_id = new Cashdepartment(department_id:department_id,pers_id:(type?0:executor),operationdate:new Date(),admin_id:initiator,comment:description,expensetype_id:expensetype_id).fillData(valuta_id:857,maincashtype:9,maincashclass:2,receipt:file_id,summa:summa).save(failOnError:true)?.id?:0
      if(id) new Cashevent(cashreport_id:id,cashzakaz_id:0,casheventtype_id:8,admin_id:initiator).setData(properties).save(failOnError:true)
    }
    else if(iStatus==2&&modstatus in 0..1&&iAccesslevel==3){
      if (modstatus==0) cashdepartment_id = new Cashdepartment(department_id:department_id,pers_id:(type?0:executor),operationdate:new Date(),admin_id:initiator,comment:description,expensetype_id:expensetype_id).fillData(valuta_id:857,maincashtype:9,maincashclass:2,receipt:file_id,summa:summa).save(failOnError:true)?.id?:0
      if(id) new Cashevent(cashreport_id:id,cashzakaz_id:0,casheventtype_id:9,admin_id:initiator).setData(properties).save(failOnError:true)
      modstatus = 2
      confirmdate = new Date()
    }
    else if(iStatus==-2&&modstatus in 0..1&&iAccesslevel==3){
      if (modstatus==1) cashdepartment_id = new Cashdepartment(department_id:department_id,pers_id:(type?0:executor),operationdate:new Date(),admin_id:initiator,comment:'отмена подтверждения',expensetype_id:expensetype_id).fillData(valuta_id:857,maincashtype:9,maincashclass:2,receipt:file_id,summa:-summa).save(failOnError:true)?.id?:0
      new Cashevent(cashreport_id:id,cashzakaz_id:0,casheventtype_id:10,admin_id:initiator).setData(properties).save(failOnError:true)
      modstatus = -2
    }
    else if(iStatus==-2&&modstatus==2&&iAccesslevel==3/*&&isThisMonth()*/){
      cashdepartment_id = new Cashdepartment(department_id:department_id,pers_id:(type?0:executor),operationdate:new Date(),admin_id:initiator,comment:'отмена подтверждения',expensetype_id:expensetype_id).fillData(valuta_id:857,maincashtype:9,maincashclass:2,receipt:file_id,summa:-summa).save(failOnError:true)?.id?:0
      new Cashevent(cashreport_id:id,cashzakaz_id:0,casheventtype_id:10,admin_id:initiator).setData(properties).save(failOnError:true)
      modstatus = -2
    }
    else if(iStatus==-1&&modstatus in 0..1 &&iAccesslevel==2){
      if (modstatus==1) cashdepartment_id = new Cashdepartment(department_id:department_id,pers_id:(type?0:executor),operationdate:new Date(),admin_id:initiator,comment:'отмена подтверждения',expensetype_id:expensetype_id).fillData(valuta_id:857,maincashtype:9,maincashclass:2,receipt:file_id,summa:-summa).save(failOnError:true)?.id?:0
      new Cashevent(cashreport_id:id,cashzakaz_id:0,casheventtype_id:10,admin_id:initiator).setData(properties).save(failOnError:true)
      modstatus = -1
    }
    else if(iStatus==0&&modstatus<0&&iAccesslevel in [0,1,2,3,4,6]){
      modstatus = 0
      new Cashevent(cashreport_id:id,cashzakaz_id:0,casheventtype_id:7,admin_id:initiator).setData(properties).save(failOnError:true)
    }
    this
  }

  Boolean isThisMonth(){
    repdate.getMonth()==new Date().getMonth()&&repdate.getYear()==new Date().getYear()
  }
}