class Deal {
  static mapping = { version false }

  Integer id
  Integer client_id
  Integer dtype = 1
  Date dstart
  Date dend
  Date moddate = new Date()
  BigDecimal commission = 0.0g
  BigDecimal subcommission = 0.0g
  BigDecimal retcommission = 0.0g
  BigDecimal repayment = 0.0g
  BigDecimal income = 0.0g
  BigDecimal outlay = 0.0g
  BigDecimal dealsaldo = 0.0g
  Integer modstatus = 0

  def beforeInsert(){
    dealsaldo = income - outlay
  }

  def beforeUpdate(){
    moddate = new Date()
    dealsaldo = income - outlay
  }

  Deal computeDates(ArrayList _payrequestlist){
    _payrequestlist.each {
      if (!dstart||it.paydate<dstart) dstart = it.paydate
      if (it.paydate>dend) dend = it.paydate
    }
    this
  }

  Deal updateComission(BigDecimal _summa){
    commission += _summa
    this
  }

  Deal updateSubComission(BigDecimal _summa){
    subcommission += _summa
    this
  }

  Deal updateRetComission(BigDecimal _summa){
    retcommission += _summa
    this
  }

  Deal updateRepayment(BigDecimal _summa){
    repayment += _summa
    this
  }

  Deal updateIncome(BigDecimal _summa){
    income += _summa
    this
  }

  Deal updateOutlay(BigDecimal _summa){
    outlay += _summa
    this
  }

  Deal confirmDeal(){
    modstatus = 1
    Client.get(client_id)?.updateSaldo(dealsaldo)?.updateAddSaldo(subcommission-retcommission)?.save(flush:true,failOnError:true)
    Payrequest.findAllByDeal_id(id).each{
      it.csiSetConfirmstatus(1).save(flush:true,failOnError:true)
    }
    this
  }

  Deal cancellDeal(){
    modstatus = 0
    Client.get(client_id)?.updateSaldo(-dealsaldo)?.updateAddSaldo(retcommission-subcommission)?.save(flush:true,failOnError:true)
    Payrequest.findAllByDeal_id(id).each{
      it.csiSetConfirmstatus(0).save(flush:true,failOnError:true)
    }
    this
  }

  Deal recomputeDeal(){
    def requestlist = Payrequest.findAllByDeal_id(id)
    commission = requestlist.sum{ it.computeComission() }?:0.0g
    subcommission = requestlist.sum{ it.computeSubComission() }?:0.0g
    retcommission = requestlist.sum{ it.is_clientcommission||it.is_midcommission?it.clientdelta:0.0g }?:0.0g
    repayment = requestlist.sum{ it.clientcommission }?:0.0g
    income = requestlist.sum{ it.computeIncome() }?:0.0g
    outlay = requestlist.sum{ it.computeOutlay() }?:0.0g
    this
  }

}