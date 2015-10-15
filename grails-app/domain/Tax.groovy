class Tax {  
  static mapping = {
    version false
  }

  Integer id
  String name
  String shortname
  Integer columnnumber
  Integer paydate
  Integer kbkrazdel_id

  String toString(){
  	"$id - $shortname"
  }  
}