class Cashrequest {
  static mapping = {
    version false
  }
  private enum Historyfields {
    MODSTATUS, SUMMA, MARGIN
  }

  Integer id
  Date inputdate = new Date()
  Date reqdate
  Integer modstatus = 1
  Integer summa
  String comment = ''
  Long initiator
  Float margin = 0f

  def transient adm_id

  def afterInsert(){
    new Cashrequesthist(cashrequest_id:id,admin_id:initiator).setData(properties).save(failOnError:true)
  }

  def beforeUpdate(){
    if(isHaveDirty()) new Cashrequesthist(cashrequest_id:id,admin_id:adm_id).setData(properties).save(failOnError:true)
  }

  Cashrequest csiSetSumma(iSumma){
    summa = iSumma?:0
    this
  }

  Cashrequest csiSetAdmin(iAdmin){
    adm_id = iAdmin?:0
    this
  }

  Cashrequest setData(_request,_accesslevel){
    if(modstatus < 5 && _accesslevel in [3,5]) csiSetSumma(_request.summa)
    if(_accesslevel in [3,5]) comment = _request.comment?:''
    if(modstatus < 5 && _accesslevel == 4) margin = _request.margin?_request.margin.toFloat():0f
    this
  }

  Cashrequest csiSetModstatus(iStatus,_accesslevel){
    if(iStatus==7&&modstatus in [1,2,6]&&_accesslevel==3){
      modstatus = 7
      Cashzakaz.findAllByCashrequest_id(id).each{ it.csiSetAdmin(adm_id).csiSetModstatus(1,_accesslevel).csiSetCashrequestId(0).save(failOnError:true) }
    }
    else if(iStatus==2&&modstatus==1&&_accesslevel==3){
      modstatus = 2
      Cashzakaz.findAllByCashrequest_id(id).each{ it.csiSetAdmin(adm_id).csiSetModstatus(2,_accesslevel).save(failOnError:true) }
    }
    else if(iStatus==6&&modstatus in [2,3]&&_accesslevel==5){
      modstatus = 6
      Cashzakaz.findAllByCashrequest_id(id).each{ it.csiSetAdmin(adm_id).csiSetModstatus(1,_accesslevel).save(failOnError:true) }
    }
    else if(iStatus==3&&modstatus in [2,6]&&_accesslevel==5){
      modstatus = 3
      Cashzakaz.findAllByCashrequest_id(id).each{ it.csiSetAdmin(adm_id).csiSetModstatus(2,_accesslevel).save(failOnError:true) }
    }
    else if(iStatus==4&&modstatus in 1..3&&_accesslevel==3){
      modstatus = 4
      Cashzakaz.findAllByCashrequest_id(id).each{ it.csiSetAdmin(adm_id).csiSetModstatus(2,_accesslevel).save(failOnError:true) }
    }
    else if(iStatus==1&&modstatus==4&&_accesslevel==3){
      modstatus = 1
      Cashzakaz.findAllByCashrequest_id(id).each{ it.csiSetAdmin(adm_id).csiSetModstatus(1,_accesslevel).save(failOnError:true) }
    }
    else if(iStatus==5&&modstatus==4&&_accesslevel==4){
      modstatus = 5
      Cashzakaz.findAllByCashrequest_id(id).each{ it.csiSetAdmin(adm_id).approveByManager(_accesslevel).save(failOnError:true) }
      new Payrequest(cashrequest_id:id).csiSetPayrequest(paycat:4,paytype:8,client_id:Client.findByIs_t(1)?.id,paydate:String.format('%td.%<tm.%<tY',new Date()),is_nds:1,comment:'Запрос на откуп',summa:summa / (1 - margin / 100)).csiSetPayoff(margin).csiSetInitiator(adm_id).save(failOnError:true)
    }
    this
  }

  Boolean isHaveDirty (){ return Cashrequest.Historyfields.values().toList().find{ isDirty(it.toString().toLowerCase())}?true:false }

}