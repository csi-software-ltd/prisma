class Cashclass {
  static mapping = { version false }

  Integer id
  String name
  Integer is_type1
  Integer is_type2
  Integer is_type3
  Integer is_type4
  Integer is_type5
  Integer is_agent
  Integer is_create_deprecord
  Integer is_defaultexpense
  String confkey

  static Boolean isNeedDepRecord(_cashclass){
    Cashclass.get(_cashclass)?.is_create_deprecord==1&&_cashclass!=7
  }
}