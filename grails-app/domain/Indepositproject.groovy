class Indepositproject {
  static mapping = { version false }

  Integer id
  Integer indeposit_id
  Integer project_id
  BigDecimal summa
  Integer is_percent = 0
  Integer is_transfer = 0
  Integer payrequest_id = 0
  Integer cash_id = 0
  Integer related_id = 0
  Date operationdate
  Date inputdate = new Date()

  def beforeDelete(){
    Indepositproject.withNewSession {
      if (related_id>0) Indepositproject.findByIndeposit_idAndId(indeposit_id,related_id)?.delete(flush:true)
    }
  }

  Indepositproject csiSetProject(iProject){
    project_id = iProject?:9
    this
  }

  Indepositproject csiSetSumma(dbSumma){
    summa = dbSumma?:0.0g
    this
  }

  Indepositproject csiSetIsTransfer(){
    is_transfer = 1
    this
  }

  Indepositproject csiSetIsPercent(){
    is_percent = 1
    this
  }

  Indepositproject csiSetRelated(iId){
    related_id = iId?:0
    this
  }

}