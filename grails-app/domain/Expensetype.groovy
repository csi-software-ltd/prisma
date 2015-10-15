class Expensetype {
  static mapping = { version false }

  Integer id
  String name
  String razdel
  String podrazdel
  Integer type = 1
  Integer modstatus = 1
  Integer expensetype1_id
  Integer expensetype2_id
  Integer is_car

  Expensetype setData(_request){
    name = _request.name
    expensetype1_id = _request.expensetype1_id
    expensetype2_id = _request.expensetype2_id?:0
    razdel = Expensetype1.get(expensetype1_id)?.name?:''
    podrazdel = Expensetype2.get(expensetype2_id)?.name?:''
    is_car = _request.is_car?:0
    this
  }

  Expensetype csiSetModstatus(iStatus){
    modstatus = iStatus?:0
    this
  }

  Expensetype csisetRazdel(sRazdel){
    razdel = sRazdel?:''
    this
  }

  Expensetype csisetPodrazdel(sPodrazdel){
    podrazdel = sPodrazdel?:''
    this
  }

  String toString(){
    "$razdel${podrazdel?' - '+podrazdel:''} - $name"
  }
}