class Agentperiod {
  static mapping = { version false }

  Integer id
  Integer kredit_id
  Date startdate
  Date enddate
  Integer client_id

  String toString(){
    def _kredit = Kredit.get(kredit_id)
    "$_kredit.anumber с ${String.format('%td.%<tm.%<tY',startdate)} по ${String.format('%td.%<tm.%<tY',enddate)} в ${Bank.get(_kredit.bank_id)?.shortname}"
  }


  Agentperiod fillFrom(Kredit _kredit){
    startdate = _kredit.startdate
    enddate = _kredit.enddate
    client_id = _kredit.client_id
    this
  }

  Agentperiod csiSetStartdate(Date _startdate){
    startdate = _startdate
    this
  }

  Agentperiod csiSetEnddate(Date _enddate){
    enddate = _enddate
    this
  }

  Agentperiod csiSetClient_id(Integer _client_id){
    client_id = _client_id
    this
  }
}