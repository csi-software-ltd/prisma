class Agentkredit {
  static mapping = {
    version false
  }
  static constraints = {
    calcdate(nullable:true)
  }

  Integer id
  Integer agentagr_id
  Integer kredit_id
  Integer agentperiod_id
  Float rate
  Float cost
  Integer payterm
  Integer calcperiod
  Date calcdate

  Agentkredit csiSetKreditPeriod(Agentperiod _period){
    kredit_id = _period?.kredit_id?:0
    agentperiod_id = _period?.id?:0
    this
  }

  Agentkredit setData(_request){
    rate = _request.rate?_request.rate.toFloat():0f
    cost = _request.cost?_request.cost.toFloat():0f
    payterm = _request.payterm?:0
    calcperiod = _request.calcperiod?:0
    this
  }

  Agentkredit updateCalcdate(Date _enddate){
    calcdate = _enddate
    this
  }

}