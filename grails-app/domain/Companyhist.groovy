class Companyhist {
  static mapping = { version false }
  static constraints = {
    namedate(nullable:true)
    adrdate(nullable:true)
    capitaldate(nullable:true)
  }

  Integer id
  Integer company_id
  String legalname
  Date namedate
  String oktmo
  String kpp
  String okato
  String legaladr
  Date adrdate
  Long capital
  Date capitaldate
  Integer capitalsecure
  Integer capitalpaid
  Long cost
  String tel
  String taxinspection_id
  String pfrfreg
  String fssreg
  Integer taxoption_id
  Integer activitystatus_id
  Long admin_id = 0
  Date inputdate

  Companyhist setData(_prop){
    properties = _prop
    inputdate = new Date()
    this
  }

}