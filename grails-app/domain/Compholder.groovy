class Compholder {
  static mapping = {
    version false
  }
  static constraints = {
    enddate(nullable:true)
  }

  Integer id
  Integer company_id
  Long pers_id
  Integer holdcompany_id
  Integer share
  Long summa
  Date startdate
  Date enddate
  Integer modstatus
  String comment
  Integer admin_id

  Compholder setData(_request){
    share = _request.share
    startdate = _request.founder_startdate
    enddate = _request.founder_enddate?:null
    comment = _request.comment?:''
    summa = _request.summa?:0l
    this
  }

  Compholder updateModstatus(){
    modstatus = enddate?0:1
    if(modstatus==1) Compholder.findByCompany_idAndPers_idAndHoldcompany_idAndModstatusAndIdNotEqual(company_id,pers_id,holdcompany_id,1,id)?.csiSetEndate(startdate-1)?.updateModstatus()?.csiSetAdmin(admin_id)?.merge(failOnError:true)
    this
  }

  Compholder csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  Compholder csiSetEndate(dDate){
    enddate = dDate
    this
  }

}