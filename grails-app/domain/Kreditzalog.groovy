class Kreditzalog {
  static mapping = { version false }
  static constraints = {
    zalogstart(nullable:true)
    zalogend(nullable:true)
    strakhdate(nullable:true)
    strakhvalidity(nullable:true)
  }

  Integer id
  Integer kredit_id
  Integer is_zalogagr = 0
  Integer zalogtype_id
  String pledger
  String zalogagr
  String zalogprim
  Date zalogstart
  Date zalogend
  BigDecimal zalogcost
  BigDecimal marketcost
  String strakhnumber
  Date strakhdate
  Date strakhvalidity
  Long strakhsumma
  Integer parent = 0
  Integer space1 = 0
  Integer space2 = 0

  def beforeInsert(){
    Kredit.withNewSession{
      Kredit.get(kredit_id).csiSetZalogstatus(2)?.save(flush:true)
    }
  }

  def beforeDelete(){
    if (!Kreditzalog.findAllByKredit_idAndIdNotEqual(kredit_id,id))
      Kredit.withNewSession{
        Kredit.get(kredit_id).csiSetZalogstatus(1)?.save(flush:true)
      }
  }

  Kreditzalog setData(_request){
    is_zalogagr = _request.is_zalogagr?:0
    zalogtype_id = _request.zalogtype_id
    pledger = _request.pledger
    zalogagr = _request.zalogagr?:''
    zalogprim = _request.zalogprim?:''
    zalogstart = _request.zalogstart
    zalogend = _request.zalogend
    zalogcost = _request.zalogcost?:0.0g
    marketcost = _request.marketcost?:0.0g
    strakhnumber = _request.strakhnumber?:''
    strakhdate = _request.strakhdate
    strakhvalidity = _request.strakhvalidity
    strakhsumma = _request.strakhsumma?:0l
    space1 = _request.space1?:0
    space2 = !space1?0:_request.space2?:0
    this
  }

  Kreditzalog cloneData(Kreditzalog _zalog){
    is_zalogagr = _zalog.is_zalogagr
    zalogtype_id = _zalog.zalogtype_id
    pledger = _zalog.pledger
    zalogagr = 'Доп.соглашение к договору '+_zalog.zalogagr
    zalogprim = ''
    zalogcost = _zalog.zalogcost
    marketcost = _zalog.marketcost
    strakhnumber = _zalog.strakhnumber
    strakhdate = _zalog.strakhdate
    strakhvalidity = _zalog.strakhvalidity
    strakhsumma = _zalog.strakhsumma
    space1 = _zalog.space1
    space2 = _zalog.space2
    this
  }
}