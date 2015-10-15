class Agentrateforperiods {
  static mapping = { version false }

  Integer id
  Integer agentkredit_id
  Integer agent_id
  Integer agentratekreditplan_id
  Float rate
  Integer is_sub
  Integer subtype
  Integer is_display

  Agentrateforperiods setMainData(_prop){
    properties = _prop
    this
  }

  Agentrateforperiods csiSetPlanId(_palnId){
    agentratekreditplan_id = _palnId
    this
  }

	Agentrateforperiods setData(_request){
		rate = _request."rate_$id"?_request."rate_$id".toFloat():0f
		this
	}

}