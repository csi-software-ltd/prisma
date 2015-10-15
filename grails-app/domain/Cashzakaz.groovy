class Cashzakaz {
  static mapping = {
    version false
  }

  Integer id
  Long initiator
  Long summa
  String purpose
  Integer valuta_id
  Date inputdate = new Date()
  Integer department_id
  Integer modstatus
  Integer is_managerapprove = 0
  Date moddate = new Date()
  String comment
  Integer cashrequest_id = 0
  Integer salaryreport_id = 0
  Long file_id = 0
  Date todate

  def transient admin_id

  def afterInsert(){
    new Cashevent(cashzakaz_id:id,casheventtype_id:1,admin_id:initiator,cashreport_id:0).setData(properties).save(failOnError:true)
  }

	def beforeUpdate(){
		moddate = new Date()
    if(isDirty('summa')) new Cashevent(cashzakaz_id:id,casheventtype_id:12,admin_id:admin_id,cashreport_id:0).setData(properties).save(failOnError:true)
	}

  Cashzakaz setData(_request){
		summa = _request.summa?:summa
		purpose = _request.purpose
		valuta_id = _request.valuta_id?:valuta_id?:857
		comment = _request.comment?:''
    todate = _request.todate?:Tools.getNextWorkedDate(new Date())
    this
  }

  Cashzakaz approveByManager(iAccesslevel){
    if(iAccesslevel==4) is_managerapprove = 1
    this
  }

  Cashzakaz csiSetModstatus(iStatus,iAccesslevel){
    modstatus = modstatus?:1
    if(iStatus==1&&modstatus==2&&is_managerapprove==0&&iAccesslevel in [3,5]){
      modstatus = 1
      if (salaryreport_id>0) {
        //отмена подтверждения авансовой ведомости
        def avans = Salaryreport.get(salaryreport_id)?.csiSetModstatus(1)?.csiSetConfirm(0)?.save(failOnError:true)
      }
    }
    else if(iStatus==2&&modstatus==1&&iAccesslevel in [3,5]){
      modstatus = 2
      new Cashevent(cashzakaz_id:id,casheventtype_id:3,admin_id:admin_id,cashreport_id:0).setData(properties).save(failOnError:true)
      if (salaryreport_id>0) {
        //подтверждение авансовой ведомости
        Salaryreport.get(salaryreport_id)?.csiSetConfirm(1)?.save(failOnError:true)
      }
    }
    else if(iStatus==3&&modstatus==2&&iAccesslevel==3){
      modstatus = 3
      new Cashevent(cashzakaz_id:id,casheventtype_id:15,admin_id:admin_id,cashreport_id:0).setData(properties).save(failOnError:true)
    }
    else if(iStatus==2&&modstatus==3&&iAccesslevel==3){
      modstatus = 2
      new Cashevent(cashzakaz_id:id,casheventtype_id:16,admin_id:admin_id,cashreport_id:0).setData(properties).save(failOnError:true)
    }
    else if(iStatus==5&&modstatus==1&&iAccesslevel==3&&!cashrequest_id){
      modstatus = 5
      new Cashevent(cashzakaz_id:id,casheventtype_id:2,admin_id:admin_id,cashreport_id:0).setData(properties).save(failOnError:true)
      if (salaryreport_id>0) {
        //сброс статусов авансовой ведомости в случае отклонения заявки
        def avans = Salaryreport.get(salaryreport_id)?.csiSetZeroModstatus()?.csiSetConfirm(-1)?.save(failOnError:true)
        Salary.findAllByMonthAndYearAndDepartment_id(avans?.month,avans?.year,avans?.department_id).each{
          it.csiSetPrepaystatus(0).save(flush:true)
        }
      }
    }
    else if(iStatus==1&&modstatus==5){
      modstatus = 1
      new Cashevent(cashzakaz_id:id,casheventtype_id:1,admin_id:admin_id,cashreport_id:0).setData(properties).save(failOnError:true)
    }
    else if(iStatus==-1&&modstatus==1&&!cashrequest_id){
      modstatus = -1
      new Cashevent(cashzakaz_id:id,casheventtype_id:6,admin_id:admin_id,cashreport_id:0).setData(properties).save(failOnError:true)
    }
    else if(iStatus==4&&modstatus==3&&iAccesslevel==3){
      modstatus = 4
      new Cashevent(cashzakaz_id:id,casheventtype_id:5,admin_id:admin_id,cashreport_id:0).setData(properties).save(failOnError:true)
    }
    else if(iStatus==4&&modstatus==1&&iAccesslevel==3&&!cashrequest_id){
      modstatus = 4
      new Cashevent(cashzakaz_id:id,casheventtype_id:5,admin_id:admin_id,cashreport_id:0).setData(properties).save(failOnError:true)
      if (salaryreport_id>0) {
        //подтверждение авансовой ведомости
        Salaryreport.get(salaryreport_id)?.csiSetConfirm(1)?.save(failOnError:true)
      }
    }
    else if(iStatus==1&&modstatus!=4&&is_managerapprove==1&&iAccesslevel==3){
      modstatus = 1
      todate = Tools.getNextWorkedDate(new Date())
      cashrequest_id = 0
      is_managerapprove = 0
      new Cashevent(cashzakaz_id:id,casheventtype_id:18,admin_id:admin_id,cashreport_id:0).setData(properties).save(failOnError:true)
    }
    else if(iStatus==3&&modstatus==4&&iAccesslevel==3){
      modstatus = 3
      new Cashevent(cashzakaz_id:id,casheventtype_id:17,admin_id:admin_id,cashreport_id:0).setData(properties).save(failOnError:true)
    }
    this
  }

  Cashzakaz csiSetAdmin(lAdmin){
    admin_id = lAdmin?:0
    if (!initiator) initiator = admin_id
    this
  }

  Cashzakaz csiSetSalaryreportId(iId){
    salaryreport_id = iId?:0
    this
  }

  Integer getSummaRub(){
    summa
  }

  Cashzakaz csiSetCashrequestId(iId){
    cashrequest_id = iId?:0
    if (!cashrequest_id) csiSetModstatus(1,3)
    this
  }

  Cashzakaz csiSetIssuanceScan(iFileId){
    file_id = iFileId?:0
    if (iFileId) csiSetModstatus(4,3)
    else csiSetModstatus(3,3)
  }

}