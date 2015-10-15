class Cashdepartment {
  static mapping = {
    version false
  }

  Integer id
  Integer department_id
  Long summa
  Long saldo
  Integer valuta_id = 857
  Long pers_id
  Integer type
  Integer cashclass
  Integer is_dep
  Long receipt
  Date inputdate = new Date()
  Date operationdate
  Date platperiod
  Integer expensetype_id = 0
  Long admin_id
  String comment

  Cashdepartment fillData(_request){
    type = _request.maincashtype==1?2:_request.maincashtype==5?5:_request.maincashtype==9?9:4
    cashclass = _request.maincashclass
    receipt = _request.receipt
    platperiod = operationdate
    summa = _request.summa
    if (pers_id) {
      is_dep = 0
      department_id = User.get(pers_id)?.department_id?:department_id
      saldo = User.get(pers_id)?.changeSaldo(cashclass in [1,3,4,5]?0:type==2?summa:-summa)?.changeLoansaldo(cashclass!=3?0:type==2?summa:-summa)?.changePenalty(cashclass!=5?0:type==4?-summa:summa)?.save(failOnError:true)?.saldo
    } else {
      is_dep = 1
      saldo = Department.get(department_id)?.changeSaldo(cashclass in [4]?0:type==2?summa:-summa)?.save(failOnError:true)?.cashsaldo?:0
    }

    this
  }

  Cashdepartment setData(_request){
    type = _request.depcashtype==1&&is_dep==0?2:_request.depcashtype
    cashclass = _request.depcashclass
    operationdate = _request.operationdate
    platperiod = _request.operationdate
    comment = _request.comment?:''
    summa = _request.summa
    if (is_dep==1) {
      saldo = Department.get(department_id)?.changeSaldo(type==3?summa:-summa)?.save(failOnError:true)?.cashsaldo
      new Cashdepartment(department_id:User.get(pers_id)?.department_id?:department_id,pers_id:pers_id,admin_id:admin_id,receipt:receipt,is_dep:0,expensetype_id:expensetype_id).setData(depcashtype:_request.depcashtype,depcashclass:cashclass,operationdate:operationdate,comment:comment,summa:summa).save(failOnError:true)
    } else {
      saldo = User.get(pers_id)?.changeSaldo(cashclass in [1,4]?0:type==2?summa:-summa)?.save(failOnError:true)?.saldo
    }
    this
  }

  Cashdepartment csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  Cashdepartment csiSetReceipt(iFileId){
    receipt = iFileId?:0
    this
  }

  Cashdepartment updateExpensetype_id(iType){
    expensetype_id = iType?:0
    this
  }

}