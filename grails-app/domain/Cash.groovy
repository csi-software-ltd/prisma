class Cash {
  static mapping = {
    version false
  }
  private enum Historyfields {
    DEPARTMENT_ID, SUMMA, PERS_ID, TYPE, CASHCLASS
  }

  Integer id
  Integer department_id
  Long summa = 0
  Long saldo
  Integer valuta_id
  Long pers_id
  Integer agentagr_id = 0
  Integer agent_id = 0
  Integer indeposit_id = 0
  Integer type
  Integer cashclass
  Long receipt = 0
  Date inputdate = new Date()
  Date operationdate
  Date platperiod
  Long admin_id
  String comment
  Integer project_id
  Integer car_id = 0
  Integer expensetype_id
  Long tagadmin_id
  Integer cashdepartment_id = 0

  def afterInsert(){
    if (cashclass==18&&indeposit_id>0) Indepositproject.withNewSession { new Indepositproject(indeposit_id:indeposit_id,operationdate:operationdate,cash_id:id).csiSetProject(Project.findByIs_main(1)?.id).csiSetSumma(type==2?summa:-summa).save(flush:true) }
    if (cashclass==19&&type==1&&indeposit_id>0)
      Indepositproject.withNewSession {
        def depbody = summa
        new Project().csiSearchIndepositProjects(indeposit_id).each{ prj ->
          def prcsum = prj.computeIndepositProjectPercent(indeposit_id,operationdate)
          if (prcsum>0&&depbody>=prcsum) {
            new Indepositproject(indeposit_id:indeposit_id,operationdate:operationdate,cash_id:id).csiSetProject(prj.id).csiSetSumma(prcsum).csiSetIsPercent().save(flush:true)
            depbody -= prcsum
          }
        }
      }
  }

  def beforeUpdate(){
    ///////indeposit>>>
    if (cashclass==18&&indeposit_id>0){
      Indepositproject.withNewSession {
        Indepositproject.findAllByCash_id(id).each{ it.delete(flush:true) }
        new Indepositproject(indeposit_id:indeposit_id,operationdate:operationdate,cash_id:id).csiSetProject(Project.findByIs_main(1)?.id).csiSetSumma(type==2?summa:-summa).save(flush:true)
      }
    } else if (cashclass==19&&type==1&&indeposit_id>0){
      Indepositproject.withNewSession {
        Indepositproject.findAllByCash_id(id).each{ it.delete(flush:true) }
        def depbody = summa
        new Project().csiSearchIndepositProjects(indeposit_id).each{ prj ->
          def prcsum = prj.computeIndepositProjectPercent(indeposit_id,operationdate)
          if (prcsum>0&&depbody>=prcsum) {
            new Indepositproject(indeposit_id:indeposit_id,operationdate:operationdate,cash_id:id).csiSetProject(prj.id).csiSetSumma(prcsum).csiSetIsPercent().save(flush:true)
            depbody -= prcsum
          }
        }
      }
    } else Indepositproject.withNewSession { Indepositproject.findAllByCash_id(id).each{ it.delete(flush:true) } }
    ///////indeposit<<<
  }

	Cash setData(_request){
    project_id = _request.project_id
    if (getPersistentValue('project_id')!=Project.findByIs_main(1).id) Project.get(getPersistentValue('project_id'))?.changeLoansaldo(-(getPersistentValue('summa')?:0))?.save(failOnError:true)
    if (project_id!=Project.findByIs_main(1).id) Project.get(project_id).changeLoansaldo(summa).save(failOnError:true)
    expensetype_id = _request.expensetype_id
    car_id = !Expensetype.get(expensetype_id)?.is_car?0:_request.car_id?:0
    comment = _request.comment?:''
    if (isDirty('expensetype_id')) Cashdepartment.get(cashdepartment_id)?.updateExpensetype_id(expensetype_id)?.save(failOnError:true)
    if (!id || id == getLastId()){
  		valuta_id = _request.valuta_id?:857
  		pers_id = computePersId(_request)
      department_id = computeDepId(_request)
      agentagr_id = _request.maincashclass!=3?0:_request.agentagr_id?:0
      agent_id = _request.maincashtype==2||_request.maincashclass!=3?0:_request.agent_id?:0
      indeposit_id = !(_request.maincashclass in [18,19])?0:_request.indeposit_id?:0
  		type = _request.maincashtype
  		cashclass = _request.maincashclass
      operationdate = _request.operationdate
  		platperiod = _request.platperiod?:_request.operationdate
      summa = _request.summa
      if (isHaveDirty()&&(getPersistentValue('department_id')>0||getPersistentValue('pers_id')>0)){
        if (getPersistentValue('type')!=2&&Cashclass.isNeedDepRecord(getPersistentValue('cashclass'))) cashdepartment_id = new Cashdepartment(department_id:getPersistentValue('department_id'),pers_id:getPersistentValue('pers_id'),operationdate:new Date(),admin_id:admin_id,comment:'корректировка предыдущей записи',platperiod:getPersistentValue('platperiod'),expensetype_id:getPersistentValue('expensetype_id')).fillData(valuta_id:getPersistentValue('valuta_id'),maincashtype:getPersistentValue('type'),maincashclass:getPersistentValue('cashclass')==2?2:getPersistentValue('cashclass')==1?4:getPersistentValue('cashclass')==16?5:1,receipt:0,summa:-getPersistentValue('summa')).save(failOnError:true)?.id?:0
        if (getPersistentValue('type')!=2&&getPersistentValue('cashclass')==7) cashdepartment_id = new Cashdepartment(department_id:User.get(getPersistentValue('pers_id'))?.department_id,pers_id:getPersistentValue('pers_id'),operationdate:new Date(),admin_id:admin_id,comment:'корректировка предыдущей записи',platperiod:getPersistentValue('platperiod'),expensetype_id:getPersistentValue('expensetype_id')).fillData(valuta_id:getPersistentValue('valuta_id'),maincashtype:getPersistentValue('type'),maincashclass:3,receipt:0,summa:-getPersistentValue('summa')).save(failOnError:true)?.id?:0
      }
      if (getPersistentValue('type')==3&&getPersistentValue('cashclass')==8) Holding.findByName('dopcardsaldo').changeSaldo(getPersistentValue('summa')).save(failOnError:true)
      if (getPersistentValue('cashclass')==17) Holding.findByName('storesaldo').changeSaldo(getPersistentValue('type')==2?-(getPersistentValue('summa')?:0):(getPersistentValue('summa')?:0)).save(failOnError:true)
      if ((!id||isHaveDirty())&&(department_id>0||pers_id>0)){
        if (type!=2&&Cashclass.isNeedDepRecord(cashclass)) cashdepartment_id = new Cashdepartment(department_id:department_id,pers_id:pers_id,operationdate:new Date(),admin_id:admin_id,comment:comment,platperiod:platperiod,expensetype_id:expensetype_id).fillData(valuta_id:valuta_id,maincashtype:type,maincashclass:cashclass==2?2:cashclass==1?4:cashclass==16?5:1,receipt:receipt,summa:summa).save(failOnError:true)?.id?:0
        if (type!=2&&cashclass==7) cashdepartment_id = new Cashdepartment(department_id:User.get(pers_id)?.department_id,pers_id:pers_id,operationdate:new Date(),admin_id:admin_id,comment:comment,platperiod:platperiod,expensetype_id:expensetype_id).fillData(valuta_id:valuta_id,maincashtype:type,maincashclass:3,receipt:receipt,summa:summa).save(failOnError:true)?.id?:0
      }
      if (type==3&&cashclass==8) Holding.findByName('dopcardsaldo').changeSaldo(-summa).save(failOnError:true)
      if (cashclass==17) Holding.findByName('storesaldo').changeSaldo(type==2?summa:-summa).save(failOnError:true)
      saldo = Holding.get(1).changeSaldo(getPersistentValue('type')==5?0:getPersistentValue('type')==1?(getPersistentValue('summa')?:0):-(getPersistentValue('summa')?:0)).changeSaldo(type==5?0:type==1?-summa:summa).save(failOnError:true)?.cashsaldo
    }
    this
	}

  Cash csiSetAdmin(iAdmin){
    if (!id || id == getLastId()) admin_id = iAdmin?:0
    this
  }

  Cash csiSetTagAdmin(iAdmin){
    tagadmin_id = iAdmin?:0
    this
  }

  Cash csiSetReceipt(iFileId){
    receipt = iFileId?:receipt
    this
  }

  Integer computeExpensetype(){
    def oCashclass = Cashclass.get(cashclass)
    if(!oCashclass.is_defaultexpense) return 0
    return Tools.getIntVal(Dynconfig.findByName(oCashclass.confkey)?.value,0)
  }

  static Integer getLastId(){
    Cash.last()?.id
  }

  Boolean isHaveDirty (){ return Cash.Historyfields.values().toList().find{ isDirty(it.toString().toLowerCase())}?true:false }

  private Long computePersId(_request){
    if (_request.maincashclass==7)
      return (_request.loaner_id?:0)
    else if (_request.maincashclass==12)
      return _request.parkinger_id
    else if (_request.maincashtype==2||_request.maincashclass in [3,4,8,9,17]||(_request.department_id&&_request.maincashclass!=16))
      return 0
    return _request.pers_id?:0
  }

  private Integer computeDepId(_request){
    if (_request.maincashtype==2||_request.maincashclass in [3,7,8,9,17])
      return 0
    return _request.department_id?:User.get(pers_id)?.department_id?:0
  }
}