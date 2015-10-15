class Salary {
  static mapping = {
    version false
  }
  static constraints = {
    prepaydate(nullable:true)
    offdate(nullable:true)
    cashdate(nullable:true)
  }

  Long id
  Integer month
  Integer year
  Long pers_id
  Integer department_id
  Date inputdate = new Date()
  Date moddate = new Date()
  BigDecimal actsalary = 0.0g
  BigDecimal offsalary = 0.0g
  Integer prepayment  = 0
  BigDecimal prevfix = 0.0g
  BigDecimal cardmain = 0.0g
  BigDecimal cardadd = 0.0g
  Integer cash = 0
  Integer bonus = 0
  Integer shtraf = 0
  Float overloadhour = 0f
  Integer overloadsumma = 0
  Integer holiday = 0
  Integer reholiday = 0
  Integer precashpayment = 0
  Integer modstatus = 0
  Integer prepaystatus = 0
  Date prepaydate
  Integer offstatus = 0
  Date offdate
  Integer cashstatus = 0
  Date cashdate

	def beforeUpdate(){
		moddate = new Date()
	}

  Salary setOffPaymentDirectorsData(Salarycomp scomp, Integer _actsalary){
    department_id = 0
    actsalary = actsalary?:Pers.get(pers_id)?.actsalary?.toBigDecimal()?:0.0g
    cardmain += scomp.cardmain
    cardadd += scomp.cardadd
    reholiday = 0
    cash += scomp.cashsalary
    this
  }

  Salary setOffPaymentEmployeeData(Salarycomp scomp, Integer _actsalary){
    if (!department_id){
      def oDepartment = Department.get(User.findByModstatusAndPers_id(1,pers_id)?.department_id?:0)
      department_id = oDepartment?.parent?:oDepartment?.id?:0
    }
    actsalary = _actsalary?.toBigDecimal()?:0.0g
    cardmain += scomp?.cardmain?:0.0g
    cardadd = 0
    def prevDate = new Date(year-1900,month-2,1)
    reholiday = Salary.findByPers_idAndYearAndMonth(pers_id,prevDate.getYear()+1900,prevDate.getMonth()+1)?.holiday?:0
    prevfix = Payrequest.findAll{ month(paydate) == this.month &&
                                  year(paydate) == this.year &&
                                  paytype == 1 &&
                                  paycat == 3 &&
                                  is_dop == 1 &&
                                  paydate >= new Date(this.year-1900,this.month-1,Tools.getIntVal(Dynconfig.findByName('salary.buhreport.datecreated')?.value,15)) && 
                                  pers_id == this.pers_id
                                }.sum{ it.summa }?:0.0g
    cash = Math.ceil(actsalary - prepayment - cardmain - reholiday - prevfix).toInteger()
    this
  }

  Salary csiComputePrepaymentSumma(_summa){
    actsalary = _summa?:0.0g
    offsalary = new CompersSearch().csiFindActiveCompers(pers_id)?.sum{ it.salary }?:0.0g
    prevfix = Payrequest.findAll{ month(paydate) == this.month &&
                                  year(paydate) == this.year &&
                                  paytype == 1 &&
                                  paycat == 3 &&
                                  is_dop == 1 &&
                                  paydate >= new Date(this.year-1900,this.month-1,Tools.getIntVal(Dynconfig.findByName('salary.buhreport.datecreated')?.value,15)) && 
                                  pers_id == this.pers_id
                                }.sum{ it.summa }?:0.0g
    prepayment = Math.round(actsalary/2-prevfix)
    cash = (!cash?0:cash-prepayment-prevfix)
    this
  }

  Salary csiSetPrepaymentSumma(_summa){
    prepayment = _summa?:0
    this
  }

  Salary csiSetModstatus(iStatus){
    modstatus = iStatus?:0
    this
  }

  Salary csiSetConfirm(iStatus){
    is_confirm = iStatus?:0
    this
  }

  Salary csiSetPrepaystatus(iStatus){
    prepaystatus = iStatus?:0
    this
  }

  Salary csiSetPrepaydate(dDate){
    prepaydate = dDate
    this
  }

  Salary csiSetCashstatus(iStatus){
    cashstatus = iStatus?:0
    this
  }

  Salary csiSetCashdate(dDate){
    cashdate = dDate
    this
  }

  Salary updateCashData(_request){
    cash += ((_request.bonus?:0)-bonus)+(shtraf-(_request.shtraf?:0))+(prevfix-(_request.prevfix?:0))+((_request.overloadsumma?:0)-overloadsumma)+((_request.holiday?:0)-holiday)+(precashpayment-(_request.precashpayment?:0))+((department_id?actsalary:_request.actsalary.toBigDecimal()?:0.0g)-actsalary).toInteger()
    actsalary = department_id?actsalary:_request.actsalary.toBigDecimal()?:0.0g
    bonus = _request.bonus?:0
    shtraf = _request.shtraf?:0
    overloadhour = ((_request.overloadhour?:0f)*2).toInteger()/2
    overloadsumma = _request.overloadsumma?:0
    holiday = _request.holiday?:0
    precashpayment = _request.precashpayment?:0
    prevfix = _request.prevfix?:0.0g
    this
  }

}