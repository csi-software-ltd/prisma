class Agentagrbank {
  static mapping = { version false }

  Integer id
  Integer agentagr_id
  String bank_id
  Integer is_main = 0

  Agentagrbank updateBank(sBankId){
    bank_id = sBankId
    this
  }

}